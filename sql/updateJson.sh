#!/usr/bin/env bash

set -eux

rand=$(date --iso-8601=seconds | tr -d -c 0-9)
createdb vng-"$rand"

psql -d vng-"$rand" < diwi_testset_simplified.sql

psql_execute (){
    psql -d vng-"$rand" --tuples-only -c "$@"
}

export_enum (){
    psql_execute "select to_json( enum_range(null::diwi_testset_simplified.$1))" | jq  > enums/$1.json
}

export_enums(){
    test -d enums && rm -r enums
    mkdir enums

    enums=($(psql_execute "select distinct t.typname from pg_enum as e inner join pg_type as t ON e.enumtypid = t.oid;"))

    for enum in "${enums[@]}"
    do
        export_enum "$enum"
    done

    cp -r enums ../frontend/src/api/
}

export_enums


dropdb vng-"$rand"

