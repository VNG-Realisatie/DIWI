#!/usr/bin/env bash
set +eux

service="$1"
shift 1

echo "$@"
docker compose -f docker-compose."$service".dev.yml "$@"
