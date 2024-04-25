#!/usr/bin/env bash
set -eux

docker compose stop
docker compose start database

sleep 5

./backup.sh

docker compose stop

sudo rm -r data/*

docker compose start
