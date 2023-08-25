#!/usr/bin/env bash
set -eux

htpasswd -B htpasswd "$1"

docker compose exec frontend nginx -s reload
