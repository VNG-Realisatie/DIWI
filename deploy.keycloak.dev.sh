#!/usr/bin/env bash

docker compose -f docker-compose.keycloak.dev.yml watch "$@"
