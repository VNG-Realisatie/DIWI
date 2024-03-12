#!/usr/bin/env bash
set -eu

# createUser user-name client-role-name
function createUser(){
    ./kcadm.sh create users -r diwi-test-realm \
        -s username="$1" \
        -s enabled=true \
        -s email="$1@example.com" \
        -s emailVerified=true \
        -s credentials='[{"type":"password","value":"'"$1"'","temporary":false}]' \
        || true
}

createUser admin admin
