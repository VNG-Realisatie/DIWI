#!/usr/bin/env bash
set -eux

./backup.sh

git pull

. ./version.sh

./fixBrokenMigrations.sh

docker compose pull
docker compose up --build --remove-orphans -d
