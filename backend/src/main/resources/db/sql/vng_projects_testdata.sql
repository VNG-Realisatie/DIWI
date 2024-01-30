--
-- Data for Name: organization; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.organization (id) VALUES ('a0fd243b-667b-428e-a3f1-bb0fcee141d9');
INSERT INTO diwi_testset.organization (id) VALUES ('018d187d-597f-7a5c-ad30-f380804274d3');
INSERT INTO diwi_testset.organization (id) VALUES ('018d1d85-639f-701c-b73f-0f35f98409bc');
INSERT INTO diwi_testset.organization (id) VALUES ('018d1d8f-4bcb-7b31-a392-25b5ef963c70');

--
-- Data for Name: user; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset."user" (id) VALUES ('2122426c-6e70-419c-a054-f51dd24d798b');

--
-- Data for Name: milestone; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.milestone (id) VALUES ('292414eb-4a4e-405c-954b-e01bb355bc9b');
INSERT INTO diwi_testset.milestone (id) VALUES ('07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0');
INSERT INTO diwi_testset.milestone (id) VALUES ('ee3cbe15-7a5d-42c6-a807-0a7593a24d4f');
INSERT INTO diwi_testset.milestone (id) VALUES ('342887ab-2106-45c4-9565-a4b83f4d3362');
INSERT INTO diwi_testset.milestone (id) VALUES ('7c6bfe67-6618-4a6a-9507-2cd15f88c11f');
INSERT INTO diwi_testset.milestone (id) VALUES ('1c155b85-29e3-4fec-85a2-96bda344fe8a');
INSERT INTO diwi_testset.milestone (id) VALUES ('529ca978-1d23-418a-a94c-bd219454a587');
INSERT INTO diwi_testset.milestone (id) VALUES ('4237d087-3d45-4acf-a1a6-0a67b754608e');


--
-- Data for Name: milestone_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('fad2d7e7-4d17-4216-b063-f028b49d81a2', '292414eb-4a4e-405c-954b-e01bb355bc9b', '2024-01-10', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-01 20:06:32+02', '2024-01-15 20:06:32+02', 'GEPLAND', 'Milestone ian 10');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('c7a00daa-0c52-4bb0-9915-78855da9997f', '292414eb-4a4e-405c-954b-e01bb355bc9b', '2024-01-10', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', NULL, 'GEPLAND', 'Milestone ian 10 - v2');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('f33114d9-946f-41ec-a5b2-bf152929fc88', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '2024-01-30', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-01 20:06:32+02', '2024-01-15 20:06:32+02', 'gepland', 'Milestone ian 30');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('4277edda-68eb-498e-bf98-6f4010ea18ad', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '2024-01-30', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', NULL, 'GEPLAND', 'Milestone ian 30 - v2');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('fc8e4916-3442-4d7c-8f5f-34b477108040', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '2024-05-01', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 1');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('50164197-792e-41b8-928d-aaaa54ae2cf4', '342887ab-2106-45c4-9565-a4b83f4d3362', '2024-05-30', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 30');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('dc7fa3d7-1be8-4917-8fe9-c99136a14420', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '2024-05-15', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 15');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('5d6e237e-29c2-4890-b2e4-9b92533c56f6', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '2024-05-05', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 5');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('5b607079-72d9-47cb-adba-523c61c98a7e', '529ca978-1d23-418a-a94c-bd219454a587', '2024-05-25', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 25');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('1d212147-4eea-4899-93f2-4fa08dcbc622', '4237d087-3d45-4acf-a1a6-0a67b754608e', '2024-05-18', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 18');


--
-- Data for Name: organization_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.organization_state (id, organization_id, parent_organization_id, naam, change_end_date, change_start_date, change_user_id) VALUES ('4872eac4-9911-47ef-b4d6-62e4e9223926', '018d1d85-639f-701c-b73f-0f35f98409bc', NULL, 'Org1', NULL, '2024-01-10 20:06:32+02', '2122426c-6e70-419c-a054-f51dd24d798b');


--
-- Data for Name: project; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project (id) VALUES ('466da5e2-c96f-4856-aa17-6b37a1c21edc');
INSERT INTO diwi_testset.project (id) VALUES ('54a0c192-3454-4f7a-becd-96d214461987');
INSERT INTO diwi_testset.project (id) VALUES ('5c9fbb50-d8fd-480d-9e38-7f3b391d3110');


--
-- Data for Name: project_duration_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('56e7e05f-8b42-4dcb-bc5a-98d99a5411fc', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', '2024-01-16 20:06:32+02');
INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('4e5b7e0b-84c4-4c07-b703-0bbe304b34bf', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('465f0b66-308a-4267-8275-235d10341c93', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('f3cc3638-5fca-4cf0-9b0a-0f6d7cd12a7d', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '529ca978-1d23-418a-a94c-bd219454a587', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL);


--
-- Data for Name: project_fase_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_fase) VALUES ('4ff23141-ae6e-46b7-8583-31d5bef66f26', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-10 20:06:32+02', NULL, '_1_INITIATIEFFASE');
INSERT INTO diwi_testset.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_fase) VALUES ('38545e50-ecad-4f34-b784-0b050c25d02e', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '4237d087-3d45-4acf-a1a6-0a67b754608e', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, '_1_INITIATIEFFASE');
INSERT INTO diwi_testset.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_fase) VALUES ('99518d7c-a909-41ca-8814-894b703ed65c', '4237d087-3d45-4acf-a1a6-0a67b754608e', '342887ab-2106-45c4-9565-a4b83f4d3362', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, '_3_VERGUNNINGSFASE');
INSERT INTO diwi_testset.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_fase) VALUES ('9af79780-4aae-479e-9351-1b59906c01d2', '4237d087-3d45-4acf-a1a6-0a67b754608e', '342887ab-2106-45c4-9565-a4b83f4d3362', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, '_3_VERGUNNINGSFASE');


--
-- Data for Name: project_gemeenterol_value; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_gemeenterol_value (id) VALUES ('47964244-4f7e-4ed5-93ba-b7667a3e8445');
INSERT INTO diwi_testset.project_gemeenterol_value (id) VALUES ('5cdd1b67-2174-4efd-8e82-a69cb322205f');


--
-- Data for Name: project_gemeenterol_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_gemeenterol_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_gemeenterol_value_id) VALUES ('dc8b2c8c-587a-4f99-a84c-f871d4bb7e9f', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '5cdd1b67-2174-4efd-8e82-a69cb322205f');
INSERT INTO diwi_testset.project_gemeenterol_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_gemeenterol_value_id) VALUES ('651798c3-034e-45ef-b347-3492d0553e25', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '47964244-4f7e-4ed5-93ba-b7667a3e8445');
INSERT INTO diwi_testset.project_gemeenterol_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_gemeenterol_value_id) VALUES ('45bbdf32-4a53-46e3-91c0-14325b97998a', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, '47964244-4f7e-4ed5-93ba-b7667a3e8445');
INSERT INTO diwi_testset.project_gemeenterol_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_gemeenterol_value_id) VALUES ('89d36ee1-8a5e-4219-8b64-8fc079c33ca2', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, '47964244-4f7e-4ed5-93ba-b7667a3e8445');
INSERT INTO diwi_testset.project_gemeenterol_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_gemeenterol_value_id) VALUES ('7c0a174b-099a-4f69-9d6b-4202ca169982', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, '5cdd1b67-2174-4efd-8e82-a69cb322205f');


--
-- Data for Name: project_gemeenterol_value_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_gemeenterol_value_state (id, value_label, change_user_id, change_start_date, change_end_date, project_gemeenterol_value_id) VALUES ('465c80e2-749e-433d-9af2-07cf5d0b6e46', 'Role 1', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '47964244-4f7e-4ed5-93ba-b7667a3e8445');
INSERT INTO diwi_testset.project_gemeenterol_value_state (id, value_label, change_user_id, change_start_date, change_end_date, project_gemeenterol_value_id) VALUES ('36e4a034-3e09-4d1e-92c1-bca2ca98ad34', 'Role 2', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '5cdd1b67-2174-4efd-8e82-a69cb322205f');


--
-- Data for Name: project_name_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('58b7586a-8364-4c68-a973-2460a7965a04', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', '2024-01-06 20:06:32+02', 'Name 1');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('3df20eda-5566-4a87-8d9f-4f490ad2be68', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-06 20:06:32+02', '2024-01-10 20:06:32+02', 'Name 2');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('c0153d9e-34bf-4da4-81b6-97d9603b875d', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-10 20:06:32+02', NULL, 'Name 3');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('2a0f5b93-b57b-42c3-aec4-415fc6277e2d', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'Name 1');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('2239ffe2-d9dc-4275-bbca-0773a4667d08', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '342887ab-2106-45c4-9565-a4b83f4d3362', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'Name 2');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('856e2898-6e92-478f-b9cd-fdfa84b7558a', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'Prj 2 Name 1');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('ddc8c610-ed05-4cd1-aa85-10f0188e98bb', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '4237d087-3d45-4acf-a1a6-0a67b754608e', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'Prj 2 Name 2');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('8e58ffdd-d420-4860-b015-5b2b4112686a', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '4237d087-3d45-4acf-a1a6-0a67b754608e', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', '2024-01-05 20:06:32+02', 'Prj 2 Name 2 - old');
INSERT INTO diwi_testset.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, name) VALUES ('49f60177-6f57-41a4-94a9-255bbb978d46', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'Current project - future segment');


--
-- Data for Name: project_plan_type_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('39033af1-82cb-435a-8110-a0bf08dba9a6', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('2e2f280d-c12a-47cb-8776-fb9aba3f5902', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '4237d087-3d45-4acf-a1a6-0a67b754608e', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('bdac729b-dc0b-49d1-af50-cbb2efcc661e', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('a023428c-fa13-4a4f-8125-21a6855fb36b', '1c155b85-29e3-4fec-85a2-96bda344fe8a', '4237d087-3d45-4acf-a1a6-0a67b754608e', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('51648bc7-100b-4da8-ae69-5d5362f885f4', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL);


--
-- Data for Name: project_plan_type_changelog_value; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('4064d78b-94d6-4c75-9e6a-9890fbef6837', '39033af1-82cb-435a-8110-a0bf08dba9a6', 'PAND_TRANSFORMATIE');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('cc7afaa1-bb20-43c2-97a8-323255bb6914', '39033af1-82cb-435a-8110-a0bf08dba9a6', 'HERSTRUCTURERING');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('798f46e7-86b0-4e2e-bdbf-689d6deaa3fc', '39033af1-82cb-435a-8110-a0bf08dba9a6', 'UITBREIDING_OVERIG');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('3762c0fa-058b-4403-a071-8b1a48c76cc9', '2e2f280d-c12a-47cb-8776-fb9aba3f5902', 'PAND_TRANSFORMATIE');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('e74a26cb-4e5f-404a-a8d0-bd040abacf61', '2e2f280d-c12a-47cb-8776-fb9aba3f5902', 'HERSTRUCTURERING');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('c43fe772-a62b-4df4-b882-86af321ee15a', 'bdac729b-dc0b-49d1-af50-cbb2efcc661e', 'PAND_TRANSFORMATIE');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('0b324ae1-32de-4815-9356-cc8eeffafc9e', 'bdac729b-dc0b-49d1-af50-cbb2efcc661e', 'UITBREIDING_OVERIG');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('ccb26363-f881-455e-86df-d050388a5b19', 'a023428c-fa13-4a4f-8125-21a6855fb36b', 'HERSTRUCTURERING');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('5fc2b026-98a8-40f2-a3fb-8a2f63edebe9', 'a023428c-fa13-4a4f-8125-21a6855fb36b', 'PAND_TRANSFORMATIE');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('57ffa27e-046d-46a0-95a7-42ca99e94b9b', '51648bc7-100b-4da8-ae69-5d5362f885f4', 'UITBREIDING_OVERIG');


--
-- Data for Name: project_planologische_planstatus_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_planologische_planstatus_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('4029f955-d129-4f8a-b974-fe0f41927315', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_planologische_planstatus_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('9d6db690-fc2c-438a-bd46-6baa54e48a5e', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL);
INSERT INTO diwi_testset.project_planologische_planstatus_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('ce37c83a-e6f7-478e-86e0-743a3d5696c4', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL);


--
-- Data for Name: project_planologische_planstatus_changelog_value; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('56b59872-d2f5-4723-bd6a-758aa23c9690', '4029f955-d129-4f8a-b974-fe0f41927315', '_2A_VASTGESTELD');
INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('0f7381f6-7a68-46b4-8cdb-9ed69b8863ff', '4029f955-d129-4f8a-b974-fe0f41927315', '_3_IN_VOORBEREIDING');
INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('ccdba8aa-dfe8-41aa-939d-90aacba88378', '9d6db690-fc2c-438a-bd46-6baa54e48a5e', '_4A_OPGENOMEN_IN_VISIE');
INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('7cb35caf-5a54-449c-abe0-ae4ec0a05f23', '9d6db690-fc2c-438a-bd46-6baa54e48a5e', '_2A_VASTGESTELD');
INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('09aaa81a-0423-4aba-8135-b872be650bfc', '9d6db690-fc2c-438a-bd46-6baa54e48a5e', '_1C_ONHERROEPELIJK_MET_BW_NODIG');
INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('60883b15-1e2b-4825-84b7-c8e82bc75671', 'ce37c83a-e6f7-478e-86e0-743a3d5696c4', '_2A_VASTGESTELD');
INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('401dfb55-6f89-4f89-b925-00796291a61d', 'ce37c83a-e6f7-478e-86e0-743a3d5696c4', '_3_IN_VOORBEREIDING');


--
-- Data for Name: project_priorisering_value; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_priorisering_value (id) VALUES ('49390d77-08de-4993-969f-2264d8f4ca8c');
INSERT INTO diwi_testset.project_priorisering_value (id) VALUES ('04d1fc97-bb25-4246-8413-9ec5a1044db7');
INSERT INTO diwi_testset.project_priorisering_value (id) VALUES ('6a91fb22-a44a-4da9-b5f3-933596522e10');


--
-- Data for Name: project_priorisering_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_priorisering_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_priorisering_value_id, value_type, project_priorisering_min_value_id, project_priorisering_max_value_id) VALUES ('1746b507-0e82-424e-b73f-4f6d2320cbe7', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', '2024-01-17 20:06:32+02', '04d1fc97-bb25-4246-8413-9ec5a1044db7', 'SINGLE_VALUE', NULL, NULL);
INSERT INTO diwi_testset.project_priorisering_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_priorisering_value_id, value_type, project_priorisering_min_value_id, project_priorisering_max_value_id) VALUES ('9e2999f1-d22b-46cb-971b-b3a1d9ae1bcb', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-17 20:06:32+02', NULL, NULL, 'RANGE', '49390d77-08de-4993-969f-2264d8f4ca8c', '6a91fb22-a44a-4da9-b5f3-933596522e10');
INSERT INTO diwi_testset.project_priorisering_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_priorisering_value_id, value_type, project_priorisering_min_value_id, project_priorisering_max_value_id) VALUES ('e1f3cc1e-b475-4f75-9b77-2c2c0d6d3f81', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, '04d1fc97-bb25-4246-8413-9ec5a1044db7', 'SINGLE_VALUE', NULL, NULL);
INSERT INTO diwi_testset.project_priorisering_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_priorisering_value_id, value_type, project_priorisering_min_value_id, project_priorisering_max_value_id) VALUES ('4e6b7d8d-7cab-4f4c-86d5-7bc9252cdd25', '4237d087-3d45-4acf-a1a6-0a67b754608e', '529ca978-1d23-418a-a94c-bd219454a587', '54a0c192-3454-4f7a-becd-96d214461987', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-04 20:06:32+02', NULL, NULL, 'RANGE', '04d1fc97-bb25-4246-8413-9ec5a1044db7', '6a91fb22-a44a-4da9-b5f3-933596522e10');


--
-- Data for Name: project_priorisering_value_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_priorisering_value_state (id, value_label, ordinal_level, change_user_id, change_start_date, change_end_date, project_priorisering_value_id) VALUES ('291a6e8a-143c-4c89-8ca9-dc739655fa11', 'low', 1, '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '49390d77-08de-4993-969f-2264d8f4ca8c');
INSERT INTO diwi_testset.project_priorisering_value_state (id, value_label, ordinal_level, change_user_id, change_start_date, change_end_date, project_priorisering_value_id) VALUES ('ce7bc56d-5dfe-45fe-baad-c9bbd603fc72', 'medium', 2, '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '04d1fc97-bb25-4246-8413-9ec5a1044db7');
INSERT INTO diwi_testset.project_priorisering_value_state (id, value_label, ordinal_level, change_user_id, change_start_date, change_end_date, project_priorisering_value_id) VALUES ('cfac114f-6a5f-45fb-bed2-ba167d910325', 'high', 3, '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '6a91fb22-a44a-4da9-b5f3-933596522e10');


--
-- Data for Name: project_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_state (id, project_id, owner_organization_id, change_user_id, change_start_date, change_end_date, confidentiality_level, project_colour) VALUES ('e8a7bf18-3bf0-4917-afe2-49523f262cf5', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '018d1d85-639f-701c-b73f-0f35f98409bc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', '2024-01-07 20:06:32.023052+02', 'EXTERN_RAPPORTAGE', '#334455');
INSERT INTO diwi_testset.project_state (id, project_id, owner_organization_id, change_user_id, change_start_date, change_end_date, confidentiality_level, project_colour) VALUES ('d91019f7-f859-4469-b0be-f2515dbee563', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '018d1d85-639f-701c-b73f-0f35f98409bc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-07 20:06:32.023052+02', NULL, 'OPENBAAR', '#334455');
INSERT INTO diwi_testset.project_state (id, project_id, owner_organization_id, change_user_id, change_start_date, change_end_date, confidentiality_level, project_colour) VALUES ('c62e372c-e563-4a72-8f03-1132082577b9', '54a0c192-3454-4f7a-becd-96d214461987', '018d1d85-639f-701c-b73f-0f35f98409bc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'EXTERN_RAPPORTAGE', '#123123');
INSERT INTO diwi_testset.project_state (id, project_id, owner_organization_id, change_user_id, change_start_date, change_end_date, confidentiality_level, project_colour) VALUES ('e74d8d9b-bff6-4008-83ef-8b95e822bf83', '5c9fbb50-d8fd-480d-9e38-7f3b391d3110', '018d1d85-639f-701c-b73f-0f35f98409bc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'EXTERN_RAPPORTAGE', '#456456');

