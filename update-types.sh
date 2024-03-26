#!/usr/bin/bash
set -eux

root_dir=$(git rev-parse --show-toplevel)

cd "$root_dir"/backend
mvn io.swagger.core.v3:swagger-maven-plugin-jakarta:resolve

cd "$root_dir"/frontend
yarn && yarn run create-types
