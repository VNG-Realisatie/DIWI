ALTER TABLE diwi_testset.project_state
    ADD COLUMN IF NOT EXISTS latitude FLOAT8;
ALTER TABLE diwi_testset.project_state
    ADD COLUMN IF NOT EXISTS longitude FLOAT8;
