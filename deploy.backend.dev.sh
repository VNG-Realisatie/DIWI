#!/usr/bin/env bash

docker compose -f docker-compose.backend.dev.yml up "$@"
