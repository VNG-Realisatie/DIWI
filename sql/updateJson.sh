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

export_things(){
    # regio
    # gemeente
    psql_execute_file gemeente.sql | jq . > json/gemeente.json
    # buurt
    psql_execute_file buurt.sql | jq . > json/buurt.json
    # wijk
    psql_execute_file wijk.sql | jq . > json/wijk.json

    # rol gemeente - see project_gemeenterol_value_state
    psql_execute_file gemeente_rol.sql | jq . > json/gemeente_rol.json
    # actor list (for project leider drop down)
    # organization list (for owner drop down)

}

test -d json && rm -r json
mkdir json

export_enums
export_projects
export_things

cp -r json ../frontend/src/api

dropdb vng-"$rand"

