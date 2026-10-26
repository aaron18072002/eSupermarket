<#
.SYNOPSIS
    eSupermarket - Local Database Initialization & Seeding Script (PowerShell for Windows)

.DESCRIPTION
    Applies Flyway migrations and seed data to local PostgreSQL Docker containers
    for the Điện Máy Xanh microservice domains (catalog_db, user_db, order_db, inventory_db).

.PARAMETER Domain
    The domain database to manage: 'catalog', 'user', 'order', 'inventory', or 'all'.

.PARAMETER Mode
    Migration mode:
      'all'     - Reset schema and run all migrations in order (default)
      '--update' - Apply migrations without dropping schema/data
      '--schema' - Reset schema and apply only schema DDL migrations
      '--seed'   - Apply only seed data without resetting schema

.EXAMPLE
    .\local-init-data.ps1 catalog
    .\local-init-data.ps1 catalog --seed
    .\local-init-data.ps1 user --update
    .\local-init-data.ps1 all
#>

[CmdletBinding()]
param(
    [Parameter(Position = 0, Mandatory = $false)]
    [ValidateSet("catalog", "user", "inventory", "order", "all", "help")]
    [string]$Domain = "catalog",

    [Parameter(Position = 1, Mandatory = $false)]
    [string]$Mode = "all"
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$MigrationsDir = Join-Path $ScriptDir "migrations"

# Enforce UTF-8 console and pipeline encoding across Windows PowerShell
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)

function Write-Info([string]$Message) {
    Write-Host "[INFO] $Message" -ForegroundColor Cyan
}

function Write-Success([string]$Message) {
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Warn([string]$Message) {
    Write-Host "[WARN] $Message" -ForegroundColor Yellow
}

function Write-ErrorMsg([string]$Message) {
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Test-DockerContainer([string]$ContainerName) {
    $running = docker ps --filter "name=$ContainerName" --filter "status=running" --format "{{.Names}}"
    if (-not $running -or $running.Trim() -ne $ContainerName) {
        Write-ErrorMsg "Container '$ContainerName' is not running!"
        Write-Info "Please start the database container first: docker compose up -d"
        return $false
    }
    return $true
}

function Reset-DatabaseSchema([string]$Container, [string]$User, [string]$Database) {
    Write-Warn "Resetting database [$Database] (dropping & recreating public schema)..."
    $sqlCmd = "DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public; GRANT ALL ON SCHEMA public TO $User;"
    docker exec -i $Container psql -U $User -d $Database -c $sqlCmd | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Database [$Database] schema reset successfully."
    } else {
        throw "Failed to reset database [$Database] schema."
    }
}

function Apply-SqlFile([string]$Container, [string]$User, [string]$Database, [string]$FilePath) {
    $fileName = Split-Path -Leaf $FilePath
    if (-not (Test-Path $FilePath)) {
        throw "Migration file not found: $FilePath"
    }

    Write-Info "Applying [$fileName] to database [$Database]..."
    $containerTmpPath = "/tmp/$fileName"

    # 1. Copy raw UTF-8 SQL file directly into container (bypasses Windows pipeline transcoding)
    docker cp $FilePath "${Container}:${containerTmpPath}"
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to copy [$fileName] into container [$Container]"
    }

    # 2. Execute SQL file natively within PostgreSQL using UTF-8 client encoding
    docker exec -i -e PGCLIENTENCODING=UTF8 $Container psql -v ON_ERROR_STOP=1 -U $User -d $Database -f $containerTmpPath | Out-Null
    $execStatus = $LASTEXITCODE

    # 3. Clean up temporary file from container
    docker exec -i $Container rm -f $containerTmpPath | Out-Null

    if ($execStatus -eq 0) {
        Write-Success "Applied [$fileName] successfully."
    } else {
        throw "Failed to apply [$fileName] to [$Database]"
    }
}

function Invoke-DomainMigrations([string]$DomainName, [string]$Container, [string]$Database, [string]$MigrationMode) {
    $domainDir = Join-Path $MigrationsDir $DomainName
    if (-not (Test-Path $domainDir)) {
        Write-Warn "Migrations directory not found at '$domainDir'. Skipping domain [$DomainName]."
        return
    }

    Write-Info "Starting database setup for domain: [$($DomainName.ToUpper())] (mode: $MigrationMode)"
    if (-not (Test-DockerContainer $Container)) {
        return
    }

    $User = "application"

    switch ($MigrationMode.ToLower()) {
        "all" {
            Reset-DatabaseSchema $Container $User $Database
        }
        "--update" {
            Write-Info "Running in UPDATE mode (preserving existing data and applying migrations)..."
        }
        "--migrate" {
            Write-Info "Running in MIGRATE mode (preserving existing data and applying migrations)..."
        }
        "--schema" {
            Reset-DatabaseSchema $Container $User $Database
        }
        "--seed" {
            Write-Info "Running in SEED mode (preserving existing schema)..."
        }
        default {
            Write-ErrorMsg "Unknown mode '$MigrationMode'. Use --update, --schema, --seed, or omit for all."
            return
        }
    }

    $sqlFiles = Get-ChildItem -Path $domainDir -Filter "V*__*.sql" | Sort-Object Name
    if (-not $sqlFiles -or $sqlFiles.Count -eq 0) {
        Write-Warn "No migration files found in '$domainDir'."
        return
    }

    $appliedCount = 0
    foreach ($file in $sqlFiles) {
        $fileName = $file.Name

        if ($MigrationMode -eq "--schema") {
            if ($fileName -match "(schema|init|create|alter|table)") {
                Apply-SqlFile $Container $User $Database $file.FullName
                $appliedCount++
            }
        } elseif ($MigrationMode -eq "--seed") {
            if ($fileName -match "(seed|data|sample)") {
                Apply-SqlFile $Container $User $Database $file.FullName
                $appliedCount++
            }
        } else {
            Apply-SqlFile $Container $User $Database $file.FullName
            $appliedCount++
        }
    }

    Write-Success "Domain [$($DomainName.ToUpper())] completed successfully ($appliedCount migration files applied)!"
}

# Show help if requested
if ($Domain -eq "help" -or $Domain -eq "-h" -or $Domain -eq "--help") {
    Write-Host "=================================================================" -ForegroundColor Cyan
    Write-Host "  Điện Máy Xanh / eSupermarket - Local Database Initialization" -ForegroundColor Cyan
    Write-Host "================================================================="
    Write-Host "Usage:"
    Write-Host "  .\local-init-data.ps1 <domain> [mode]"
    Write-Host ""
    Write-Host "Domains: catalog, user, inventory, order, all"
    Write-Host "Modes:   all (default), --update, --schema, --seed"
    exit 0
}

# Domain routing
switch ($Domain) {
    "catalog" {
        Invoke-DomainMigrations "catalog" "esupermarket-postgres-catalog-1" "catalog_db" $Mode
    }
    "user" {
        Invoke-DomainMigrations "user" "esupermarket-postgres-user-1" "user_db" $Mode
    }
    "inventory" {
        Invoke-DomainMigrations "inventory" "esupermarket-postgres-inventory-1" "inventory_db" $Mode
    }
    "order" {
        Invoke-DomainMigrations "order" "esupermarket-postgres-order-1" "order_db" $Mode
    }
    "all" {
        Invoke-DomainMigrations "catalog" "esupermarket-postgres-catalog-1" "catalog_db" $Mode
        Invoke-DomainMigrations "user" "esupermarket-postgres-user-1" "user_db" $Mode
        Invoke-DomainMigrations "inventory" "esupermarket-postgres-inventory-1" "inventory_db" $Mode
        Invoke-DomainMigrations "order" "esupermarket-postgres-order-1" "order_db" $Mode
    }
}
