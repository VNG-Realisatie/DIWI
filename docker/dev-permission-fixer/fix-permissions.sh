#!/bin/sh

set -eux

if [ -n "$VOLUME_USER_ID" ];
then
    chown -R "$VOLUME_USER_ID" /data
fi

