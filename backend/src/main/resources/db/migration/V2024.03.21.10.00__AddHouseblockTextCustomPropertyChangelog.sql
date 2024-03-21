CREATE TABLE diwi_testset.woningblok_maatwerk_text_changelog (
    "id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "woningblok_id" UUID NOT NULL,
    value TEXT NOT NULL,
    "create_user_id" UUID NOT NULL,
    "change_user_id" UUID,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    "eigenschap_id" UUID NOT NULL
);

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT woningblok_maatwerk_text_changelog_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD COLUMN IF NOT EXISTS "eigenschap_id" UUID NOT NULL;

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD COLUMN IF NOT EXISTS "eigenschap_id" UUID NOT NULL;

ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__eigenschap FOREIGN KEY ("eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
