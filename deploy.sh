#!/usr/bin/env bash

touch htpasswd # Create the file if it isn't there yet, otherwise docker will create a directory

docker compose pull
[ $? -eq 0 ] && docker compose up --build --remove-orphans -d
