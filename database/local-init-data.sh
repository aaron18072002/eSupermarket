#!/usr/bin/env bash

# ==============================================================================
# eSupermarket - Local Database Initialization & Seeding Script
# Usage:
#   ./local-init-data.sh [domain] [mode]
#
# Examples:
#   ./local-init-data.sh catalog          # Reset schema and run all migrations (V1, V2, V3) for catalog_db
#   ./local-init-data.sh catalog --schema # Reset schema and run only schema migration (V1)
#   ./local-init-data.sh catalog --seed   # Run only seed data (V2, V3) without resetting schema
#   ./local-init-data.sh all              # Reset and run all migrations for all available domains
# ==============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MIGRATIONS_DIR="${SCRIPT_DIR}/migrations"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print helper functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if a Docker container is running
check_container() {
    local container_name="$1"
    if ! docker ps --filter "name=${container_name}" --filter "status=running" --format '{{.Names}}' | grep -q "^${container_name}$"; then
        log_error "Container '${container_name}' is not running!"
        log_info "Please start the database container first: (cd \"${SCRIPT_DIR}\" && docker compose up -d)"
        return 1
    fi
    return 0
}

# Execute a single SQL file against a PostgreSQL container
apply_sql_file() {
    local container="$1"
    local user="$2"
    local database="$3"
    local file_path="$4"
    local file_name
    file_name="$(basename "${file_path}")"

    if [ ! -f "${file_path}" ]; then
        log_error "Migration file not found: ${file_path}"
        return 1
    fi

    log_info "Applying [${file_name}] to database [${database}]..."
    if docker exec -i "${container}" psql -v ON_ERROR_STOP=1 -U "${user}" -d "${database}" < "${file_path}" > /dev/null; then
        log_success "Applied [${file_name}] successfully."
    else
        log_error "Failed to apply [${file_name}] to [${database}]"
        return 1
    fi
}

# Reset (drop & recreate) the public schema in a PostgreSQL database
reset_schema() {
    local container="$1"
    local user="$2"
    local database="$3"

    log_warn "Resetting database [${database}] (dropping & recreating public schema)..."
    if docker exec -i "${container}" psql -U "${user}" -d "${database}" -c \
        "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public; GRANT ALL ON SCHEMA public TO ${user};" > /dev/null 2>&1; then
        log_success "Database [${database}] schema reset successfully."
    else
        log_error "Failed to reset database [${database}] schema."
        return 1
    fi
}

# Seed Catalog Domain
seed_catalog() {
    local mode="${1:-all}"
    local container="esupermarket-postgres-catalog-1"
    local user="application"
    local database="catalog_db"
    local domain_dir="${MIGRATIONS_DIR}/catalog"

    log_info "Starting database setup for domain: [CATALOG] (mode: ${mode})"
    check_container "${container}"

    case "${mode}" in
        --schema)
            reset_schema "${container}" "${user}" "${database}"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V1__init_catalog_schema.sql"
            ;;
        --seed)
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V2__seed_reference_data.sql"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V3__seed_sample_products.sql"
            ;;
        all|"")
            reset_schema "${container}" "${user}" "${database}"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V1__init_catalog_schema.sql"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V2__seed_reference_data.sql"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V3__seed_sample_products.sql"
            ;;
        *)
            log_error "Unknown mode '${mode}'. Use --schema, --seed, or omit for all."
            return 1
            ;;
    esac

    log_success "Catalog domain completed successfully!"
}

# Seed Inventory Domain (Placeholder for future)
seed_inventory() {
    local mode="${1:-all}"
    local container="esupermarket-postgres-inventory-1"
    local user="application"
    local database="inventory_db"
    local domain_dir="${MIGRATIONS_DIR}/inventory"

    if [ ! -d "${domain_dir}" ]; then
        log_warn "Inventory migrations directory not found at '${domain_dir}'. Skipping inventory domain."
        return 0
    fi

    log_info "Starting database setup for domain: [INVENTORY] (mode: ${mode})"
    check_container "${container}"

    # Future: execute inventory migration files here
    log_success "Inventory domain completed successfully!"
}

# Seed User Domain
seed_user() {
    local mode="${1:-all}"
    local container="esupermarket-postgres-catalog-1"
    local user="application"
    local database="user_db"
    local domain_dir="${MIGRATIONS_DIR}/user"

    log_info "Starting database setup for domain: [USER] (mode: ${mode})"
    check_container "${container}"

    # Ensure database exists
    docker exec -i "${container}" psql -U "${user}" -d postgres -tc "SELECT 1 FROM pg_database WHERE datname='${database}'" | grep -q 1 || \
        docker exec -i "${container}" psql -U "${user}" -d postgres -c "CREATE DATABASE ${database};"

    case "${mode}" in
        --schema)
            reset_schema "${container}" "${user}" "${database}"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V1__init_user_schema.sql"
            ;;
        --seed)
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V2__seed_admin_user.sql"
            ;;
        all|"")
            reset_schema "${container}" "${user}" "${database}"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V1__init_user_schema.sql"
            apply_sql_file "${container}" "${user}" "${database}" "${domain_dir}/V2__seed_admin_user.sql"
            ;;
        *)
            log_error "Unknown mode '${mode}'. Use --schema, --seed, or omit for all."
            return 1
            ;;
    esac

    log_success "User domain completed successfully!"
}

# Display help/usage
usage() {
    echo "================================================================="
    echo "  eSupermarket - Local Database Initialization Script"
    echo "================================================================="
    echo "Usage:"
    echo "  $0 <domain> [mode]"
    echo ""
    echo "Available Domains:"
    echo "  catalog       Manage catalog_db"
    echo "  user          Manage user_db"
    echo "  inventory     Manage inventory_db (future)"
    echo "  all           Manage all available domains"
    echo ""
    echo "Modes (optional):"
    echo "  --schema      Reset schema and apply only schema migrations (V1)"
    echo "  --seed        Apply only seed data (V2, V3) without resetting"
    echo "  (none)        Reset schema and apply all migrations (V1, V2, V3)"
    echo ""
    echo "Examples:"
    echo "  $0 catalog"
    echo "  $0 user"
    echo "  $0 all"
    echo "================================================================="
}

# Main entry point
main() {
    local domain="${1:-}"
    local mode="${2:-all}"

    if [ -z "${domain}" ] || [ "${domain}" = "-h" ] || [ "${domain}" = "--help" ]; then
        usage
        exit 0
    fi

    case "${domain}" in
        catalog)
            seed_catalog "${mode}"
            ;;
        user)
            seed_user "${mode}"
            ;;
        inventory)
            seed_inventory "${mode}"
            ;;
        all)
            seed_catalog "${mode}"
            seed_user "${mode}"
            seed_inventory "${mode}"
            ;;
        *)
            log_error "Unknown domain: '${domain}'"
            usage
            exit 1
            ;;
    esac
}

main "$@"
