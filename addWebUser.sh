#!/usr/bin/env bash
set -eux

touch htpasswd # Create the file if it isn't there yet
htpasswd -B htpasswd "$1"

docker compose exec frontend nginx -s reload
