#!/usr/bin/env bash
set -eux

. ./version.sh

./fixBrokenMigrations.sh

docker compose pull
docker compose up --build --remove-orphans -d
