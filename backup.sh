#!/usr/bin/env bash

# Script to make backups to make rollback easier after a failed deployment

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

timestamp=$(date --iso-8601=seconds)

# Check if databse container is running
if  docker compose ps | grep -q database; then
    docker compose exec -T database pg_dump -U "$DIWI_DB_USERNAME" -d "$DIWI_DB_NAME" --format=c > "backup/predeploy-$timestamp.dump"
else
    echo "Database container is not running, backup skipped"
fi

# Backup git hash so we can rollback to the specific version
git rev-parse HEAD > "backup/predeploy-$timestamp.githash"

# prune old backups
find backup -type f -mtime +7 -delete
