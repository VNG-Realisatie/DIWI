ALTER TABLE diwi_testset.actor_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.actor_state RENAME CONSTRAINT fk_actor_state__change_user TO fk_actor_state__create_user;
ALTER TABLE diwi_testset.actor_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.actor_state
    ADD CONSTRAINT fk_actor_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.buurt_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.buurt_state RENAME CONSTRAINT fk_buurt_state__change_user TO fk_buurt_state__create_user;
ALTER TABLE diwi_testset.buurt_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.buurt_state
    ADD CONSTRAINT fk_buurt_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.document_soort_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.document_soort_state RENAME CONSTRAINT fk_document_soort_state__user TO fk_document_soort_state__create_user;
ALTER TABLE diwi_testset.document_soort_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.gemeente_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.gemeente_state RENAME CONSTRAINT fk_gemeente_state__gchange_user TO fk_gemeente_state__create_user;
ALTER TABLE diwi_testset.gemeente_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.gemeente_state
    ADD CONSTRAINT fk_gemeente_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state RENAME CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__change_user TO fk_maatwerk_categorie_waarde_state__create_user;
ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.maatwerk_categorie_waarde_state
    ADD CONSTRAINT fk_maatwerk_categorie_waarde_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.maatwerk_eigenschap_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.maatwerk_eigenschap_state RENAME CONSTRAINT fk_woningblok_maatwerk_eigenschap_state__change_user TO fk_maatwerk_eigenschap_state__create_user;
ALTER TABLE diwi_testset.maatwerk_eigenschap_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.maatwerk_eigenschap_state
    ADD CONSTRAINT fk_maatwerk_eigenschap_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state RENAME CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__change_user TO fk_maatwerk_ordinaal_waarde_state__create_user;
ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.maatwerk_ordinaal_waarde_state
    ADD CONSTRAINT fk_maatwerk_ordinaal_waarde_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.milestone_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.milestone_state RENAME CONSTRAINT fk_milestone_state__change_user TO fk_milestone_state__create_user;
ALTER TABLE diwi_testset.milestone_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.milestone_state
    ADD CONSTRAINT fk_milestone_state_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.organization_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.organization_state RENAME CONSTRAINT fk_organization_state__change_user TO fk_organization_state__create_user;
ALTER TABLE diwi_testset.organization_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.organization_state
    ADD CONSTRAINT fk_organization_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.organization_to_project RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.organization_to_project RENAME CONSTRAINT fk_organization_to_project__change_user TO fk_organization_to_project__create_user;
ALTER TABLE diwi_testset.organization_to_project ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.organization_to_project
    ADD CONSTRAINT fk_organization_to_project__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.organization_to_plan RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.organization_to_plan RENAME CONSTRAINT fk_organization_to_plan__change_user TO fk_organization_to_plan__create_user;
ALTER TABLE diwi_testset.organization_to_plan ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.organization_to_plan
    ADD CONSTRAINT fk_organization_to_plan__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.organization_to_document RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.organization_to_document RENAME CONSTRAINT fk_organization_to_document__change_user TO fk_organization_to_document__create_user;
ALTER TABLE diwi_testset.organization_to_document ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.organization_to_document
    ADD CONSTRAINT fk_organization_to_document__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_doelgroep RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_doelgroep RENAME CONSTRAINT fk_plan_conditie_doelgroep__change_user TO fk_plan_conditie_doelgroep__create_user;
ALTER TABLE diwi_testset.plan_conditie_doelgroep ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_eigendom_en_waarde RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_eigendom_en_waarde RENAME CONSTRAINT fk_plan_conditie_eigendom_en_waarde__change_user TO fk_plan_conditie_eigendom_en_waarde__create_user;
ALTER TABLE diwi_testset.plan_conditie_eigendom_en_waarde ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_gemeente_indeling RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_gemeente_indeling RENAME CONSTRAINT fk_plan_conditie_gemeente_indeling__change_user TO fk_plan_conditie_gemeente_indeling__create_user;
ALTER TABLE diwi_testset.plan_conditie_gemeente_indeling ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_gemeente_indeling
    ADD CONSTRAINT fk_plan_conditie_gemeente_indeling__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_grondpositie RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_grondpositie RENAME CONSTRAINT fk_plan_conditie_grondpositie__change_user TO fk_plan_conditie_grondpositie__create_user;
ALTER TABLE diwi_testset.plan_conditie_grondpositie ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_grootte RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_grootte RENAME CONSTRAINT fk_plan_conditie_grootte__change_user TO fk_plan_conditie_grootte__create_user;
ALTER TABLE diwi_testset.plan_conditie_grootte ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_kadastraal RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_kadastraal RENAME CONSTRAINT fk_plan_conditie_geografie__change_user TO fk_plan_conditie_kadastraal__create_user;
ALTER TABLE diwi_testset.plan_conditie_kadastraal ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_kadastraal
    ADD CONSTRAINT fk_plan_conditie_kadastraal__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_maatwerk_boolean RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_boolean RENAME CONSTRAINT fk_plan_conditie_maatwerk_boolean__change_user TO fk_plan_conditie_maatwerk_boolean__create_user;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_boolean ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_maatwerk_categorie RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_categorie RENAME CONSTRAINT fk_plan_conditie_maatwerk_categorie__change_user TO fk_plan_conditie_maatwerk_categorie__create_user;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_categorie ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_maatwerk_numeriek RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_numeriek RENAME CONSTRAINT fk_plan_conditie_maatwerk_numeriek__change_user TO fk_plan_conditie_maatwerk_numeriek__create_user;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_numeriek ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_maatwerk_ordinaal RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_ordinaal RENAME CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__change_user TO fk_plan_conditie_maatwerk_ordinaal__create_user;
ALTER TABLE diwi_testset.plan_conditie_maatwerk_ordinaal ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_plan_conditie_maatwerk_ordinaal__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_programmering RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_programmering RENAME CONSTRAINT fk_plan_conditie_programmering__change_user TO fk_plan_conditie_programmering__create_user;
ALTER TABLE diwi_testset.plan_conditie_programmering ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_state RENAME CONSTRAINT fk_plan_conditie_state__change_user TO fk_plan_conditie_state__create_user;
ALTER TABLE diwi_testset.plan_conditie_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_conditie_type_en_fysiek RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_conditie_type_en_fysiek RENAME CONSTRAINT fk_plan_conditie_type_en_fysiek_voorkomen__change_user TO fk_plan_conditie_type_en_fysiek__create_user;
ALTER TABLE diwi_testset.plan_conditie_type_en_fysiek ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_conditie_type_en_fysiek
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_soort_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_soort_state RENAME CONSTRAINT fk_plan_soort_state__user TO fk_plan_soort_state__create_user;
ALTER TABLE diwi_testset.plan_soort_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.plan_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.plan_state RENAME CONSTRAINT fk_plan_state__change_user TO fk_plan_state__create_user;
ALTER TABLE diwi_testset.plan_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.plan_state
    ADD CONSTRAINT fk_plan_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_actor_rol_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_actor_rol_changelog RENAME CONSTRAINT fk_project_actor_rol_changelog__change_user TO fk_project_actor_rol_changelog__create_user;
ALTER TABLE diwi_testset.project_actor_rol_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_actor_rol_changelog
    ADD CONSTRAINT fk_project_actor_rol_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_actor_rol_value_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_actor_rol_value_state RENAME CONSTRAINT fk_project_actor_rol_value__user TO fk_project_actor_rol_value_state__create_user;
ALTER TABLE diwi_testset.project_actor_rol_value_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_actor_rol_value_state
    ADD CONSTRAINT fk_project_actor_rol_value_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_duration_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_duration_changelog RENAME CONSTRAINT fk_project_duration_changelog__change_user TO fk_project_duration_changelog__create_user;
ALTER TABLE diwi_testset.project_duration_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_fase_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_fase_changelog RENAME CONSTRAINT fk_project_fase_changelog__change_user TO fk_project_fase_changelog__create_user;
ALTER TABLE diwi_testset.project_fase_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_filiatie RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_filiatie RENAME CONSTRAINT fk_project_filiatie__change_user TO fk_project_filiatie__create_user;
ALTER TABLE diwi_testset.project_filiatie ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_gemeenterol_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_gemeenterol_changelog RENAME CONSTRAINT fk_project_gemeenterol_changelog__change_user TO fk_project_gemeenterol_changelog__create_user;
ALTER TABLE diwi_testset.project_gemeenterol_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_gemeenterol_changelog
    ADD CONSTRAINT fk_project_gemeenterol_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_gemeenterol_value_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_gemeenterol_value_state RENAME CONSTRAINT fk_project_gemeenterol_value_state__user TO fk_project_gemeenterol_value_state__create_user;
ALTER TABLE diwi_testset.project_gemeenterol_value_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_gemeenterol_value_state
    ADD CONSTRAINT fk_project_gemeenterol_value_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_maatwerk_boolean_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_maatwerk_boolean_changelog RENAME CONSTRAINT fk_project_maatwerk_boolean_changelog__change_user TO fk_project_maatwerk_boolean_changelog__create_user;
ALTER TABLE diwi_testset.project_maatwerk_boolean_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_maatwerk_categorie_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_maatwerk_categorie_changelog RENAME CONSTRAINT fk_project_maatwerk_categorie_changelog__change_user TO fk_project_maatwerk_categorie_changelog__create_user;
ALTER TABLE diwi_testset.project_maatwerk_categorie_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_maatwerk_numeriek_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_maatwerk_numeriek_changelog RENAME CONSTRAINT fk_project_maatwerk_numeriek_changelog__change_user TO fk_project_maatwerk_numeriek_changelog__create_user;
ALTER TABLE diwi_testset.project_maatwerk_numeriek_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_maatwerk_ordinaal_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_maatwerk_ordinaal_changelog RENAME CONSTRAINT fk_project_maatwerk_ordinaal_changelog__change_user TO fk_project_maatwerk_ordinaal_changelog__create_user;
ALTER TABLE diwi_testset.project_maatwerk_ordinaal_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_name_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_name_changelog RENAME CONSTRAINT fk_project_name_changelog__change_user TO fk_project_name_changelog__create_user;
ALTER TABLE diwi_testset.project_name_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_plan_type_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_plan_type_changelog RENAME CONSTRAINT fk_project_plan_type_changelog__change_user TO fk_project_plan_type_changelog__create_user;
ALTER TABLE diwi_testset.project_plan_type_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_planologische_planstatus_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_planologische_planstatus_changelog RENAME CONSTRAINT fk_project_planologische_planstatus_changelog__change_user TO fk_project_planologische_planstatus_changelog__create_user;
ALTER TABLE diwi_testset.project_planologische_planstatus_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_priorisering_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_priorisering_changelog RENAME CONSTRAINT fk_project_priorisering_changelog__change_user TO fk_project_priorisering_changelog__create_user;
ALTER TABLE diwi_testset.project_priorisering_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_priorisering_changelog
    ADD CONSTRAINT fk_project_priorisering_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_priorisering_value_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_priorisering_value_state RENAME CONSTRAINT fk_project_priorisering_value_state__user TO fk_project_priorisering_value_state__create_user;
ALTER TABLE diwi_testset.project_priorisering_value_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_priorisering_value_state
    ADD CONSTRAINT fk_project_priorisering_value_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.project_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.project_state RENAME CONSTRAINT fk_project_state__change_user TO fk_project_state__create_user;
ALTER TABLE diwi_testset.project_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.project_state
    ADD CONSTRAINT fk_project_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.software_module_rights RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.software_module_rights RENAME CONSTRAINT fk_software_module_rights__change_user TO fk_software_module_rights__create_user;
ALTER TABLE diwi_testset.software_module_rights ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.user_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.user_state RENAME CONSTRAINT fk_user_state__change_user TO fk_user_state__create_user;
ALTER TABLE diwi_testset.user_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.user_state
    ADD CONSTRAINT fk_user_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.user_to_organization RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.user_to_organization RENAME CONSTRAINT fk_user_to_organization__change_user TO fk_user_to_organization__create_user;
ALTER TABLE diwi_testset.user_to_organization ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.user_to_organization
    ADD CONSTRAINT fk_user_to_organization__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.wijk_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.wijk_state RENAME CONSTRAINT fk_wijk_state__change_user TO fk_wijk_state__create_user;
ALTER TABLE diwi_testset.wijk_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.wijk_state
    ADD CONSTRAINT fk_wijk_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_doelgroep_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_doelgroep_changelog RENAME CONSTRAINT fk_woningblok_doelgroep_changelog__change_user TO fk_woningblok_doelgroep_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_doelgroep_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_duration_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_duration_changelog RENAME CONSTRAINT fk_woningblok_duration_changelog__change_user TO fk_woningblok_duration_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_duration_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog RENAME CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__change_user TO fk_woningblok_eigendom_en_waarde_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_gemeente_indeling_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_gemeente_indeling_changelog
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE diwi_testset.woningblok_gemeente_indeling_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_gemeente_indeling_changelog
    ADD CONSTRAINT fk_woningblok_gemeente_indeling_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_grondpositie_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_grondpositie_changelog RENAME CONSTRAINT fk_woningblok_grondpositie_changelog__change_user TO fk_woningblok_grondpositie_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_grondpositie_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_grootte_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_grootte_changelog RENAME CONSTRAINT fk_woningblok_grootte_changelog__change_user TO fk_woningblok_grootte_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_grootte_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog RENAME CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__change_user TO fk_woningblok_kadastrale_koppeling_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_kadastrale_koppeling_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_kadastrale_koppeling_changelog
    ADD CONSTRAINT fk_woningblok_kadastrale_koppeling_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_maatwerk_boolean_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_maatwerk_boolean_changelog RENAME CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__change_user TO fk_woningblok_maatwerk_boolean_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_maatwerk_boolean_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_maatwerk_categorie_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_maatwerk_categorie_changelog RENAME CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__change_user TO fk_woningblok_maatwerk_categorie_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_maatwerk_categorie_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_maatwerk_numeriek_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_maatwerk_numeriek_changelog RENAME CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__change_user TO fk_woningblok_maatwerk_numeriek_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_maatwerk_numeriek_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_maatwerk_ordinaal_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_maatwerk_ordinaal_changelog RENAME CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__change_user TO fk_woningblok_maatwerk_ordinaal_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_maatwerk_ordinaal_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_mutatie_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_mutatie_changelog RENAME CONSTRAINT fk_woningblok_mutatie_changelog__change_user TO fk_woningblok_mutatie_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_mutatie_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_naam_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_naam_changelog RENAME CONSTRAINT fk_woningblok_naam_changelog__change_user TO fk_woningblok_naam_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_naam_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_opleverdatum_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_opleverdatum_changelog RENAME CONSTRAINT fk_woningblok_opleverdatum_changelog__change_user TO fk_woningblok_opleverdatum_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_opleverdatum_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_opleverdatum_changelog
    ADD CONSTRAINT fk_woningblok_opleverdatum_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_programmering_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_programmering_changelog RENAME CONSTRAINT fk_woningblok_programmering_changelog__change_user TO fk_woningblok_programmering_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_programmering_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_state RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_state RENAME CONSTRAINT fk_woningblok_state__change_user TO fk_woningblok_state__create_user;
ALTER TABLE diwi_testset.woningblok_state ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog RENAME COLUMN change_user_id TO create_user_id;
ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog RENAME CONSTRAINT fk_woningblok_type_en_fysiek_changelog__change_user TO fk_woningblok_type_en_fysiek_changelog__create_user;
ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog ADD COLUMN change_user_id UUID;
ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi_testset."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

