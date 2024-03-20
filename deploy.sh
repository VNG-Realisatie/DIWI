#!/usr/bin/env bash
set -eux

git pull

docker compose pull

. ./version.sh
docker compose up --build --remove-orphans -d
