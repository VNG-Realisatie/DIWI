INSERT INTO diwi_testset."user" (id) VALUES ('2122426c-6e70-419c-a054-f51dd24d798b');

INSERT INTO diwi_testset.userGroup (id) VALUES ('018d1d85-639f-701c-b73f-0f35f98409bc');

INSERT INTO diwi_testset.project (id) VALUES ('466da5e2-c96f-4856-aa17-6b37a1c21edc');
INSERT INTO diwi_testset.project (id) VALUES ('54a0c192-3454-4f7a-becd-96d214461987');
INSERT INTO diwi_testset.project (id) VALUES ('5c9fbb50-d8fd-480d-9e38-7f3b391d3110');

INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('292414eb-4a4e-405c-954b-e01bb355bc9b', '466da5e2-c96f-4856-aa17-6b37a1c21edc');
INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc');
INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '54a0c192-3454-4f7a-becd-96d214461987');
INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('342887ab-2106-45c4-9565-a4b83f4d3362', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110');
INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110');

INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('fad2d7e7-4d17-4216-b063-f028b49d81a2', '292414eb-4a4e-405c-954b-e01bb355bc9b', NOW() + interval '10 day', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-01 20:06:32+02', '2024-01-15 20:06:32+02', 'GEPLAND', 'M1');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('4277edda-68eb-498e-bf98-6f4010ea18ad', '292414eb-4a4e-405c-954b-e01bb355bc9b', NOW() - interval '10 days', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-01 20:06:32+02', null, 'GEPLAND', 'M1-v2');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('c7a00daa-0c52-4bb0-9915-78855da9997f', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', NOW() - interval '5 days', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', NULL, 'GEPLAND', 'M2');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('fc8e4916-3442-4d7c-8f5f-34b477108040', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', NOW() + interval '10 days', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'M3');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('50164197-792e-41b8-928d-aaaa54ae2cf4', '342887ab-2106-45c4-9565-a4b83f4d3362', NOW() + interval '15 days', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'M4');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('dc7fa3d7-1be8-4917-8fe9-c99136a14420', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', NOW() + interval '20 days', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'M5');

INSERT INTO diwi_testset.project_state (id, project_id, create_user_id, change_start_date, change_end_date, confidentiality_level, project_colour)
    VALUES ('d91019f7-f859-4469-b0be-f2515dbee563', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '10 days', NULL, 'PUBLIC', '#334455');
INSERT INTO diwi_testset.project_state (id, project_id, create_user_id, change_start_date, change_end_date, confidentiality_level, project_colour)
    VALUES ('e8a7bf18-3bf0-4917-afe2-49523f262cf5', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '5 days', NOW() - interval '1 day', 'EXTERNAL_REGIONAL', '#001122');
INSERT INTO diwi_testset.project_state (id, project_id, create_user_id, change_start_date, change_end_date, confidentiality_level, project_colour)
    VALUES ('c62e372c-e563-4a72-8f03-1132082577b9', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, 'PUBLIC', '#223344');
INSERT INTO diwi_testset.project_state (id, project_id, create_user_id, change_start_date, change_end_date, confidentiality_level, project_colour)
    VALUES ('e74d8d9b-bff6-4008-83ef-8b95e822bf83', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, 'EXTERNAL_REGIONAL', '#456456');

INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('56e7e05f-8b42-4dcb-bc5a-98d99a5411fc', '292414eb-4a4e-405c-954b-e01bb355bc9b', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '5 days', NOW() - interval '1 day');
INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('4e5b7e0b-84c4-4c07-b703-0bbe304b34bf', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);
INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('465f0b66-308a-4267-8275-235d10341c93', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '342887ab-2106-45c4-9565-a4b83f4d3362', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);
INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('f3cc3638-5fca-4cf0-9b0a-0f6d7cd12a7d', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);

INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, name)
    VALUES ('58b7586a-8364-4c68-a973-2460a7965a04', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, 'Current project Phase 1');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, name)
    VALUES ('3df20eda-5566-4a87-8d9f-4f490ad2be68', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, 'Current project Phase 2');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, name)
    VALUES ('c0153d9e-34bf-4da4-81b6-97d9603b875d', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, 'Future project Phase 1');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, name)
    VALUES ('2a0f5b93-b57b-42c3-aec4-415fc6277e2d', '342887ab-2106-45c4-9565-a4b83f4d3362', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, 'Future project Phase 1');
INSERT INTO diwi_testset.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, project_fase)
    VALUES ('4ff23141-ae6e-46b7-8583-31d5bef66f26', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '342887ab-2106-45c4-9565-a4b83f4d3362', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, '_1_CONCEPT');
INSERT INTO diwi_testset.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, project_fase)
    VALUES ('38545e50-ecad-4f34-b784-0b050c25d02e', '342887ab-2106-45c4-9565-a4b83f4d3362', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL, '_1_CONCEPT');

INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('39033af1-82cb-435a-8110-a0bf08dba9a6', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '342887ab-2106-45c4-9565-a4b83f4d3362', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);
INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('2e2f280d-c12a-47cb-8776-fb9aba3f5902', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0',  'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);
INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('bdac729b-dc0b-49d1-af50-cbb2efcc661e', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);
INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date)
    VALUES ('a023428c-fa13-4a4f-8125-21a6855fb36b', '342887ab-2106-45c4-9565-a4b83f4d3362', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', NULL);


INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type)
    VALUES ('4064d78b-94d6-4c75-9e6a-9890fbef6837', '39033af1-82cb-435a-8110-a0bf08dba9a6', 'PAND_TRANSFORMATIE');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type)
    VALUES ('cc7afaa1-bb20-43c2-97a8-323255bb6914', '2e2f280d-c12a-47cb-8776-fb9aba3f5902', 'HERSTRUCTURERING');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type)
    VALUES ('798f46e7-86b0-4e2e-bdbf-689d6deaa3fc', 'bdac729b-dc0b-49d1-af50-cbb2efcc661e', 'UITBREIDING_OVERIG');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type)
    VALUES ('3762c0fa-058b-4403-a071-8b1a48c76cc9', 'a023428c-fa13-4a4f-8125-21a6855fb36b', 'PAND_TRANSFORMATIE');


INSERT INTO diwi_testset.woningblok (id, project_id) VALUES ('8c401741-8c23-4cfd-ab49-b3b710758be2', '54a0c192-3454-4f7a-becd-96d214461987');
INSERT INTO diwi_testset.woningblok (id, project_id) VALUES ('16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110');


DO $$
DECLARE
    prop_id uuid;
    prop_cat_id uuid;
BEGIN
    SELECT
        property_id INTO prop_id
    FROM
        diwi_testset.property_state
    WHERE
            property_name = 'municipality'
    LIMIT 1;

INSERT INTO diwi_testset.project_category_changelog (id, project_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, property_id)
    VALUES ('dbb45f99-d474-494f-bc40-797a0b07557d', '54a0c192-3454-4f7a-becd-96d214461987', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', prop_id);
INSERT INTO diwi_testset.project_category_changelog (id, project_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, property_id)
    VALUES ('d26445d8-a207-4c25-877d-57be6ffd86e4', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '2122426c-6e70-419c-a054-f51dd24d798b', NOW() - interval '1 day', prop_id);

INSERT INTO diwi_testset.property_category_value (id, property_id)
    VALUES (gen_random_uuid(), prop_id)
        RETURNING id INTO prop_cat_id;
INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
    VALUES (gen_random_uuid(), prop_cat_id, 'Gemeente 1', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi_testset.project_category_changelog_value (id, project_category_changelog_id, property_value_id)
    VALUES ('e3f58b45-26cd-4872-91dc-fc46780780cc', 'dbb45f99-d474-494f-bc40-797a0b07557d', prop_cat_id);
INSERT INTO diwi_testset.project_category_changelog_value (id, project_category_changelog_id, property_value_id)
    VALUES ('b62d327d-a97c-4403-a7ca-4649c8827870', 'd26445d8-a207-4c25-877d-57be6ffd86e4', prop_cat_id);

INSERT INTO diwi_testset.property_category_value (id, property_id)
    VALUES (gen_random_uuid(), prop_id)
        RETURNING id INTO prop_cat_id;
INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
    VALUES (gen_random_uuid(), prop_cat_id, 'Gemeente 2', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi_testset.project_category_changelog_value (id, project_category_changelog_id, property_value_id)
    VALUES ('858dfe33-9441-4f33-a0e1-2c0709c5d170', 'dbb45f99-d474-494f-bc40-797a0b07557d', prop_cat_id);


END
$$;
