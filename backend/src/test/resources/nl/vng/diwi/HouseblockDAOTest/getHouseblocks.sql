INSERT INTO diwi."user" (id) VALUES ('2122426c-6e70-419c-a054-f51dd24d798b');

INSERT INTO diwi.project (id) VALUES ('_projectUuid_');

INSERT INTO diwi.milestone (id, project_id) VALUES ('ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '_projectUuid_');
INSERT INTO diwi.milestone (id, project_id) VALUES ('342887ab-2106-45c4-9565-a4b83f4d3362', '_projectUuid_');

INSERT INTO diwi.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('fc8e4916-3442-4d7c-8f5f-34b477108040', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '_startDate_', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 1');
INSERT INTO diwi.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving)
    VALUES ('50164197-792e-41b8-928d-aaaa54ae2cf4', '342887ab-2106-45c4-9565-a4b83f4d3362', '_endDate_', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', NULL, 'GEPLAND', 'Milestone May 30');


INSERT INTO diwi.woningblok (id, project_id) VALUES ('16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', '_projectUuid_');

INSERT INTO diwi.woningblok_duration_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('c8aa5543-734e-4282-8d00-c2ece87503d3', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi.woningblok_state (id, woningblok_id, create_user_id, change_start_date)
    VALUES ('018eaa5b-44a9-7387-bbe2-b7844d9529a8', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi.woningblok_naam_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, naam, create_user_id, change_start_date)
    VALUES ('6452192f-f978-4b68-b047-262a2e34a1f8', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', 'Woningblok 1', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi.woningblok_mutatie_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, mutation_kind, amount)
    VALUES ('82fbe354-97d9-462d-97d0-21a9efd0fbe2', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', '2024-01-05 20:06:32+02', 'CONSTRUCTION', 25);

INSERT INTO diwi.woningblok_grondpositie_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('be6b2f40-1199-458a-9883-ed8dd9164dfc', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());
INSERT INTO diwi.woningblok_grondpositie_changelog_value (id, woningblok_grondpositie_changelog_id, grondpositie, amount)
    VALUES ('a6fdef4f-c862-4602-9be3-c1b534bef78f', 'be6b2f40-1199-458a-9883-ed8dd9164dfc', 'FORMELE_TOESTEMMING_GRONDEIGENAAR', 10);
INSERT INTO diwi.woningblok_grondpositie_changelog_value (id, woningblok_grondpositie_changelog_id, grondpositie, amount)
    VALUES ('6bc189e8-6aad-4d4e-ac8f-190ef0fe6adb', 'be6b2f40-1199-458a-9883-ed8dd9164dfc', 'INTENTIE_MEDEWERKING_GRONDEIGENAAR', 20);
INSERT INTO diwi.woningblok_grondpositie_changelog_value (id, woningblok_grondpositie_changelog_id, grondpositie, amount)
    VALUES ('8e065a2e-51ac-461e-bc2b-b66f77ac31dd', 'be6b2f40-1199-458a-9883-ed8dd9164dfc', 'GEEN_TOESTEMMING_GRONDEIGENAAR', 30);

INSERT INTO diwi.woningblok_type_en_fysiek_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('7690992c-33ac-4069-b774-f2089d440c38', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, property_value_id, amount)
    SELECT '84f76396-50d2-4d97-947e-5db7f1c62ec0', '7690992c-33ac-4069-b774-f2089d440c38', category_value_id, 5
           FROM diwi.property_category_value_state WHERE value_label = 'Tussenwoning' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, property_value_id, amount)
    SELECT '3ffce17b-82ae-4a63-8d3c-4bfd68627b4d', '7690992c-33ac-4069-b774-f2089d440c38', category_value_id, 15
        FROM diwi.property_category_value_state WHERE value_label = 'Hoekwoning' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, property_value_id, amount)
    SELECT 'd5c6efa8-b712-4d49-a1c7-b0040016134d', '7690992c-33ac-4069-b774-f2089d440c38', category_value_id, 35
        FROM diwi.property_category_value_state WHERE value_label = 'Twee onder een kap' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, property_value_id, amount)
    SELECT '80986454-7e42-4195-af78-0e998647c7ac', '7690992c-33ac-4069-b774-f2089d440c38', category_value_id, 25
        FROM diwi.property_category_value_state WHERE value_label = 'Vrijstaand' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, property_value_id, amount)
    SELECT 'e10ff5b4-9abf-41e8-80c6-7940c00ab912', '7690992c-33ac-4069-b774-f2089d440c38', category_value_id, 55
        FROM diwi.property_category_value_state WHERE value_label = 'Portiekflat' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, property_value_id, amount)
    SELECT 'd9a921b9-f206-45f9-94e9-c63eb137c2ea', '7690992c-33ac-4069-b774-f2089d440c38', category_value_id, 45
        FROM diwi.property_category_value_state WHERE value_label = 'Gallerijflat' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_type_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, woning_type, amount)
    VALUES ('9e9730ed-8083-458c-9ed9-2046ecd79fd9', '7690992c-33ac-4069-b774-f2089d440c38', 'EENGEZINSWONING', 103);
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_type_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, woning_type, amount)
    VALUES ('51c588c4-bf81-47b4-a61b-282c9967817b', '7690992c-33ac-4069-b774-f2089d440c38', 'MEERGEZINSWONING', 67);

INSERT INTO diwi.woningblok_doelgroep_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('b44386c9-3ebb-4168-aac6-ca6bc751fa42', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, property_value_id, amount)
    SELECT 'de2b5b08-db8f-4db4-bc6c-7c9349689165', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', category_value_id, 3
        FROM diwi.property_category_value_state WHERE value_label = 'Regulier' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, property_value_id, amount)
    SELECT '25506e80-5286-4043-8392-8aa86a19baea', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', category_value_id, 6
        FROM diwi.property_category_value_state WHERE value_label = 'Jongeren' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, property_value_id, amount)
    SELECT '3896ff5b-acc2-4907-8165-75cbedf4534f', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', category_value_id, 9
        FROM diwi.property_category_value_state WHERE value_label = 'Student' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, property_value_id, amount)
    SELECT '46e70cfe-9519-4d9e-bb4e-a6840ed75731', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', category_value_id, 12
        FROM diwi.property_category_value_state WHERE value_label = 'Ouderen' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, property_value_id, amount)
    SELECT '4db78b78-8341-43ff-bd49-52e67af336b3', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', category_value_id, 15
        FROM diwi.property_category_value_state WHERE value_label = 'GHZ' AND change_end_date IS NULL;
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, property_value_id, amount)
    SELECT '46340e62-0ea4-4986-a422-1cdea0c9a4c7', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', category_value_id, 18
        FROM diwi.property_category_value_state WHERE value_label = 'Grote gezinnen' AND change_end_date IS NULL;

INSERT INTO diwi.woningblok_programmering_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, programmering)
VALUES ('61ab6cfb-54ac-4beb-9227-43207792cfa2', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(), true);

INSERT INTO diwi.woningblok_grootte_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, value, value_range, value_type)
VALUES ('f03e3d29-c8bf-44d4-816a-cb9de50af790', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(), null, '[11.1, 22.2]', 'RANGE');

INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date,
                                                                  waarde_value_type, waarde_value, huurbedrag_value_type, huurbedrag_value, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range)
VALUES ('aecc55e0-a560-41fe-b829-f3b2e099a0a7', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(),
        'SINGLE_VALUE', 10, 'SINGLE_VALUE', 20, 5, 'KOOPWONING', null, null);

INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date,
                                                                  waarde_value_type, waarde_value, huurbedrag_value_type, huurbedrag_value, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range)
VALUES ('90530dc3-e1de-47fb-8308-3ae3bdced0a9', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(),
        'RANGE', null, 'RANGE', null, 5, 'HUURWONING_WONINGCORPORATIE', '[10, 12]', '[8, 9]');

