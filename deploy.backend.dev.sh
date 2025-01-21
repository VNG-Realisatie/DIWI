#!/usr/bin/env bash

set -eux
export UID

docker compose -f docker-compose.backend.dev.yml build "$@"
docker compose -f docker-compose.backend.dev.yml up --watch "$@"
