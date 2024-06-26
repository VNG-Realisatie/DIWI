#!/usr/bin/env bash

# Read .env file
while IFS="=" read -r key value; do
    if [[ -z "$key" ]]; then
        continue
    elif [[ "$key" =~ ^# ]]; then
        continue
    else
        printf -v "$key" %s "$value"
    fi
done < .env

set -eux

#set DIWI_DB_NAME and DIWI_DB_USERNAME if not set
DIWI_DB_NAME=${DIWI_DB_NAME:-diwi}
DIWI_DB_USERNAME=${DIWI_DB_USERNAME:-diwi}

read -p "Enter timestamp: " timestamp

# Create backup first
#./backup.sh

function restoreDB() {
    pg_restore -U $DIWI_DB_USERNAME -d $DIWI_DB_NAME "backup/predeploy-$timestamp.dump" -c
}

restoreDB $timestamp
