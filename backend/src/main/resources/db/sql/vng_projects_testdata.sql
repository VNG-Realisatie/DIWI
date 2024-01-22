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


--
-- Data for Name: milestone_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('fad2d7e7-4d17-4216-b063-f028b49d81a2', '292414eb-4a4e-405c-954b-e01bb355bc9b', '2024-01-10', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-01 20:06:32+02', '2024-01-15 20:06:32+02', 'gepland', 'Milestone ian 10');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('c7a00daa-0c52-4bb0-9915-78855da9997f', '292414eb-4a4e-405c-954b-e01bb355bc9b', '2024-01-10', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', NULL, 'gepland', 'Milestone ian 10 - v2');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('f33114d9-946f-41ec-a5b2-bf152929fc88', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '2024-01-30', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-01 20:06:32+02', '2024-01-15 20:06:32+02', 'gepland', 'Milestone ian 30');
INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, change_user_id, change_start_date, change_end_date, status, omschrijving) VALUES ('4277edda-68eb-498e-bf98-6f4010ea18ad', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '2024-01-30', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', NULL, 'gepland', 'Milestone ian 30 - v2');


--
-- Data for Name: organization_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.organization_state (id, organization_id, parent_organization_id, naam, change_end_date, change_start_date, change_user_id) VALUES ('4872eac4-9911-47ef-b4d6-62e4e9223926', '018d1d85-639f-701c-b73f-0f35f98409bc', NULL, 'Org1', NULL, '2024-01-10 20:06:32+02', '2122426c-6e70-419c-a054-f51dd24d798b');


--
-- Data for Name: project; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project (id) VALUES ('466da5e2-c96f-4856-aa17-6b37a1c21edc');
INSERT INTO diwi_testset.project (id) VALUES ('54a0c192-3454-4f7a-becd-96d214461987');

--
-- Data for Name: project_duration_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('56e7e05f-8b42-4dcb-bc5a-98d99a5411fc', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-15 20:06:32+02', '2024-01-16 20:06:32+02');
INSERT INTO diwi_testset.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('4e5b7e0b-84c4-4c07-b703-0bbe304b34bf', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL);


--
-- Data for Name: project_fase_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_fase) VALUES ('4ff23141-ae6e-46b7-8583-31d5bef66f26', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-10 20:06:32+02', NULL, '1_Initiatieffase');

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


--
-- Data for Name: project_plan_type_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_plan_type_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('39033af1-82cb-435a-8110-a0bf08dba9a6', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL);


--
-- Data for Name: project_plan_type_changelog_value; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('4064d78b-94d6-4c75-9e6a-9890fbef6837', '39033af1-82cb-435a-8110-a0bf08dba9a6', 'pand_transformatie');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('cc7afaa1-bb20-43c2-97a8-323255bb6914', '39033af1-82cb-435a-8110-a0bf08dba9a6', 'herstructurering');
INSERT INTO diwi_testset.project_plan_type_changelog_value (id, changelog_id, plan_type) VALUES ('798f46e7-86b0-4e2e-bdbf-689d6deaa3fc', '39033af1-82cb-435a-8110-a0bf08dba9a6', 'uitbreiding_overig');


--
-- Data for Name: project_planologische_planstatus_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_planologische_planstatus_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date) VALUES ('4029f955-d129-4f8a-b974-fe0f41927315', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL);


--
-- Data for Name: project_planologische_planstatus_changelog_value; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('56b59872-d2f5-4723-bd6a-758aa23c9690', '4029f955-d129-4f8a-b974-fe0f41927315', '2a_vastgesteld');
INSERT INTO diwi_testset.project_planologische_planstatus_changelog_value (id, planologische_planstatus_changelog_id, planologische_planstatus) VALUES ('0f7381f6-7a68-46b4-8cdb-9ed69b8863ff', '4029f955-d129-4f8a-b974-fe0f41927315', '3_in_voorbereiding');


--
-- Data for Name: project_priorisering_value; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_priorisering_value (id) VALUES ('49390d77-08de-4993-969f-2264d8f4ca8c');
INSERT INTO diwi_testset.project_priorisering_value (id) VALUES ('04d1fc97-bb25-4246-8413-9ec5a1044db7');
INSERT INTO diwi_testset.project_priorisering_value (id) VALUES ('6a91fb22-a44a-4da9-b5f3-933596522e10');


--
-- Data for Name: project_priorisering_changelog; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_priorisering_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_priorisering_value_id, value_type, project_priorisering_min_value_id, project_priorisering_max_value_id) VALUES ('1746b507-0e82-424e-b73f-4f6d2320cbe7', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', '2024-01-17 20:06:32+02', '04d1fc97-bb25-4246-8413-9ec5a1044db7', 'single_value', NULL, NULL);
INSERT INTO diwi_testset.project_priorisering_changelog (id, start_milestone_id, end_milestone_id, project_id, change_user_id, change_start_date, change_end_date, project_priorisering_value_id, value_type, project_priorisering_min_value_id, project_priorisering_max_value_id) VALUES ('9e2999f1-d22b-46cb-971b-b3a1d9ae1bcb', '292414eb-4a4e-405c-954b-e01bb355bc9b', '07f3a0ad-f76a-4dd9-b4c6-6eb8113968e0', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-17 20:06:32+02', NULL, NULL, 'range', '49390d77-08de-4993-969f-2264d8f4ca8c', '6a91fb22-a44a-4da9-b5f3-933596522e10');


--
-- Data for Name: project_priorisering_value_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_priorisering_value_state (id, value_label, ordinal_level, change_user_id, change_start_date, change_end_date, project_priorisering_value_id) VALUES ('291a6e8a-143c-4c89-8ca9-dc739655fa11', 'low', 1, '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '49390d77-08de-4993-969f-2264d8f4ca8c');
INSERT INTO diwi_testset.project_priorisering_value_state (id, value_label, ordinal_level, change_user_id, change_start_date, change_end_date, project_priorisering_value_id) VALUES ('ce7bc56d-5dfe-45fe-baad-c9bbd603fc72', 'medium', 2, '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '04d1fc97-bb25-4246-8413-9ec5a1044db7');
INSERT INTO diwi_testset.project_priorisering_value_state (id, value_label, ordinal_level, change_user_id, change_start_date, change_end_date, project_priorisering_value_id) VALUES ('cfac114f-6a5f-45fb-bed2-ba167d910325', 'high', 3, '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-16 20:06:32+02', NULL, '6a91fb22-a44a-4da9-b5f3-933596522e10');


--
-- Data for Name: project_state; Type: TABLE DATA; Schema: diwi_testset; Owner: vng
--

INSERT INTO diwi_testset.project_state (id, project_id, owner_organization_id, change_user_id, change_start_date, change_end_date, confidentiality_level, project_colour) VALUES ('e8a7bf18-3bf0-4917-afe2-49523f262cf5', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '018d1d85-639f-701c-b73f-0f35f98409bc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', '2024-01-07 20:06:32.023052+02', 'extern_rapportage', '#334455');
INSERT INTO diwi_testset.project_state (id, project_id, owner_organization_id, change_user_id, change_start_date, change_end_date, confidentiality_level, project_colour) VALUES ('d91019f7-f859-4469-b0be-f2515dbee563', '466da5e2-c96f-4856-aa17-6b37a1c21edc', '018d1d85-639f-701c-b73f-0f35f98409bc', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-07 20:06:32.023052+02', NULL, 'openbaar', '#334455');
