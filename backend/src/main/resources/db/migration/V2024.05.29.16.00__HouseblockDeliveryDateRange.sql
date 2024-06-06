ALTER TABLE diwi_testset.woningblok_opleverdatum_changelog
    RENAME TO woningblok_deliverydate_changelog;

ALTER TABLE diwi_testset.woningblok_deliverydate_changelog
    RENAME COLUMN verwachte_opleverdatum TO latest_deliverydate;
ALTER TABLE diwi_testset.woningblok_deliverydate_changelog
    ADD earliest_deliverydate DATE;
UPDATE diwi_testset.woningblok_deliverydate_changelog
    SET earliest_deliverydate = latest_deliverydate;
ALTER TABLE diwi_testset.woningblok_deliverydate_changelog
    ALTER COLUMN earliest_deliverydate SET NOT NULL;


