#!/usr/bin/env bash
# Wrapper around the keycloak admin tool in the docker container.
set -e

if [ -f .env ]
then
  . .env
fi

if [ -z $KEYCLOAK_PORT ]
then
    KEYCLOAK_PORT=1780
fi

docker compose -f docker-compose.backend.dev.yml \
    exec keycloak /opt/keycloak/bin/kcadm.sh "$@" \
    --no-config \
    --server "http://localhost:$KEYCLOAK_PORT" \
    --realm master \
    --user admin \
    --password admin
