#!/bin/bash
SCRIPT_DIR=$(dirname -- "${BASH_SOURCE[0]}") &> /dev/null && pwd

VITE_REACT_APP_VERSION_NUMBER=$(sed -n 's/.*"version": "\(.*\)",/\1/p' "$SCRIPT_DIR/frontend/package.json")
export VITE_REACT_APP_VERSION_NUMBER

VITE_REACT_APP_GIT_SHA=$(git rev-parse HEAD)
export VITE_REACT_APP_GIT_SHA

VITE_REACT_APP_DEPLOY_DATE=$(date -Iseconds)
export VITE_REACT_APP_DEPLOY_DATE

version_env_file="$SCRIPT_DIR/version.env"

update_env_var() {
    local var_name=$1
    local var_value=$2
    local env_file="$version_env_file"

    if grep -q "^$var_name=" "$env_file"; then
        sed -i "s/^$var_name=.*/$var_name=$var_value/" "$env_file"
    else
        echo "$var_name=$var_value" >> "$env_file"
    fi
}

update_env_var "VITE_REACT_APP_VERSION_NUMBER" "$VITE_REACT_APP_VERSION_NUMBER"
update_env_var "VITE_REACT_APP_GIT_SHA" "$VITE_REACT_APP_GIT_SHA"
update_env_var "VITE_REACT_APP_DEPLOY_DATE" "$VITE_REACT_APP_DEPLOY_DATE"
