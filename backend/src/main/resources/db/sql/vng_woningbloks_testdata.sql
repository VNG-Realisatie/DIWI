-- Present woningblok
INSERT INTO diwi_testset.woningblok_duration_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('c8aa5543-734e-4282-8d00-c2ece87503d3', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi_testset.woningblok_naam_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, naam, create_user_id, change_start_date)
    VALUES ('6452192f-f978-4b68-b047-262a2e34a1f8', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', 'Woningblok 1', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi_testset.woningblok_mutatie_changelog_soort_value (id, woningblok_mutatie_changelog_id, mutatie_soort)
    VALUES ('53dc07c8-0807-48ec-a570-a68cd9efa179', '82fbe354-97d9-462d-97d0-21a9efd0fbe2', 'TRANSFORMATIE');
INSERT INTO diwi_testset.woningblok_mutatie_changelog_soort_value (id, woningblok_mutatie_changelog_id, mutatie_soort)
    VALUES ('c2512305-3cd0-408c-a795-fe71a590740d', '82fbe354-97d9-462d-97d0-21a9efd0fbe2', 'SPLITSING');

INSERT INTO diwi_testset.woningblok_grondpositie_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('be6b2f40-1199-458a-9883-ed8dd9164dfc', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());
INSERT INTO diwi_testset.woningblok_grondpositie_changelog_value (id, woningblok_grondpositie_changelog_id, grondpositie, amount)
    VALUES ('a6fdef4f-c862-4602-9be3-c1b534bef78f', 'be6b2f40-1199-458a-9883-ed8dd9164dfc', 'FORMELE_TOESTEMMING_GRONDEIGENAAR', 10);
INSERT INTO diwi_testset.woningblok_grondpositie_changelog_value (id, woningblok_grondpositie_changelog_id, grondpositie, amount)
    VALUES ('6bc189e8-6aad-4d4e-ac8f-190ef0fe6adb', 'be6b2f40-1199-458a-9883-ed8dd9164dfc', 'INTENTIE_MEDEWERKING_GRONDEIGENAAR', 20);
INSERT INTO diwi_testset.woningblok_grondpositie_changelog_value (id, woningblok_grondpositie_changelog_id, grondpositie, amount)
    VALUES ('8e065a2e-51ac-461e-bc2b-b66f77ac31dd', 'be6b2f40-1199-458a-9883-ed8dd9164dfc', 'GEEN_TOESTEMMING_GRONDEIGENAAR', 30);

INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('7690992c-33ac-4069-b774-f2089d440c38', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, fysiek_voorkomen, amount)
    VALUES ('84f76396-50d2-4d97-947e-5db7f1c62ec0', '7690992c-33ac-4069-b774-f2089d440c38', 'TUSSENWONING', 5);
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, fysiek_voorkomen, amount)
    VALUES ('3ffce17b-82ae-4a63-8d3c-4bfd68627b4d', '7690992c-33ac-4069-b774-f2089d440c38', 'HOEKWONING', 15);
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, fysiek_voorkomen, amount)
    VALUES ('d5c6efa8-b712-4d49-a1c7-b0040016134d', '7690992c-33ac-4069-b774-f2089d440c38', 'TWEE_ONDER_EEN_KAP', 35);
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, fysiek_voorkomen, amount)
    VALUES ('80986454-7e42-4195-af78-0e998647c7ac', '7690992c-33ac-4069-b774-f2089d440c38', 'VRIJSTAAND', 25);
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, fysiek_voorkomen, amount)
    VALUES ('e10ff5b4-9abf-41e8-80c6-7940c00ab912', '7690992c-33ac-4069-b774-f2089d440c38', 'PORTIEKFLAT', 55);
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, fysiek_voorkomen, amount)
    VALUES ('d9a921b9-f206-45f9-94e9-c63eb137c2ea', '7690992c-33ac-4069-b774-f2089d440c38', 'GALLERIJFLAT', 45);
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_type_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, woning_type, amount)
    VALUES ('9e9730ed-8083-458c-9ed9-2046ecd79fd9', '7690992c-33ac-4069-b774-f2089d440c38', 'EENGEZINSWONING', 103);
INSERT INTO diwi_testset.woningblok_type_en_fysiek_changelog_type_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, woning_type, amount)
    VALUES ('51c588c4-bf81-47b4-a61b-282c9967817b', '7690992c-33ac-4069-b774-f2089d440c38', 'MEERGEZINSWONING', 67);

INSERT INTO diwi_testset.woningblok_doelgroep_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('b44386c9-3ebb-4168-aac6-ca6bc751fa42', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());
INSERT INTO diwi_testset.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, doelgroep, amount)
    VALUES ('de2b5b08-db8f-4db4-bc6c-7c9349689165', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', 'REGULIER', 3);
INSERT INTO diwi_testset.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, doelgroep, amount)
    VALUES ('25506e80-5286-4043-8392-8aa86a19baea', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', 'JONGEREN', 6);
INSERT INTO diwi_testset.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, doelgroep, amount)
    VALUES ('3896ff5b-acc2-4907-8165-75cbedf4534f', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', 'STUDENTEN', 9);
INSERT INTO diwi_testset.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, doelgroep, amount)
    VALUES ('46e70cfe-9519-4d9e-bb4e-a6840ed75731', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', 'OUDEREN', 12);
INSERT INTO diwi_testset.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, doelgroep, amount)
    VALUES ('4db78b78-8341-43ff-bd49-52e67af336b3', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', 'GEHANDICAPTEN_EN_ZORG', 15);
INSERT INTO diwi_testset.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, doelgroep, amount)
    VALUES ('46340e62-0ea4-4986-a422-1cdea0c9a4c7', 'b44386c9-3ebb-4168-aac6-ca6bc751fa42', 'GROTE_GEZINNEN', 18);

INSERT INTO diwi_testset.woningblok_programmering_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, programmering)
    VALUES ('61ab6cfb-54ac-4beb-9227-43207792cfa2', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(), true);

INSERT INTO diwi_testset.woningblok_grootte_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, value, value_range, value_type)
    VALUES ('f03e3d29-c8bf-44d4-816a-cb9de50af790', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(), null, '[11.1, 22.2]', 'RANGE');

INSERT INTO diwi_testset.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date,
    waarde_value_type, waarde_value, huurbedrag_value_type, huurbedrag_value, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range)
VALUES ('aecc55e0-a560-41fe-b829-f3b2e099a0a7', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(),
        'SINGLE_VALUE', 10, 'SINGLE_VALUE', 20, 5, 'KOOPWONING', null, null);

INSERT INTO diwi_testset.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date,
    waarde_value_type, waarde_value, huurbedrag_value_type, huurbedrag_value, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range)
VALUES ('90530dc3-e1de-47fb-8308-3ae3bdced0a9', '16dbf29e-bd3e-419e-9b2c-0bfd834c0d19', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(),
        'RANGE', null, 'RANGE', null, 5, 'HUURWONING_WONINGCORPORATIE', '[10, 12]', '[8, 9]');


-- Future woningblok
INSERT INTO diwi_testset.woningblok (id, project_id) VALUES ('7f402e53-7faf-4e1f-a5a9-2249682eef7c', '54a0c192-3454-4f7a-becd-96d214461987');

INSERT INTO diwi_testset.woningblok_duration_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('f34ba231-b2cf-41d8-abb8-306d511e7b9d', '7f402e53-7faf-4e1f-a5a9-2249682eef7c', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '342887ab-2106-45c4-9565-a4b83f4d3362', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi_testset.woningblok_naam_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, naam, create_user_id, change_start_date)
    VALUES ('8e4d8b73-5929-49b8-ade2-98ba20131e46', '7f402e53-7faf-4e1f-a5a9-2249682eef7c', '7c6bfe67-6618-4a6a-9507-2cd15f88c11f', '342887ab-2106-45c4-9565-a4b83f4d3362', 'Woningblok 2', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

-- Past woningblok
INSERT INTO diwi_testset.milestone (id, project_id) VALUES ('13fcd006-456a-48c3-883d-da89113e3f17', '54a0c192-3454-4f7a-becd-96d214461987');

INSERT INTO diwi_testset.milestone_state (id, milestone_id, date, create_user_id, change_start_date, status, omschrijving)
    VALUES ('d9aa3be7-23c3-475b-ac5a-5de6e2f3a866', '13fcd006-456a-48c3-883d-da89113e3f17', '2024-03-10', '2122426c-6e70-419c-a054-f51dd24d798b', NOW(), 'GEPLAND', 'Milestone 10 march');

INSERT INTO diwi_testset.woningblok (id, project_id)
    VALUES ('19950bd0-163d-421f-8022-a967edefef5c', '54a0c192-3454-4f7a-becd-96d214461987');

INSERT INTO diwi_testset.woningblok_duration_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date)
    VALUES ('c9e8c4e7-7d1b-44b0-87dc-67f74683a864', '19950bd0-163d-421f-8022-a967edefef5c', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '13fcd006-456a-48c3-883d-da89113e3f17', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());

INSERT INTO diwi_testset.woningblok_naam_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, naam, create_user_id, change_start_date)
    VALUES ('f8b7817c-ebac-4cb8-81cf-9751abf2bc43', '19950bd0-163d-421f-8022-a967edefef5c', 'ee3cbe15-7a5d-42c6-a807-0a7593a24d4f', '13fcd006-456a-48c3-883d-da89113e3f17', 'Woningblok 3', '2122426c-6e70-419c-a054-f51dd24d798b', NOW());
