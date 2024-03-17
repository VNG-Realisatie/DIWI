CREATE TABLE diwi_testset.project_registry_link_changelog (
    "id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "create_user_id" UUID NOT NULL,
    "change_user_id" UUID,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog
    ADD CONSTRAINT project_registry_link_changelog_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi_testset.project_registry_link_changelog_value (
    "id" UUID NOT NULL,
    "project_registry_link_changelog_id" UUID NOT NULL,
    brk_gemeente_code text NOT NULL,
    brk_sectie text,
    brk_perceelnummer integer,
    brk_selectie text
);

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog_value
    ADD CONSTRAINT project_registry_link_changelog_value_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.project_registry_link_changelog_value
    ADD CONSTRAINT fk_project_registry_link_changelog_value__changelog FOREIGN KEY ("project_registry_link_changelog_id") REFERENCES diwi_testset.project_registry_link_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


DROP TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog_value;
DROP TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog;
