#!/usr/bin/env bash

docker compose -f docker-compose.keycloak.dev.yml watch "$@" &
docker compose -f docker-compose.keycloak.dev.yml up "$@"
