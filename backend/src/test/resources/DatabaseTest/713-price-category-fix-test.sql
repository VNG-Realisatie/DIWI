CREATE SCHEMA diwi;
CREATE SCHEMA public;
CREATE COLLATION public.diwi_numeric (provider = icu, locale = 'en-u-kn-true');
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';
CREATE TYPE diwi.blueprint_element AS ENUM (
    'MUTATION',
    'PROJECT_PHASE',
    'TARGET_GROUP',
    'PHYSICAL_APPEARANCE',
    'OWNERSHIP_BUY',
    'OWNERSHIP_RENT',
    'PROJECT_MAP',
    'RESIDENTIAL_PROJECTS',
    'DELIVERABLES',
    'DELAYED_PROJECTS'
);
CREATE TYPE diwi.conditie_type AS ENUM (
    'PLAN_CONDITIE',
    'DOEL_CONDITIE'
);
CREATE TYPE diwi.confidentiality AS ENUM (
    'PRIVATE',
    'INTERNAL_CIVIL',
    'INTERNAL_MANAGEMENT',
    'INTERNAL_COUNCIL',
    'EXTERNAL_REGIONAL',
    'EXTERNAL_GOVERNMENTAL',
    'PUBLIC'
);
CREATE TYPE diwi.doel_richting AS ENUM (
    'MINIMAAL',
    'MAXIMAAL'
);
CREATE TYPE diwi.doel_soort AS ENUM (
    'AANTAL',
    'PERCENTAGE'
);
CREATE TYPE diwi.doelgroep AS ENUM (
    'REGULIER',
    'JONGEREN',
    'STUDENTEN',
    'OUDEREN',
    'GEHANDICAPTEN_EN_ZORG',
    'GROTE_GEZINNEN'
);
CREATE TYPE diwi.eigendom_soort AS ENUM (
    'KOOPWONING',
    'HUURWONING_PARTICULIERE_VERHUURDER',
    'HUURWONING_WONINGCORPORATIE'
);
CREATE TYPE diwi.fysiek_voorkomen AS ENUM (
    'TUSSENWONING',
    'HOEKWONING',
    'TWEE_ONDER_EEN_KAP',
    'VRIJSTAAND',
    'PORTIEKFLAT',
    'GALLERIJFLAT'
);
CREATE TYPE diwi.grondpositie AS ENUM (
    'FORMELE_TOESTEMMING_GRONDEIGENAAR',
    'INTENTIE_MEDEWERKING_GRONDEIGENAAR',
    'GEEN_TOESTEMMING_GRONDEIGENAAR'
);
CREATE TYPE diwi.maatwerk_eigenschap_type AS ENUM (
    'BOOLEAN',
    'CATEGORY',
    'ORDINAL',
    'NUMERIC',
    'TEXT',
    'RANGE_CATEGORY'
);
CREATE TYPE diwi.maatwerk_object_soort AS ENUM (
    'PROJECT',
    'WONINGBLOK'
);
CREATE TYPE diwi.milestone_status AS ENUM (
    'VOORSPELD',
    'GEPLAND',
    'GEREALISEERD',
    'AFGEBROKEN'
);
CREATE TYPE diwi.mutation_kind AS ENUM (
    'CONSTRUCTION',
    'DEMOLITION'
);
CREATE TYPE diwi.plan_type AS ENUM (
    'PAND_TRANSFORMATIE',
    'TRANSFORMATIEGEBIED',
    'HERSTRUCTURERING',
    'VERDICHTING',
    'UITBREIDING_UITLEG',
    'UITBREIDING_OVERIG'
);
CREATE TYPE diwi.planologische_planstatus AS ENUM (
    '_1A_ONHERROEPELIJK',
    '_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG',
    '_1C_ONHERROEPELIJK_MET_BW_NODIG',
    '_2A_VASTGESTELD',
    '_2B_VASTGESTELD_MET_UITWERKING_NODIG',
    '_2C_VASTGESTELD_MET_BW_NODIG',
    '_3_IN_VOORBEREIDING',
    '_4A_OPGENOMEN_IN_VISIE',
    '_4B_NIET_OPGENOMEN_IN_VISIE'
);
CREATE TYPE diwi.project_phase AS ENUM (
    '_1_CONCEPT',
    '_2_INITIATIVE',
    '_3_DEFINITION',
    '_4_DESIGN',
    '_5_PREPARATION',
    '_6_REALIZATION',
    '_7_AFTERCARE'
);
CREATE TYPE diwi.property_type AS ENUM (
    'FIXED',
    'CUSTOM'
);
CREATE TYPE diwi.software_module AS ENUM (
    'ADMIN_PANEL',
    'BEHEER_PORTAAL',
    'DASHBOARD_PUBLIEK',
    'DASHBOARD_PROVINCIALE_PLANMONITOR',
    'DASHBOARD_GEMEENTERAAD',
    'DASHBOARD_INTERNE_UITVOERING'
);
CREATE TYPE diwi.software_rights AS ENUM (
    'ADMIN',
    'CRUD_TO_OWN',
    'CRUD_TO_ALL',
    'VIEW_ONLY'
);
CREATE TYPE diwi.user_role AS ENUM (
    'Admin',
    'UserPlus',
    'User',
    'Management',
    'Council',
    'External'
);
CREATE TYPE diwi.value_type AS ENUM (
    'SINGLE_VALUE',
    'RANGE'
);
CREATE TYPE diwi.woning_type AS ENUM (
    'EENGEZINSWONING',
    'MEERGEZINSWONING'
);
CREATE FUNCTION diwi.create_demo_user_org(id uuid, system_user_id uuid, first_name text, last_name text) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO diwi_testset."user"(id)
        VALUES(id);
    INSERT INTO diwi_testset."user_state"(id, user_id, create_user_id, change_start_date, identity_provider_id, "first_name", "last_name")
        VALUES(id, id, system_user_id, now(), '', first_name, last_name);
    INSERT INTO diwi_testset."organization"(id)
        VALUES(id);
    INSERT INTO diwi_testset."organization_state"(id, organization_id, create_user_id, change_start_date, naam)
        VALUES(id, id, system_user_id, now(), first_name || ' ' || last_name);
    INSERT INTO diwi_testset."user_to_organization"(id, organization_id, user_id, create_user_id, change_start_date)
        VALUES(id, id, id, system_user_id, now());
END;
$$;
CREATE FUNCTION diwi.get_active_and_future_projects_list(_now_ date, _offset_ integer, _limit_ integer, _sortcolumn_ text, _sortdirection_ text, _filtercolumn_ text, _filtervalues_ text[], _filtercondition_ text, _user_role_ text, _user_uuid_ uuid) RETURNS TABLE(projectid uuid, projectstateid uuid, projectname text, projectownersarray text[], projectcolor text, latitude double precision, longitude double precision, confidentialitylevel diwi.confidentiality, startdate date, enddate date, plantype text[], priority jsonb, projectphase diwi.project_phase, planningplanstatus text[], municipalityrole jsonb, totalvalue bigint, municipality jsonb, district jsonb, neighbourhood jsonb, geometry text)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT  q.projectId,
        q.projectStateId,
        q.projectName,
        q.projectOwners            AS projectOwnersArray,
        q.projectColor,
        q.latitude,
        q.longitude,
        q.confidentialityLevel,
        q.startDate,
        q.endDate,
        q.planType,
        q.priorityList            AS priority,
        q.projectPhase,
        q.planningPlanStatus,
        q.municipalityRoleList    AS municipalityRole,
        q.totalValue,
        q.municipalityList               AS municipality,
        q.districtList             AS district,
        q.neighbourhoodList        AS neighbourhood,
        null                       AS geometry
FROM (
    WITH
        active_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date
        ),
        active_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                diwi.project_name_changelog pnc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pnc.change_end_date IS NULL
        ),
        active_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                diwi.project_fase_changelog pfc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pfc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pfc.change_end_date IS NULL
        ),
        active_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                diwi.project_plan_type_changelog pptc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pptc.change_end_date IS NULL
            GROUP BY pptc.project_id
        ),
        active_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                diwi.project_planologische_planstatus_changelog pppc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pppc.change_end_date IS NULL
            GROUP BY pppc.project_id
        ),
        active_project_woningblok_totalvalue AS (
            SELECT
                w.project_id,
                SUM(wmc.amount *
                    CASE wmc.mutation_kind
                        WHEN 'CONSTRUCTION' THEN 1
                        WHEN 'DEMOLITION' THEN -1
                    END) AS total_value
            FROM
                diwi.woningblok_mutatie_changelog wmc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.woningblok w ON wmc.woningblok_id = w.id
                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        active_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label) ORDER BY pcvs.value_label ASC)) AS fixedPropValuesList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS fixedPropValuesNamesList
            FROM
                diwi.project_category_changelog pcc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pcc.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        active_project_ordinal_fixed_props AS (
            SELECT
                ppc.project_id, ps.property_name AS fixedPropertyName,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                    END AS ordinalValuesNamesList,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                    END AS ordinalValuesList
            FROM
                diwi.project_ordinal_changelog ppc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = ppc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vs
                              ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMin
                              ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMax
                              ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND ppc.change_end_date IS NULL
            GROUP BY ppc.project_id, ps.property_name, ppc.value_type
        ),
        future_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate, sms.milestone_id AS start_milestone_id
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date > _now_
        ),
        future_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                future_projects fp
                    JOIN diwi.project_name_changelog pnc ON fp.id = pnc.project_id
                        AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
        ),
        future_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                future_projects fp
                    JOIN diwi.project_fase_changelog pfc ON fp.id = pfc.project_id
                        AND pfc.start_milestone_id = fp.start_milestone_id AND pfc.change_end_date IS NULL
        ),
        future_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                future_projects fp
                    JOIN diwi.project_plan_type_changelog pptc ON fp.id = pptc.project_id
                        AND pptc.start_milestone_id = fp.start_milestone_id AND pptc.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            GROUP BY pptc.project_id
        ),
        future_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                future_projects fp
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON fp.id = pppc.project_id
                        AND pppc.start_milestone_id = fp.start_milestone_id AND pppc.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            GROUP BY  pppc.project_id
        ),
        future_project_woningblok_totalvalue AS (
            SELECT
                w.project_id,
                SUM(wmc.amount *
                    CASE wmc.mutation_kind
                        WHEN 'CONSTRUCTION' THEN 1
                        WHEN 'DEMOLITION' THEN -1
                    END) AS total_value
            FROM
                future_projects fp
                    JOIN diwi.woningblok w ON fp.id = w.project_id
                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                        AND wmc.start_milestone_id = fp.start_milestone_id AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        future_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label)  ORDER BY pcvs.value_label ASC)) AS fixedPropValuesList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS fixedPropValuesNamesList
            FROM
                future_projects fp
                    JOIN diwi.project_category_changelog pcc ON fp.id = pcc.project_id
                        AND pcc.start_milestone_id = fp.start_milestone_id AND pcc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        future_project_ordinal_fixed_props AS (
            SELECT
                ppc.project_id, ps.property_name AS fixedPropertyName,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                    END AS ordinalValuesNamesList,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                    END AS ordinalValuesList
            FROM
                future_projects fp
                    JOIN diwi.project_ordinal_changelog ppc ON fp.id = ppc.project_id
                        AND ppc.start_milestone_id = fp.start_milestone_id AND ppc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vs
                              ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMin
                              ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMax
                              ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
            GROUP BY ppc.project_id, ps.property_name, ppc.value_type
        ),
        past_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                ems.date <= _now_
        ),
        past_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                past_projects pp
                    JOIN diwi.project_name_changelog pnc ON pp.id = pnc.project_id
                        AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
        ),
        past_project_fases AS (
            SELECT
                 pfc.project_id, pfc.project_fase
            FROM
                past_projects pp
                    JOIN diwi.project_fase_changelog pfc ON pp.id = pfc.project_id
                        AND pfc.end_milestone_id = pp.end_milestone_id AND pfc.change_end_date IS NULL
        ),
        past_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                past_projects pp
                    JOIN diwi.project_plan_type_changelog pptc ON pp.id = pptc.project_id
                        AND pptc.end_milestone_id = pp.end_milestone_id AND pptc.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            GROUP BY pptc.project_id
        ),
        past_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                past_projects pp
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON pp.id = pppc.project_id
                        AND pppc.end_milestone_id = pp.end_milestone_id AND pppc.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            GROUP BY pppc.project_id
        ),
        past_project_woningblok_totalvalue AS (
            SELECT
                w.project_id,
                SUM(wmc.amount *
                    CASE wmc.mutation_kind
                        WHEN 'CONSTRUCTION' THEN 1
                        WHEN 'DEMOLITION' THEN -1
                    END) AS total_value
            FROM
                past_projects pp
                    JOIN diwi.woningblok w ON pp.id = w.project_id
                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                        AND wmc.end_milestone_id = pp.end_milestone_id AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        past_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label)  ORDER BY pcvs.value_label ASC)) AS fixedPropValuesList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS fixedPropValuesNamesList
            FROM
                past_projects pp
                    JOIN diwi.project_category_changelog pcc ON pp.id = pcc.project_id
                    AND pcc.end_milestone_id = pp.end_milestone_id AND pcc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        past_project_ordinal_fixed_props AS (
            SELECT
                ppc.project_id, ps.property_name AS fixedPropertyName,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                    END AS ordinalValuesNamesList,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                    END AS ordinalValuesList
            FROM
                past_projects pp
                    JOIN diwi.project_ordinal_changelog ppc ON pp.id = ppc.project_id
                        AND ppc.end_milestone_id = pp.end_milestone_id AND ppc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vs
                              ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMin
                              ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMax
                              ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
            GROUP BY ppc.project_id, ps.property_name, ppc.value_type
        ),
        project_users AS (
            SELECT
                q.project_id    AS project_id,
                array_agg(array[q.usergroup_id::TEXT, q.usergroup_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users,
                array_agg(q.user_initials ORDER BY q.user_initials)      AS users_initials
            FROM (
                SELECT DISTINCT
                    ps.project_id as project_id,
                    us.user_id AS user_id,
                    LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                    us.last_name AS user_last_name,
                    us.first_name AS user_first_name,
                    ugs.usergroup_id AS usergroup_id,
                    ugs.naam AS usergroup_name
                FROM diwi.project_state ps
                    JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                    JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                    LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id AND utug.change_end_date IS NULL
                    LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                WHERE
                    ps.change_end_date IS NULL
                ) AS q
            GROUP BY q.project_id
        )
    SELECT ap.id                    AS projectId,
           ps.id                    AS projectStateId,
           apn.name                 AS projectName,
           ps.project_colour        AS projectColor,
           ps.latitude              AS latitude,
           ps.longitude             AS longitude,
           ps.confidentiality_level AS confidentialityLevel,
           owners.users                  AS projectOwners,
           owners.users_initials         AS projectOwnersInitials,
           ap.startDate             AS startDate,
           to_char( ap.startDate, 'YYYY-MM-DD') AS startDateStr,
           ap.endDate               AS endDate,
           to_char( ap.endDate, 'YYYY-MM-DD') AS endDateStr,
           appt.plan_types          AS planType,
           apop.ordinalValuesNamesList   AS priorityNamesList,
           apop.ordinalValuesList   AS priorityList,
           apf.project_fase         AS projectPhase,
           appp.planning_planstatus AS planningPlanStatus,
           apmr.fixedPropValuesList         AS municipalityRoleList,
           apmr.fixedPropValuesNamesList    AS municipalityRoleNamesList,
           apwv.total_value         AS totalValue,
           apr.fixedPropValuesList         AS municipalityList,
           apr.fixedPropValuesNamesList    AS municipalityNamesList,
           apd.fixedPropValuesList         AS districtList,
           apd.fixedPropValuesNamesList    AS districtNamesList,
           apne.fixedPropValuesList         AS neighbourhoodList,
           apne.fixedPropValuesNamesList   AS neighbourhoodNamesList
    FROM
        active_projects ap
            LEFT JOIN diwi.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
            LEFT JOIN active_project_names apn ON apn.project_id = ap.id
            LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
            LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
            LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
            LEFT JOIN active_project_woningblok_totalvalue apwv ON apwv.project_id = ap.id
            LEFT JOIN active_project_ordinal_fixed_props apop ON apop.project_id = ap.id AND apop.fixedPropertyName = 'priority'
            LEFT JOIN active_project_fixed_props apmr ON apmr.project_id = ap.id AND apmr.fixedPropertyName = 'municipalityRole'
            LEFT JOIN active_project_fixed_props apr ON apr.project_id = ap.id AND apr.fixedPropertyName = 'municipality'
            LEFT JOIN active_project_fixed_props apd ON apd.project_id = ap.id AND apd.fixedPropertyName = 'district'
            LEFT JOIN active_project_fixed_props apne ON apne.project_id = ap.id AND apne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id
    UNION
    SELECT fp.id                    AS projectId,
           ps.id                    AS projectStateId,
           fpn.name                 AS projectName,
           ps.project_colour        AS projectColor,
           ps.latitude              AS latitude,
           ps.longitude             AS longitude,
           ps.confidentiality_level AS confidentialityLevel,
           owners.users                  AS projectOwners,
           owners.users_initials         AS projectOwnersInitials,
           fp.startDate             AS startDate,
           to_char( fp.startDate, 'YYYY-MM-DD') AS startDateStr,
           fp.endDate               AS endDate,
           to_char( fp.endDate, 'YYYY-MM-DD') AS endDateStr,
           fppt.plan_types          AS planType,
           fpop.ordinalValuesNamesList   AS priorityNamesList,
           fpop.ordinalValuesList   AS priorityList,
           fpf.project_fase         AS projectPhase,
           fppp.planning_planstatus AS planningPlanStatus,
           fpmr.fixedPropValuesList         AS municipalityRoleList,
           fpmr.fixedPropValuesNamesList    AS municipalityRoleNamesList,
           fpwv.total_value         AS totalValue,
           fpr.fixedPropValuesList         AS municipalityList,
           fpr.fixedPropValuesNamesList    AS municipalityNamesList,
           fpd.fixedPropValuesList         AS districtList,
           fpd.fixedPropValuesNamesList    AS districtNamesList,
           fpne.fixedPropValuesList         AS neighbourhoodList,
           fpne.fixedPropValuesNamesList   AS neighbourhoodNamesList
    FROM
        future_projects fp
            LEFT JOIN diwi.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
            LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
            LEFT JOIN future_project_plan_types fppt ON fppt.project_id = fp.id
            LEFT JOIN future_project_fases fpf ON fpf.project_id = fp.id
            LEFT JOIN future_project_planologische_planstatus fppp ON fppp.project_id = fp.id
            LEFT JOIN future_project_woningblok_totalvalue fpwv ON fpwv.project_id = fp.id
            LEFT JOIN future_project_ordinal_fixed_props fpop ON fpop.project_id = fp.id AND fpop.fixedPropertyName = 'priority'
            LEFT JOIN future_project_fixed_props fpmr ON fpmr.project_id = fp.id AND fpmr.fixedPropertyName = 'municipalityRole'
            LEFT JOIN future_project_fixed_props fpr ON fpr.project_id = fp.id AND fpr.fixedPropertyName = 'municipality'
            LEFT JOIN future_project_fixed_props fpd ON fpd.project_id = fp.id AND fpd.fixedPropertyName = 'district'
            LEFT JOIN future_project_fixed_props fpne ON fpne.project_id = fp.id AND fpne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id
    UNION
    SELECT pp.id                    AS projectId,
           ps.id                    AS projectStateId,
           ppn.name                 AS projectName,
           ps.project_colour        AS projectColor,
           ps.latitude              AS latitude,
           ps.longitude             AS longitude,
           ps.confidentiality_level AS confidentialityLevel,
           owners.users                  AS projectOwners,
           owners.users_initials         AS projectOwnersInitials,
           pp.startDate             AS startDate,
           to_char( pp.startDate, 'YYYY-MM-DD') AS startDateStr,
           pp.endDate               AS endDate,
           to_char( pp.endDate, 'YYYY-MM-DD') AS endDateStr,
           pppt.plan_types          AS planType,
           ppop.ordinalValuesNamesList   AS priorityNamesList,
           ppop.ordinalValuesList   AS priorityList,
           ppf.project_fase         AS projectPhase,
           pppp.planning_planstatus AS planningPlanStatus,
           ppmr.fixedPropValuesList         AS municipalityRoleList,
           ppmr.fixedPropValuesNamesList    AS municipalityRoleNamesList,
           ppwv.total_value         AS totalValue,
           ppr.fixedPropValuesList         AS municipalityList,
           ppr.fixedPropValuesNamesList    AS municipalityNamesList,
           ppd.fixedPropValuesList         AS districtList,
           ppd.fixedPropValuesNamesList    AS districtNamesList,
           ppne.fixedPropValuesList         AS neighbourhoodList,
           ppne.fixedPropValuesNamesList   AS neighbourhoodNamesList
    FROM
        past_projects pp
            LEFT JOIN diwi.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
            LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
            LEFT JOIN past_project_plan_types pppt ON pppt.project_id = pp.id
            LEFT JOIN past_project_fases ppf ON ppf.project_id = pp.id
            LEFT JOIN past_project_planologische_planstatus pppp ON pppp.project_id = pp.id
            LEFT JOIN past_project_woningblok_totalvalue ppwv ON ppwv.project_id = pp.id
            LEFT JOIN past_project_ordinal_fixed_props ppop ON ppop.project_id = pp.id AND ppop.fixedPropertyName = 'priority'
            LEFT JOIN past_project_fixed_props ppmr ON ppmr.project_id = pp.id AND ppmr.fixedPropertyName = 'municipalityRole'
            LEFT JOIN past_project_fixed_props ppr ON ppr.project_id = pp.id AND ppr.fixedPropertyName = 'municipality'
            LEFT JOIN past_project_fixed_props ppd ON ppd.project_id = pp.id AND ppd.fixedPropertyName = 'district'
            LEFT JOIN past_project_fixed_props ppne ON ppne.project_id = pp.id AND ppne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id
) AS q
  WHERE
    (
      ( _user_uuid_::TEXT IN (select owners.id from unnest(q.projectOwners) with ordinality owners(id,n) where owners.n % 6 = 3)) OR
      ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
      ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
      ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
    )
    AND
        CASE
            WHEN _filterCondition_ = 'CONTAINS' AND _filterColumn_  = 'projectName' THEN q.projectName ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'CONTAINS' AND  _filterColumn_  = 'startDate' THEN q.startDateStr ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'CONTAINS' AND  _filterColumn_  = 'endDate' THEN q.endDateStr ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'ANY_OF' AND  _filterColumn_  = 'confidentialityLevel' THEN q.confidentialityLevel = ANY(_filterValues_::diwi.confidentiality[])
            WHEN _filterCondition_ = 'ANY_OF' AND  _filterColumn_  = 'projectPhase' THEN q.projectPhase = ANY(_filterValues_::diwi.project_phase[])
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'planType' THEN q.planType && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'priority' THEN q.priorityNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'planningPlanStatus' THEN q.planningPlanStatus && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'municipalityRole' THEN q.municipalityRoleNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'municipality' THEN q.municipalityNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'district' THEN q.districtNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'neighbourhood' THEN q.neighbourhoodNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'projectOwners' THEN q.projectOwnersInitials && _filterValues_
            WHEN _filterColumn_ IS NULL THEN 1 = 1
        END
    ORDER BY
        CASE WHEN _sortColumn_ = 'projectName' AND _sortDirection_ = 'ASC' THEN q.projectName END ASC,
        CASE WHEN _sortColumn_ = 'totalValue' AND _sortDirection_ = 'ASC' THEN q.totalValue END ASC,
        CASE WHEN _sortColumn_ = 'endDate' AND _sortDirection_ = 'ASC' THEN q.endDate END ASC,
        CASE WHEN _sortColumn_ = 'startDate' AND _sortDirection_ = 'ASC' THEN q.startDate END ASC,
        CASE WHEN _sortColumn_ = 'confidentialityLevel' AND _sortDirection_ = 'ASC' THEN q.confidentialityLevel END ASC,
        CASE WHEN _sortColumn_ = 'projectPhase' AND _sortDirection_ = 'ASC' THEN q.projectPhase END ASC,
        CASE WHEN _sortColumn_ = 'planType' AND _sortDirection_ = 'ASC' THEN q.planType END ASC,
        CASE WHEN _sortColumn_ = 'priority' AND _sortDirection_ = 'ASC' THEN q.priorityNamesList COLLATE "diwi_numeric" END ASC,
        CASE WHEN _sortColumn_ = 'planningPlanStatus' AND _sortDirection_ = 'ASC' THEN q.planningPlanStatus END ASC,
        CASE WHEN _sortColumn_ = 'municipalityRole' AND _sortDirection_ = 'ASC' THEN q.municipalityRoleNamesList END ASC,
        CASE WHEN _sortColumn_ = 'municipality' AND _sortDirection_ = 'ASC' THEN q.municipalityNamesList END ASC,
        CASE WHEN _sortColumn_ = 'district' AND _sortDirection_ = 'ASC' THEN q.districtNamesList END ASC,
        CASE WHEN _sortColumn_ = 'neighbourhood' AND _sortDirection_ = 'ASC' THEN q.neighbourhoodNamesList END ASC,
        CASE WHEN _sortColumn_ = 'projectOwners' AND _sortDirection_ = 'ASC' THEN q.projectOwnersInitials END ASC,
        CASE WHEN _sortColumn_ = 'projectName' AND _sortDirection_ = 'DESC' THEN q.projectName END DESC,
        CASE WHEN _sortColumn_ = 'totalValue' AND _sortDirection_ = 'DESC' THEN q.totalValue END DESC,
        CASE WHEN _sortColumn_ = 'endDate' AND _sortDirection_ = 'DESC' THEN q.endDate END DESC,
        CASE WHEN _sortColumn_ = 'startDate' AND _sortDirection_ = 'DESC' THEN q.startDate END DESC,
        CASE WHEN _sortColumn_ = 'confidentialityLevel' AND _sortDirection_ = 'DESC' THEN q.confidentialityLevel END DESC,
        CASE WHEN _sortColumn_ = 'projectPhase' AND _sortDirection_ = 'DESC' THEN q.projectPhase END DESC,
        CASE WHEN _sortColumn_ = 'planType' AND _sortDirection_ = 'DESC' THEN q.planType END DESC,
        CASE WHEN _sortColumn_ = 'priority' AND _sortDirection_ = 'DESC' THEN q.priorityNamesList COLLATE "diwi_numeric" END DESC,
        CASE WHEN _sortColumn_ = 'planningPlanStatus' AND _sortDirection_ = 'DESC' THEN q.planningPlanStatus END DESC,
        CASE WHEN _sortColumn_ = 'municipalityRole' AND _sortDirection_ = 'DESC' THEN q.municipalityRoleNamesList END DESC,
        CASE WHEN _sortColumn_ = 'municipality' AND _sortDirection_ = 'DESC' THEN q.municipalityNamesList END DESC,
        CASE WHEN _sortColumn_ = 'district' AND _sortDirection_ = 'DESC' THEN q.districtNamesList END DESC,
        CASE WHEN _sortColumn_ = 'neighbourhood' AND _sortDirection_ = 'DESC' THEN q.neighbourhoodNamesList END DESC,
        CASE WHEN _sortColumn_ = 'projectOwners' AND _sortDirection_ = 'DESC' THEN q.projectOwnersInitials END DESC
    LIMIT _limit_ OFFSET _offset_;
END;$$;
CREATE FUNCTION diwi.get_active_or_future_project_custom_properties(_project_uuid_ uuid, _now_ date) RETURNS TABLE(custompropertyid uuid, booleanvalue boolean, numericvalue double precision, numericvaluerange numrange, numericvaluetype diwi.value_type, textvalue text, categories uuid[], ordinalvalueid uuid, ordinalminvalueid uuid, ordinalmaxvalueid uuid, propertytype diwi.maatwerk_eigenschap_type)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT
    q.customPropertyId,
    q.booleanValue,
    q.numericValue,
    q.numericValueRange,
    q.numericValueType,
    q.textValue,
    q.categories,
    q.ordinalValueId,
    q.ordinalMinValueId,
    q.ordinalMaxValueId,
    q.propertyType
FROM (
         WITH
             active_projects AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi.project p
                         JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  sms.date <= _now_ AND _now_ < ems.date AND p.id = _project_uuid_
             ),
             active_projects_booleanCP AS (
                 SELECT
                     ap.id, pbc.eigenschap_id, pbc.value
                 FROM
                     active_projects ap
                         JOIN diwi.project_maatwerk_boolean_changelog pbc ON ap.id = pbc.project_id AND pbc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pbc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pbc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_projects_numericCP AS (
                 SELECT
                     ap.id, pnc.eigenschap_id, pnc.value, pnc.value_range, pnc.value_type
                 FROM
                     active_projects ap
                         JOIN diwi.project_maatwerk_numeriek_changelog pnc ON ap.id = pnc.project_id AND pnc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_projects_textCP AS (
                 SELECT
                     ap.id, ptc.property_id, ptc.value
                 FROM
                     active_projects ap
                         JOIN diwi.project_text_changelog ptc ON ap.id = ptc.project_id AND ptc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = ptc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = ptc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_projects_categoriesCP AS (
                 SELECT
                     ap.id, pcc.property_id, array_agg(pccv.property_value_id) AS categories
                 FROM
                     active_projects ap
                         JOIN diwi.project_category_changelog pcc ON ap.id = pcc.project_id AND pcc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY ap.id, pcc.property_id
             ),
             active_projects_ordinalCP AS (
                 SELECT
                     ap.id, poc.property_id, poc.value_id AS ordinal_value_id, poc.min_value_id AS ordinal_min_value_id, poc.max_value_id AS ordinal_max_value_id
                 FROM
                     active_projects ap
                         JOIN diwi.project_ordinal_changelog poc ON ap.id = poc.project_id AND poc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = poc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = poc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             future_projects AS (
                 SELECT
                     p.id, sms.milestone_id AS start_milestone_id
                 FROM
                     diwi.project p
                         JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE sms.date > _now_  AND p.id = _project_uuid_
             ),
             future_projects_numericCP AS (
                 SELECT
                     fp.id, pnc.eigenschap_id, pnc.value, pnc.value_range, pnc.value_type
                 FROM
                     future_projects fp
                         JOIN diwi.project_maatwerk_numeriek_changelog pnc ON fp.id = pnc.project_id
                            AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
             ),
             future_projects_booleanCP AS (
                 SELECT
                     fp.id, pbc.eigenschap_id, pbc.value
                 FROM
                     future_projects fp
                         JOIN diwi.project_maatwerk_boolean_changelog pbc ON fp.id = pbc.project_id
                         AND pbc.start_milestone_id = fp.start_milestone_id AND pbc.change_end_date IS NULL
             ),
             future_projects_textCP AS (
                 SELECT
                     fp.id, ptc.property_id, ptc.value
                 FROM
                     future_projects fp
                         JOIN diwi.project_text_changelog ptc ON fp.id = ptc.project_id
                         AND ptc.start_milestone_id = fp.start_milestone_id AND ptc.change_end_date IS NULL
             ),
             future_projects_categoriesCP AS (
                 SELECT
                     fp.id, pcc.property_id, array_agg(pccv.property_value_id) AS categories
                 FROM
                     future_projects fp
                         JOIN diwi.project_category_changelog pcc ON fp.id = pcc.project_id
                            AND pcc.start_milestone_id = fp.start_milestone_id AND pcc.change_end_date IS NULL
                         LEFT JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                 GROUP BY fp.id, pcc.property_id
             ),
             future_projects_ordinalCP AS (
                 SELECT
                     fp.id, poc.property_id, poc.value_id AS ordinal_value_id, poc.min_value_id AS ordinal_min_value_id, poc.max_value_id AS ordinal_max_value_id
                 FROM
                     future_projects fp
                         JOIN diwi.project_ordinal_changelog poc ON fp.id = poc.project_id
                         AND poc.start_milestone_id = fp.start_milestone_id AND poc.change_end_date IS NULL
             ),
             past_projects AS (
                 SELECT
                     p.id, ems.milestone_id AS end_milestone_id
                 FROM
                     diwi.project p
                         JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE ems.date <= _now_ AND p.id = _project_uuid_
             ),
             past_projects_numericCP AS (
                 SELECT
                     pp.id, pnc.eigenschap_id, pnc.value, pnc.value_range, pnc.value_type
                 FROM
                     past_projects pp
                         JOIN diwi.project_maatwerk_numeriek_changelog pnc ON pp.id = pnc.project_id
                            AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
             ),
             past_projects_booleanCP AS (
                 SELECT
                     pp.id, pbc.eigenschap_id, pbc.value
                 FROM
                     past_projects pp
                         JOIN diwi.project_maatwerk_boolean_changelog pbc ON pp.id = pbc.project_id
                            AND pbc.end_milestone_id = pp.end_milestone_id AND pbc.change_end_date IS NULL
             ),
             past_projects_textCP AS (
                 SELECT
                     pp.id, ptc.property_id, ptc.value
                 FROM
                     past_projects pp
                         JOIN diwi.project_text_changelog ptc ON pp.id = ptc.project_id
                            AND ptc.end_milestone_id = pp.end_milestone_id AND ptc.change_end_date IS NULL
             ),
             past_projects_categoriesCP AS (
                 SELECT
                     pp.id, pcc.property_id, array_agg(pccv.property_value_id) AS categories
                 FROM
                     past_projects pp
                         JOIN diwi.project_category_changelog pcc ON pp.id = pcc.project_id
                            AND pcc.end_milestone_id = pp.end_milestone_id AND pcc.change_end_date IS NULL
                         LEFT JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                 GROUP BY pp.id, pcc.property_id
             ),
             past_projects_ordinalCP AS (
                 SELECT
                     pp.id, poc.property_id, poc.value_id AS ordinal_value_id, poc.min_value_id AS ordinal_min_value_id, poc.max_value_id AS ordinal_max_value_id
                 FROM
                     past_projects pp
                         JOIN diwi.project_ordinal_changelog poc ON pp.id = poc.project_id
                            AND poc.end_milestone_id = pp.end_milestone_id AND poc.change_end_date IS NULL
             )
         SELECT
             ap.id AS projectId,
             apb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_projects ap
                JOIN active_projects_booleanCP apb ON ap.id = apb.id
         UNION
         SELECT
             ap.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             apn.value AS numericValue,
             apn.value_range AS numericValueRange,
             apn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_projects ap
                JOIN active_projects_numericCP apn ON ap.id = apn.id
         UNION
         SELECT
             ap.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             apt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apt.property_id AS customPropertyId,
             'TEXT'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_projects ap
                JOIN active_projects_textCP apt ON ap.id = apt.id
         UNION
         SELECT
             ap.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             apc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apc.property_id AS customPropertyId,
             'CATEGORY'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_projects ap
             JOIN active_projects_categoriesCP apc ON ap.id = apc.id
         UNION
         SELECT
             ap.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             apo.ordinal_value_id AS ordinalValueId,
             apo.ordinal_min_value_id AS ordinalMinValueId,
             apo.ordinal_max_value_id AS ordinalMaxValueId,
             apo.property_id AS customPropertyId,
             'ORDINAL'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_projects ap
             JOIN active_projects_ordinalCP apo ON ap.id = apo.id
         UNION
         SELECT
             fp.id AS projectId,
             fpb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_projects fp
                  JOIN future_projects_booleanCP fpb ON fp.id = fpb.id
         UNION
         SELECT
             fp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             fpn.value AS numericValue,
             fpn.value_range AS numericValueRange,
             fpn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_projects fp
                  JOIN future_projects_numericCP fpn ON fp.id = fpn.id
         UNION
         SELECT
             fp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             fpt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpt.property_id AS customPropertyId,
             'TEXT'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_projects fp
                  JOIN future_projects_textCP fpt ON fp.id = fpt.id
         UNION
         SELECT
             fp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             fpc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpc.property_id AS customPropertyId,
             'CATEGORY'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_projects fp
             JOIN future_projects_categoriesCP fpc ON fp.id = fpc.id
         UNION
         SELECT
             fp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             fpo.property_id AS customPropertyId,
             fpo.ordinal_value_id AS ordinalValueId,
             fpo.ordinal_min_value_id AS ordinalMinValueId,
             fpo.ordinal_max_value_id AS ordinalMaxValueId,
             'ORDINAL'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_projects fp
             JOIN future_projects_ordinalCP fpo ON fp.id = fpo.id
         UNION
         SELECT
             pp.id AS projectId,
             ppb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             ppb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_projects pp
             JOIN past_projects_booleanCP ppb ON pp.id = ppb.id
         UNION
         SELECT
             pp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             ppn.value AS numericValue,
             ppn.value_range AS numericValueRange,
             ppn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             ppn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_projects pp
             JOIN past_projects_numericCP ppn ON pp.id = ppn.id
         UNION
         SELECT
             pp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             ppt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             ppt.property_id AS customPropertyId,
             'TEXT'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_projects pp
             JOIN past_projects_textCP ppt ON pp.id = ppt.id
         UNION
         SELECT
             pp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             ppc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             ppc.property_id AS customPropertyId,
             'CATEGORY'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_projects pp
             JOIN past_projects_categoriesCP ppc ON pp.id = ppc.id
         UNION
         SELECT
             pp.id AS projectId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             ppo.property_id AS customPropertyId,
             ppo.ordinal_value_id AS ordinalValueId,
             ppo.ordinal_min_value_id AS ordinalMinValueId,
             ppo.ordinal_max_value_id AS ordinalMaxValueId,
             'ORDINAL'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_projects pp
             JOIN past_projects_ordinalCP ppo ON pp.id = ppo.id
     ) AS q
        JOIN diwi.property_state cps ON cps.property_id = q.customPropertyId AND cps.change_end_date IS NULL
        JOIN diwi.property cp ON cps.property_id = cp.id AND cp.type = 'CUSTOM'
WHERE q.projectId = _project_uuid_;
END;$$;
CREATE FUNCTION diwi.get_active_or_future_project_snapshot(_project_uuid_ uuid, _now_ date, _user_role_ text, _user_uuid_ uuid) RETURNS TABLE(projectid uuid, projectstateid uuid, projectname text, projectownersarray text[], projectcolor text, latitude double precision, longitude double precision, confidentialitylevel diwi.confidentiality, startdate date, enddate date, plantype text[], priority jsonb, projectphase diwi.project_phase, planningplanstatus text[], municipalityrole jsonb, totalvalue bigint, municipality jsonb, district jsonb, neighbourhood jsonb, geometry text)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT  q.projectId,
        q.projectStateId,
        q.projectName,
        q.projectOwners            AS projectOwnersArray,
        q.projectColor,
        q.latitude,
        q.longitude,
        q.confidentialityLevel,
        q.startDate,
        q.endDate,
        q.planType,
        q.priorityList            AS priority,
        q.projectPhase,
        q.planningPlanStatus,
        q.municipalityRoleList    AS municipalityRole,
        q.totalValue,
        q.municipalityList               AS municipality,
        q.districtList             AS district,
        q.neighbourhoodList        AS neighbourhood,
        q.geometry                 AS geometry
FROM (
         WITH
             active_projects AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi.project p
                         JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND p.id = _project_uuid_
             ),
             active_project_names AS (
                 SELECT
                     pnc.project_id, pnc.name
                 FROM
                     diwi.project_name_changelog pnc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pnc.change_end_date IS NULL AND pnc.project_id = _project_uuid_
             ),
             active_project_fases AS (
                 SELECT
                     pfc.project_id, pfc.project_fase
                 FROM
                     diwi.project_fase_changelog pfc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pfc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pfc.change_end_date IS NULL AND pfc.project_id = _project_uuid_
             ),
             active_project_plan_types AS (
                 SELECT
                     pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
                 FROM
                     diwi.project_plan_type_changelog pptc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pptc.change_end_date IS NULL AND pptc.project_id = _project_uuid_
                 GROUP BY pptc.project_id
             ),
             active_project_planologische_planstatus AS (
                 SELECT
                     pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
                 FROM
                     diwi.project_planologische_planstatus_changelog pppc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pppc.change_end_date IS NULL AND pppc.project_id = _project_uuid_
                 GROUP BY pppc.project_id
             ),
             active_project_woningblok_totalvalue AS (
                 SELECT
                     w.project_id,
                     SUM(wmc.amount *
                         CASE wmc.mutation_kind
                             WHEN 'CONSTRUCTION' THEN 1
                             WHEN 'DEMOLITION' THEN -1
                        END) AS total_value
                 FROM
                     diwi.woningblok_mutatie_changelog wmc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.woningblok w ON wmc.woningblok_id = w.id
                         JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND wmc.change_end_date IS NULL AND w.project_id = _project_uuid_
                 GROUP BY w.project_id
             ),
             active_project_fixed_props AS (
                 SELECT
                     pcc.project_id, ps.property_name AS fixedPropertyName,
                     to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label))) AS fixedPropValuesList
                 FROM
                    diwi.project_category_changelog pcc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                         JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                         JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pcc.change_end_date IS NULL AND pcc.project_id = _project_uuid_
                 GROUP BY pcc.project_id, ps.property_name
             ),
             active_project_ordinal_fixed_props AS (
                 SELECT
                     ppc.project_id, ps.property_name AS fixedPropertyName,
                     CASE
                         WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                         WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                     array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                         END AS ordinalValuesList
                 FROM
                     diwi.project_ordinal_changelog ppc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = ppc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vs
                                   ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vsMin
                                   ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vsMax
                                   ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND ppc.change_end_date IS NULL
                 GROUP BY ppc.project_id, ps.property_name, ppc.value_type
             ),
             active_project_text_fixed_props AS (
                 SELECT
                     ppc.project_id, ps.property_name AS fixedPropertyName, ppc.value as fixedPropertyValue
                 FROM
                     diwi.project_text_changelog ppc
                         JOIN diwi.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = ppc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND ppc.change_end_date IS NULL
                 GROUP BY ppc.project_id, ps.property_name, ppc.value
             ),
             future_projects AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate, sms.milestone_id AS start_milestone_id
                 FROM
                     diwi.project p
                         JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND p.id = _project_uuid_
             ),
             future_project_names AS (
                 SELECT
                     pnc.project_id, pnc.name
                 FROM
                     future_projects fp
                        JOIN diwi.project_name_changelog pnc ON fp.id = pnc.project_id
                            AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
             ),
             future_project_fases AS (
                 SELECT
                     pfc.project_id, pfc.project_fase
                 FROM
                     future_projects fp
                        JOIN diwi.project_fase_changelog pfc ON fp.id = pfc.project_id
                            AND pfc.start_milestone_id = fp.start_milestone_id AND pfc.change_end_date IS NULL
             ),
             future_project_plan_types AS (
                 SELECT
                     pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
                 FROM
                     future_projects fp
                        JOIN diwi.project_plan_type_changelog pptc ON fp.id = pptc.project_id
                            AND pptc.start_milestone_id = fp.start_milestone_id AND pptc.change_end_date IS NULL
                        JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
                 GROUP BY pptc.project_id
             ),
             future_project_planologische_planstatus AS (
                 SELECT
                     pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
                 FROM
                     future_projects fp
                        JOIN diwi.project_planologische_planstatus_changelog pppc ON fp.id = pppc.project_id
                            AND pppc.start_milestone_id = fp.start_milestone_id AND pppc.change_end_date IS NULL
                        JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
                 GROUP BY  pppc.project_id
             ),
             future_project_woningblok_totalvalue AS (
                 SELECT
                     w.project_id,
                     SUM(wmc.amount *
                         CASE wmc.mutation_kind
                             WHEN 'CONSTRUCTION' THEN 1
                             WHEN 'DEMOLITION' THEN -1
                         END) AS total_value
                 FROM
                     future_projects fp
                        JOIN diwi.woningblok w ON fp.id = w.project_id
                        JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                        JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                            AND wmc.start_milestone_id = fp.start_milestone_id AND wmc.change_end_date IS NULL
                 GROUP BY w.project_id
             ),
             future_project_fixed_props AS (
                 SELECT
                     pcc.project_id, ps.property_name AS fixedPropertyName,
                     to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label))) AS fixedPropValuesList
                 FROM
                     future_projects fp
                         JOIN diwi.project_category_changelog pcc ON fp.id = pcc.project_id
                            AND pcc.start_milestone_id = fp.start_milestone_id AND pcc.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                         JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                         JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
                 GROUP BY pcc.project_id, ps.property_name
             ),
             future_project_ordinal_fixed_props AS (
                 SELECT
                     ppc.project_id, ps.property_name AS fixedPropertyName,
                     CASE
                         WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                         WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                     array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                         END AS ordinalValuesList
                 FROM
                     future_projects fp
                         JOIN diwi.project_ordinal_changelog ppc ON fp.id = ppc.project_id
                         AND ppc.start_milestone_id = fp.start_milestone_id AND ppc.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vs
                                   ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vsMin
                                   ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vsMax
                                   ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
                 GROUP BY ppc.project_id, ps.property_name, ppc.value_type
             ),
             future_project_text_fixed_props AS (
                 SELECT
                     ppc.project_id, ps.property_name AS fixedPropertyName, ppc.value as fixedPropertyValue
                 FROM
                     future_projects fp
                         JOIN diwi.project_text_changelog ppc ON fp.id = ppc.project_id
                            AND ppc.start_milestone_id = fp.start_milestone_id AND ppc.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                 GROUP BY ppc.project_id, ps.property_name, ppc.value
             ),
             past_projects AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
                 FROM
                     diwi.project p
                         JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     ems.date <= _now_ AND p.id = _project_uuid_
             ),
             past_project_names AS (
                 SELECT
                     pnc.project_id, pnc.name
                 FROM
                     past_projects pp
                         JOIN diwi.project_name_changelog pnc ON pp.id = pnc.project_id
                            AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
             ),
             past_project_fases AS (
                 SELECT
                     pfc.project_id, pfc.project_fase
                 FROM
                     past_projects pp
                         JOIN diwi.project_fase_changelog pfc ON pp.id = pfc.project_id
                            AND pfc.end_milestone_id = pp.end_milestone_id AND pfc.change_end_date IS NULL
             ),
             past_project_plan_types AS (
                 SELECT
                     pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
                 FROM
                     past_projects pp
                         JOIN diwi.project_plan_type_changelog pptc ON pp.id = pptc.project_id
                            AND pptc.end_milestone_id = pp.end_milestone_id AND pptc.change_end_date IS NULL
                         JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
                 GROUP BY pptc.project_id
             ),
             past_project_planologische_planstatus AS (
                 SELECT
                     pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
                 FROM
                     past_projects pp
                         JOIN diwi.project_planologische_planstatus_changelog pppc ON pp.id = pppc.project_id
                            AND pppc.end_milestone_id = pp.end_milestone_id AND pppc.change_end_date IS NULL
                         JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
                 GROUP BY pppc.project_id
             ),
             past_project_woningblok_totalvalue AS (
                 SELECT
                     w.project_id,
                     SUM(wmc.amount *
                         CASE wmc.mutation_kind
                             WHEN 'CONSTRUCTION' THEN 1
                             WHEN 'DEMOLITION' THEN -1
                         END) AS total_value
                 FROM
                     past_projects pp
                         JOIN diwi.woningblok w ON pp.id = w.project_id
                         JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                            AND wmc.end_milestone_id = pp.end_milestone_id AND wmc.change_end_date IS NULL
                 GROUP BY w.project_id
             ),
             past_project_fixed_props AS (
                 SELECT
                     pcc.project_id, ps.property_name AS fixedPropertyName,
                     to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label))) AS fixedPropValuesList
                 FROM
                     past_projects pp
                         JOIN diwi.project_category_changelog pcc ON pp.id = pcc.project_id
                            AND pcc.end_milestone_id = pp.end_milestone_id AND pcc.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                         JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                         JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
                 GROUP BY pcc.project_id, ps.property_name
             ),
             past_project_ordinal_fixed_props AS (
                 SELECT
                     ppc.project_id, ps.property_name AS fixedPropertyName,
                     CASE
                         WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                         WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                     array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                         END AS ordinalValuesList
                 FROM
                     past_projects pp
                         JOIN diwi.project_ordinal_changelog ppc ON pp.id = ppc.project_id
                         AND ppc.end_milestone_id = pp.end_milestone_id AND ppc.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vs
                                   ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vsMin
                                   ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                         LEFT JOIN diwi.property_ordinal_value_state vsMax
                                   ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
                 GROUP BY ppc.project_id, ps.property_name, ppc.value_type
             ),
             past_project_text_fixed_props AS (
                 SELECT
                     ppc.project_id, ps.property_name AS fixedPropertyName, ppc.value as fixedPropertyValue
                 FROM
                     past_projects pp
                         JOIN diwi.project_text_changelog ppc ON pp.id = ppc.project_id
                            AND ppc.end_milestone_id = pp.end_milestone_id AND ppc.change_end_date IS NULL
                         JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                         JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                 GROUP BY ppc.project_id, ps.property_name, ppc.value
             ),
             project_users AS (
                 SELECT
                     q.project_id    AS project_id,
                     array_agg(array[q.usergroup_id::TEXT, q.usergroup_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users
                 FROM (
                          SELECT DISTINCT
                              ps.project_id as project_id,
                              us.user_id AS user_id,
                              LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                              us.last_name AS user_last_name,
                              us.first_name AS user_first_name,
                              ugs.usergroup_id AS usergroup_id,
                              ugs.naam AS usergroup_name
                          FROM diwi.project_state ps
                              JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                              JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                              LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id AND utug.change_end_date IS NULL
                              LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                          WHERE
                              ps.change_end_date IS NULL AND ps.project_id = _project_uuid_
                      ) AS q
                 GROUP BY q.project_id
             )
         SELECT ap.id                    AS projectId,
                ps.id                    AS projectStateId,
                apn.name                 AS projectName,
                ps.project_colour        AS projectColor,
                ps.latitude              AS latitude,
                ps.longitude             AS longitude,
                ps.confidentiality_level AS confidentialityLevel,
                owners.users             AS projectOwners,
                ap.startDate             AS startDate,
                ap.endDate               AS endDate,
                appt.plan_types          AS planType,
                apop.ordinalValuesList   AS priorityList,
                apf.project_fase         AS projectPhase,
                appp.planning_planstatus AS planningPlanStatus,
                apmr.fixedPropValuesList AS municipalityRoleList,
                apwv.total_value         AS totalValue,
                apr.fixedPropValuesList  AS municipalityList,
                apd.fixedPropValuesList  AS districtList,
                apne.fixedPropValuesList AS neighbourhoodList,
                apg.fixedPropertyValue   AS geometry
         FROM
             active_projects ap
                 LEFT JOIN diwi.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
                 LEFT JOIN active_project_names apn ON apn.project_id = ap.id
                 LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
                 LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
                 LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
                 LEFT JOIN active_project_woningblok_totalvalue apwv ON apwv.project_id = ap.id
                 LEFT JOIN active_project_ordinal_fixed_props apop ON apop.project_id = ap.id AND apop.fixedPropertyName = 'priority'
                 LEFT JOIN active_project_fixed_props apmr ON apmr.project_id = ap.id AND apmr.fixedPropertyName = 'municipalityRole'
                 LEFT JOIN active_project_fixed_props apr ON apr.project_id = ap.id AND apr.fixedPropertyName = 'municipality'
                 LEFT JOIN active_project_fixed_props apd ON apd.project_id = ap.id AND apd.fixedPropertyName = 'district'
                 LEFT JOIN active_project_fixed_props apne ON apne.project_id = ap.id AND apne.fixedPropertyName = 'neighbourhood'
                 LEFT JOIN active_project_text_fixed_props apg ON apg.project_id = ap.id AND apg.fixedPropertyName = 'geometry'
                 LEFT JOIN project_users owners ON ps.project_id = owners.project_id
         UNION
         SELECT fp.id                    AS projectId,
                ps.id                    AS projectStateId,
                fpn.name                 AS projectName,
                ps.project_colour        AS projectColor,
                ps.latitude              AS latitude,
                ps.longitude             AS longitude,
                ps.confidentiality_level AS confidentialityLevel,
                owners.users             AS projectOwners,
                fp.startDate             AS startDate,
                fp.endDate               AS endDate,
                fppt.plan_types          AS planType,
                fpop.ordinalValuesList   AS priorityList,
                fpf.project_fase         AS projectPhase,
                fppp.planning_planstatus AS planningPlanStatus,
                fpmr.fixedPropValuesList AS municipalityRoleList,
                fpwv.total_value         AS totalValue,
                fpr.fixedPropValuesList  AS municipalityList,
                fpd.fixedPropValuesList  AS districtList,
                fpne.fixedPropValuesList AS neighbourhoodList,
                fpg.fixedPropertyValue   AS geometry
         FROM
             future_projects fp
                 LEFT JOIN diwi.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
                 LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
                 LEFT JOIN future_project_plan_types fppt ON fppt.project_id = fp.id
                 LEFT JOIN future_project_fases fpf ON fpf.project_id = fp.id
                 LEFT JOIN future_project_planologische_planstatus fppp ON fppp.project_id = fp.id
                 LEFT JOIN future_project_woningblok_totalvalue fpwv ON fpwv.project_id = fp.id
                 LEFT JOIN future_project_ordinal_fixed_props fpop ON fpop.project_id = fp.id AND fpop.fixedPropertyName = 'priority'
                 LEFT JOIN future_project_fixed_props fpmr ON fpmr.project_id = fp.id AND fpmr.fixedPropertyName = 'municipalityRole'
                 LEFT JOIN future_project_fixed_props fpr ON fpr.project_id = fp.id AND fpr.fixedPropertyName = 'municipality'
                 LEFT JOIN future_project_fixed_props fpd ON fpd.project_id = fp.id AND fpd.fixedPropertyName = 'district'
                 LEFT JOIN future_project_fixed_props fpne ON fpne.project_id = fp.id AND fpne.fixedPropertyName = 'neighbourhood'
                 LEFT JOIN future_project_text_fixed_props fpg ON fpg.project_id = fp.id AND fpg.fixedPropertyName = 'geometry'
                 LEFT JOIN project_users owners ON ps.project_id = owners.project_id
         UNION
         SELECT pp.id                    AS projectId,
                ps.id                    AS projectStateId,
                ppn.name                 AS projectName,
                ps.project_colour        AS projectColor,
                ps.latitude              AS latitude,
                ps.longitude             AS longitude,
                ps.confidentiality_level AS confidentialityLevel,
                owners.users             AS projectOwners,
                pp.startDate             AS startDate,
                pp.endDate               AS endDate,
                pppt.plan_types          AS planType,
                ppop.ordinalValuesList   AS priorityList,
                ppf.project_fase         AS projectPhase,
                pppp.planning_planstatus AS planningPlanStatus,
                ppmr.fixedPropValuesList AS municipalityRoleList,
                ppwv.total_value         AS totalValue,
                ppr.fixedPropValuesList  AS municipalityList,
                ppd.fixedPropValuesList  AS districtList,
                ppne.fixedPropValuesList AS neighbourhoodList,
                ppg.fixedPropertyValue   AS geometry
         FROM
             past_projects pp
                 LEFT JOIN diwi.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
                 LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
                 LEFT JOIN past_project_plan_types pppt ON pppt.project_id = pp.id
                 LEFT JOIN past_project_fases ppf ON ppf.project_id = pp.id
                 LEFT JOIN past_project_planologische_planstatus pppp ON pppp.project_id = pp.id
                 LEFT JOIN past_project_woningblok_totalvalue ppwv ON ppwv.project_id = pp.id
                 LEFT JOIN past_project_ordinal_fixed_props ppop ON ppop.project_id = pp.id AND ppop.fixedPropertyName = 'priority'
                 LEFT JOIN past_project_fixed_props ppmr ON ppmr.project_id = pp.id AND ppmr.fixedPropertyName = 'municipalityRole'
                 LEFT JOIN past_project_fixed_props ppr ON ppr.project_id = pp.id AND ppr.fixedPropertyName = 'municipality'
                 LEFT JOIN past_project_fixed_props ppd ON ppd.project_id = pp.id AND ppd.fixedPropertyName = 'district'
                 LEFT JOIN past_project_fixed_props ppne ON ppne.project_id = pp.id AND ppne.fixedPropertyName = 'neighbourhood'
                 LEFT JOIN past_project_text_fixed_props ppg ON ppg.project_id = pp.id AND ppg.fixedPropertyName = 'geometry'
                 LEFT JOIN project_users owners ON ps.project_id = owners.project_id
     ) AS q
WHERE q.projectId = _project_uuid_ AND
    (
      ( _user_uuid_::TEXT IN (select owners.id from unnest(q.projectOwners) with ordinality owners(id,n) where owners.n % 6 = 3)) OR
      ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
      ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
      ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
    )
    LIMIT 1;
END;$$;
CREATE FUNCTION diwi.get_dashboard_blueprints(_bp_uuid_ uuid, _bp_user_uuid_ uuid) RETURNS TABLE(id uuid, name text, elements text[], users jsonb)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
WITH active_blueprints AS (
    SELECT
        bs.blueprint_id AS blueprint_id,
        bs.id AS blueprint_state_id,
        bs.name AS name
    FROM
        diwi.blueprint_state bs
    WHERE
        bs.change_end_date IS NULL
            AND
        CASE
            WHEN _bp_uuid_ IS NOT NULL THEN bs.blueprint_id = _bp_uuid_
            WHEN _bp_uuid_ IS NULL THEN 1 = 1
        END
    ),
    blueprint_users AS (
        SELECT
            q.blueprint_id    AS blueprint_id,
            to_jsonb(array_agg(jsonb_build_object('userGroupUuid', q.usergroup_id::TEXT, 'userGroupName', q.usergroup_name, 'uuid', q.user_id::TEXT,
                'initials', q.user_initials, 'lastName', q.user_last_name, 'firstName', q.user_first_name))) AS users,
            array_agg(q.user_id) AS userIds
        FROM (
            SELECT DISTINCT
                ab.blueprint_id as blueprint_id,
                us.user_id AS user_id,
                LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                us.last_name AS user_last_name,
                us.first_name AS user_first_name,
                ugs.usergroup_id AS usergroup_id,
                ugs.naam AS usergroup_name
            FROM
                active_blueprints ab
                    JOIN diwi.blueprint_to_usergroup btug ON ab.blueprint_state_id = btug.blueprint_state_id
                    JOIN diwi.usergroup_state ugs ON btug.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                    LEFT JOIN diwi.user_to_usergroup utug ON ugs.usergroup_id = utug.usergroup_id AND utug.change_end_date IS NULL
                    LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
        ) AS q
        GROUP BY q.blueprint_id
    ),
    blueprint_elements AS (
        SELECT
            ab.blueprint_id    AS blueprint_id,
            array_agg(bte.element::TEXT) FILTER (WHERE bte.element IS NOT NULL) AS elements
        FROM
            active_blueprints ab
                LEFT JOIN diwi.blueprint_to_element bte ON ab.blueprint_state_id = bte.blueprint_state_id
        GROUP BY ab.blueprint_id
    )
SELECT
    ab.blueprint_id,
    ab.name,
    be.elements,
    bu.users
FROM
    active_blueprints ab
        LEFT JOIN blueprint_users bu ON bu.blueprint_id = ab.blueprint_id
        LEFT JOIN blueprint_elements be ON be.blueprint_id = ab.blueprint_id
WHERE
    CASE
        WHEN _bp_user_uuid_ IS NOT NULL THEN bu.userIds @> ARRAY[_bp_user_uuid_]
        WHEN _bp_user_uuid_ IS NULL THEN 1 = 1
    END
ORDER BY ab.name;
END;$$;
CREATE FUNCTION diwi.get_houseblock_custom_properties(_woningblok_uuid_ uuid, _now_ date) RETURNS TABLE(custompropertyid uuid, booleanvalue boolean, numericvalue double precision, numericvaluerange numrange, numericvaluetype diwi.value_type, textvalue text, categories uuid[], ordinalvalueid uuid, ordinalminvalueid uuid, ordinalmaxvalueid uuid, propertytype diwi.maatwerk_eigenschap_type)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT
    q.customPropertyId,
    q.booleanValue,
    q.numericValue,
    q.numericValueRange,
    q.numericValueType,
    q.textValue,
    q.categories,
    q.ordinalValueId,
    q.ordinalMinValueId,
    q.ordinalMaxValueId,
    q.propertyType
FROM (
         WITH
             active_woningbloks AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi.woningblok p
                         JOIN diwi.woningblok_state ws ON ws.woningblok_id = p.id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = p.id AND wdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  sms.date <= _now_ AND _now_ < ems.date AND p.id = _woningblok_uuid_
             ),
             active_woningbloks_booleanCP AS (
                 SELECT
                     ap.id, wbc.eigenschap_id, wbc.value
                 FROM
                     active_woningbloks ap
                         JOIN diwi.woningblok_maatwerk_boolean_changelog wbc ON ap.id = wbc.woningblok_id AND wbc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wbc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wbc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_numericCP AS (
                 SELECT
                     ap.id, wnc.eigenschap_id, wnc.value, wnc.value_range, wnc.value_type
                 FROM
                     active_woningbloks ap
                         JOIN diwi.woningblok_maatwerk_numeriek_changelog wnc ON ap.id = wnc.woningblok_id AND wnc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_textCP AS (
                 SELECT
                     ap.id, wtc.eigenschap_id, wtc.value
                 FROM
                     active_woningbloks ap
                         JOIN diwi.woningblok_maatwerk_text_changelog wtc ON ap.id = wtc.woningblok_id AND wtc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wtc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wtc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_categoriesCP AS (
                 SELECT
                     ap.id, wcc.eigenschap_id, array_agg(wccv.eigenschap_waarde_id) AS categories
                 FROM
                     active_woningbloks ap
                         JOIN diwi.woningblok_maatwerk_categorie_changelog wcc ON ap.id = wcc.woningblok_id AND wcc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wcc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wcc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_maatwerk_categorie_changelog_value wccv ON wccv.woningblok_maatwerk_categorie_changelog_id = wcc.id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY ap.id, wcc.eigenschap_id
             ),
             active_woningbloks_ordinalCP AS (
                 SELECT
                     ap.id, woc.eigenschap_id, woc.value_id AS ordinal_value_id, woc.min_value_id AS ordinal_min_value_id, woc.max_value_id AS ordinal_max_value_id
                 FROM
                     active_woningbloks ap
                         JOIN diwi.woningblok_maatwerk_ordinaal_changelog woc ON ap.id = woc.woningblok_id AND woc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = woc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = woc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             future_woningbloks AS (
                 SELECT
                     p.id, sms.milestone_id AS start_milestone_id
                 FROM
                     diwi.woningblok p
                         JOIN diwi.woningblok_state ws ON ws.woningblok_id = p.id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = p.id AND wdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE sms.date > _now_  AND p.id = _woningblok_uuid_
             ),
             future_woningbloks_numericCP AS (
                 SELECT
                     fp.id, wnc.eigenschap_id, wnc.value, wnc.value_range, wnc.value_type
                 FROM
                     future_woningbloks fp
                         JOIN diwi.woningblok_maatwerk_numeriek_changelog wnc ON fp.id = wnc.woningblok_id
                            AND wnc.start_milestone_id = fp.start_milestone_id AND wnc.change_end_date IS NULL
             ),
             future_woningbloks_booleanCP AS (
                 SELECT
                     fp.id, wbc.eigenschap_id, wbc.value
                 FROM
                     future_woningbloks fp
                         JOIN diwi.woningblok_maatwerk_boolean_changelog wbc ON fp.id = wbc.woningblok_id
                         AND wbc.start_milestone_id = fp.start_milestone_id AND wbc.change_end_date IS NULL
             ),
             future_woningbloks_textCP AS (
                 SELECT
                     fp.id, wtc.eigenschap_id, wtc.value
                 FROM
                     future_woningbloks fp
                         JOIN diwi.woningblok_maatwerk_text_changelog wtc ON fp.id = wtc.woningblok_id
                         AND wtc.start_milestone_id = fp.start_milestone_id AND wtc.change_end_date IS NULL
             ),
             future_woningbloks_categoriesCP AS (
                 SELECT
                     fp.id, wcc.eigenschap_id, array_agg(wccv.eigenschap_waarde_id) AS categories
                 FROM
                     future_woningbloks fp
                         JOIN diwi.woningblok_maatwerk_categorie_changelog wcc ON fp.id = wcc.woningblok_id
                            AND wcc.start_milestone_id = fp.start_milestone_id AND wcc.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_maatwerk_categorie_changelog_value wccv ON wccv.woningblok_maatwerk_categorie_changelog_id = wcc.id
                 GROUP BY fp.id, wcc.eigenschap_id
             ),
             future_woningbloks_ordinalCP AS (
                 SELECT
                     fp.id, woc.eigenschap_id, woc.value_id AS ordinal_value_id, woc.min_value_id AS ordinal_min_value_id, woc.max_value_id AS ordinal_max_value_id
                 FROM
                     future_woningbloks fp
                         JOIN diwi.woningblok_maatwerk_ordinaal_changelog woc ON fp.id = woc.woningblok_id
                         AND woc.start_milestone_id = fp.start_milestone_id AND woc.change_end_date IS NULL
             ),
             past_woningbloks AS (
                 SELECT
                     p.id, ems.milestone_id AS end_milestone_id
                 FROM
                     diwi.woningblok p
                         JOIN diwi.woningblok_state ws ON ws.woningblok_id = p.id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = p.id AND wdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE ems.date <= _now_  AND p.id = _woningblok_uuid_
             ),
             past_woningbloks_numericCP AS (
                 SELECT
                     pw.id, wnc.eigenschap_id, wnc.value, wnc.value_range, wnc.value_type
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_maatwerk_numeriek_changelog wnc ON pw.id = wnc.woningblok_id
                            AND wnc.end_milestone_id = pw.end_milestone_id AND wnc.change_end_date IS NULL
             ),
             past_woningbloks_booleanCP AS (
                 SELECT
                     pw.id, wbc.eigenschap_id, wbc.value
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_maatwerk_boolean_changelog wbc ON pw.id = wbc.woningblok_id
                         AND wbc.end_milestone_id = pw.end_milestone_id AND wbc.change_end_date IS NULL
             ),
             past_woningbloks_textCP AS (
                 SELECT
                     pw.id, wtc.eigenschap_id, wtc.value
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_maatwerk_text_changelog wtc ON pw.id = wtc.woningblok_id
                         AND wtc.end_milestone_id = pw.end_milestone_id AND wtc.change_end_date IS NULL
             ),
             past_woningbloks_categoriesCP AS (
                 SELECT
                     pw.id, wcc.eigenschap_id, array_agg(wccv.eigenschap_waarde_id) AS categories
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_maatwerk_categorie_changelog wcc ON pw.id = wcc.woningblok_id
                            AND wcc.end_milestone_id = pw.end_milestone_id AND wcc.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_maatwerk_categorie_changelog_value wccv ON wccv.woningblok_maatwerk_categorie_changelog_id = wcc.id
                 GROUP BY pw.id, wcc.eigenschap_id
             ),
             past_woningbloks_ordinalCP AS (
                 SELECT
                     pw.id, woc.eigenschap_id, woc.value_id AS ordinal_value_id, woc.min_value_id AS ordinal_min_value_id, woc.max_value_id AS ordinal_max_value_id
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_maatwerk_ordinaal_changelog woc ON pw.id = woc.woningblok_id
                         AND woc.end_milestone_id = pw.end_milestone_id AND woc.change_end_date IS NULL
             )
         SELECT
             ap.id AS woningblokId,
             apb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
                JOIN active_woningbloks_booleanCP apb ON ap.id = apb.id
         UNION
         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             apn.value AS numericValue,
             apn.value_range AS numericValueRange,
             apn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
                JOIN active_woningbloks_numericCP apn ON ap.id = apn.id
         UNION
         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             apt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
                JOIN active_woningbloks_textCP apt ON ap.id = apt.id
         UNION
         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             apc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
             JOIN active_woningbloks_categoriesCP apc ON ap.id = apc.id
         UNION
         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             apo.ordinal_value_id AS ordinalValueId,
             apo.ordinal_min_value_id AS ordinalMinValueId,
             apo.ordinal_max_value_id AS ordinalMaxValueId,
             apo.eigenschap_id AS customPropertyId,
             'ORDINAL'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
             JOIN active_woningbloks_ordinalCP apo ON ap.id = apo.id
         UNION
         SELECT
             fp.id AS woningblokId,
             fpb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
                  JOIN future_woningbloks_booleanCP fpb ON fp.id = fpb.id
         UNION
         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             fpn.value AS numericValue,
             fpn.value_range AS numericValueRange,
             fpn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
                  JOIN future_woningbloks_numericCP fpn ON fp.id = fpn.id
         UNION
         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             fpt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
                  JOIN future_woningbloks_textCP fpt ON fp.id = fpt.id
         UNION
         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             fpc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
             JOIN future_woningbloks_categoriesCP fpc ON fp.id = fpc.id
         UNION
         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             fpo.eigenschap_id AS customPropertyId,
             fpo.ordinal_value_id AS ordinalValueId,
             fpo.ordinal_min_value_id AS ordinalMinValueId,
             fpo.ordinal_max_value_id AS ordinalMaxValueId,
             'ORDINAL'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
             JOIN future_woningbloks_ordinalCP fpo ON fp.id = fpo.id
         UNION
         SELECT
             pw.id AS woningblokId,
             pwb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_booleanCP pwb ON pw.id = pwb.id
         UNION
         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             pwn.value AS numericValue,
             pwn.value_range AS numericValueRange,
             pwn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_numericCP pwn ON pw.id = pwn.id
         UNION
         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             pwt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_textCP pwt ON pw.id = pwt.id
         UNION
         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             pwc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_categoriesCP pwc ON pw.id = pwc.id
         UNION
         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             pwo.eigenschap_id AS customPropertyId,
             pwo.ordinal_value_id AS ordinalValueId,
             pwo.ordinal_min_value_id AS ordinalMinValueId,
             pwo.ordinal_max_value_id AS ordinalMaxValueId,
             'ORDINAL'::"diwi"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_ordinalCP pwo ON pw.id = pwo.id
     ) AS q
        JOIN diwi.property_state cps ON cps.property_id = q.customPropertyId AND cps.change_end_date IS NULL
WHERE q.woningblokId = _woningblok_uuid_;
END;$$;
CREATE FUNCTION diwi.get_houseblock_snapshots(_project_uuid_ uuid, _houseblock_uuid_ uuid, _now_ date) RETURNS TABLE(projectid uuid, houseblockid uuid, houseblockname text, startdate date, enddate date, sizevalue double precision, sizevaluerange numrange, sizevaluetype diwi.value_type, programming boolean, mutationamount integer, mutationkind diwi.mutation_kind, ownershipvaluelist jsonb, nopermissionowner integer, intentionpermissionowner integer, formalpermissionowner integer, physicalappearancelist jsonb, meergezinswoning integer, eengezinswoning integer, targetgrouplist jsonb)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT  q.projectId,
        q.woningblokId,
        q.woningblokName,
        q.startDate,
        q.endDate,
        q.sizeValue,
        q.sizeValueRange,
        q.sizeValueType,
        q.programming,
        q.mutationAmount,
        q.mutationKind,
        q.ownershipValueList,
        q.noPermissionOwner,
        q.intentionPermissionOwner,
        q.formalPermissionOwner,
        q.physicalAppearance,
        q.meergezinswoning,
        q.eengezinswoning,
        q.targetGroup
FROM (
         WITH
             active_woningbloks AS (
                 SELECT
                     w.id, w.project_id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi.woningblok w
                         JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  sms.date <= _now_ AND _now_ < ems.date AND
                    CASE
                        WHEN _houseblock_uuid_ IS NOT NULL THEN w.id = _houseblock_uuid_
                        WHEN _houseblock_uuid_ IS NULL THEN 1 = 1
                    END
                    AND
                    CASE
                        WHEN _project_uuid_ IS NOT NULL THEN w.project_id = _project_uuid_
                        WHEN _project_uuid_ IS NULL THEN 1 = 1
                    END
             ),
             active_woningbloks_names AS (
                 SELECT
                     aw.id, wnc.naam AS name
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_naam_changelog wnc ON aw.id = wnc.woningblok_id AND wnc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_groundposition AS (
                 SELECT
                     aw.id, ftg.amount AS formalPermissionOwner, img.amount AS intentionPermissionOwner, gtg.amount AS noPermissionOwner
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_grondpositie_changelog wgpc ON aw.id = wgpc.woningblok_id AND wgpc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wgpc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wgpc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value ftg ON ftg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND ftg.grondpositie = 'FORMELE_TOESTEMMING_GRONDEIGENAAR'
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value img ON img.woningblok_grondpositie_changelog_id = wgpc.id
                                AND img.grondpositie = 'INTENTIE_MEDEWERKING_GRONDEIGENAAR'
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value gtg ON gtg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND gtg.grondpositie = 'GEEN_TOESTEMMING_GRONDEIGENAAR'
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_fysiek AS (
                 SELECT
                     aw.id, to_jsonb(array_agg(jsonb_build_object('id', wcfv.property_value_id, 'amount', wcfv.amount)) FILTER (WHERE wcfv.property_value_id IS NOT NULL)) AS physicalAppearance,
                     meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON aw.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_fysiek_value wcfv ON wcfv.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY aw.id, meer.amount, eeng.amount
             ),
             active_woningbloks_doelgroep AS (
                 SELECT
                     aw.id, to_jsonb(array_agg(jsonb_build_object('id', wdcv.property_value_id, 'amount', wdcv.amount))) AS targetGroup
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_doelgroep_changelog wdgc ON aw.id = wdgc.woningblok_id AND wdgc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdgc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdgc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.woningblok_doelgroep_changelog_value wdcv ON wdcv.woningblok_doelgroep_changelog_id = wdgc.id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY aw.id
             ),
             active_woningbloks_mutation AS (
                 SELECT
                     aw.id, wmc.mutation_kind AS mutationKind, wmc.amount AS mutationAmount
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_mutatie_changelog wmc ON aw.id = wmc.woningblok_id AND wmc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_programming AS (
                 SELECT
                     aw.id, wpc.programmering AS programming
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_programmering_changelog wpc ON aw.id = wpc.woningblok_id AND wpc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wpc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wpc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_size AS (
                 SELECT
                     aw.id, wgc.value AS sizeValue, wgc.value_range AS sizeValueRange, wgc.value_type AS sizeValueType
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_grootte_changelog wgc ON aw.id = wgc.woningblok_id AND wgc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wgc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wgc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_ownership_value AS (
                 SELECT
                     aw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1,
                                    'ownershipRangeCategoryId', wewc.ownership_property_value_id, 'ownershipRentalRangeCategoryId', wewc.rental_property_value_id))) AS ownershipValue
                 FROM
                     active_woningbloks aw
                         JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON aw.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY aw.id
             ),
             future_woningbloks AS (
                 SELECT
                     w.id, w.project_id, sms.date AS startDate, sms.milestone_id AS start_milestone_id, ems.date AS endDate
                 FROM
                     diwi.woningblok w
                         JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  sms.date > _now_ AND
                    CASE
                        WHEN _houseblock_uuid_ IS NOT NULL THEN w.id = _houseblock_uuid_
                        WHEN _houseblock_uuid_ IS NULL THEN 1 = 1
                    END
                    AND
                    CASE
                        WHEN _project_uuid_ IS NOT NULL THEN w.project_id = _project_uuid_
                        WHEN _project_uuid_ IS NULL THEN 1 = 1
                    END
             ),
             future_woningbloks_names AS (
                 SELECT
                     fw.id, wnc.naam AS name
                 FROM
                    future_woningbloks fw
                         JOIN diwi.woningblok_naam_changelog wnc ON fw.id = wnc.woningblok_id
                                AND wnc.start_milestone_id = fw.start_milestone_id
                                AND wnc.change_end_date IS NULL
             ),
             future_woningbloks_groundposition AS (
                 SELECT
                     fw.id, ftg.amount AS formalPermissionOwner, img.amount AS intentionPermissionOwner, gtg.amount AS noPermissionOwner
                 FROM
                     future_woningbloks fw
                         JOIN diwi.woningblok_grondpositie_changelog wgpc ON fw.id = wgpc.woningblok_id
                                AND wgpc.start_milestone_id = fw.start_milestone_id
                                AND wgpc.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value ftg ON ftg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND ftg.grondpositie = 'FORMELE_TOESTEMMING_GRONDEIGENAAR'
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value img ON img.woningblok_grondpositie_changelog_id = wgpc.id
                                AND img.grondpositie = 'INTENTIE_MEDEWERKING_GRONDEIGENAAR'
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value gtg ON gtg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND gtg.grondpositie = 'GEEN_TOESTEMMING_GRONDEIGENAAR'
             ),
             future_woningbloks_fysiek AS (
                 SELECT
                     fw.id, to_jsonb(array_agg(jsonb_build_object('id', wcfv.property_value_id, 'amount', wcfv.amount)) FILTER (WHERE wcfv.property_value_id IS NOT NULL)) AS physicalAppearance,
                     meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                 FROM
                     future_woningbloks fw
                         JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON fw.id = wtfc.woningblok_id
                                AND wtfc.start_milestone_id = fw.start_milestone_id
                                AND wtfc.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_fysiek_value wcfv ON wcfv.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                 GROUP BY fw.id, meer.amount, eeng.amount
             ),
             future_woningbloks_doelgroep AS (
                 SELECT
                     fw.id, to_jsonb(array_agg(jsonb_build_object('id', wdcv.property_value_id, 'amount', wdcv.amount))) AS targetGroup
                 FROM
                     future_woningbloks fw
                         JOIN diwi.woningblok_doelgroep_changelog wdgc ON fw.id = wdgc.woningblok_id
                                AND wdgc.start_milestone_id = fw.start_milestone_id
                                AND wdgc.change_end_date IS NULL
                         JOIN diwi.woningblok_doelgroep_changelog_value wdcv ON wdcv.woningblok_doelgroep_changelog_id = wdgc.id
                 GROUP BY fw.id
             ),
             future_woningbloks_mutation AS (
                 SELECT
                     fw.id, wmc.mutation_kind AS mutationKind, wmc.amount AS mutationAmount
                 FROM
                     future_woningbloks fw
                         JOIN diwi.woningblok_mutatie_changelog wmc ON fw.id = wmc.woningblok_id
                                    AND wmc.start_milestone_id = fw.start_milestone_id
                                    AND wmc.change_end_date IS NULL
             ),
             future_woningbloks_programming AS (
                 SELECT
                     fw.id, wpc.programmering AS programming
                 FROM
                     future_woningbloks fw
                         JOIN diwi.woningblok_programmering_changelog wpc ON fw.id = wpc.woningblok_id
                                AND wpc.start_milestone_id = fw.start_milestone_id
                                AND wpc.change_end_date IS NULL
             ),
             future_woningbloks_size AS (
                 SELECT
                     fw.id, wgc.value AS sizeValue, wgc.value_range AS sizeValueRange, wgc.value_type AS sizeValueType
                 FROM
                     future_woningbloks fw
                         JOIN diwi.woningblok_grootte_changelog wgc ON fw.id = wgc.woningblok_id
                                AND wgc.start_milestone_id = fw.start_milestone_id
                                AND wgc.change_end_date IS NULL
             ),
             future_ownership_value AS (
                 SELECT
                     fw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1,
                                    'ownershipRangeCategoryId', wewc.ownership_property_value_id, 'ownershipRentalRangeCategoryId', wewc.rental_property_value_id))) AS ownershipValue
                 FROM
                     future_woningbloks fw
                         JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON fw.id = wewc.woningblok_id
                                AND wewc.start_milestone_id = fw.start_milestone_id
                                AND wewc.change_end_date IS NULL
                 GROUP BY fw.id
             ),
             past_woningbloks AS (
                 SELECT
                     w.id, w.project_id, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
                 FROM
                     diwi.woningblok w
                         JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  ems.date <= _now_ AND
                    CASE
                        WHEN _houseblock_uuid_ IS NOT NULL THEN w.id = _houseblock_uuid_
                        WHEN _houseblock_uuid_ IS NULL THEN 1 = 1
                    END
                    AND
                    CASE
                        WHEN _project_uuid_ IS NOT NULL THEN w.project_id = _project_uuid_
                        WHEN _project_uuid_ IS NULL THEN 1 = 1
                    END
             ),
             past_woningbloks_names AS (
                 SELECT
                     pw.id, wnc.naam AS name
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_naam_changelog wnc ON pw.id = wnc.woningblok_id
                                AND wnc.end_milestone_id = pw.end_milestone_id
                                AND wnc.change_end_date IS NULL
             ),
             past_woningbloks_groundposition AS (
                 SELECT
                     pw.id, ftg.amount AS formalPermissionOwner, img.amount AS intentionPermissionOwner, gtg.amount AS noPermissionOwner
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_grondpositie_changelog wgpc ON pw.id = wgpc.woningblok_id
                                AND wgpc.end_milestone_id = pw.end_milestone_id
                                AND wgpc.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value ftg ON ftg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND ftg.grondpositie = 'FORMELE_TOESTEMMING_GRONDEIGENAAR'
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value img ON img.woningblok_grondpositie_changelog_id = wgpc.id
                                AND img.grondpositie = 'INTENTIE_MEDEWERKING_GRONDEIGENAAR'
                         LEFT JOIN diwi.woningblok_grondpositie_changelog_value gtg ON gtg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND gtg.grondpositie = 'GEEN_TOESTEMMING_GRONDEIGENAAR'
             ),
             past_woningbloks_fysiek AS (
                 SELECT
                     pw.id, to_jsonb(array_agg(jsonb_build_object('id', wcfv.property_value_id, 'amount', wcfv.amount)) FILTER (WHERE wcfv.property_value_id IS NOT NULL)) AS physicalAppearance,
                     meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON pw.id = wtfc.woningblok_id
                                AND wtfc.end_milestone_id = pw.end_milestone_id
                                AND wtfc.change_end_date IS NULL
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_fysiek_value wcfv ON wcfv.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                         LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                 GROUP BY pw.id, meer.amount, eeng.amount
             ),
             past_woningbloks_doelgroep AS (
                 SELECT
                     pw.id, to_jsonb(array_agg(jsonb_build_object('id', wdcv.property_value_id, 'amount', wdcv.amount))) AS targetGroup
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_doelgroep_changelog wdgc ON pw.id = wdgc.woningblok_id
                                AND wdgc.end_milestone_id = pw.end_milestone_id
                                AND wdgc.change_end_date IS NULL
                         JOIN diwi.woningblok_doelgroep_changelog_value wdcv ON wdcv.woningblok_doelgroep_changelog_id = wdgc.id
                 GROUP BY pw.id
             ),
             past_woningbloks_mutation AS (
                 SELECT
                     pw.id, wmc.mutation_kind AS mutationKind, wmc.amount AS mutationAmount
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_mutatie_changelog wmc ON pw.id = wmc.woningblok_id
                                    AND wmc.end_milestone_id = pw.end_milestone_id
                                    AND wmc.change_end_date IS NULL
             ),
             past_woningbloks_programming AS (
                 SELECT
                     pw.id, wpc.programmering AS programming
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_programmering_changelog wpc ON pw.id = wpc.woningblok_id
                                AND wpc.end_milestone_id = pw.end_milestone_id
                                AND wpc.change_end_date IS NULL
             ),
             past_woningbloks_size AS (
                 SELECT
                     pw.id, wgc.value AS sizeValue, wgc.value_range AS sizeValueRange, wgc.value_type AS sizeValueType
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_grootte_changelog wgc ON pw.id = wgc.woningblok_id
                                AND wgc.end_milestone_id = pw.end_milestone_id
                                AND wgc.change_end_date IS NULL
             ),
             past_ownership_value AS (
                 SELECT
                     pw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1,
                                    'ownershipRangeCategoryId', wewc.ownership_property_value_id, 'ownershipRentalRangeCategoryId', wewc.rental_property_value_id))) AS ownershipValue
                 FROM
                     past_woningbloks pw
                         JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON pw.id = wewc.woningblok_id
                                AND wewc.end_milestone_id = pw.end_milestone_id
                                AND wewc.change_end_date IS NULL
                 GROUP BY pw.id
             )
         SELECT aw.id                           AS woningblokId,
                aw.project_id                   AS projectId,
                awn.name                        AS woningblokName,
                aw.startDate                    AS startDate,
                aw.endDate                      AS endDate,
                aws.sizeValue                   AS sizeValue,
                aws.sizeValueRange              AS sizeValueRange,
                aws.sizeValueType               AS sizeValueType,
                awp.programming                 AS programming,
                awm.mutationAmount              AS mutationAmount,
                awm.mutationKind                AS mutationKind,
                aov.ownershipValue              AS ownershipValueList,
                awgp.noPermissionOwner          AS noPermissionOwner,
                awgp.intentionPermissionOwner   AS intentionPermissionOwner,
                awgp.formalPermissionOwner      AS formalPermissionOwner,
                awf.physicalAppearance          AS physicalAppearance,
                awf.meergezinswoning            AS meergezinswoning,
                awf.eengezinswoning             AS eengezinswoning,
                awdg.targetGroup                AS targetGroup
         FROM
             active_woningbloks aw
                 LEFT JOIN active_woningbloks_names awn ON awn.id = aw.id
                 LEFT JOIN active_woningbloks_size aws ON aws.id = aw.id
                 LEFT JOIN active_woningbloks_programming awp ON awp.id = aw.id
                 LEFT JOIN active_woningbloks_groundposition awgp ON awgp.id = aw.id
                 LEFT JOIN active_woningbloks_fysiek awf ON awf.id = aw.id
                 LEFT JOIN active_woningbloks_doelgroep awdg ON awdg.id = aw.id
                 LEFT JOIN active_woningbloks_mutation awm ON awm.id = aw.id
                 LEFT JOIN active_ownership_value aov ON aov.id = aw.id
         UNION
         SELECT fw.id                           AS woningblokId,
             fw.project_id                   AS projectId,
             fwn.name                        AS woningblokName,
             fw.startDate                    AS startDate,
             fw.endDate                      AS endDate,
             fws.sizeValue                   AS sizeValue,
             fws.sizeValueRange              AS sizeValueRange,
             fws.sizeValueType               AS sizeValueType,
             fwp.programming                 AS programming,
             fwm.mutationAmount              AS mutationAmount,
             fwm.mutationKind                AS mutationKind,
             fov.ownershipValue              AS ownershipValueList,
             fwgp.noPermissionOwner          AS noPermissionOwner,
             fwgp.intentionPermissionOwner   AS intentionPermissionOwner,
             fwgp.formalPermissionOwner      AS formalPermissionOwner,
             fwf.physicalAppearance          AS physicalAppearance,
             fwf.meergezinswoning            AS meergezinswoning,
             fwf.eengezinswoning             AS eengezinswoning,
             fwdg.targetGroup                AS targetGroup
         FROM
             future_woningbloks fw
             LEFT JOIN future_woningbloks_names fwn ON fwn.id = fw.id
             LEFT JOIN future_woningbloks_size fws ON fws.id = fw.id
             LEFT JOIN future_woningbloks_programming fwp ON fwp.id = fw.id
             LEFT JOIN future_woningbloks_groundposition fwgp ON fwgp.id = fw.id
             LEFT JOIN future_woningbloks_fysiek fwf ON fwf.id = fw.id
             LEFT JOIN future_woningbloks_doelgroep fwdg ON fwdg.id = fw.id
             LEFT JOIN future_woningbloks_mutation fwm ON fwm.id = fw.id
             LEFT JOIN future_ownership_value fov ON fov.id = fw.id
         UNION
         SELECT pw.id                           AS woningblokId,
                pw.project_id                   AS projectId,
                pwn.name                        AS woningblokName,
                pw.startDate                    AS startDate,
                pw.endDate                      AS endDate,
                pws.sizeValue                   AS sizeValue,
                pws.sizeValueRange              AS sizeValueRange,
                pws.sizeValueType               AS sizeValueType,
                pwp.programming                 AS programming,
                pwm.mutationAmount              AS mutationAmount,
                pwm.mutationKind                AS mutationKind,
                pov.ownershipValue              AS ownershipValueList,
                pwgp.noPermissionOwner          AS noPermissionOwner,
                pwgp.intentionPermissionOwner   AS intentionPermissionOwner,
                pwgp.formalPermissionOwner      AS formalPermissionOwner,
                pwf.physicalAppearance          AS physicalAppearance,
                pwf.meergezinswoning            AS meergezinswoning,
                pwf.eengezinswoning             AS eengezinswoning,
                pwdg.targetGroup                AS targetGroup
         FROM
             past_woningbloks pw
                 LEFT JOIN past_woningbloks_names pwn ON pwn.id = pw.id
                 LEFT JOIN past_woningbloks_size pws ON pws.id = pw.id
                 LEFT JOIN past_woningbloks_programming pwp ON pwp.id = pw.id
                 LEFT JOIN past_woningbloks_groundposition pwgp ON pwgp.id = pw.id
                 LEFT JOIN past_woningbloks_fysiek pwf ON pwf.id = pw.id
                 LEFT JOIN past_woningbloks_doelgroep pwdg ON pwdg.id = pw.id
                 LEFT JOIN past_woningbloks_mutation pwm ON pwm.id = pw.id
                 LEFT JOIN past_ownership_value pov ON pov.id = pw.id
     ) AS q
    ORDER BY q.woningblokName, q.startDate, q.endDate;
END;$$;
CREATE FUNCTION diwi.get_multi_project_dashboard_snapshot(_snapshot_date_ date, _user_role_ text, _user_uuid_ uuid) RETURNS TABLE(physicalappearance jsonb, targetgroup jsonb)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT
    q.physicalAppearance AS physicalAppearance,
    q.targetGroup AS targetGroup
FROM (
         WITH
             projects AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi.project p
                         JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
             ),
             woningbloks AS (
                 SELECT
                     w.id, w.project_id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi.woningblok w
                         JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                         JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
             ),
             woningbloks_physical_appearance AS (
                 SELECT
                     pcvs.value_label AS label, SUM(wcfv.amount) AS amount
                 FROM
                     woningbloks w
                         JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON w.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.woningblok_type_en_fysiek_changelog_fysiek_value wcfv ON wcfv.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                         JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = wcfv.property_value_id AND pcvs.change_end_date IS NULL
                 WHERE
                     sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
                 GROUP BY pcvs.value_label
             ),
             woningbloks_target_group AS (
                 SELECT
                     pcvs.value_label AS label, SUM(wcfv.amount) AS amount
                 FROM
                     woningbloks w
                         JOIN diwi.woningblok_doelgroep_changelog wtfc ON w.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                         JOIN diwi.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi.woningblok_doelgroep_changelog_value wcfv ON wcfv.woningblok_doelgroep_changelog_id = wtfc.id
                         JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = wcfv.property_value_id AND pcvs.change_end_date IS NULL
                 WHERE
                     sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
                 GROUP BY pcvs.value_label
             ),
             project_users AS (
                 SELECT
                     q.project_id    AS project_id,
                     array_agg(array[q.usergroup_id::TEXT, q.usergroup_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users
                 FROM (
                          SELECT DISTINCT
                              ps.project_id as project_id,
                              us.user_id AS user_id,
                              LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                              us.last_name AS user_last_name,
                              us.first_name AS user_first_name,
                              ugs.usergroup_id AS usergroup_id,
                              ugs.naam AS usergroup_name
                          FROM diwi.project_state ps
                              JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                              JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                              LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id AND utug.change_end_date IS NULL
                              LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                          WHERE
                              ps.change_end_date IS NULL
                      ) AS q
                 GROUP BY q.project_id
             )
         SELECT
                p.id                               AS projectId,
                ps.confidentiality_level           AS confidentialityLevel,
                owners.users                       AS projectOwners,
                wpa.physicalAppearance             AS physicalAppearance,
                wtp.targetGroup                    AS targetGroup
         FROM
             projects p
                 LEFT JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                 LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('name', label, 'amount', amount))) AS physicalAppearance
                                    FROM woningbloks_physical_appearance) AS wpa ON true
                 LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('name', label, 'amount', amount))) AS targetGroup
                                    FROM woningbloks_target_group) AS wtp ON true
                 LEFT JOIN project_users owners ON ps.project_id = owners.project_id
     ) AS q
WHERE
    (
      ( _user_uuid_::TEXT IN (select owners.id from unnest(q.projectOwners) with ordinality owners(id,n) where owners.n % 6 = 3)) OR
      ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
      ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
      ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
    )
    LIMIT 1;
END;$$;
CREATE FUNCTION diwi.get_project_dashboard_snapshot(_project_uuid_ uuid, _snapshot_date_ date, _user_role_ text, _user_uuid_ uuid) RETURNS TABLE(projectid uuid, physicalappearance jsonb, pricecategoryown jsonb, pricecategoryrent jsonb, planning jsonb)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT
    q.projectId          AS projectId,
    q.physicalAppearance AS physicalAppearance,
    q.priceCategoryOwn   AS priceCategoryOwn,
    q.priceCategoryRent  AS priceCategoryRent,
    q.planning           AS planning
FROM (
        WITH
            current_project AS (
                SELECT
                    p.id, sms.date AS startDate, ems.date AS endDate
                FROM
                    diwi.project p
                        JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND p.id = _project_uuid_
            ),
            woningbloks AS (
                SELECT
                    w.id, w.project_id, sms.date AS startDate, ems.date AS endDate
                FROM
                    diwi.woningblok w
                        JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                        JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE  sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND w.project_id = _project_uuid_
            ),
            woningbloks_physical_appearance AS (
                SELECT
                    pcvs.value_label AS label, SUM(wcfv.amount) AS amount
                FROM
                    woningbloks w
                        JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON w.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.woningblok_type_en_fysiek_changelog_fysiek_value wcfv ON wcfv.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                        JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = wcfv.property_value_id AND pcvs.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
                GROUP BY pcvs.value_label
            ),
            woningbloks_pricecategory_own AS (
                SELECT
                    prcvs.id AS id, prcvs.name AS label, prcvs.min AS min, prcvs.max AS max, SUM(wewc.amount) AS amount
                FROM
                    woningbloks w
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON w.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.property_range_category_value_state prcvs ON prcvs.range_category_value_id = wewc.ownership_property_value_id AND prcvs.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND wewc.eigendom_soort = 'KOOPWONING'
                GROUP BY prcvs.id, prcvs.name
            ),
            woningbloks_pricecategory_rent AS (
                SELECT
                    prcvs.id AS id, prcvs.name AS label,  prcvs.min AS min, prcvs.max AS max, SUM(wewc.amount) AS amount
                FROM
                    woningbloks w
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON w.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.property_range_category_value_state prcvs ON prcvs.range_category_value_id = wewc.rental_property_value_id AND prcvs.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND wewc.eigendom_soort != 'KOOPWONING'
                GROUP BY prcvs.id, prcvs.name
            ),
            current_project_users AS (
                SELECT
                    q.project_id    AS project_id,
                    array_agg(array[q.usergroup_id::TEXT, q.usergroup_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users
                FROM (
                    SELECT DISTINCT
                        ps.project_id as project_id,
                        us.user_id AS user_id,
                        LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                        us.last_name AS user_last_name,
                        us.first_name AS user_first_name,
                        ugs.usergroup_id AS usergroup_id,
                        ugs.naam AS usergroup_name
                    FROM
                        diwi.project_state ps
                            JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                            JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                            LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id AND utug.change_end_date IS NULL
                            LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                    WHERE
                        ps.change_end_date IS NULL AND ps.project_id = _project_uuid_
                    ) AS q
                GROUP BY q.project_id
            ),
            planning AS (
                SELECT  planningQuery.projectId             AS projectId,
                    planningQuery.projectName           AS projectName,
                    planningQuery.deliveryYear          AS deliveryYear,
                    COALESCE(planningQuery.totalValue, 0) AS amount
                FROM (
                WITH
                    active_projects AS (
                        SELECT
                            p.id, sms.date AS startDate, ems.date AS endDate
                        FROM
                            diwi.project p
                                JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                                JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                                JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                            WHERE
                                sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
                        ),
                    active_project_names AS (
                        SELECT
                            pnc.project_id, pnc.name
                        FROM
                            diwi.project_name_changelog pnc
                                JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                                JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
                        WHERE
                            sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND pnc.change_end_date IS NULL
                        ),
                    active_project_woningblok_totalvalue AS (
                        SELECT
                            w.project_id,
                            SUM(wmc.amount *
                                CASE wmc.mutation_kind
                                    WHEN 'CONSTRUCTION' THEN 1
                                    WHEN 'DEMOLITION' THEN -1
                            END) AS total_value
                        FROM
                            diwi.woningblok_mutatie_changelog wmc
                                JOIN diwi.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                                JOIN diwi.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                                JOIN diwi.woningblok w ON wmc.woningblok_id = w.id
                                JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                            WHERE
                                sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND wmc.change_end_date IS NULL
                            GROUP BY w.project_id
                        ),
                        future_projects AS (
                            SELECT
                                p.id, sms.date AS startDate, ems.date AS endDate, sms.milestone_id AS start_milestone_id
                            FROM
                                diwi.project p
                                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                            WHERE
                                sms.date > _snapshot_date_
                        ),
                        future_project_names AS (
                            SELECT
                                pnc.project_id, pnc.name
                            FROM
                                future_projects fp
                                    JOIN diwi.project_name_changelog pnc ON fp.id = pnc.project_id
                                        AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
                        ),
                        future_project_woningblok_totalvalue AS (
                            SELECT
                                w.project_id,
                                SUM(wmc.amount *
                                    CASE wmc.mutation_kind
                                        WHEN 'CONSTRUCTION' THEN 1
                                        WHEN 'DEMOLITION' THEN -1
                                    END) AS total_value
                            FROM
                                future_projects fp
                                    JOIN diwi.woningblok w ON fp.id = w.project_id
                                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                                        AND wmc.start_milestone_id = fp.start_milestone_id AND wmc.change_end_date IS NULL
                            GROUP BY w.project_id
                        ),
                        past_projects AS (
                            SELECT
                                p.id, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
                            FROM
                                diwi.project p
                                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                            WHERE
                                ems.date <= _snapshot_date_
                        ),
                        past_project_names AS (
                            SELECT
                                pnc.project_id, pnc.name
                            FROM
                                past_projects pp
                                    JOIN diwi.project_name_changelog pnc ON pp.id = pnc.project_id
                                        AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
                        ),
                        past_project_woningblok_totalvalue AS (
                            SELECT
                                w.project_id,
                                SUM(wmc.amount *
                                    CASE wmc.mutation_kind
                                        WHEN 'CONSTRUCTION' THEN 1
                                        WHEN 'DEMOLITION' THEN -1
                                    END) AS total_value
                            FROM
                                past_projects pp
                                    JOIN diwi.woningblok w ON pp.id = w.project_id
                                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                                        AND wmc.end_milestone_id = pp.end_milestone_id AND wmc.change_end_date IS NULL
                            GROUP BY w.project_id
                        ),
                        project_users AS (
                            SELECT
                                ps.project_id as project_id,
                                array_agg(us.user_id) AS userIds
                                    FROM diwi.project_state ps
                                        JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                                        JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                                        LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id AND utug.change_end_date IS NULL
                                        LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                                    WHERE
                                        ps.change_end_date IS NULL
                            GROUP BY ps.project_id
                        )
                    SELECT
                        ap.id                    AS projectId,
                        apn.name                 AS projectName,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.userIds                  AS projectOwners,
                        to_char( ap.endDate, 'YYYY') AS deliveryYear,
                        apwv.total_value         AS totalValue
                    FROM
                        active_projects ap
                            LEFT JOIN diwi.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
                            LEFT JOIN active_project_names apn ON apn.project_id = ap.id
                            LEFT JOIN active_project_woningblok_totalvalue apwv ON apwv.project_id = ap.id
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id
                    UNION
                    SELECT
                        fp.id                    AS projectId,
                        fpn.name                 AS projectName,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.userIds                  AS projectOwners,
                        to_char( fp.endDate, 'YYYY') AS deliveryYear,
                        fpwv.total_value         AS totalValue
                    FROM
                        future_projects fp
                            LEFT JOIN diwi.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
                            LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
                            LEFT JOIN future_project_woningblok_totalvalue fpwv ON fpwv.project_id = fp.id
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id
                    UNION
                    SELECT
                        pp.id                    AS projectId,
                        ppn.name                 AS projectName,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.userIds                  AS projectOwners,
                        to_char( pp.endDate, 'YYYY') AS deliveryYear,
                        ppwv.total_value         AS totalValue
                    FROM
                        past_projects pp
                            LEFT JOIN diwi.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
                            LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
                            LEFT JOIN past_project_woningblok_totalvalue ppwv ON ppwv.project_id = pp.id
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id
                ) AS planningQuery
                WHERE
                (
                    ( _user_uuid_ = ANY(planningQuery.projectOwners)) OR
                    ( _user_role_ IN ('User', 'UserPlus') AND planningQuery.confidentialityLevel != 'PRIVATE') OR
                    ( _user_role_ = 'Management' AND planningQuery.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
                    ( _user_role_ = 'Council' AND planningQuery.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
                )
            )
        SELECT
            p.id                               AS projectId,
            ps.confidentiality_level           AS confidentialityLevel,
            owners.users                       AS projectOwners,
            wpa.physicalAppearance             AS physicalAppearance,
            wpco.priceCategoryOwn              AS priceCategoryOwn,
            wpcr.priceCategoryRent             AS priceCategoryRent,
            pl.planning                        AS planning
        FROM
            current_project p
                LEFT JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('name', label, 'amount', amount))) AS physicalAppearance
                                FROM woningbloks_physical_appearance) AS wpa ON true
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('id', id, 'name', label, 'min', min, 'max', max, 'amount', amount))) AS priceCategoryOwn
                                FROM woningbloks_pricecategory_own) AS wpco ON true
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('id', id, 'name', label, 'min', min, 'max', max, 'amount', amount))) AS priceCategoryRent
                                FROM woningbloks_pricecategory_rent) AS wpcr ON true
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('projectId', planning.projectId, 'name', planning.projectName, 'year', deliveryYear, 'amount', amount))) AS planning
                                FROM planning) AS pl ON true
                LEFT JOIN current_project_users owners ON ps.project_id = owners.project_id
        ) AS q
WHERE q.projectId = _project_uuid_ AND
    (
      ( _user_uuid_::TEXT IN (select owners.id from unnest(q.projectOwners) with ordinality owners(id,n) where owners.n % 6 = 3)) OR
      ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
      ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
      ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
    )
LIMIT 1;
END;$$;
CREATE FUNCTION diwi.get_property_definitions(_cp_uuid_ uuid, _cp_object_type_ character varying, _cp_disabled_ boolean, _cp_type_ character varying) RETURNS TABLE(id uuid, name text, type diwi.property_type, objecttype diwi.maatwerk_object_soort, propertytype diwi.maatwerk_eigenschap_type, disabled boolean, categories jsonb, ordinals jsonb, ranges jsonb)
    LANGUAGE plpgsql
    AS $$
BEGIN
RETURN QUERY
SELECT cp.id                                                                                                                          AS id,
       cpState.property_name                                                                                                          AS name,
       cp.type                                                                                                                        AS type,
       cpState.property_object_type                                                                                                   AS objectType,
       cpState.property_type                                                                                                          AS propertyType,
       CASE WHEN cpState.change_end_date IS NULL THEN false ELSE TRUE END                                                             AS disabled,
       to_jsonb(array_agg(
           jsonb_build_object('id', catState.category_value_id, 'name', catState.value_label, 'disabled',
                              catState.change_end_date IS NOT NULL)) FILTER (WHERE catState.category_value_id IS NOT NULL))            AS categories,
       to_jsonb(array_agg(
           jsonb_build_object('id', ordState.ordinal_value_id, 'name', ordState.value_label, 'level', ordState.ordinal_level,
                              'disabled', ordState.change_end_date IS NOT NULL)) FILTER (WHERE ordState.ordinal_value_id IS NOT NULL)) AS ordinals,
       to_jsonb(array_agg(
           jsonb_build_object('id', rangeState.range_category_value_id, 'name', rangeState.name, 'min', rangeState.min, 'max', rangeState.max,
                              'disabled', rangeState.change_end_date IS NOT NULL)) FILTER (WHERE rangeState.range_category_value_id IS NOT NULL)) AS ranges
FROM diwi.property cp
    LEFT JOIN LATERAL (
        SELECT *
            FROM diwi.property_state cps
            WHERE cps.property_id = cp.id
            ORDER BY cps.change_start_date DESC
        LIMIT 1) cpState ON TRUE
    LEFT JOIN diwi.property_category_value cat ON cat.property_id = cp.id
    LEFT JOIN LATERAL (
        SELECT cs.category_value_id, cs.value_label, cs.change_end_date
            FROM diwi.property_category_value_state cs
            WHERE cs.category_value_id = cat.id
            ORDER BY cs.change_start_date DESC
        LIMIT 1) catState ON TRUE
    LEFT JOIN diwi.property_ordinal_value ord ON ord.property_id = cp.id
    LEFT JOIN LATERAL (
        SELECT os.ordinal_value_id, os.value_label, os.ordinal_level, os.change_end_date
            FROM diwi.property_ordinal_value_state os
            WHERE os.ordinal_value_id = ord.id
            ORDER BY os.change_start_date DESC
        LIMIT 1) ordState ON TRUE
    LEFT JOIN diwi.property_range_category_value rng ON rng.property_id = cp.id
    LEFT JOIN LATERAL (
        SELECT rs.range_category_value_id, rs.name, rs.min, rs.max, rs.change_end_date
            FROM diwi.property_range_category_value_state rs
            WHERE rs.range_category_value_id = rng.id
            ORDER BY rs.change_start_date DESC
        LIMIT 1) rangeState ON TRUE
WHERE
    CASE
        WHEN _cp_uuid_ IS NOT NULL THEN cp.id = _cp_uuid_
        WHEN _cp_object_type_ IS NOT NULL THEN cpState.property_object_type = CAST (_cp_object_type_ AS diwi.maatwerk_object_soort)
        ELSE 1 = 1
    END
    AND
    CASE
        WHEN _cp_disabled_ IS NULL THEN 1 = 1
        WHEN _cp_disabled_ IS TRUE THEN cpState.change_end_date IS NOT NULL
        WHEN _cp_disabled_ IS FALSE THEN cpState.change_end_date IS NULL
    END
    AND
    CASE
        WHEN _cp_type_ IS NOT NULL THEN cp.type = CAST (_cp_type_ AS diwi.property_type)
        ELSE 1 = 1
    END
GROUP BY cp.id, cpState.property_name, cp.type, cpState.property_object_type, cpState.property_type, disabled
ORDER BY cpState.property_name;
END;$$;
CREATE FUNCTION diwi.set_end_date_now() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	 DECLARE BEGIN
	 IF OLD.change_end_date IS NULL THEN
        OLD.change_end_date = NOW();
        NEW."id" = 10;
        INSERT INTO diwi.project_priorisering_value_state SELECT NEW.*;
	 END If;
	 RETURN OLD;
	END;
	$$;
CREATE FUNCTION diwi.set_start_date_now() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	 DECLARE BEGIN
	 NEW.change_start_date = NOW();
	 RETURN NEW;
	END;
	$$;
SET default_tablespace = '';
SET default_table_access_method = heap;
CREATE TABLE diwi.blueprint (
    id uuid NOT NULL
);
CREATE TABLE diwi.blueprint_state (
    id uuid NOT NULL,
    blueprint_id uuid NOT NULL,
    name text NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);
CREATE TABLE diwi.blueprint_to_element (
    id uuid NOT NULL,
    blueprint_state_id uuid NOT NULL,
    element diwi.blueprint_element
);
CREATE TABLE diwi.blueprint_to_usergroup (
    id uuid NOT NULL,
    blueprint_state_id uuid NOT NULL,
    usergroup_id uuid NOT NULL
);
CREATE TABLE diwi.document (
    id uuid NOT NULL,
    project_id uuid NOT NULL
);
CREATE TABLE diwi.document_soort (
    id uuid NOT NULL
);
CREATE TABLE diwi.document_soort_state (
    id uuid NOT NULL,
    document_soort_id uuid NOT NULL,
    waarde_label text NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.document_state (
    id uuid NOT NULL,
    document_id uuid NOT NULL,
    milestone_id uuid NOT NULL,
    naam text NOT NULL,
    notitie text,
    file_path text,
    change_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi.confidentiality NOT NULL
);
CREATE TABLE diwi.document_state_soort_value (
    id uuid NOT NULL,
    document_state_id uuid NOT NULL,
    document_soort_id uuid NOT NULL
);
CREATE TABLE diwi.milestone (
    id uuid NOT NULL,
    project_id uuid NOT NULL
);
CREATE TABLE diwi.milestone_state (
    id uuid NOT NULL,
    milestone_id uuid NOT NULL,
    date date NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    status diwi.milestone_status NOT NULL,
    omschrijving text,
    change_user_id uuid
);
CREATE TABLE diwi.plan (
    id uuid NOT NULL
);
CREATE TABLE diwi.plan_conditie (
    id uuid NOT NULL
);
CREATE TABLE diwi.plan_conditie_doelgroep (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_doelgroep_value (
    id uuid NOT NULL,
    plan_conditie_doelgroep_id uuid NOT NULL,
    doelgroep diwi.doelgroep NOT NULL
);
CREATE TABLE diwi.plan_conditie_eigendom_en_waarde (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    waarde_value_type diwi.value_type,
    waarde_value integer,
    huurbedrag_value_type diwi.value_type,
    huurbedrag_value integer,
    waarde_value_range int8range,
    huurbedrag_value_range int8range,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_eigendom_en_waarde_soort_value (
    id uuid NOT NULL,
    plan_conditie_eigendom_en_waarde_id uuid NOT NULL,
    eigendom_soort diwi.eigendom_soort NOT NULL
);
CREATE TABLE diwi.plan_conditie_grondpositie (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_grondpositie_value (
    id uuid NOT NULL,
    plan_conditie_grondpositie_id uuid NOT NULL,
    grondpositie diwi.grondpositie NOT NULL
);
CREATE TABLE diwi.plan_conditie_grootte (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi.value_type NOT NULL,
    value double precision,
    value_range numrange,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_kadastraal (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_kadastraal_value (
    id uuid NOT NULL,
    plan_conditie_kadastraal_id uuid NOT NULL,
    brk_gemeente_code text NOT NULL,
    brk_sectie text,
    brk_perceelnummer integer,
    brk_selectie text
);
CREATE TABLE diwi.plan_conditie_maatwerk_boolean (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    eigenschap_waarde boolean NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_maatwerk_categorie (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_maatwerk_categorie_value (
    id uuid NOT NULL,
    plan_conditie_maatwerk_categorie_id uuid NOT NULL,
    eigenschap_waarde_id uuid NOT NULL
);
CREATE TABLE diwi.plan_conditie_maatwerk_numeriek (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    value double precision,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi.value_type NOT NULL,
    value_range numrange,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_maatwerk_ordinaal (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi.value_type NOT NULL,
    value_id uuid,
    min_value_id uuid,
    max_value_id uuid,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_programmering (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    programmering boolean NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_state (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    plan_id uuid NOT NULL,
    conditie_type diwi.conditie_type NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_type_en_fysiek (
    id uuid NOT NULL,
    plan_conditie_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_conditie_type_en_fysiek_fysiek_value (
    id uuid NOT NULL,
    plan_conditie_type_en_fysiek_id uuid NOT NULL,
    fysiek_voorkomen diwi.fysiek_voorkomen NOT NULL
);
CREATE TABLE diwi.plan_conditie_type_en_fysiek_type_value (
    id uuid NOT NULL,
    plan_conditie_type_en_fysiek_id uuid NOT NULL,
    woning_type diwi.woning_type NOT NULL
);
CREATE TABLE diwi.plan_soort (
    id uuid NOT NULL
);
CREATE TABLE diwi.plan_soort_state (
    id uuid NOT NULL,
    plan_soort_id uuid NOT NULL,
    waarde_label text NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.plan_state (
    id uuid NOT NULL,
    plan_id uuid NOT NULL,
    naam text NOT NULL,
    deadline date NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi.confidentiality NOT NULL,
    create_user_id uuid NOT NULL,
    doel_soort diwi.doel_soort NOT NULL,
    doel_richting diwi.doel_richting NOT NULL,
    doel_waarde double precision NOT NULL,
    start_datum date NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.plan_state_soort_value (
    id uuid NOT NULL,
    plan_state_id uuid NOT NULL,
    plan_soort_id uuid NOT NULL
);
CREATE TABLE diwi.project (
    id uuid NOT NULL
);
CREATE TABLE diwi.project_category_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid,
    property_id uuid NOT NULL
);
CREATE TABLE diwi.project_category_changelog_value (
    id uuid NOT NULL,
    project_category_changelog_id uuid NOT NULL,
    property_value_id uuid NOT NULL
);
CREATE TABLE diwi.project_duration_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.project_fase_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    project_fase diwi.project_phase NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.project_filiatie (
    id uuid NOT NULL,
    old_project_id uuid NOT NULL,
    new_project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.project_maatwerk_boolean_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    value boolean NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    eigenschap_id uuid NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.project_maatwerk_numeriek_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    value double precision,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    eigenschap_id uuid NOT NULL,
    value_type diwi.value_type NOT NULL,
    value_range numrange,
    change_user_id uuid
);
CREATE TABLE diwi.project_name_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    name text NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.project_ordinal_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    value_id uuid,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi.value_type NOT NULL,
    min_value_id uuid,
    max_value_id uuid,
    change_user_id uuid,
    property_id uuid NOT NULL
);
CREATE TABLE diwi.project_plan_type_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.project_plan_type_changelog_value (
    id uuid NOT NULL,
    changelog_id uuid NOT NULL,
    plan_type diwi.plan_type NOT NULL
);
CREATE TABLE diwi.project_planologische_planstatus_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.project_planologische_planstatus_changelog_value (
    id uuid NOT NULL,
    planologische_planstatus_changelog_id uuid NOT NULL,
    planologische_planstatus diwi.planologische_planstatus NOT NULL
);
CREATE TABLE diwi.project_registry_link_changelog (
    id uuid NOT NULL,
    project_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);
CREATE TABLE diwi.project_registry_link_changelog_value (
    id uuid NOT NULL,
    project_registry_link_changelog_id uuid NOT NULL,
    brk_gemeente_code text NOT NULL,
    brk_sectie text,
    brk_perceelnummer integer,
    plot_feature json NOT NULL,
    subselection_geometry json
);
CREATE TABLE diwi.project_state (
    id uuid NOT NULL,
    project_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    confidentiality_level diwi.confidentiality NOT NULL,
    project_colour text NOT NULL,
    change_user_id uuid,
    latitude double precision,
    longitude double precision
);
CREATE TABLE diwi.project_text_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    project_id uuid NOT NULL,
    value text NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    property_id uuid NOT NULL
);
CREATE TABLE diwi.property (
    id uuid NOT NULL,
    type diwi.property_type DEFAULT 'CUSTOM'::diwi.property_type NOT NULL
);
CREATE TABLE diwi.property_category_value (
    id uuid NOT NULL,
    property_id uuid NOT NULL
);
CREATE TABLE diwi.property_category_value_state (
    id uuid NOT NULL,
    category_value_id uuid NOT NULL,
    value_label text NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.property_ordinal_value (
    id uuid NOT NULL,
    property_id uuid NOT NULL
);
CREATE TABLE diwi.property_ordinal_value_state (
    id uuid NOT NULL,
    ordinal_value_id uuid NOT NULL,
    value_label text NOT NULL,
    ordinal_level integer NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.property_range_category_value (
    id uuid NOT NULL,
    property_id uuid NOT NULL
);
CREATE TABLE diwi.property_range_category_value_state (
    id uuid NOT NULL,
    range_category_value_id uuid NOT NULL,
    name text NOT NULL,
    min numeric(16,6) NOT NULL,
    max numeric(16,6),
    create_user_id uuid NOT NULL,
    change_user_id uuid,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);
CREATE TABLE diwi.property_state (
    id uuid NOT NULL,
    property_name text NOT NULL,
    property_type diwi.maatwerk_eigenschap_type NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    property_id uuid NOT NULL,
    property_object_type diwi.maatwerk_object_soort NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.software_module_rights (
    id uuid NOT NULL,
    organization_id uuid NOT NULL,
    software_module diwi.software_module NOT NULL,
    confidentiality_level diwi.confidentiality NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    software_rights diwi.software_rights NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi."user" (
    id uuid NOT NULL,
    "system_user" boolean DEFAULT false NOT NULL
);
CREATE TABLE diwi.user_state (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    identity_provider_id text NOT NULL,
    last_name text NOT NULL,
    first_name text NOT NULL,
    change_user_id uuid,
    role diwi.user_role NOT NULL,
    email text,
    organization text,
    phone_number text,
    contact_person text,
    department text,
    prefixes text
);
CREATE TABLE diwi.user_to_usergroup (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    usergroup_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.usergroup (
    id uuid NOT NULL,
    single_user boolean DEFAULT true NOT NULL
);
CREATE TABLE diwi.usergroup_state (
    id uuid NOT NULL,
    usergroup_id uuid NOT NULL,
    parent_usergroup_id uuid,
    naam text NOT NULL,
    change_end_date timestamp with time zone,
    change_start_date timestamp with time zone NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.usergroup_to_document (
    id uuid NOT NULL,
    usergroup_id uuid NOT NULL,
    document_id uuid NOT NULL,
    change_end_date timestamp with time zone,
    change_start_date timestamp with time zone NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.usergroup_to_plan (
    id uuid NOT NULL,
    usergroup_id uuid NOT NULL,
    plan_id uuid NOT NULL,
    change_end_date timestamp with time zone,
    change_start_date timestamp with time zone NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.usergroup_to_project (
    id uuid NOT NULL,
    usergroup_id uuid NOT NULL,
    project_id uuid NOT NULL,
    change_end_date timestamp with time zone,
    change_start_date timestamp with time zone NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok (
    id uuid NOT NULL,
    project_id uuid NOT NULL
);
CREATE TABLE diwi.woningblok_deliverydate_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    latest_deliverydate date NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid,
    earliest_deliverydate date NOT NULL
);
CREATE TABLE diwi.woningblok_doelgroep_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_doelgroep_changelog_value (
    id uuid NOT NULL,
    woningblok_doelgroep_changelog_id uuid NOT NULL,
    amount integer NOT NULL,
    property_value_id uuid NOT NULL
);
CREATE TABLE diwi.woningblok_duration_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_eigendom_en_waarde_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    waarde_value integer,
    huurbedrag_value integer,
    change_user_id uuid,
    amount integer NOT NULL,
    eigendom_soort diwi.eigendom_soort NOT NULL,
    waarde_value_range int4range,
    huurbedrag_value_range int4range,
    ownership_property_value_id uuid,
    rental_property_value_id uuid
);
CREATE TABLE diwi.woningblok_grondpositie_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_grondpositie_changelog_value (
    id uuid NOT NULL,
    woningblok_grondpositie_changelog_id uuid NOT NULL,
    grondpositie diwi.grondpositie NOT NULL,
    amount integer NOT NULL
);
CREATE TABLE diwi.woningblok_grootte_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value double precision,
    value_range numrange,
    value_type diwi.value_type NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_maatwerk_boolean_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    value boolean NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    eigenschap_id uuid NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_maatwerk_categorie_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid,
    eigenschap_id uuid NOT NULL
);
CREATE TABLE diwi.woningblok_maatwerk_categorie_changelog_value (
    id uuid NOT NULL,
    woningblok_maatwerk_categorie_changelog_id uuid NOT NULL,
    eigenschap_waarde_id uuid NOT NULL
);
CREATE TABLE diwi.woningblok_maatwerk_numeriek_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    value double precision,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    eigenschap_id uuid NOT NULL,
    value_type diwi.value_type NOT NULL,
    value_range numrange,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_maatwerk_ordinaal_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    value_id uuid,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    value_type diwi.value_type NOT NULL,
    min_value_id uuid,
    max_value_id uuid,
    change_user_id uuid,
    eigenschap_id uuid NOT NULL
);
CREATE TABLE diwi.woningblok_maatwerk_text_changelog (
    id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    value text NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    eigenschap_id uuid NOT NULL
);
CREATE TABLE diwi.woningblok_mutatie_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    amount integer NOT NULL,
    change_user_id uuid,
    mutation_kind diwi.mutation_kind NOT NULL
);
CREATE TABLE diwi.woningblok_naam_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    naam text NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_programmering_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    programmering boolean NOT NULL,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_state (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_user_id uuid,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);
CREATE TABLE diwi.woningblok_type_en_fysiek_changelog (
    id uuid NOT NULL,
    woningblok_id uuid NOT NULL,
    start_milestone_id uuid NOT NULL,
    end_milestone_id uuid NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone,
    change_user_id uuid
);
CREATE TABLE diwi.woningblok_type_en_fysiek_changelog_fysiek_value (
    id uuid NOT NULL,
    woningblok_type_en_fysiek_voorkomen_changelog_id uuid NOT NULL,
    amount integer NOT NULL,
    property_value_id uuid NOT NULL
);
CREATE TABLE diwi.woningblok_type_en_fysiek_changelog_type_value (
    id uuid NOT NULL,
    woningblok_type_en_fysiek_voorkomen_changelog_id uuid NOT NULL,
    woning_type diwi.woning_type NOT NULL,
    amount integer NOT NULL
);
CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);
INSERT INTO diwi.milestone (id, project_id) VALUES ('0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc');
INSERT INTO diwi.milestone (id, project_id) VALUES ('0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc');
INSERT INTO diwi.milestone (id, project_id) VALUES ('0191daac-a46b-7879-ab2e-c98da02cd62d', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc');
INSERT INTO diwi.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving, change_user_id) VALUES ('0191daa9-f078-7c5f-8a8d-626103ed2d94', '0191daa9-f078-7d49-8481-f26d5dffa873', '2024-01-01', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:40:55.916413+02', NULL, 'GEPLAND', NULL, NULL);
INSERT INTO diwi.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving, change_user_id) VALUES ('0191daa9-f078-736d-b789-692d597fd74a', '0191daa9-f078-72da-8d73-d8c66df01d73', '2030-12-31', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:40:55.916413+02', NULL, 'GEPLAND', NULL, NULL);
INSERT INTO diwi.milestone_state (id, milestone_id, date, create_user_id, change_start_date, change_end_date, status, omschrijving, change_user_id) VALUES ('0191daac-a46b-7048-9688-308fc1386f53', '0191daac-a46b-7879-ab2e-c98da02cd62d', '2024-09-10', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:53.067432+02', NULL, 'GEPLAND', '2024-09-10', NULL);
INSERT INTO diwi.project (id) VALUES ('0191daa9-f078-77f1-bbc7-e3b15bb0f0cc');
INSERT INTO diwi.project_duration_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('0191daa9-f079-778e-adb2-e618373a5be8', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:40:55.916413+02', NULL, NULL);
INSERT INTO diwi.project_fase_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, project_fase, change_user_id) VALUES ('0191daa9-f079-79a3-87a5-4c8bfac477da', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:40:55.916413+02', NULL, '_1_CONCEPT', NULL);
INSERT INTO diwi.project_name_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, name, change_user_id) VALUES ('0191daa9-f079-7554-8e1f-451446608a8a', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:40:55.916413+02', NULL, 'test', NULL);
INSERT INTO diwi.project_planologische_planstatus_changelog (id, start_milestone_id, end_milestone_id, project_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('0191daa9-f079-77c8-8f9e-ab2c9d3435c7', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:40:55.916413+02', NULL, NULL);
INSERT INTO diwi.project_registry_link_changelog (id, project_id, start_milestone_id, end_milestone_id, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0191daac-a46c-7c57-8f21-49860f0d3c83', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', '0191daac-a46b-7879-ab2e-c98da02cd62d', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, '2024-09-10 08:43:53.066173+02', NULL);
INSERT INTO diwi.project_registry_link_changelog_value (id, project_registry_link_changelog_id, brk_gemeente_code, brk_sectie, brk_perceelnummer, plot_feature, subselection_geometry) VALUES ('0191daac-a46c-7103-a34e-f79eb9c1746f', '0191daac-a46c-7c57-8f21-49860f0d3c83', '534', 'R', 169, '{"type":"FeatureCollection","features":[{"id":"perceel.a50e2e31-5985-4e6a-8ec3-0e633b35493b","type":"Feature","bbox":[568289.6714348193,6894506.668136177,586088.2904181624,6901938.659254265],"geometry":{"type":"Polygon","coordinates":[[[586075.8578339716,6901938.659254265],[568289.6714348193,6901888.853878047],[573874.3736895111,6894506.668136177],[586088.2904181624,6894536.671606382],[586075.8578339716,6901938.659254265]]]},"properties":{"AKRKadastraleGemeenteCodeCode":"583","AKRKadastraleGemeenteCodeWaarde":"LLS00","beginGeldigheid":"2016-02-16T17:38:46Z","identificatieLokaalID":"89250016970000","identificatieNamespace":"NL.IMKAD.KadastraalObject","kadastraleGemeenteCode":"534","kadastraleGemeenteWaarde":"Lelystad","kadastraleGrootteWaarde":"41135100","perceelnummer":"169","perceelnummerPlaatscoordinaatX":"141760.997","perceelnummerPlaatscoordinaatY":"506310.657","perceelnummerRotatie":"0","perceelnummerVerschuivingDeltaX":"0","perceelnummerVerschuivingDeltaY":"0","sectie":"R","soortGrootteCode":"1","soortGrootteWaarde":"Vastgesteld","statusHistorieCode":"G","statusHistorieWaarde":"Geldig","tijdstipRegistratie":"2016-02-16T17:38:46Z","volgnummer":"0"}}],"crs":{"properties":{"name":"urn:ogc:def:crs:EPSG::3857"},"type":"name"}}', NULL);
INSERT INTO diwi.project_state (id, project_id, create_user_id, change_start_date, change_end_date, confidentiality_level, project_colour, change_user_id, latitude, longitude) VALUES ('0191daac-a6a2-7983-a821-a665044e9d92', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:53.614854+02', NULL, 'INTERNAL_CIVIL', '#FF5733', NULL, 577188.9809264909, 6898222.663695221);
INSERT INTO diwi.project_state (id, project_id, create_user_id, change_start_date, change_end_date, confidentiality_level, project_colour, change_user_id, latitude, longitude) VALUES ('0191daa9-f079-7adc-bcfd-d51b986ef400', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:40:55.916413+02', '2024-09-10 08:43:53.614854+02', 'INTERNAL_CIVIL', '#FF5733', '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, NULL);
INSERT INTO diwi.property (id, type) VALUES ('4b45c60e-9708-42e0-90b1-add84eccbbde', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('14e60f22-0b40-41da-9719-9d8fa44cef50', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('1e2bc8ce-52a5-4477-906b-5a05f5f687ee', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('6777a7d2-f4d1-4726-b729-845f11e1ddf3', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('b590f5e3-de6e-4edd-8878-e62208135f57', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('83b0089b-c0c6-429f-8e12-26994d359ad0', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('4f30fe9d-4598-44c4-aa0f-ed9c69689d9a', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('0bb51933-743c-45c5-a6d4-722ac52ffa85', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('560aa6bc-1852-43f1-945d-cb55481b20c3', 'FIXED');
INSERT INTO diwi.property (id, type) VALUES ('fc95b3b7-3592-42b8-971e-b0bebe792c49', 'FIXED');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('51a8ff9a-8b7c-4ca5-9a6b-5d249cf1e200', '4b45c60e-9708-42e0-90b1-add84eccbbde');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('301dba25-c1d5-4081-bfda-4518a428116e', '4b45c60e-9708-42e0-90b1-add84eccbbde');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('72949299-5e94-49e2-ac98-0dd482ba59e1', '4b45c60e-9708-42e0-90b1-add84eccbbde');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('b58ceec9-5230-4af3-9fe6-297092cd8d77', '4b45c60e-9708-42e0-90b1-add84eccbbde');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('a533b308-68ec-48d3-9ab8-3d9d9b941335', '4b45c60e-9708-42e0-90b1-add84eccbbde');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('3ce5a122-7b25-401e-955e-08f5933543a6', '4b45c60e-9708-42e0-90b1-add84eccbbde');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('e6c042f4-1cf7-4307-961c-158043eab9d9', '14e60f22-0b40-41da-9719-9d8fa44cef50');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('eaaea4fc-fbdf-4f36-a29f-9416f2f42d49', '14e60f22-0b40-41da-9719-9d8fa44cef50');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('cd161a03-190b-4c55-8c3b-23764edad824', '14e60f22-0b40-41da-9719-9d8fa44cef50');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('6ca07d6e-9bb1-4519-8ec5-0b714109fa9e', '14e60f22-0b40-41da-9719-9d8fa44cef50');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('ccc63ef0-ab36-4e74-8e95-a1bbf6ac0fc4', '14e60f22-0b40-41da-9719-9d8fa44cef50');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('0fb40eb0-daa1-42d8-bd92-4ba30de8bf99', '14e60f22-0b40-41da-9719-9d8fa44cef50');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('b8337223-de20-44b6-82a0-416b8bf99dab', '83b0089b-c0c6-429f-8e12-26994d359ad0');
INSERT INTO diwi.property_category_value (id, property_id) VALUES ('81aaa040-4ad8-494f-bc3e-903410a11aa5', '83b0089b-c0c6-429f-8e12-26994d359ad0');
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('b24dec74-f36b-4d2b-8d8c-f51f38cd9ae0', '51a8ff9a-8b7c-4ca5-9a6b-5d249cf1e200', 'Regulier', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.661526+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('00e44b29-22a9-4995-860b-0f157a5f237c', '301dba25-c1d5-4081-bfda-4518a428116e', 'Jongeren', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.661526+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('c3219d62-fee2-4b13-94ff-642beaa9cd8a', '72949299-5e94-49e2-ac98-0dd482ba59e1', 'Student', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.661526+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('8d6d64e3-a368-49c5-ae40-2fbb65aace9c', 'b58ceec9-5230-4af3-9fe6-297092cd8d77', 'Ouderen', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.661526+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('3e8b3c68-f22f-4a82-b58a-df73544e6f9e', 'a533b308-68ec-48d3-9ab8-3d9d9b941335', 'GHZ', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.661526+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('7b025a6b-b330-47ac-ba29-72ff03bca8b1', '3ce5a122-7b25-401e-955e-08f5933543a6', 'Grote gezinnen', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.661526+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('1f3688ca-2fe2-4a96-b6f6-c7714297b079', 'e6c042f4-1cf7-4307-961c-158043eab9d9', 'Tussenwoning', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.676919+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('9c57081b-1c04-47b2-bb84-7d420425dd13', 'eaaea4fc-fbdf-4f36-a29f-9416f2f42d49', 'Hoekwoning', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.676919+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('445279aa-2f35-4aa6-a7a9-d5e29d5ee520', 'cd161a03-190b-4c55-8c3b-23764edad824', 'Twee onder een kap', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.676919+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('aafb0090-1112-48d3-8649-6040fc547ca9', '6ca07d6e-9bb1-4519-8ec5-0b714109fa9e', 'Vrijstaand', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.676919+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('9c0b365a-2328-4b4d-9ea5-28b2fff99eeb', 'ccc63ef0-ab36-4e74-8e95-a1bbf6ac0fc4', 'Portiekflat', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.676919+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('6a41855b-d556-4501-a208-90454b526566', '0fb40eb0-daa1-42d8-bd92-4ba30de8bf99', 'Gallerijflat', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.676919+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('09e16ebb-c3eb-4cb2-9eb1-a1fc66f3c235', 'b8337223-de20-44b6-82a0-416b8bf99dab', 'Opdrachtgever', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.733085+02', NULL, NULL);
INSERT INTO diwi.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('c9d1af0a-1479-4a36-b77d-0f5b823d8e85', '81aaa040-4ad8-494f-bc3e-903410a11aa5', 'Vergunningverlener', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.733085+02', NULL, NULL);
INSERT INTO diwi.property_ordinal_value (id, property_id) VALUES ('0e39c961-d07b-4a42-aa4f-f4c11b545979', '4f30fe9d-4598-44c4-aa0f-ed9c69689d9a');
INSERT INTO diwi.property_ordinal_value (id, property_id) VALUES ('ebb551b1-0672-49da-b44a-a697df175c06', '4f30fe9d-4598-44c4-aa0f-ed9c69689d9a');
INSERT INTO diwi.property_ordinal_value (id, property_id) VALUES ('01d66dbb-7e72-480e-8694-d8305ee61c4f', '4f30fe9d-4598-44c4-aa0f-ed9c69689d9a');
INSERT INTO diwi.property_ordinal_value_state (id, ordinal_value_id, value_label, ordinal_level, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('089a5d2e-5a4d-4c19-aac3-4001a7e3eb67', '0e39c961-d07b-4a42-aa4f-f4c11b545979', 'Laag', 1, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.569704+02', NULL, NULL);
INSERT INTO diwi.property_ordinal_value_state (id, ordinal_value_id, value_label, ordinal_level, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('7d2f0278-eb82-42ac-bacf-d4ffdd3b91a4', 'ebb551b1-0672-49da-b44a-a697df175c06', 'Middel', 2, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.569704+02', NULL, NULL);
INSERT INTO diwi.property_ordinal_value_state (id, ordinal_value_id, value_label, ordinal_level, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('80509d7d-8a47-444b-8e23-8a9e0cc6eb3e', '01d66dbb-7e72-480e-8694-d8305ee61c4f', 'Hoog', 3, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.569704+02', NULL, NULL);
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('c58249ca-0bfa-4efd-be51-cc0bb4a3e809', '560aa6bc-1852-43f1-945d-cb55481b20c3');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('024f0b9d-a7f8-47c1-ab31-7553d3abb087', '560aa6bc-1852-43f1-945d-cb55481b20c3');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('a91a033d-7a4b-4a60-8367-7de7e62cdcf3', '560aa6bc-1852-43f1-945d-cb55481b20c3');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('a78b34a1-9250-4967-8851-f70692677bc0', '560aa6bc-1852-43f1-945d-cb55481b20c3');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('725d22e8-2c7e-4ec5-ac63-857eb85ece0a', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('ab6d92a5-32f1-4bb1-aea8-416c32546775', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('b6d1c8d7-2d31-4889-bfee-9e0199d6f3c6', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('e05e04fe-4670-4db5-acde-f422b95fb1c8', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('c34d7e77-e423-4726-b647-06b01ea17b16', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('a7f23802-3bfb-47fb-a4fd-4a540f40da00', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('ab54fcb9-d0fb-4e0b-a27b-6ddd6834c16a', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('0191daaf-a540-71f5-9942-09d515058549', '560aa6bc-1852-43f1-945d-cb55481b20c3');
INSERT INTO diwi.property_range_category_value (id, property_id) VALUES ('0191daaf-f08d-7f9b-aaa6-1db056e999a0', 'fc95b3b7-3592-42b8-971e-b0bebe792c49');
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0e4ea09b-c0c3-4caf-bd25-dcb255b3c466', 'c58249ca-0bfa-4efd-be51-cc0bb4a3e809', 'Koop: 30000 - ', 30000.000000, NULL, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('b61c15c3-5779-4c9a-a643-6cd456e3b20e', '024f0b9d-a7f8-47c1-ab31-7553d3abb087', 'Koop: 20000 - 30001', 20000.000000, 30001.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('dcb8cb4e-1718-4b9a-9854-2c0f13e93d43', 'a91a033d-7a4b-4a60-8367-7de7e62cdcf3', 'Koop: 10000 - 20001', 10000.000000, 20001.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('f4fa8440-aae3-40bc-9df2-bfe1bcce13ba', 'a78b34a1-9250-4967-8851-f70692677bc0', 'Koop: 100000', 100000.000000, 100000.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('36163b3a-8c22-4fd4-898c-b6198436e907', '725d22e8-2c7e-4ec5-ac63-857eb85ece0a', 'Huur: 10000 - 20001', 10000.000000, 20001.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('fc47684f-8af3-4a00-b06c-cb2b9050d02c', 'ab6d92a5-32f1-4bb1-aea8-416c32546775', 'Huur: 50000 - 60001', 50000.000000, 60001.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('49008b1b-4318-4cd0-832f-6d968077b5fa', 'b6d1c8d7-2d31-4889-bfee-9e0199d6f3c6', 'Huur: 40000 - 50001', 40000.000000, 50001.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('74dc3161-7f5a-4cfc-8305-6ac1b294c52b', 'e05e04fe-4670-4db5-acde-f422b95fb1c8', 'Huur: 60000 -', 60000.000000, NULL, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('caa08110-0c7f-43b9-a7c4-69282de66ccb', 'c34d7e77-e423-4726-b647-06b01ea17b16', 'Huur: 20000 - 30001', 20000.000000, 30001.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0e3244bf-d98d-423c-b8ea-10b518973df2', 'a7f23802-3bfb-47fb-a4fd-4a540f40da00', 'Huur: 30000 - ', 30000.000000, NULL, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('9623fd62-5075-42a3-aabb-aa96a2421d18', 'ab54fcb9-d0fb-4e0b-a27b-6ddd6834c16a', 'Huur: 100000', 100000.000000, 100000.000000, 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL, '2024-09-10 08:45:36.071733+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0191daaf-a548-790c-a675-3b7f528e17c0', '0191daaf-a540-71f5-9942-09d515058549', 'Koop 1.6', 100.000000, 200.000000, '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, '2024-09-10 08:47:09.843901+02', NULL);
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0191daaf-f08d-7d05-9afd-c4b7d59e4037', '0191daaf-f08d-7f9b-aaa6-1db056e999a0', 'Huur 1.6', 100.000000, 200.000000, '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, '2024-09-10 08:47:29.159038+02', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('e614ee02-a868-4ca0-a066-c3b31bb82570', 'targetGroup', 'CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.661526+02', NULL, '4b45c60e-9708-42e0-90b1-add84eccbbde', 'WONINGBLOK', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('c3019976-cdb8-490d-af47-c71b7062e17e', 'physicalAppearance', 'CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.676919+02', NULL, '14e60f22-0b40-41da-9719-9d8fa44cef50', 'WONINGBLOK', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('eac645ed-5ae0-4b9c-a3f0-1c5c51c6e02a', 'municipality', 'CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.700801+02', NULL, '1e2bc8ce-52a5-4477-906b-5a05f5f687ee', 'PROJECT', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('fa2feed3-9125-420b-be01-03cbcbd45072', 'district', 'CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.700801+02', NULL, '6777a7d2-f4d1-4726-b729-845f11e1ddf3', 'PROJECT', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('4e54d1ea-4f1c-400c-9f65-7eee8b806a00', 'neighbourhood', 'CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.700801+02', NULL, 'b590f5e3-de6e-4edd-8878-e62208135f57', 'PROJECT', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('00206c32-b6c6-4250-a251-0740a62e874b', 'municipalityRole', 'CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.733085+02', NULL, '83b0089b-c0c6-429f-8e12-26994d359ad0', 'PROJECT', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('7ae0cfeb-ac1e-4919-9348-422861c4f8dd', 'priority', 'ORDINAL', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.746956+02', NULL, '4f30fe9d-4598-44c4-aa0f-ed9c69689d9a', 'PROJECT', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('4376743b-a714-45f1-9baa-7dd8be539176', 'geometry', 'TEXT', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.770577+02', NULL, '0bb51933-743c-45c5-a6d4-722ac52ffa85', 'PROJECT', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('853dd9f0-3227-49c0-a296-33e7c4d4f99f', 'priceRangeBuy', 'RANGE_CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:45:36.01635+02', NULL, '560aa6bc-1852-43f1-945d-cb55481b20c3', 'WONINGBLOK', NULL);
INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, change_end_date, property_id, property_object_type, change_user_id) VALUES ('260165b1-5121-49cf-91fd-50da574a4b58', 'priceRangeRent', 'RANGE_CATEGORY', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:45:36.01635+02', NULL, 'fc95b3b7-3592-42b8-971e-b0bebe792c49', 'WONINGBLOK', NULL);
INSERT INTO diwi."user" (id, "system_user") VALUES ('d9cedd33-a3b2-49b1-8f33-b7315aca84fa', true);
INSERT INTO diwi."user" (id, "system_user") VALUES ('88e5153b-0c16-40b2-96bd-d1c52a0c2233', false);
INSERT INTO diwi."user" (id, "system_user") VALUES ('ad905510-b444-4eba-8db8-e901e9f4db72', false);
INSERT INTO diwi."user" (id, "system_user") VALUES ('0191daa8-72a7-7484-803f-c41451b33e1b', false);
INSERT INTO diwi.user_state (id, user_id, create_user_id, change_start_date, change_end_date, identity_provider_id, last_name, first_name, change_user_id, role, email, organization, phone_number, contact_person, department, prefixes) VALUES ('88e5153b-0c16-40b2-96bd-d1c52a0c2233', '88e5153b-0c16-40b2-96bd-d1c52a0c2233', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.558552+02', NULL, '', '1', 'Demo user', NULL, 'UserPlus', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO diwi.user_state (id, user_id, create_user_id, change_start_date, change_end_date, identity_provider_id, last_name, first_name, change_user_id, role, email, organization, phone_number, contact_person, department, prefixes) VALUES ('ad905510-b444-4eba-8db8-e901e9f4db72', 'ad905510-b444-4eba-8db8-e901e9f4db72', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.558552+02', NULL, '', '2', 'Demo user', NULL, 'UserPlus', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO diwi.user_state (id, user_id, create_user_id, change_start_date, change_end_date, identity_provider_id, last_name, first_name, change_user_id, role, email, organization, phone_number, contact_person, department, prefixes) VALUES ('0191daa8-72b1-7078-986a-421dd7c2b7aa', '0191daa8-72a7-7484-803f-c41451b33e1b', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:39:18.153402+02', NULL, '9c31b066-1d56-4bdf-8356-f2321967581a', 'Min', 'Ad', NULL, 'UserPlus', 'noreply@phinin.com', NULL, NULL, NULL, NULL, NULL);
INSERT INTO diwi.user_to_usergroup (id, user_id, usergroup_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('88e5153b-0c16-40b2-96bd-d1c52a0c2233', '88e5153b-0c16-40b2-96bd-d1c52a0c2233', '88e5153b-0c16-40b2-96bd-d1c52a0c2233', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.558552+02', NULL, NULL);
INSERT INTO diwi.user_to_usergroup (id, user_id, usergroup_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('ad905510-b444-4eba-8db8-e901e9f4db72', 'ad905510-b444-4eba-8db8-e901e9f4db72', 'ad905510-b444-4eba-8db8-e901e9f4db72', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:36:03.558552+02', NULL, NULL);
INSERT INTO diwi.user_to_usergroup (id, user_id, usergroup_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('0191daa8-72b2-77b4-ae6d-17fdeddbfd3a', '0191daa8-72a7-7484-803f-c41451b33e1b', '0191daa8-72b2-7dcc-93f8-24b70d7077d4', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', '2024-09-10 08:39:18.153402+02', NULL, NULL);
INSERT INTO diwi.usergroup (id, single_user) VALUES ('88e5153b-0c16-40b2-96bd-d1c52a0c2233', true);
INSERT INTO diwi.usergroup (id, single_user) VALUES ('ad905510-b444-4eba-8db8-e901e9f4db72', true);
INSERT INTO diwi.usergroup (id, single_user) VALUES ('3f80263f-3fb4-4d4d-9bb9-752da6144e07', false);
INSERT INTO diwi.usergroup (id, single_user) VALUES ('0191daa8-72b2-7dcc-93f8-24b70d7077d4', true);
INSERT INTO diwi.usergroup_state (id, usergroup_id, parent_usergroup_id, naam, change_end_date, change_start_date, create_user_id, change_user_id) VALUES ('88e5153b-0c16-40b2-96bd-d1c52a0c2233', '88e5153b-0c16-40b2-96bd-d1c52a0c2233', NULL, 'Demo user 1', NULL, '2024-09-10 08:36:03.558552+02', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL);
INSERT INTO diwi.usergroup_state (id, usergroup_id, parent_usergroup_id, naam, change_end_date, change_start_date, create_user_id, change_user_id) VALUES ('ad905510-b444-4eba-8db8-e901e9f4db72', 'ad905510-b444-4eba-8db8-e901e9f4db72', NULL, 'Demo user 2', NULL, '2024-09-10 08:36:03.558552+02', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL);
INSERT INTO diwi.usergroup_state (id, usergroup_id, parent_usergroup_id, naam, change_end_date, change_start_date, create_user_id, change_user_id) VALUES ('af60e75a-2e01-42fd-8eb4-79760417dc05', '3f80263f-3fb4-4d4d-9bb9-752da6144e07', NULL, 'Tijdelijke groep', NULL, '2024-09-10 08:36:03.856817+02', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL);
INSERT INTO diwi.usergroup_state (id, usergroup_id, parent_usergroup_id, naam, change_end_date, change_start_date, create_user_id, change_user_id) VALUES ('0191daa8-72b2-74b9-ad0d-c2673d42d3aa', '0191daa8-72b2-7dcc-93f8-24b70d7077d4', NULL, 'Ad Min', NULL, '2024-09-10 08:39:18.153402+02', 'd9cedd33-a3b2-49b1-8f33-b7315aca84fa', NULL);
INSERT INTO diwi.usergroup_to_project (id, usergroup_id, project_id, change_end_date, change_start_date, create_user_id, change_user_id) VALUES ('0191daa9-f07a-7d7f-82fe-b0660307c950', '0191daa8-72b2-7dcc-93f8-24b70d7077d4', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc', NULL, '2024-09-10 08:40:55.916413+02', '0191daa8-72a7-7484-803f-c41451b33e1b', NULL);
INSERT INTO diwi.woningblok (id, project_id) VALUES ('0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-77f1-bbc7-e3b15bb0f0cc');
INSERT INTO diwi.woningblok_deliverydate_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, latest_deliverydate, create_user_id, change_start_date, change_end_date, change_user_id, earliest_deliverydate) VALUES ('0191daac-916b-7a81-a7a6-dc6fa22deeb5', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '2030-12-31', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, '2030-12-31');
INSERT INTO diwi.woningblok_doelgroep_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('0191daac-916f-7376-bc6e-8b6a59e8383c', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL);
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, amount, property_value_id) VALUES ('0191daac-916f-7d6d-9734-59036b1fa682', '0191daac-916f-7376-bc6e-8b6a59e8383c', 0, 'b58ceec9-5230-4af3-9fe6-297092cd8d77');
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, amount, property_value_id) VALUES ('0191daac-916f-7d0f-aac0-778c70310571', '0191daac-916f-7376-bc6e-8b6a59e8383c', 0, '72949299-5e94-49e2-ac98-0dd482ba59e1');
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, amount, property_value_id) VALUES ('0191daac-916f-7db5-a996-cfc1d0faceff', '0191daac-916f-7376-bc6e-8b6a59e8383c', 0, '51a8ff9a-8b7c-4ca5-9a6b-5d249cf1e200');
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, amount, property_value_id) VALUES ('0191daac-9170-7d92-bd1b-99078564bbb9', '0191daac-916f-7376-bc6e-8b6a59e8383c', 0, '301dba25-c1d5-4081-bfda-4518a428116e');
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, amount, property_value_id) VALUES ('0191daac-9170-7038-b6d7-2ffa15b9acca', '0191daac-916f-7376-bc6e-8b6a59e8383c', 0, '3ce5a122-7b25-401e-955e-08f5933543a6');
INSERT INTO diwi.woningblok_doelgroep_changelog_value (id, woningblok_doelgroep_changelog_id, amount, property_value_id) VALUES ('0191daac-9170-7414-9c6e-9a95cb2f8718', '0191daac-916f-7376-bc6e-8b6a59e8383c', 0, 'a533b308-68ec-48d3-9ab8-3d9d9b941335');
INSERT INTO diwi.woningblok_duration_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('0191daac-916b-7261-b08e-72aa650d0a89', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL);
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916c-7ef3-be1e-911261bc458b', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 3, 'KOOPWONING', NULL, NULL, 'c58249ca-0bfa-4efd-be51-cc0bb4a3e809', NULL);
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916c-7a6d-a260-35876736fdc5', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 2, 'KOOPWONING', NULL, NULL, '024f0b9d-a7f8-47c1-ab31-7553d3abb087', NULL);
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916c-700e-b67d-7d016f8917a7', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 1, 'KOOPWONING', NULL, NULL, 'a91a033d-7a4b-4a60-8367-7de7e62cdcf3', NULL);
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916d-7a56-919b-eac5668b5390', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 10, 'KOOPWONING', NULL, NULL, 'a78b34a1-9250-4967-8851-f70692677bc0', NULL);
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916c-776a-b76f-a9f0eb169208', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 1, 'HUURWONING_WONINGCORPORATIE', NULL, NULL, NULL, '725d22e8-2c7e-4ec5-ac63-857eb85ece0a');
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916d-79ee-af7a-230a83593064', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 5, 'HUURWONING_PARTICULIERE_VERHUURDER', NULL, NULL, NULL, 'ab6d92a5-32f1-4bb1-aea8-416c32546775');
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916c-7fe1-a4bb-3e3d29a50b74', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 4, 'HUURWONING_PARTICULIERE_VERHUURDER', NULL, NULL, NULL, 'b6d1c8d7-2d31-4889-bfee-9e0199d6f3c6');
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916d-74d8-bef3-5fc413c6cd29', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 6, 'HUURWONING_PARTICULIERE_VERHUURDER', NULL, NULL, NULL, 'e05e04fe-4670-4db5-acde-f422b95fb1c8');
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916c-7069-963f-969a4cc6f98e', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 2, 'HUURWONING_WONINGCORPORATIE', NULL, NULL, NULL, 'c34d7e77-e423-4726-b647-06b01ea17b16');
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916c-7ac2-8a69-385791c89dc2', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 3, 'HUURWONING_WONINGCORPORATIE', NULL, NULL, NULL, 'a7f23802-3bfb-47fb-a4fd-4a540f40da00');
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916d-72ee-a5f5-5b2bf4a70c90', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 10, 'HUURWONING_PARTICULIERE_VERHUURDER', NULL, NULL, NULL, 'ab54fcb9-d0fb-4e0b-a27b-6ddd6834c16a');
INSERT INTO diwi.woningblok_eigendom_en_waarde_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, waarde_value, huurbedrag_value, change_user_id, amount, eigendom_soort, waarde_value_range, huurbedrag_value_range, ownership_property_value_id, rental_property_value_id) VALUES ('0191daac-916d-7a32-bc68-7fcd1248e352', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL, NULL, NULL, 10, 'HUURWONING_WONINGCORPORATIE', NULL, NULL, NULL, 'ab54fcb9-d0fb-4e0b-a27b-6ddd6834c16a');
INSERT INTO diwi.woningblok_mutatie_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, amount, change_user_id, mutation_kind) VALUES ('0191daac-916c-74a9-9e4a-5dde26d55628', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, 1000, NULL, 'CONSTRUCTION');
INSERT INTO diwi.woningblok_naam_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, naam, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('0191daac-916b-78af-a7a3-5c1095bdfa60', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', 'test', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL);
INSERT INTO diwi.woningblok_state (id, woningblok_id, create_user_id, change_user_id, change_start_date, change_end_date) VALUES ('0191daac-916b-7403-aebf-f90d8c71a507', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, '2024-09-10 08:43:48.202478+02', NULL);
INSERT INTO diwi.woningblok_type_en_fysiek_changelog (id, woningblok_id, start_milestone_id, end_milestone_id, create_user_id, change_start_date, change_end_date, change_user_id) VALUES ('0191daac-916d-709e-874c-fa85ca915c1a', '0191daac-916a-75f9-952c-1e4f4e1561ce', '0191daa9-f078-7d49-8481-f26d5dffa873', '0191daa9-f078-72da-8d73-d8c66df01d73', '0191daa8-72a7-7484-803f-c41451b33e1b', '2024-09-10 08:43:48.202478+02', NULL, NULL);
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, amount, property_value_id) VALUES ('0191daac-916e-7631-90a7-3be1c2fd9929', '0191daac-916d-709e-874c-fa85ca915c1a', 0, 'ccc63ef0-ab36-4e74-8e95-a1bbf6ac0fc4');
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, amount, property_value_id) VALUES ('0191daac-916e-795f-936e-9e8e2ce78ccd', '0191daac-916d-709e-874c-fa85ca915c1a', 0, 'e6c042f4-1cf7-4307-961c-158043eab9d9');
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, amount, property_value_id) VALUES ('0191daac-916e-72f1-a159-445596274c35', '0191daac-916d-709e-874c-fa85ca915c1a', 0, 'eaaea4fc-fbdf-4f36-a29f-9416f2f42d49');
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, amount, property_value_id) VALUES ('0191daac-916e-7165-80c7-8afbf4d0195c', '0191daac-916d-709e-874c-fa85ca915c1a', 0, 'cd161a03-190b-4c55-8c3b-23764edad824');
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, amount, property_value_id) VALUES ('0191daac-916e-7d44-bd68-02e6eb805eab', '0191daac-916d-709e-874c-fa85ca915c1a', 0, '6ca07d6e-9bb1-4519-8ec5-0b714109fa9e');
INSERT INTO diwi.woningblok_type_en_fysiek_changelog_fysiek_value (id, woningblok_type_en_fysiek_voorkomen_changelog_id, amount, property_value_id) VALUES ('0191daac-916e-71f2-8e8a-0194cb6bf1d5', '0191daac-916d-709e-874c-fa85ca915c1a', 0, '0fb40eb0-daa1-42d8-bd92-4ba30de8bf99');
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (1, '2024.01.10.10.00', 'Diwi BASELINE', 'SQL', 'V2024.01.10.10.00__Diwi_BASELINE.sql', 865898922, 'emiel', '2024-09-10 06:36:02.746671', 355, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (2, '2024.01.19.10.00', 'AddUserName', 'SQL', 'V2024.01.19.10.00__AddUserName.sql', 1789447597, 'emiel', '2024-09-10 06:36:03.236627', 10, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (3, '2024.02.23.16.00', 'AddCreateUserColumn', 'SQL', 'V2024.02.23.16.00__AddCreateUserColumn.sql', 147614236, 'emiel', '2024-09-10 06:36:03.256491', 72, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (4, '2024.03.06.16.00', 'AddProject GemeenteIndeling Tables', 'SQL', 'V2024.03.06.16.00__AddProject_GemeenteIndeling_Tables.sql', 2073322776, 'emiel', '2024-09-10 06:36:03.368051', 33, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (5, '2024.03.07.10.00', 'AddHouseblockAmountColumns', 'SQL', 'V2024.03.07.10.00__AddHouseblockAmountColumns.sql', 2135329062, 'emiel', '2024-09-10 06:36:03.415631', 5, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (6, '2024.03.15.18.00', 'CustomProperties', 'SQL', 'V2024.03.15.18.00__CustomProperties.sql', -80656097, 'emiel', '2024-09-10 06:36:03.428792', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (7, '2024.03.17.10.00', 'AddProjectRegistryLinkTables', 'SQL', 'V2024.03.17.10.00__AddProjectRegistryLinkTables.sql', 807314309, 'emiel', '2024-09-10 06:36:03.440682', 20, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (8, '2024.03.18.10.00', 'AddProjectLocation', 'SQL', 'V2024.03.18.10.00__AddProjectLocation.sql', -1201918943, 'emiel', '2024-09-10 06:36:03.471076', 1, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (9, '2024.03.18.13.00', 'AddPGCryptoForPg12', 'SQL', 'V2024.03.18.13.00__AddPGCryptoForPg12.sql', 458433873, 'emiel', '2024-09-10 06:36:03.478301', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (10, '2024.03.18.14.00', 'AddSystemUser', 'SQL', 'V2024.03.18.14.00__AddSystemUser.sql', -320437406, 'emiel', '2024-09-10 06:36:03.491108', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (11, '2024.03.18.16.00', 'AddProjectTextCustomPropertyChangelog', 'SQL', 'V2024.03.18.16.00__AddProjectTextCustomPropertyChangelog.sql', -64151011, 'emiel', '2024-09-10 06:36:03.498957', 16, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (12, '2024.03.19.11.00', 'AddGeoJsonToPlot', 'SQL', 'V2024.03.19.11.00__AddGeoJsonToPlot.sql', 1921092325, 'emiel', '2024-09-10 06:36:03.523572', 1, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (13, '2024.03.21.10.00', 'AddHouseblockTextCustomPropertyChangelog', 'SQL', 'V2024.03.21.10.00__AddHouseblockTextCustomPropertyChangelog.sql', -2119143952, 'emiel', '2024-09-10 06:36:03.530826', 16, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (14, '2024.03.22.14.00', 'demo users', 'SQL', 'V2024.03.22.14.00__demo_users.sql', -1929218722, 'emiel', '2024-09-10 06:36:03.555229', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (15, '2024.03.22.14.30', 'prioriteiten', 'SQL', 'V2024.03.22.14.30__prioriteiten.sql', 786635933, 'emiel', '2024-09-10 06:36:03.56685', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (16, '2024.03.22.15.00', 'rol gemeente', 'SQL', 'V2024.03.22.15.00__rol_gemeente.sql', -420053119, 'emiel', '2024-09-10 06:36:03.577981', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (17, '2024.04.02.11.00', 'AddPolygonToRegistryLink', 'SQL', 'V2024.04.02.11.00__AddPolygonToRegistryLink.sql', -1310091223, 'emiel', '2024-09-10 06:36:03.587795', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (18, '2024.04.02.15.00', 'delete actors roles', 'SQL', 'V2024.04.02.15.00__delete_actors_roles.sql', -1112588776, 'emiel', '2024-09-10 06:36:03.597071', 5, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (19, '2024.04.03.11.00', 'CustomPropertyUniqueIndex', 'SQL', 'V2024.04.03.11.00__CustomPropertyUniqueIndex.sql', -678091591, 'emiel', '2024-09-10 06:36:03.611475', 6, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (20, '2024.04.03.16.00', 'CustomPropertiesRefactoring', 'SQL', 'V2024.04.03.16.00__CustomPropertiesRefactoring.sql', 1726442328, 'emiel', '2024-09-10 06:36:03.625432', 5, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (21, '2024.04.04.10.00', 'RestoreHouseblockStateTable', 'SQL', 'V2024.04.04.10.00__RestoreHouseblockStateTable.sql', 10174366, 'emiel', '2024-09-10 06:36:03.637682', 10, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (22, '2024.04.05.16.00', 'TargetGroupProperties', 'SQL', 'V2024.04.05.16.00__TargetGroupProperties.sql', -1407712932, 'emiel', '2024-09-10 06:36:03.656699', 6, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (23, '2024.04.05.17.00', 'PhysicalAppearanceProperties', 'SQL', 'V2024.04.05.17.00__PhysicalAppearanceProperties.sql', 2130667057, 'emiel', '2024-09-10 06:36:03.672157', 5, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (24, '2024.04.10.14.00', 'ChangeHouseblockMutation', 'SQL', 'V2024.04.10.14.00__ChangeHouseblockMutation.sql', -134087759, 'emiel', '2024-09-10 06:36:03.686355', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (25, '2024.04.11.12.00', 'ProjectRegionFixedProperties', 'SQL', 'V2024.04.11.12.00__ProjectRegionFixedProperties.sql', 219461592, 'emiel', '2024-09-10 06:36:03.697934', 12, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (26, '2024.04.17.12.00', 'UpdateProjectPhases', 'SQL', 'V2024.04.17.12.00__UpdateProjectPhases.sql', -931804375, 'emiel', '2024-09-10 06:36:03.720206', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (27, '2024.04.19.12.00', 'MigrateMunicipalityRoleToProperties', 'SQL', 'V2024.04.19.12.00__MigrateMunicipalityRoleToProperties.sql', -367970565, 'emiel', '2024-09-10 06:36:03.730418', 5, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (28, '2024.04.19.16.00', 'MigratePriorityToProperties', 'SQL', 'V2024.04.19.16.00__MigratePriorityToProperties.sql', 576304322, 'emiel', '2024-09-10 06:36:03.743474', 7, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (29, '2024.04.22.10.00', 'UpdateHouseblockOwnershipValuesToCents', 'SQL', 'V2024.04.22.10.00__UpdateHouseblockOwnershipValuesToCents.sql', 1941811137, 'emiel', '2024-09-10 06:36:03.759896', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (30, '2024.04.26.10.00', 'AddGeometryFixedProperty', 'SQL', 'V2024.04.26.10.00__AddGeometryFixedProperty.sql', 1756676927, 'emiel', '2024-09-10 06:36:03.767519', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (31, '2024.05.03.10.00', 'AddUserRole', 'SQL', 'V2024.05.03.10.00__AddUserRole.sql', 76838108, 'emiel', '2024-09-10 06:36:03.778914', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (32, '2024.05.08.16.00', 'RemoveProjectLeider', 'SQL', 'V2024.05.08.16.00__RemoveProjectLeider.sql', 657587736, 'emiel', '2024-09-10 06:36:03.789789', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (33, '2024.05.08.17.00', 'ConfidentialityLevels', 'SQL', 'V2024.05.08.17.00__ConfidentialityLevels.sql', -623256582, 'emiel', '2024-09-10 06:36:03.799262', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (34, '2024.05.13.12.00', 'AddUserEmailColumn', 'SQL', 'V2024.05.13.12.00__AddUserEmailColumn.sql', -1810853623, 'emiel', '2024-09-10 06:36:03.80941', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (35, '2024.05.13.16.00', 'RenameOrganizationToUserGroup', 'SQL', 'V2024.05.13.16.00__RenameOrganizationToUserGroup.sql', 189242947, 'emiel', '2024-09-10 06:36:03.820573', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (36, '2024.05.14.16.00', 'AddSingleUserGroupFlag', 'SQL', 'V2024.05.14.16.00__AddSingleUserGroupFlag.sql', 205111628, 'emiel', '2024-09-10 06:36:03.833277', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (37, '2024.05.20.10.00', 'AddUserOrganizationAndPhoneNumber', 'SQL', 'V2024.05.20.10.00__AddUserOrganizationAndPhoneNumber.sql', -868677883, 'emiel', '2024-09-10 06:36:03.842357', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (38, '2024.05.23.18.00', 'Temporary Usergroup', 'SQL', 'V2024.05.23.18.00__Temporary_Usergroup.sql', -1337287333, 'emiel', '2024-09-10 06:36:03.853258', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (39, '2024.05.29.16.00', 'HouseblockDeliveryDateRange', 'SQL', 'V2024.05.29.16.00__HouseblockDeliveryDateRange.sql', -2140427198, 'emiel', '2024-09-10 06:36:03.864596', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (40, '2024.05.29.17.00', 'RenameSchema', 'SQL', 'V2024.05.29.17.00__RenameSchema.sql', 1026812342, 'emiel', '2024-09-10 06:36:03.875035', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (41, NULL, 'CustomPropertyDefinitionsQuery', 'SQL', 'R__CustomPropertyDefinitionsQuery.sql', 1137667241, 'emiel', '2024-09-10 06:36:03.884442', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (42, NULL, 'HouseblockSnapshotCustomPropertiesQuery', 'SQL', 'R__HouseblockSnapshotCustomPropertiesQuery.sql', -1931553555, 'emiel', '2024-09-10 06:36:03.901276', 5, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (43, NULL, 'HouseblocksQuery', 'SQL', 'R__HouseblocksQuery.sql', -162952454, 'emiel', '2024-09-10 06:36:03.920832', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (44, NULL, 'MultiProjectDashboardSnapshotQuery', 'SQL', 'R__MultiProjectDashboardSnapshotQuery.sql', 443493720, 'emiel', '2024-09-10 06:36:03.938999', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (45, NULL, 'ProjectDashboardSnapshotQuery', 'SQL', 'R__ProjectDashboardSnapshotQuery.sql', 1149624812, 'emiel', '2024-09-10 06:36:03.954867', 1, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (46, NULL, 'ProjectSnapshotCustomPropertiesQuery', 'SQL', 'R__ProjectSnapshotCustomPropertiesQuery.sql', -1727717562, 'emiel', '2024-09-10 06:36:03.964099', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (47, NULL, 'ProjectSnapshotQuery', 'SQL', 'R__ProjectSnapshotQuery.sql', -1732320071, 'emiel', '2024-09-10 06:36:03.97741', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (48, NULL, 'ProjectsListQuery', 'SQL', 'R__ProjectsListQuery.sql', 1234002100, 'emiel', '2024-09-10 06:36:03.995536', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (49, '2024.06.19.17.00', 'RangePropertyType', 'SQL', 'V2024.06.19.17.00__RangePropertyType.sql', -681369947, 'emiel', '2024-09-10 06:45:35.966634', 21, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (50, '2024.06.19.18.00', 'HouseblockFixedRangeProperties', 'SQL', 'V2024.06.19.18.00__HouseblockFixedRangeProperties.sql', -1744066798, 'emiel', '2024-09-10 06:45:36.011641', 7, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (51, '2024.06.20.14.00', 'HouseblockRangeForOwnership', 'SQL', 'V2024.06.20.14.00__HouseblockRangeForOwnership.sql', -1248852208, 'emiel', '2024-09-10 06:45:36.027898', 7, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (52, '2024.07.17.14.00', 'DashboardBlueprints', 'SQL', 'V2024.07.17.14.00__DashboardBlueprints.sql', 656221009, 'emiel', '2024-09-10 06:45:36.044435', 14, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (53, '2024.07.19.14.00', 'HouseblockOwnershipMigrateToRangeCategories', 'SQL', 'V2024.07.19.14.00__HouseblockOwnershipMigrateToRangeCategories.sql', 1101487172, 'emiel', '2024-09-10 06:45:36.068045', 7, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (54, '2024.07.19.15.00', 'ChangePrivateConfidentialityToInternalCivil', 'SQL', 'V2024.07.19.15.00__ChangePrivateConfidentialityToInternalCivil.sql', 1432644452, 'emiel', '2024-09-10 06:45:36.082124', 2, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (55, NULL, 'CustomPropertyDefinitionsQuery', 'SQL', 'R__CustomPropertyDefinitionsQuery.sql', 731723707, 'emiel', '2024-09-10 06:45:36.089478', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (56, NULL, 'DashboardBlueprintsQuery', 'SQL', 'R__DashboardBlueprintsQuery.sql', 882165758, 'emiel', '2024-09-10 06:45:36.103543', 4, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (57, NULL, 'HouseblocksQuery', 'SQL', 'R__HouseblocksQuery.sql', 185132029, 'emiel', '2024-09-10 06:45:36.113987', 7, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (58, NULL, 'MultiProjectDashboardSnapshotQuery', 'SQL', 'R__MultiProjectDashboardSnapshotQuery.sql', -893359573, 'emiel', '2024-09-10 06:45:36.136722', 3, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (59, NULL, 'ProjectDashboardSnapshotQuery', 'SQL', 'R__ProjectDashboardSnapshotQuery.sql', -156730825, 'emiel', '2024-09-10 06:45:36.147784', 6, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (60, NULL, 'ProjectSnapshotQuery', 'SQL', 'R__ProjectSnapshotQuery.sql', 1099409163, 'emiel', '2024-09-10 06:45:36.164414', 7, true);
INSERT INTO public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) VALUES (61, NULL, 'ProjectsListQuery', 'SQL', 'R__ProjectsListQuery.sql', -112425552, 'emiel', '2024-09-10 06:45:36.184148', 6, true);

INSERT INTO diwi.property_range_category_value (id, property_id)
    VALUES ('0191daaf-f08d-7f9b-aaa6-1db056e999a1', '560aa6bc-1852-43f1-945d-cb55481b20c3');
INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_user_id, change_start_date, change_end_date)
    VALUES ('0191daaf-f08d-7d05-9afd-c4b7d59e4038', '0191daaf-f08d-7f9b-aaa6-1db056e999a1', 'Verschrikkelijke dure koop', 100000000.000000, NULL, '0191daa8-72a7-7484-803f-c41451b33e1b', NULL, '2024-09-10 08:47:29.159038+02', NULL);


ALTER TABLE ONLY diwi.blueprint
    ADD CONSTRAINT blueprint_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT blueprint_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.blueprint_to_element
    ADD CONSTRAINT blueprint_to_element_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.blueprint_to_usergroup
    ADD CONSTRAINT blueprint_to_usergroup_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.document
    ADD CONSTRAINT document_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.document_soort
    ADD CONSTRAINT document_soort_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.document_soort_state
    ADD CONSTRAINT document_soort_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.document_state
    ADD CONSTRAINT document_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.document_state_soort_value
    ADD CONSTRAINT document_state_soort_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.milestone
    ADD CONSTRAINT milestone_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.milestone_state
    ADD CONSTRAINT milestone_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.usergroup
    ADD CONSTRAINT organization_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.usergroup_state
    ADD CONSTRAINT organization_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT plan_conditie_categorie_maatwerk_eigenschap_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_doelgroep
    ADD CONSTRAINT plan_conditie_doelgroep_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_doelgroep_value
    ADD CONSTRAINT plan_conditie_doelgroep_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT plan_conditie_eigendom_en_waarde_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde_soort_value
    ADD CONSTRAINT plan_conditie_eigendom_en_waarde_soort_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_kadastraal
    ADD CONSTRAINT plan_conditie_geografie_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_grondpositie
    ADD CONSTRAINT plan_conditie_grondpositie_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_grondpositie_value
    ADD CONSTRAINT plan_conditie_grondpositie_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_grootte
    ADD CONSTRAINT plan_conditie_grootte_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_kadastraal_value
    ADD CONSTRAINT plan_conditie_kadastraal_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT plan_conditie_maatwerk_boolean_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_categorie_value
    ADD CONSTRAINT plan_conditie_maatwerk_categorie_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT plan_conditie_maatwerk_numeriek_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT plan_conditie_maatwerk_ordinaal_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie
    ADD CONSTRAINT plan_conditie_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_programmering
    ADD CONSTRAINT plan_conditie_programmering_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_state
    ADD CONSTRAINT plan_conditie_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek_fysiek_value
    ADD CONSTRAINT plan_conditie_type_en_fysiek_fysiek_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek_type_value
    ADD CONSTRAINT plan_conditie_type_en_fysiek_type_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek
    ADD CONSTRAINT plan_conditie_type_en_fysiek_voorkomen_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan
    ADD CONSTRAINT plan_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_soort
    ADD CONSTRAINT plan_soort_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_soort_state
    ADD CONSTRAINT plan_soort_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_state
    ADD CONSTRAINT plan_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.plan_state_soort_value
    ADD CONSTRAINT plan_state_soort_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_duration_changelog
    ADD CONSTRAINT project_duration_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_fase_changelog
    ADD CONSTRAINT project_fase_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_filiatie
    ADD CONSTRAINT project_filiatie_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_maatwerk_boolean_changelog
    ADD CONSTRAINT project_maatwerk_boolean_eigenschap_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_category_changelog
    ADD CONSTRAINT project_maatwerk_categorie_eigenschap_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_category_changelog_value
    ADD CONSTRAINT project_maatwerk_categorie_eigenschap_changelog_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT project_maatwerk_numeriek_eigenschap_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT project_maatwerk_ordinaal_eigenschap_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_text_changelog
    ADD CONSTRAINT project_maatwerk_text_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_name_changelog
    ADD CONSTRAINT project_name_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_plan_type_changelog
    ADD CONSTRAINT project_plan_type_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_plan_type_changelog_value
    ADD CONSTRAINT project_plan_type_changelog_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog
    ADD CONSTRAINT project_planologische_planstatus_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog_value
    ADD CONSTRAINT project_planologische_planstatus_changelog_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_registry_link_changelog
    ADD CONSTRAINT project_registry_link_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_registry_link_changelog_value
    ADD CONSTRAINT project_registry_link_changelog_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.project_state
    ADD CONSTRAINT project_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property_range_category_value
    ADD CONSTRAINT property_range_category_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT property_range_category_value_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.software_module_rights
    ADD CONSTRAINT software_module_rights_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.milestone
    ADD CONSTRAINT unique_milestone_id UNIQUE (id);
ALTER TABLE ONLY diwi.milestone_state
    ADD CONSTRAINT unique_milestone_state_id UNIQUE (id);
ALTER TABLE ONLY diwi."user"
    ADD CONSTRAINT unique_user_id UNIQUE (id);
ALTER TABLE ONLY diwi.user_state
    ADD CONSTRAINT unique_user_state_id UNIQUE (id);
ALTER TABLE ONLY diwi."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.user_state
    ADD CONSTRAINT user_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.user_to_usergroup
    ADD CONSTRAINT user_to_organization_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog
    ADD CONSTRAINT woningblok_doelgroep_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog_value
    ADD CONSTRAINT woningblok_doelgroep_changelog_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_duration_changelog
    ADD CONSTRAINT woningblok_duration_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT woningblok_eigendom_en_waarde_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog
    ADD CONSTRAINT woningblok_grondpositie_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog_value
    ADD CONSTRAINT woningblok_grondpositie_changelog_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_grootte_changelog
    ADD CONSTRAINT woningblok_grootte_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT woningblok_maatwerk_boolean_eigenschap_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog_value
    ADD CONSTRAINT woningblok_maatwerk_categorie_changelog_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT woningblok_maatwerk_categorie_eigenschap_changelos_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property_category_value
    ADD CONSTRAINT woningblok_maatwerk_categorie_waarde_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property_category_value_state
    ADD CONSTRAINT woningblok_maatwerk_categorie_waarde_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property
    ADD CONSTRAINT woningblok_maatwerk_eigenschap_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property_state
    ADD CONSTRAINT woningblok_maatwerk_eigenschap_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT woningblok_maatwerk_numeriek_eigenschap_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT woningblok_maatwerk_ordinaal_eigenschap_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property_ordinal_value
    ADD CONSTRAINT woningblok_maatwerk_ordinale_waarde_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.property_ordinal_value_state
    ADD CONSTRAINT woningblok_maatwerk_ordinale_waarde_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT woningblok_maatwerk_text_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_mutatie_changelog
    ADD CONSTRAINT woningblok_mutatie_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_naam_changelog
    ADD CONSTRAINT woningblok_naam_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok
    ADD CONSTRAINT woningblok_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_programmering_changelog
    ADD CONSTRAINT woningblok_programmering_changelog_pkey1 PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_state
    ADD CONSTRAINT woningblok_state_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD CONSTRAINT woningblok_type_en_fysiek_voorkomen_changelog_fysiek_voork_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog_type_value
    ADD CONSTRAINT woningblok_type_en_fysiek_voorkomen_changelog_type_value_pkey PRIMARY KEY (id);
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT woningblok_woning_type_en_fysiek_voorkomen_changelog_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);
CREATE UNIQUE INDEX idx_unique_maatwerk_eigenschap_state__eigenschap_naam ON diwi.property_state USING btree (property_name) WHERE (change_end_date IS NULL);
CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);
ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT fk_blueprint_state__blueprint FOREIGN KEY (blueprint_id) REFERENCES diwi.blueprint(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT fk_blueprint_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.blueprint_state
    ADD CONSTRAINT fk_blueprint_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.blueprint_to_element
    ADD CONSTRAINT fk_blueprint_to_element__blueprint_state FOREIGN KEY (blueprint_state_id) REFERENCES diwi.blueprint_state(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.blueprint_to_usergroup
    ADD CONSTRAINT fk_blueprint_to_usergroup__blueprint_state FOREIGN KEY (blueprint_state_id) REFERENCES diwi.blueprint_state(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.blueprint_to_usergroup
    ADD CONSTRAINT fk_blueprint_to_usergroup__usergroup FOREIGN KEY (usergroup_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document
    ADD CONSTRAINT fk_document__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_soort_state
    ADD CONSTRAINT fk_document_soort_state__document_soort FOREIGN KEY (document_soort_id) REFERENCES diwi.document_soort(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_state
    ADD CONSTRAINT fk_document_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_state
    ADD CONSTRAINT fk_document_state__document FOREIGN KEY (document_id) REFERENCES diwi.document(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_state
    ADD CONSTRAINT fk_document_state__milestone FOREIGN KEY (milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_state_soort_value
    ADD CONSTRAINT fk_document_state_soort_value__document_state FOREIGN KEY (document_state_id) REFERENCES diwi.document_state(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.document_state_soort_value
    ADD CONSTRAINT fk_document_state_soort_value__soort_value FOREIGN KEY (document_soort_id) REFERENCES diwi.document_soort(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_category_value
    ADD CONSTRAINT fk_maatwerk_categorie_waarde__maatwerk_eigenschap FOREIGN KEY (property_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_category_value_state
    ADD CONSTRAINT fk_maatwerk_categorie_waarde_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_category_value_state
    ADD CONSTRAINT fk_maatwerk_categorie_waarde_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_state
    ADD CONSTRAINT fk_maatwerk_eigenschap_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_state
    ADD CONSTRAINT fk_maatwerk_eigenschap_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_ordinal_value_state
    ADD CONSTRAINT fk_maatwerk_ordinaal_waarde_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_ordinal_value_state
    ADD CONSTRAINT fk_maatwerk_ordinaal_waarde_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_ordinal_value
    ADD CONSTRAINT fk_maatwerk_ordinal_waarde__maatwerk_eigenschap FOREIGN KEY (property_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.milestone
    ADD CONSTRAINT fk_milestone__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.milestone_state
    ADD CONSTRAINT fk_milestone_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.milestone_state
    ADD CONSTRAINT fk_milestone_state__milestone FOREIGN KEY (milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.milestone_state
    ADD CONSTRAINT fk_milestone_state_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_state
    ADD CONSTRAINT fk_organization_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_state
    ADD CONSTRAINT fk_organization_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_state
    ADD CONSTRAINT fk_organization_state__organization FOREIGN KEY (usergroup_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_state
    ADD CONSTRAINT fk_organization_state__parent_organization FOREIGN KEY (parent_usergroup_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_document
    ADD CONSTRAINT fk_organization_to_document__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_document
    ADD CONSTRAINT fk_organization_to_document__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_document
    ADD CONSTRAINT fk_organization_to_document__document FOREIGN KEY (document_id) REFERENCES diwi.document(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_document
    ADD CONSTRAINT fk_organization_to_document__organization FOREIGN KEY (usergroup_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_plan
    ADD CONSTRAINT fk_organization_to_plan__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_plan
    ADD CONSTRAINT fk_organization_to_plan__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_plan
    ADD CONSTRAINT fk_organization_to_plan__organization FOREIGN KEY (usergroup_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_plan
    ADD CONSTRAINT fk_organization_to_plan__plan FOREIGN KEY (plan_id) REFERENCES diwi.plan(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_project
    ADD CONSTRAINT fk_organization_to_project__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_project
    ADD CONSTRAINT fk_organization_to_project__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_project
    ADD CONSTRAINT fk_organization_to_project__organization FOREIGN KEY (usergroup_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.usergroup_to_project
    ADD CONSTRAINT fk_organization_to_project__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_doelgroep
    ADD CONSTRAINT fk_plan_conditie_doelgroep__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_doelgroep_value
    ADD CONSTRAINT fk_plan_conditie_doelgroep_value__conditie FOREIGN KEY (plan_conditie_doelgroep_id) REFERENCES diwi.plan_conditie_doelgroep(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde_soort_value
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde_soort_value__conditie FOREIGN KEY (plan_conditie_eigendom_en_waarde_id) REFERENCES diwi.plan_conditie_eigendom_en_waarde(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_kadastraal
    ADD CONSTRAINT fk_plan_conditie_geografie__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_grondpositie
    ADD CONSTRAINT fk_plan_conditie_grondpositie__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_grondpositie_value
    ADD CONSTRAINT fk_plan_conditie_grondpositie_value__conditie FOREIGN KEY (plan_conditie_grondpositie_id) REFERENCES diwi.plan_conditie_grondpositie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_grootte
    ADD CONSTRAINT fk_plan_conditie_grootte__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_kadastraal
    ADD CONSTRAINT fk_plan_conditie_kadastraal__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_kadastraal
    ADD CONSTRAINT fk_plan_conditie_kadastraal__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_kadastraal_value
    ADD CONSTRAINT fk_plan_conditie_kadastraal_value__conditie FOREIGN KEY (plan_conditie_kadastraal_id) REFERENCES diwi.plan_conditie_kadastraal(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_boolean
    ADD CONSTRAINT fk_plan_conditie_maatwerk_boolean__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_categorie
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_categorie_value
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie_value__conditie FOREIGN KEY (plan_conditie_maatwerk_categorie_id) REFERENCES diwi.plan_conditie_maatwerk_categorie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_categorie_value
    ADD CONSTRAINT fk_plan_conditie_maatwerk_categorie_value__waarde FOREIGN KEY (eigenschap_waarde_id) REFERENCES diwi.property_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_numeriek
    ADD CONSTRAINT fk_plan_conditie_maatwerk_numeriek__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__max_value FOREIGN KEY (max_value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__min_value FOREIGN KEY (min_value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_conditie_maatwerk_ordinaal__value FOREIGN KEY (value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_programmering
    ADD CONSTRAINT fk_plan_conditie_programmering__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__plan FOREIGN KEY (plan_id) REFERENCES diwi.plan(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_state
    ADD CONSTRAINT fk_plan_conditie_state__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek_fysiek_value
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_fysiek_value__conditie FOREIGN KEY (plan_conditie_type_en_fysiek_id) REFERENCES diwi.plan_conditie_type_en_fysiek(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek_type_value
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_type_value__conditie FOREIGN KEY (plan_conditie_type_en_fysiek_id) REFERENCES diwi.plan_conditie_type_en_fysiek(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_voorkomen__plan_conditie FOREIGN KEY (plan_conditie_id) REFERENCES diwi.plan_conditie(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_conditie_maatwerk_ordinaal
    ADD CONSTRAINT fk_plan_plan_conditie_maatwerk_ordinaal__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_soort_state
    ADD CONSTRAINT fk_plan_soort_state__plan_soort FOREIGN KEY (plan_soort_id) REFERENCES diwi.plan_soort(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_state
    ADD CONSTRAINT fk_plan_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_state
    ADD CONSTRAINT fk_plan_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_state
    ADD CONSTRAINT fk_plan_state__plan FOREIGN KEY (plan_id) REFERENCES diwi.plan(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_state_soort_value
    ADD CONSTRAINT fk_plan_state_soort_value__plan_state FOREIGN KEY (plan_state_id) REFERENCES diwi.plan_state(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.plan_state_soort_value
    ADD CONSTRAINT fk_plan_state_soort_value__soort_value FOREIGN KEY (plan_soort_id) REFERENCES diwi.plan_soort(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_chagelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_duration_changelog
    ADD CONSTRAINT fk_project_duration_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_fase_changelog
    ADD CONSTRAINT fk_project_fase_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__new_project FOREIGN KEY (new_project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_filiatie
    ADD CONSTRAINT fk_project_filiatie__old_project FOREIGN KEY (old_project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__eigenschap FOREIGN KEY (eigenschap_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_project_maatwerk_boolean_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__eigenschap FOREIGN KEY (property_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog_value
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog_value__changelog FOREIGN KEY (project_category_changelog_id) REFERENCES diwi.project_category_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_category_changelog_value
    ADD CONSTRAINT fk_project_maatwerk_categorie_changelog_value__waarde FOREIGN KEY (property_value_id) REFERENCES diwi.property_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__eigenschap FOREIGN KEY (eigenschap_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_project_maatwerk_numeriek_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__eigenschap FOREIGN KEY (property_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__max_value FOREIGN KEY (max_value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__min_value FOREIGN KEY (min_value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_ordinal_changelog
    ADD CONSTRAINT fk_project_maatwerk_ordinaal_changelog__value FOREIGN KEY (value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_text_changelog
    ADD CONSTRAINT fk_project_maatwerk_text_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_text_changelog
    ADD CONSTRAINT fk_project_maatwerk_text_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_text_changelog
    ADD CONSTRAINT fk_project_maatwerk_text_changelog__eigenschap FOREIGN KEY (property_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_text_changelog
    ADD CONSTRAINT fk_project_maatwerk_text_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_text_changelog
    ADD CONSTRAINT fk_project_maatwerk_text_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_text_changelog
    ADD CONSTRAINT fk_project_maatwerk_text_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_name_changelog
    ADD CONSTRAINT fk_project_name_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_plan_type_changelog
    ADD CONSTRAINT fk_project_plan_type_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_plan_type_changelog_value
    ADD CONSTRAINT fk_project_plan_type_changelog_value__changelog FOREIGN KEY (changelog_id) REFERENCES diwi.project_plan_type_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_planologische_planstatus_changelog_value
    ADD CONSTRAINT fk_project_planologische_planstatus_changelog_value__changelog FOREIGN KEY (planologische_planstatus_changelog_id) REFERENCES diwi.project_planologische_planstatus_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_registry_link_changelog
    ADD CONSTRAINT fk_project_registry_link_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_registry_link_changelog_value
    ADD CONSTRAINT fk_project_registry_link_changelog_value__changelog FOREIGN KEY (project_registry_link_changelog_id) REFERENCES diwi.project_registry_link_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_state
    ADD CONSTRAINT fk_project_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_state
    ADD CONSTRAINT fk_project_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.project_state
    ADD CONSTRAINT fk_project_state__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_range_category_value
    ADD CONSTRAINT fk_range_category__property FOREIGN KEY (property_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT fk_range_category_value__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT fk_range_category_value__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT fk_range_category_value__range_category FOREIGN KEY (range_category_value_id) REFERENCES diwi.property_range_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.software_module_rights
    ADD CONSTRAINT fk_software_module_rights__organization FOREIGN KEY (organization_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.user_state
    ADD CONSTRAINT fk_user_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.user_state
    ADD CONSTRAINT fk_user_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.user_state
    ADD CONSTRAINT fk_user_state__user FOREIGN KEY (user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.user_to_usergroup
    ADD CONSTRAINT fk_user_to_organization__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.user_to_usergroup
    ADD CONSTRAINT fk_user_to_organization__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.user_to_usergroup
    ADD CONSTRAINT fk_user_to_organization__organization FOREIGN KEY (usergroup_id) REFERENCES diwi.usergroup(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.user_to_usergroup
    ADD CONSTRAINT fk_user_to_organization__user FOREIGN KEY (user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok
    ADD CONSTRAINT fk_woningblok__project FOREIGN KEY (project_id) REFERENCES diwi.project(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog_value
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog_value__changelog FOREIGN KEY (woningblok_doelgroep_changelog_id) REFERENCES diwi.woningblok_doelgroep_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_doelgroep_changelog_value
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog_value__property_value FOREIGN KEY (property_value_id) REFERENCES diwi.property_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_duration_changelog
    ADD CONSTRAINT fk_woningblok_duration_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__ownership_property_ FOREIGN KEY (ownership_property_value_id) REFERENCES diwi.property_range_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__rental_property_val FOREIGN KEY (rental_property_value_id) REFERENCES diwi.property_range_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grondpositie_changelog_value
    ADD CONSTRAINT fk_woningblok_grondpositie_changelog_value__changelog FOREIGN KEY (woningblok_grondpositie_changelog_id) REFERENCES diwi.woningblok_grondpositie_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_grootte_changelog
    ADD CONSTRAINT fk_woningblok_grootte_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__eigenschap FOREIGN KEY (eigenschap_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_boolean_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_boolean_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__eigenschap FOREIGN KEY (eigenschap_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog_value
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog_value__changelog FOREIGN KEY (woningblok_maatwerk_categorie_changelog_id) REFERENCES diwi.woningblok_maatwerk_categorie_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_categorie_changelog_value
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_changelog_value__waarde FOREIGN KEY (eigenschap_waarde_id) REFERENCES diwi.property_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_category_value_state
    ADD CONSTRAINT fk_woningblok_maatwerk_categorie_waarde_state__categorie_waarde FOREIGN KEY (category_value_id) REFERENCES diwi.property_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_state
    ADD CONSTRAINT fk_woningblok_maatwerk_eigenschap_state__eigenschap FOREIGN KEY (property_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__eigenschap FOREIGN KEY (eigenschap_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_numeriek_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_numeriek_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__eigenschap FOREIGN KEY (eigenschap_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__max_value FOREIGN KEY (max_value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__min_value FOREIGN KEY (min_value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__value FOREIGN KEY (value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_ordinaal_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinaal_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.property_ordinal_value_state
    ADD CONSTRAINT fk_woningblok_maatwerk_ordinale_waarde_state__ordinale_waarde FOREIGN KEY (ordinal_value_id) REFERENCES diwi.property_ordinal_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__eigenschap FOREIGN KEY (eigenschap_id) REFERENCES diwi.property(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_maatwerk_text_changelog
    ADD CONSTRAINT fk_woningblok_maatwerk_text_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_mutatie_changelog
    ADD CONSTRAINT fk_woningblok_mutatie_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_naam_changelog
    ADD CONSTRAINT fk_woningblok_naam_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_deliverydate_changelog
    ADD CONSTRAINT fk_woningblok_opleverdatum_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_deliverydate_changelog
    ADD CONSTRAINT fk_woningblok_opleverdatum_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_deliverydate_changelog
    ADD CONSTRAINT fk_woningblok_opleverdatum_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_deliverydate_changelog
    ADD CONSTRAINT fk_woningblok_opleverdatum_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_deliverydate_changelog
    ADD CONSTRAINT fk_woningblok_opleverdatum_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_programmering_changelog
    ADD CONSTRAINT fk_woningblok_programmering_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_state
    ADD CONSTRAINT fk_woningblok_state__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__change_user FOREIGN KEY (change_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__create_user FOREIGN KEY (create_user_id) REFERENCES diwi."user"(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__end_milestone FOREIGN KEY (end_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__start_milestone FOREIGN KEY (start_milestone_id) REFERENCES diwi.milestone(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog__woningblok FOREIGN KEY (woningblok_id) REFERENCES diwi.woningblok(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog_fysiek_value__changelog FOREIGN KEY (woningblok_type_en_fysiek_voorkomen_changelog_id) REFERENCES diwi.woningblok_type_en_fysiek_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog_fysiek_value__property_v FOREIGN KEY (property_value_id) REFERENCES diwi.property_category_value(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY diwi.woningblok_type_en_fysiek_changelog_type_value
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog_type_value__changelog FOREIGN KEY (woningblok_type_en_fysiek_voorkomen_changelog_id) REFERENCES diwi.woningblok_type_en_fysiek_changelog(id) MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;
