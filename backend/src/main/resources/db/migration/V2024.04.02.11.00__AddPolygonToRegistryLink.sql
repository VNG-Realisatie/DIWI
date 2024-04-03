ALTER TABLE ONLY diwi_testset.project_registry_link_changelog_value
    ADD COLUMN IF NOT EXISTS subselection_geometry json;

ALTER TABLE diwi_testset.project_registry_link_changelog_value
    RENAME COLUMN geojson TO plot_feature;

ALTER TABLE diwi_testset.project_registry_link_changelog_value
    DROP COLUMN brk_selectie;
