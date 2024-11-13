ALTER TABLE diwi_testset."user"
    ADD COLUMN IF NOT EXISTS "system_user" boolean NOT NULL DEFAULT FALSE;

INSERT INTO diwi_testset."user" (id, "system_user") VALUES (gen_random_uuid(), true);
