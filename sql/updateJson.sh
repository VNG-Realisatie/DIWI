#!/usr/bin/env bash

set -eux

rand=$(date --iso-8601=seconds | tr -d -c 0-9)
createdb vng-"$rand"

psql -d vng-"$rand" < diwi_testset_simplified.sql

psql_execute_arg (){
    psql -d vng-"$rand" --tuples-only --no-align -c "$@"
}

psql_execute_file (){
    psql -d vng-"$rand" --tuples-only --no-align -f "$@"
}

export_enum (){
    psql_execute_arg "select to_json(enum_range(null::diwi_testset_simplified.$1))" | jq  > json/enums/"$1".json
}

export_enums(){
    test -d json/enums && rm -r json/enums
    mkdir json/enums

    enums=($(psql_execute_arg "select distinct t.typname from pg_enum as e inner join pg_type as t ON e.enumtypid = t.oid;"))

    for enum in "${enums[@]}"
    do
        export_enum "$enum"
    done
}

export_projects(){
    psql_execute_file projects.sql | jq . > json/projects.json
}

test -d json && rm -r json
mkdir json

export_enums
export_projects

cp -r json ../frontend/src/api

dropdb vng-"$rand"

