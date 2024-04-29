#!/usr/bin/env bash
set -eux

git switch -c merge-back-to-develop

git push --set-upstream origin merge-back-to-develop \
        -o merge_request.create \
        -o merge_request.target=develop \
        -o merge_request.remove_source_branch \
        -o merge_request.title="Merge back to develop" \
        -o merge_request.merge_when_pipeline_succeeds
