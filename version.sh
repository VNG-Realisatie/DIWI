#!/bin/bash

REACT_APP_GIT_SHA=$(git rev-parse HEAD)
export REACT_APP_GIT_SHA

REACT_APP_DEPLOY_DATE=$(date -Iseconds)
export REACT_APP_DEPLOY_DATE
