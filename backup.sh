#!/usr/bin/env bash

# Script to make backups to make rollback easier after a failed deployment

. ./dotenv.sh

set -eux

timestamp=$(date --iso-8601=seconds)

# Check if databse container is running
if  docker compose ps | grep -q database; then
    docker compose exec -T database pg_dump -U "$DIWI_DB_USERNAME" -d "$DIWI_DB_NAME" --format=c > "backup/predeploy-$timestamp.dump"
else
    echo "Database container is not running, backup skipped"
fi

# Backup git hash so we can rollback to the specific version
git rev-parse HEAD > "backup/predeploy-$timestamp.githash"

# prune old backups, but ignore .gitkeep
find backup -type f ! -name '.gitkeep' -mtime +7 -delete
