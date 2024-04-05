CREATE TABLE diwi_testset.woningblok_state (
    id UUID NOT NULL,
    woningblok_id UUID NOT NULL,
    create_user_id UUID NOT NULL,
    change_user_id UUID,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT woningblok_state_pkey PRIMARY KEY ("id");

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__woningblok FOREIGN KEY ("woningblok_id") REFERENCES diwi_testset.woningblok("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


INSERT INTO diwi_testset.woningblok_state (id, woningblok_id, create_user_id, change_start_date)
    SELECT gen_random_uuid(), q.woningblok_id, q.create_user_id, q.change_start_date
    FROM (
         SELECT woningblok_id, create_user_id, change_start_date, row_number() OVER (PARTITION BY woningblok_id ORDER BY change_start_date ASC) rn
         FROM diwi_testset.woningblok_duration_changelog
         ) AS q WHERE rn = 1;
