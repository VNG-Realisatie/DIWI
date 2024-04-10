DROP FUNCTION IF EXISTS get_active_or_future_project_custom_properties;

CREATE OR REPLACE FUNCTION get_active_or_future_project_custom_properties (
  _project_uuid_ uuid,
  _now_ date
)
	RETURNS TABLE (
        customPropertyId UUID,
        booleanValue BOOL,
        numericValue FLOAT8,
        numericValueRange NUMRANGE,
        numericValueType diwi_testset.value_type,
        textValue TEXT,
        categories UUID[],
        ordinalValueId UUID,
        ordinalMinValueId UUID,
        ordinalMaxValueId UUID,
        propertyType diwi_testset.maatwerk_eigenschap_type
	)
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
                     diwi_testset.project p
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  sms.date <= _now_ AND _now_ < ems.date AND p.id = _project_uuid_
             ),
             active_projects_booleanCP AS (
                 SELECT
                     ap.id, pbc.eigenschap_id, pbc.value
                 FROM
                     active_projects ap
                         JOIN diwi_testset.project_maatwerk_boolean_changelog pbc ON ap.id = pbc.project_id AND pbc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pbc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pbc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_projects_numericCP AS (
                 SELECT
                     ap.id, pnc.eigenschap_id, pnc.value, pnc.value_range, pnc.value_type
                 FROM
                     active_projects ap
                         JOIN diwi_testset.project_maatwerk_numeriek_changelog pnc ON ap.id = pnc.project_id AND pnc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_projects_textCP AS (
                 SELECT
                     ap.id, ptc.eigenschap_id, ptc.value
                 FROM
                     active_projects ap
                         JOIN diwi_testset.project_maatwerk_text_changelog ptc ON ap.id = ptc.project_id AND ptc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = ptc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = ptc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_projects_categoriesCP AS (
                 SELECT
                     ap.id, pcc.eigenschap_id, array_agg(pccv.eigenschap_waarde_id) AS categories
                 FROM
                     active_projects ap
                         JOIN diwi_testset.project_maatwerk_categorie_changelog pcc ON ap.id = pcc.project_id AND pcc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_maatwerk_categorie_changelog_value pccv ON pccv.project_maatwerk_categorie_changelog_id = pcc.id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY ap.id, pcc.eigenschap_id
             ),
             active_projects_ordinalCP AS (
                 SELECT
                     ap.id, poc.eigenschap_id, poc.value_id AS ordinal_value_id, poc.min_value_id AS ordinal_min_value_id, poc.max_value_id AS ordinal_max_value_id
                 FROM
                     active_projects ap
                         JOIN diwi_testset.project_maatwerk_ordinaal_changelog poc ON ap.id = poc.project_id AND poc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = poc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = poc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             future_projects AS (
                 SELECT
                     p.id, sms.milestone_id AS start_milestone_id
                 FROM
                     diwi_testset.project p
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE sms.date > _now_  AND p.id = _project_uuid_
             ),
             future_projects_numericCP AS (
                 SELECT
                     fp.id, pnc.eigenschap_id, pnc.value, pnc.value_range, pnc.value_type
                 FROM
                     future_projects fp
                         JOIN diwi_testset.project_maatwerk_numeriek_changelog pnc ON fp.id = pnc.project_id
                            AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
             ),
             future_projects_booleanCP AS (
                 SELECT
                     fp.id, pbc.eigenschap_id, pbc.value
                 FROM
                     future_projects fp
                         JOIN diwi_testset.project_maatwerk_boolean_changelog pbc ON fp.id = pbc.project_id
                         AND pbc.start_milestone_id = fp.start_milestone_id AND pbc.change_end_date IS NULL
             ),
             future_projects_textCP AS (
                 SELECT
                     fp.id, ptc.eigenschap_id, ptc.value
                 FROM
                     future_projects fp
                         JOIN diwi_testset.project_maatwerk_text_changelog ptc ON fp.id = ptc.project_id
                         AND ptc.start_milestone_id = fp.start_milestone_id AND ptc.change_end_date IS NULL
             ),
             future_projects_categoriesCP AS (
                 SELECT
                     fp.id, pcc.eigenschap_id, array_agg(pccv.eigenschap_waarde_id) AS categories
                 FROM
                     future_projects fp
                         JOIN diwi_testset.project_maatwerk_categorie_changelog pcc ON fp.id = pcc.project_id
                            AND pcc.start_milestone_id = fp.start_milestone_id AND pcc.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_maatwerk_categorie_changelog_value pccv ON pccv.project_maatwerk_categorie_changelog_id = pcc.id
                 GROUP BY fp.id, pcc.eigenschap_id
             ),
             future_projects_ordinalCP AS (
                 SELECT
                     fp.id, poc.eigenschap_id, poc.value_id AS ordinal_value_id, poc.min_value_id AS ordinal_min_value_id, poc.max_value_id AS ordinal_max_value_id
                 FROM
                     future_projects fp
                         JOIN diwi_testset.project_maatwerk_ordinaal_changelog poc ON fp.id = poc.project_id
                         AND poc.start_milestone_id = fp.start_milestone_id AND poc.change_end_date IS NULL

             )

         SELECT
             ap.id AS projectId,
             apb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi_testset.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             'NUMERIC'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             apt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             apc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             apo.eigenschap_id AS customPropertyId,
             'ORDINAL'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM active_projects ap
             JOIN active_projects_ordinalCP apo ON ap.id = apo.id

         UNION

         SELECT
             fp.id AS projectId,
             fpb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi_testset.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             'NUMERIC'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             fpt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             fpc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
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
             fpo.eigenschap_id AS customPropertyId,
             fpo.ordinal_value_id AS ordinalValueId,
             fpo.ordinal_min_value_id AS ordinalMinValueId,
             fpo.ordinal_max_value_id AS ordinalMaxValueId,
             'ORDINAL'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM future_projects fp
             JOIN future_projects_ordinalCP fpo ON fp.id = fpo.id

     ) AS q

        JOIN diwi_testset.property_state cps ON cps.property_id = q.customPropertyId AND cps.change_end_date IS NULL

WHERE q.projectId = _project_uuid_;


END;$$
