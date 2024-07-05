#!/usr/bin/env bash

declare -r realmName="diwi-test-realm"
declare -r roleName="diwi-admin"

declare -r userName="admin"
declare -r userPass="admin"

# createUser user-name client-role-name
function createUser(){
    ./kcadm.sh create users -r $realmName \
        -s username="$1" \
        -s enabled=true \
        -s email="$1@example.com" \
        -s firstName="$3" \
        -s lastName="$4" \
        -s emailVerified=true \
        -s credentials='[{"type":"password","value":"'"$2"'","temporary":false}]' \
        || true
}

# make sure that the 'diwi-admin' role exists in the test realm
./kcadm.sh create roles -r $realmName \
    -s name=$roleName \
    -s 'description=Defines permission to become a diwi Admin user'

# add admin user
createUser $userName $userPass Ad Min

# assign 'diwi-admin' role to created user
./kcadm.sh add-roles --uusername $userName --rolename $roleName -r $realmName

./kcadm.sh add-roles --uusername service-account-diwi-test-client --rolename view-users -r $realmName --cclientid realm-management
./kcadm.sh add-roles --uusername service-account-diwi-test-client --rolename manage-users -r $realmName --cclientid realm-management
./kcadm.sh add-roles --uusername service-account-diwi-test-client --rolename query-users -r $realmName --cclientid realm-management

# Create additional users

createUser userplus userplus User Plus
