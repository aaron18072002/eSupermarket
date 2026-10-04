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

# Generic migration executor for any domain
execute_domain_migrations() {
    local domain_name="$1"
    local container="$2"
    local database="$3"
    local mode="${4:-all}"
    local user="application"
    local domain_dir="${MIGRATIONS_DIR}/${domain_name}"

    if [ ! -d "${domain_dir}" ]; then
        log_warn "Migrations directory not found at '${domain_dir}'. Skipping domain [${domain_name}]."
        return 0
    fi

    log_info "Starting database setup for domain: [${domain_name^^}] (mode: ${mode})"
    check_container "${container}"

    # Handle schema reset according to mode
    case "${mode}" in
        all|"")
            reset_schema "${container}" "${user}" "${database}"
            ;;
        --update|--migrate)
            log_info "Running in UPDATE/MIGRATE mode (preserving existing data and applying migrations)..."
            ;;
        --schema)
            reset_schema "${container}" "${user}" "${database}"
            ;;
        --seed)
            log_info "Running in SEED mode (preserving existing schema)..."
            ;;
        *)
            log_error "Unknown mode '${mode}'. Use --update, --schema, --seed, or omit for all."
            return 1
            ;;
    esac

    # Discover and sort all migration files naturally (V1, V2, ... V10)
    local sql_files=()
    while IFS= read -r file; do
        [ -n "${file}" ] && sql_files+=("${file}")
    done < <(ls -1v "${domain_dir}"/V*__*.sql 2>/dev/null || true)

    if [ ${#sql_files[@]} -eq 0 ]; then
        log_warn "No migration files found in '${domain_dir}'."
        return 0
    fi

    local applied_count=0
    for sql_file in "${sql_files[@]}"; do
        local file_name
        file_name="$(basename "${sql_file}")"

        if [ "${mode}" = "--schema" ]; then
            # Schema only: execute files containing schema, init, create, or alter
            if [[ "${file_name}" =~ (schema|init|create|alter|table) ]]; then
                apply_sql_file "${container}" "${user}" "${database}" "${sql_file}"
                applied_count=$((applied_count + 1))
            fi
        elif [ "${mode}" = "--seed" ]; then
            # Seed only: execute files containing seed, data, or sample
            if [[ "${file_name}" =~ (seed|data|sample) ]]; then
                apply_sql_file "${container}" "${user}" "${database}" "${sql_file}"
                applied_count=$((applied_count + 1))
            fi
        else
            # 'all' or '--update': apply all migrations in order (old + new updated tables)
            apply_sql_file "${container}" "${user}" "${database}" "${sql_file}"
            applied_count=$((applied_count + 1))
        fi
    done

    log_success "Domain [${domain_name^^}] completed successfully (${applied_count} migration files applied)!"
}

# Seed Catalog Domain
seed_catalog() {
    execute_domain_migrations "catalog" "esupermarket-postgres-catalog-1" "catalog_db" "${1:-all}"
}

# Seed User Domain
seed_user() {
    execute_domain_migrations "user" "esupermarket-postgres-user-1" "user_db" "${1:-all}"
}

# Seed Inventory Domain (Placeholder for future)
seed_inventory() {
    execute_domain_migrations "inventory" "esupermarket-postgres-inventory-1" "inventory_db" "${1:-all}"
}

# Seed Order Domain (Placeholder for future)
seed_order() {
    execute_domain_migrations "order" "esupermarket-postgres-order-1" "order_db" "${1:-all}"
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
    echo "  order         Manage order_db (future)"
    echo "  all           Manage all available domains"
    echo ""
    echo "Modes (optional):"
    echo "  (none)        Reset schema and apply ALL migrations in order (old + new tables)"
    echo "  --update      Apply all migrations WITHOUT wiping existing data (safe schema update)"
    echo "  --schema      Reset schema and apply only schema DDL migrations"
    echo "  --seed        Apply only seed data without resetting schema"
    echo ""
    echo "Examples:"
    echo "  $0 user                  # Fresh rebuild: creates all old and new updated tables"
    echo "  $0 user --update         # Safe update: applies new tables without deleting existing data"
    echo "  $0 catalog               # Fresh rebuild for catalog_db"
    echo "  $0 all                   # Fresh rebuild for all domains"
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
        order)
            seed_order "${mode}"
            ;;
        all)
            seed_catalog "${mode}"
            seed_user "${mode}"
            seed_inventory "${mode}"
            seed_order "${mode}"
            ;;
        *)
            log_error "Unknown domain: '${domain}'"
            usage
            exit 1
            ;;
    esac
}

main "$@"
