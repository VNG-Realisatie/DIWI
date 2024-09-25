#!/usr/bin/env bash

./fixBrokenMigrations.sh

docker compose -f docker-compose.backend.dev.yml build "$@"
docker compose -f docker-compose.backend.dev.yml watch "$@" &
sleep 10
docker compose -f docker-compose.backend.dev.yml up "$@"
