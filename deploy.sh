#!/usr/bin/env bash

git pull

docker compose pull
[ $? -eq 0 ] && docker compose up --build --remove-orphans -d
