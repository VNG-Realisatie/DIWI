DROP FUNCTION IF EXISTS get_active_or_future_houseblock_snapshots;

CREATE OR REPLACE FUNCTION get_active_or_future_houseblock_snapshots (
  _project_uuid_ uuid,
  _houseblock_uuid_ uuid,
  _now_ date
)
	RETURNS TABLE (
        projectId UUID,
        houseblockId UUID,
        houseblockName TEXT,
        startDate DATE,
        endDate DATE,
        sizeValue FLOAT8,
        sizeValueRange NUMRANGE,
        sizeValueType diwi_testset.value_type,
        programming BOOL,
        grossPlanCapacity INTEGER,
        netPlanCapacity INTEGER,
        demolition INTEGER,
        mutationKind TEXT[],
        ownershipValueList JSONB,
        noPermissionOwner INTEGER,
        intentionPermissionOwner INTEGER,
        formalPermissionOwner INTEGER,
        tussenwoning INTEGER,
        tweeondereenkap INTEGER,
        portiekflat INTEGER,
        hoekwoning INTEGER,
        vrijstaand INTEGER,
        gallerijflat INTEGER,
        meergezinswoning INTEGER,
        eengezinswoning INTEGER,
        regular INTEGER,
        youth INTEGER,
        student INTEGER,
        elderly INTEGER,
        GHZ INTEGER,
        largeFamilies INTEGER
	)
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
        q.grossPlanCapacity,
        q.netPlanCapacity,
        q.demolition,
        q.mutationKind,
        q.ownershipValueList,
        q.noPermissionOwner,
        q.intentionPermissionOwner,
        q.formalPermissionOwner,
        q.tussenwoning,
        q.tweeondereenkap,
        q.portiekflat,
        q.hoekwoning,
        q.vrijstaand,
        q.gallerijflat,
        q.meergezinswoning,
        q.eengezinswoning,
        q.regular,
        q.youth,
        q.student,
        q.elderly,
        q.GHZ,
        q.largeFamilies
FROM (

         WITH
             active_woningbloks AS (
                 SELECT
                     w.id, w.project_id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi_testset.woningblok w
                         JOIN diwi_testset.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
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
                         JOIN diwi_testset.woningblok_naam_changelog wnc ON aw.id = wnc.woningblok_id AND wnc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_groundposition AS (
                 SELECT
                     aw.id, ftg.amount AS formalPermissionOwner, img.amount AS intentionPermissionOwner, gtg.amount AS noPermissionOwner
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_grondpositie_changelog wgpc ON aw.id = wgpc.woningblok_id AND wgpc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wgpc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wgpc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value ftg ON ftg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND ftg.grondpositie = 'FORMELE_TOESTEMMING_GRONDEIGENAAR'
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value img ON img.woningblok_grondpositie_changelog_id = wgpc.id
                                AND img.grondpositie = 'INTENTIE_MEDEWERKING_GRONDEIGENAAR'
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value gtg ON gtg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND gtg.grondpositie = 'GEEN_TOESTEMMING_GRONDEIGENAAR'
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_fysiek AS (
                 SELECT
                     aw.id, tuss.amount AS tussenwoning, twee.amount AS tweeondereenkap, port.amount AS portiekflat, hoek.amount AS hoekwoning,
                     vrij.amount AS vrijstaand, gall.amount AS gallerijflat, meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_type_en_fysiek_changelog wtfc ON aw.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value tuss ON tuss.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND tuss.fysiek_voorkomen = 'TUSSENWONING'
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value hoek ON hoek.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND hoek.fysiek_voorkomen = 'HOEKWONING'
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value twee ON twee.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND twee.fysiek_voorkomen = 'TWEE_ONDER_EEN_KAP'
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value vrij ON vrij.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND vrij.fysiek_voorkomen = 'VRIJSTAAND'
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value port ON port.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND port.fysiek_voorkomen = 'PORTIEKFLAT'
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value gall ON gall.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND gall.fysiek_voorkomen = 'GALLERIJFLAT'
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                         LEFT JOIN diwi_testset.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_doelgroep AS (
                 SELECT
                     aw.id,
                     reg.amount AS regular, jong.amount AS youth, stud.amount AS student, oud.amount AS elderly,
                     gez.amount AS GHZ, gg.amount AS largeFamilies
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_doelgroep_changelog wdgc ON aw.id = wdgc.woningblok_id AND wdgc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wdgc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wdgc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_doelgroep_changelog_value reg ON reg.woningblok_doelgroep_changelog_id = wdgc.id
                            AND reg.doelgroep = 'REGULIER'
                         LEFT JOIN diwi_testset.woningblok_doelgroep_changelog_value jong ON jong.woningblok_doelgroep_changelog_id = wdgc.id
                            AND jong.doelgroep = 'JONGEREN'
                         LEFT JOIN diwi_testset.woningblok_doelgroep_changelog_value stud ON stud.woningblok_doelgroep_changelog_id = wdgc.id
                            AND stud.doelgroep = 'STUDENTEN'
                         LEFT JOIN diwi_testset.woningblok_doelgroep_changelog_value oud ON oud.woningblok_doelgroep_changelog_id = wdgc.id
                            AND oud.doelgroep = 'OUDEREN'
                         LEFT JOIN diwi_testset.woningblok_doelgroep_changelog_value gez ON gez.woningblok_doelgroep_changelog_id = wdgc.id
                            AND gez.doelgroep = 'GEHANDICAPTEN_EN_ZORG'
                         LEFT JOIN diwi_testset.woningblok_doelgroep_changelog_value gg ON gg.woningblok_doelgroep_changelog_id = wdgc.id
                            AND gg.doelgroep = 'GROTE_GEZINNEN'
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_mutation AS (
                 SELECT
                     aw.id, wmc.sloop AS demolition, wmc.bruto_plancapaciteit AS grossPlanCapacity, wmc.netto_plancapaciteit AS netPlanCapacity,
                     array_remove(array_agg(wmcsv.mutatie_soort::TEXT ORDER BY wmcsv.mutatie_soort::TEXT), null) AS mutationKind
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_mutatie_changelog wmc ON aw.id = wmc.woningblok_id AND wmc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_mutatie_changelog_soort_value wmcsv ON wmcsv.woningblok_mutatie_changelog_id = wmc.id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY aw.id, wmc.sloop, wmc.bruto_plancapaciteit, wmc.netto_plancapaciteit
             ),
             active_woningbloks_programming AS (
                 SELECT
                     aw.id, wpc.programmering AS programming
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_programmering_changelog wpc ON aw.id = wpc.woningblok_id AND wpc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wpc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wpc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_size AS (
                 SELECT
                     aw.id, wgc.value AS sizeValue, wgc.value_range AS sizeValueRange, wgc.value_type AS sizeValueType
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_grootte_changelog wgc ON aw.id = wgc.woningblok_id AND wgc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wgc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wgc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_ownership_value AS (
                 SELECT
                     aw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range), 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range)))) AS ownershipValue
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_eigendom_en_waarde_changelog wewc ON aw.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY aw.id
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
                awm.grossPlanCapacity           AS grossPlanCapacity,
                awm.netPlanCapacity             AS netPlanCapacity,
                awm.demolition                  AS demolition,
                awm.mutationKind                AS mutationKind,
                aov.ownershipValue              AS ownershipValueList,
                awgp.noPermissionOwner          AS noPermissionOwner,
                awgp.intentionPermissionOwner   AS intentionPermissionOwner,
                awgp.formalPermissionOwner      AS formalPermissionOwner,
                awf.tussenwoning                AS tussenwoning,
                awf.tweeondereenkap             AS tweeondereenkap,
                awf.portiekflat                 AS portiekflat,
                awf.hoekwoning                  AS hoekwoning,
                awf.vrijstaand                  AS vrijstaand,
                awf.gallerijflat                AS gallerijflat,
                awf.meergezinswoning            AS meergezinswoning,
                awf.eengezinswoning             AS eengezinswoning,
                awdg.regular                    AS regular,
                awdg.youth                      AS youth,
                awdg.student                    AS student,
                awdg.elderly                    AS elderly,
                awdg.GHZ                        AS GHZ,
                awdg.largeFamilies              AS largeFamilies

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
     ) AS q;

END;$$
