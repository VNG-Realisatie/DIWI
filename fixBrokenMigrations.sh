#!/usr/bin/env bash


. ./dotenv.sh

set -eux

docker compose exec -T database psql -U "$DIWI_DB_USERNAME" -d "$DIWI_DB_NAME" < database-fixes/FixV2024.09.10.14.00__FixPriceCategories.sql
docker compose exec -T database psql -U "$DIWI_DB_USERNAME" -d "$DIWI_DB_NAME" < database-fixes/FixSystemUserMigrations.sql
