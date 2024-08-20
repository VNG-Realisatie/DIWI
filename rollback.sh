#!/usr/bin/env bash

# Read .env file
while IFS="=" read -r key value; do
    if [[ -z "$key" ]]; then
        continue
    elif [[ "$key" =~ ^# ]]; then
        continue
    else
        printf -v "$key" %s "$value"
    fi
done < .env

set -eux

#set DIWI_DB_NAME and DIWI_DB_USERNAME if not set
DIWI_DB_NAME=${DIWI_DB_NAME:-diwi}
DIWI_DB_USERNAME=${DIWI_DB_USERNAME:-diwi}

read -p "Enter timestamp: " timestamp

#Check if files exist
test -f "backup/predeploy-$timestamp.githash" || { echo "Error: Git hash file 'backup/predeploy-$timestamp.githash' not found."; exit 1; }
test -f "backup/predeploy-$timestamp.dump" || { echo "Error: Database dump file 'backup/predeploy-$timestamp.dump' not found."; exit 1; }

# Create backup first
./backup.sh

# Checkout git hash from timestamp
git checkout "$(cat "backup/predeploy-$timestamp.githash")"

function restoreDB() {
    # Stop all Docker containers
    docker compose stop

    # Start only the database container
    docker compose up -d database

    # Restore database
    docker compose exec -T database pg_restore -U $DIWI_DB_USERNAME -d $DIWI_DB_NAME --clean "/backup/predeploy-$timestamp.dump"

    ./deployNoPull.sh
}

restoreDB $timestamp
