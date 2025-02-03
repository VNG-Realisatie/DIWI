#!/usr/bin/env bash
set -eux

branchName=merge-to-develop/$(git rev-parse --abbrev-ref HEAD)/$(date -I)

git switch -c "$branchName"

git push --set-upstream origin "$branchName" \
        -o merge_request.create \
        -o merge_request.target=develop \
        -o merge_request.remove_source_branch \
        -o merge_request.title="Merge back to develop" \
        -o merge_request.merge_when_pipeline_succeeds
