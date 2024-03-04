CREATE COLLATION IF NOT EXISTS diwi_numeric (provider = icu, locale = 'en-u-kn-true');

ALTER TABLE diwi_testset.user_state
    ADD COLUMN IF NOT EXISTS last_name TEXT NOT NULL;
ALTER TABLE diwi_testset.user_state
    ADD COLUMN IF NOT EXISTS first_name TEXT NOT NULL;
