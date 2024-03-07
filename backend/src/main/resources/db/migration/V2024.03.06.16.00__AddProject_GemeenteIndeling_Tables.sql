CREATE TABLE diwi_testset.project_gemeente_indeling_changelog (
    "id" UUID NOT NULL,
    "project_id" UUID NOT NULL,
    "start_milestone_id" UUID NOT NULL,
    "end_milestone_id" UUID NOT NULL,
    "create_user_id" UUID NOT NULL,
    "change_user_id" UUID,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog
    ADD CONSTRAINT project_gemeente_indeling_changelog_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog__end_milestone FOREIGN KEY ("end_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog__start_milestone FOREIGN KEY ("start_milestone_id") REFERENCES diwi_testset.milestone("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog__project FOREIGN KEY ("project_id") REFERENCES diwi_testset.project("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi_testset.project_gemeente_indeling_changelog_buurt (
    "id" UUID NOT NULL,
    "project_gemeente_indeling_changelog_id" UUID NOT NULL,
    "buurt_id" UUID NOT NULL
);

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_buurt
    ADD CONSTRAINT project_gemeente_indeling_changelog_buurt_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_buurt
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog_buurt__changelog FOREIGN KEY ("project_gemeente_indeling_changelog_id") REFERENCES diwi_testset.project_gemeente_indeling_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_buurt
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog_buurt__buurt FOREIGN KEY ("buurt_id") REFERENCES diwi_testset.buurt("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi_testset.project_gemeente_indeling_changelog_gemeente (
    "id" UUID NOT NULL,
    "project_gemeente_indeling_changelog_id" UUID NOT NULL,
    "gemeente_id" UUID NOT NULL
);

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_gemeente
    ADD CONSTRAINT project_gemeente_indeling_changelog_gemeente_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_gemeente
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog_gemeente__changelog FOREIGN KEY ("project_gemeente_indeling_changelog_id") REFERENCES diwi_testset.project_gemeente_indeling_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_gemeente
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog_gemeente__gemeente FOREIGN KEY ("gemeente_id") REFERENCES diwi_testset.gemeente("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi_testset.project_gemeente_indeling_changelog_wijk (
    "id" UUID NOT NULL,
    "project_gemeente_indeling_changelog_id" UUID NOT NULL,
    "wijk_id" UUID NOT NULL
);

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_wijk
    ADD CONSTRAINT project_gemeente_indeling_changelog_wijk_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_wijk
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog_wijk__changelog FOREIGN KEY ("project_gemeente_indeling_changelog_id") REFERENCES diwi_testset.project_gemeente_indeling_changelog("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.project_gemeente_indeling_changelog_wijk
    ADD CONSTRAINT fk_project_gemeente_indeling_changelog_wijk__wijk FOREIGN KEY ("wijk_id") REFERENCES diwi_testset.wijk("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


INSERT INTO diwi_testset.project_gemeente_indeling_changelog (id, project_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, change_user_id)
    SELECT wc.id, w.project_id, wc.start_milestone_id, wc.end_milestone_id, wc.create_user_id, wc.change_start_date, wc.change_end_date, wc.change_user_id
        FROM diwi_testset.woningblok_gemeente_indeling_changelog wc
        LEFT JOIN diwi_testset.woningblok w ON wc.woningblok_id = w.id;

INSERT INTO diwi_testset.project_gemeente_indeling_changelog_buurt (id, project_gemeente_indeling_changelog_id, buurt_id)
    SELECT id, woningblok_gemeente_indeling_changelog_id, buurt_id
        FROM diwi_testset.woningblok_gemeente_indeling_changelog_buurt;

INSERT INTO diwi_testset.project_gemeente_indeling_changelog_gemeente (id, project_gemeente_indeling_changelog_id, gemeente_id)
    SELECT id, woningblok_gemeente_indeling_changelog_id, gemeente_id
        FROM diwi_testset.woningblok_gemeente_indeling_changelog_gemeente;

INSERT INTO diwi_testset.project_gemeente_indeling_changelog_wijk (id, project_gemeente_indeling_changelog_id, wijk_id)
    SELECT id, woningblok_gemeente_indeling_changelog_id, wijk_id
        FROM diwi_testset.woningblok_gemeente_indeling_changelog_wijk;

DROP TABLE diwi_testset.woningblok_gemeente_indeling_changelog_wijk;
DROP TABLE diwi_testset.woningblok_gemeente_indeling_changelog_gemeente;
DROP TABLE diwi_testset.woningblok_gemeente_indeling_changelog_buurt;
DROP TABLE diwi_testset.woningblok_gemeente_indeling_changelog;
