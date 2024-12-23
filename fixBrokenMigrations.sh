#!/usr/bin/env bash


. ./dotenv.sh

set -eux

# This only works if the database is already running. Hence the || true at the end
docker compose exec -T database psql -U "$DIWI_DB_USERNAME" -d "$DIWI_DB_NAME" < database-fixes/FixV2024.09.10.14.00__FixPriceCategories.sql || true
docker compose exec -T database psql -U "$DIWI_DB_USERNAME" -d "$DIWI_DB_NAME" < database-fixes/FixSystemUserMigrations.sql || true
