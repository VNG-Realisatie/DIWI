#!/usr/bin/env bash

docker compose pull
[ $? -eq 0 ] && docker compose up --build --remove-orphans -d
