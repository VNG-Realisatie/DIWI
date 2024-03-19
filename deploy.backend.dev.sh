#!/usr/bin/env bash

docker compose -f docker-compose.backend.dev.yml build "$@"
docker compose -f docker-compose.backend.dev.yml watch "$@" &
docker compose -f docker-compose.backend.dev.yml up "$@"
