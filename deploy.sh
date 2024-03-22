#!/usr/bin/env bash
set -eux

git pull

. ./version.sh

docker compose pull
docker compose up --build --remove-orphans -d
