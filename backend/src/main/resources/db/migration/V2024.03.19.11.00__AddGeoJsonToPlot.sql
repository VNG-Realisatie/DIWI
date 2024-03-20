ALTER TABLE ONLY diwi_testset.project_registry_link_changelog_value
    ADD COLUMN IF NOT EXISTS geojson json NOT NULL;
