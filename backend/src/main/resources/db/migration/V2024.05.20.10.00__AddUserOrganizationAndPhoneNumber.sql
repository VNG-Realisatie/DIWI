ALTER TABLE diwi_testset.user_state
    ADD COLUMN IF NOT EXISTS organization TEXT;
ALTER TABLE diwi_testset.user_state
    ADD COLUMN IF NOT EXISTS phone_number TEXT;
ALTER TABLE diwi_testset.user_state
    ADD COLUMN IF NOT EXISTS contact_person TEXT;
ALTER TABLE diwi_testset.user_state
    ADD COLUMN IF NOT EXISTS department TEXT;
ALTER TABLE diwi_testset.user_state
    ADD COLUMN IF NOT EXISTS prefixes TEXT;
