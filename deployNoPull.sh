#!/usr/bin/env bash
set -eux

. ./version.sh

docker compose pull
docker compose up --build --remove-orphans -d
