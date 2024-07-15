#!/bin/bash
SCRIPT_DIR=$(dirname -- "${BASH_SOURCE[0]}") &> /dev/null && pwd

VITE_REACT_APP_VERSION_NUMBER=$(sed -n 's/.*"version": "\(.*\)",/\1/p' "$SCRIPT_DIR/frontend/package.json")
export VITE_REACT_APP_VERSION_NUMBER

VITE_REACT_APP_GIT_SHA=$(git rev-parse HEAD)
export VITE_REACT_APP_GIT_SHA

VITE_REACT_APP_DEPLOY_DATE=$(date -Iseconds)
export VITE_REACT_APP_DEPLOY_DATE

# Function to replace or append variable
replace_or_append() {
  local file=".env"
  local key="$1"
  local value="$2"
  if grep -q "^$key=" "$file"; then
    sed -i "s/^$key=.*/$key=$value/" "$file"
  else
    echo "$key=$value" >> "$file"
  fi
}
replace_or_append "VITE_REACT_APP_VERSION_NUMBER" "$VITE_REACT_APP_VERSION_NUMBER"
replace_or_append "VITE_REACT_APP_GIT_SHA" "$VITE_REACT_APP_GIT_SHA"
replace_or_append "VITE_REACT_APP_DEPLOY_DATE" "$VITE_REACT_APP_DEPLOY_DATE"
