INSERT INTO diwi_testset."user" (id) VALUES ('2122426c-6e70-419c-a054-f51dd24d798b');

INSERT INTO diwi_testset.organization (id) VALUES ('018d1d85-639f-701c-b73f-0f35f98409bc');

INSERT INTO diwi_testset.project (id) VALUES ('466da5e2-c96f-4856-aa17-6b37a1c21edc');

INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('292414eb-4a4e-405c-954b-e01bb355bc9b', '466da5e2-c96f-4856-aa17-6b37a1c21edc');
INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc');

INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('4277edda-68eb-498e-bf98-6f4010ea18ad', '292414eb-4a4e-405c-954b-e01bb355bc9b', NOW() - interval '10 days', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-01 20:06:32+02', null, 'GEPLAND', 'M1-v2');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('c7a00daa-0c52-4bb0-9915-78855da9997f', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', NOW() + interval '5 days', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', NULL, 'GEPLAND', 'M2');

INSERT INTO diwi_testset.project_state (id, project_id, create_user_id, change_start_date, change_end_date, confidentiality_level, project_colour)
    VALUES ('d91019f7-f859-4469-b0be-f2515dbee563', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '10 days', NULL, __confidentiality__, '#334455');

INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('4e5b7e0b-84c4-4c07-b703-0bbe304b34bf', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);

INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, name)
    VALUES ('58b7586a-8364-4c68-a973-2460a7965a04', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, 'Current project Phase 1');
