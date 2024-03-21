DROP FUNCTION IF EXISTS get_active_or_future_houseblock_snapshots;
DROP FUNCTION IF EXISTS get_houseblock_snapshots;

CREATE OR REPLACE FUNCTION get_houseblock_snapshots (
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
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1))) AS ownershipValue
                 FROM
                     active_woningbloks aw
                         JOIN diwi_testset.woningblok_eigendom_en_waarde_changelog wewc ON aw.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY aw.id
             ),
             future_woningbloks AS (
                 SELECT
                     w.id, w.project_id, sms.date AS startDate, sms.milestone_id AS start_milestone_id, ems.date AS endDate
                 FROM
                     diwi_testset.woningblok w
                         JOIN diwi_testset.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
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
                         JOIN diwi_testset.woningblok_naam_changelog wnc ON fw.id = wnc.woningblok_id
                                AND wnc.start_milestone_id = fw.start_milestone_id
                                AND wnc.change_end_date IS NULL
             ),
             future_woningbloks_groundposition AS (
                 SELECT
                     fw.id, ftg.amount AS formalPermissionOwner, img.amount AS intentionPermissionOwner, gtg.amount AS noPermissionOwner
                 FROM
                     future_woningbloks fw
                         JOIN diwi_testset.woningblok_grondpositie_changelog wgpc ON fw.id = wgpc.woningblok_id
                                AND wgpc.start_milestone_id = fw.start_milestone_id
                                AND wgpc.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value ftg ON ftg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND ftg.grondpositie = 'FORMELE_TOESTEMMING_GRONDEIGENAAR'
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value img ON img.woningblok_grondpositie_changelog_id = wgpc.id
                                AND img.grondpositie = 'INTENTIE_MEDEWERKING_GRONDEIGENAAR'
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value gtg ON gtg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND gtg.grondpositie = 'GEEN_TOESTEMMING_GRONDEIGENAAR'
             ),
             future_woningbloks_fysiek AS (
                 SELECT
                     fw.id, tuss.amount AS tussenwoning, twee.amount AS tweeondereenkap, port.amount AS portiekflat, hoek.amount AS hoekwoning,
                     vrij.amount AS vrijstaand, gall.amount AS gallerijflat, meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                 FROM
                     future_woningbloks fw
                         JOIN diwi_testset.woningblok_type_en_fysiek_changelog wtfc ON fw.id = wtfc.woningblok_id
                                AND wtfc.start_milestone_id = fw.start_milestone_id
                                AND wtfc.change_end_date IS NULL
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
             ),
             future_woningbloks_doelgroep AS (
                 SELECT
                     fw.id,
                     reg.amount AS regular, jong.amount AS youth, stud.amount AS student, oud.amount AS elderly,
                     gez.amount AS GHZ, gg.amount AS largeFamilies
                 FROM
                     future_woningbloks fw
                         JOIN diwi_testset.woningblok_doelgroep_changelog wdgc ON fw.id = wdgc.woningblok_id
                                AND wdgc.start_milestone_id = fw.start_milestone_id
                                AND wdgc.change_end_date IS NULL
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
             ),
             future_woningbloks_mutation AS (
                 SELECT
                     fw.id, wmc.sloop AS demolition, wmc.bruto_plancapaciteit AS grossPlanCapacity, wmc.netto_plancapaciteit AS netPlanCapacity,
                     array_remove(array_agg(wmcsv.mutatie_soort::TEXT ORDER BY wmcsv.mutatie_soort::TEXT), null) AS mutationKind
                 FROM
                     future_woningbloks fw
                         JOIN diwi_testset.woningblok_mutatie_changelog wmc ON fw.id = wmc.woningblok_id
                                    AND wmc.start_milestone_id = fw.start_milestone_id
                                    AND wmc.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_mutatie_changelog_soort_value wmcsv ON wmcsv.woningblok_mutatie_changelog_id = wmc.id
                 GROUP BY fw.id, wmc.sloop, wmc.bruto_plancapaciteit, wmc.netto_plancapaciteit
             ),
             future_woningbloks_programming AS (
                 SELECT
                     fw.id, wpc.programmering AS programming
                 FROM
                     future_woningbloks fw
                         JOIN diwi_testset.woningblok_programmering_changelog wpc ON fw.id = wpc.woningblok_id
                                AND wpc.start_milestone_id = fw.start_milestone_id
                                AND wpc.change_end_date IS NULL
             ),
             future_woningbloks_size AS (
                 SELECT
                     fw.id, wgc.value AS sizeValue, wgc.value_range AS sizeValueRange, wgc.value_type AS sizeValueType
                 FROM
                     future_woningbloks fw
                         JOIN diwi_testset.woningblok_grootte_changelog wgc ON fw.id = wgc.woningblok_id
                                AND wgc.start_milestone_id = fw.start_milestone_id
                                AND wgc.change_end_date IS NULL
             ),
             future_ownership_value AS (
                 SELECT
                     fw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range), 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range)))) AS ownershipValue
                 FROM
                     future_woningbloks fw
                         JOIN diwi_testset.woningblok_eigendom_en_waarde_changelog wewc ON fw.id = wewc.woningblok_id
                                AND wewc.start_milestone_id = fw.start_milestone_id
                                AND wewc.change_end_date IS NULL
                 GROUP BY fw.id
             ),
             past_woningbloks AS (
                 SELECT
                     w.id, w.project_id, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
                 FROM
                     diwi_testset.woningblok w
                         JOIN diwi_testset.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
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
                         JOIN diwi_testset.woningblok_naam_changelog wnc ON pw.id = wnc.woningblok_id
                                AND wnc.end_milestone_id = pw.end_milestone_id
                                AND wnc.change_end_date IS NULL
             ),
             past_woningbloks_groundposition AS (
                 SELECT
                     pw.id, ftg.amount AS formalPermissionOwner, img.amount AS intentionPermissionOwner, gtg.amount AS noPermissionOwner
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_grondpositie_changelog wgpc ON pw.id = wgpc.woningblok_id
                                AND wgpc.end_milestone_id = pw.end_milestone_id
                                AND wgpc.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value ftg ON ftg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND ftg.grondpositie = 'FORMELE_TOESTEMMING_GRONDEIGENAAR'
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value img ON img.woningblok_grondpositie_changelog_id = wgpc.id
                                AND img.grondpositie = 'INTENTIE_MEDEWERKING_GRONDEIGENAAR'
                         LEFT JOIN diwi_testset.woningblok_grondpositie_changelog_value gtg ON gtg.woningblok_grondpositie_changelog_id = wgpc.id
                                AND gtg.grondpositie = 'GEEN_TOESTEMMING_GRONDEIGENAAR'
             ),
             past_woningbloks_fysiek AS (
                 SELECT
                     pw.id, tuss.amount AS tussenwoning, twee.amount AS tweeondereenkap, port.amount AS portiekflat, hoek.amount AS hoekwoning,
                     vrij.amount AS vrijstaand, gall.amount AS gallerijflat, meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_type_en_fysiek_changelog wtfc ON pw.id = wtfc.woningblok_id
                                AND wtfc.end_milestone_id = pw.end_milestone_id
                                AND wtfc.change_end_date IS NULL
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
             ),
             past_woningbloks_doelgroep AS (
                 SELECT
                     pw.id,
                     reg.amount AS regular, jong.amount AS youth, stud.amount AS student, oud.amount AS elderly,
                     gez.amount AS GHZ, gg.amount AS largeFamilies
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_doelgroep_changelog wdgc ON pw.id = wdgc.woningblok_id
                                AND wdgc.end_milestone_id = pw.end_milestone_id
                                AND wdgc.change_end_date IS NULL
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
             ),
             past_woningbloks_mutation AS (
                 SELECT
                     pw.id, wmc.sloop AS demolition, wmc.bruto_plancapaciteit AS grossPlanCapacity, wmc.netto_plancapaciteit AS netPlanCapacity,
                     array_remove(array_agg(wmcsv.mutatie_soort::TEXT ORDER BY wmcsv.mutatie_soort::TEXT), null) AS mutationKind
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_mutatie_changelog wmc ON pw.id = wmc.woningblok_id
                                    AND wmc.end_milestone_id = pw.end_milestone_id
                                    AND wmc.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_mutatie_changelog_soort_value wmcsv ON wmcsv.woningblok_mutatie_changelog_id = wmc.id
                 GROUP BY pw.id, wmc.sloop, wmc.bruto_plancapaciteit, wmc.netto_plancapaciteit
             ),
             past_woningbloks_programming AS (
                 SELECT
                     pw.id, wpc.programmering AS programming
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_programmering_changelog wpc ON pw.id = wpc.woningblok_id
                                AND wpc.end_milestone_id = pw.end_milestone_id
                                AND wpc.change_end_date IS NULL
             ),
             past_woningbloks_size AS (
                 SELECT
                     pw.id, wgc.value AS sizeValue, wgc.value_range AS sizeValueRange, wgc.value_type AS sizeValueType
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_grootte_changelog wgc ON pw.id = wgc.woningblok_id
                                AND wgc.end_milestone_id = pw.end_milestone_id
                                AND wgc.change_end_date IS NULL
             ),
             past_ownership_value AS (
                 SELECT
                     pw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range), 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range)))) AS ownershipValue
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_eigendom_en_waarde_changelog wewc ON pw.id = wewc.woningblok_id
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
             fwm.grossPlanCapacity           AS grossPlanCapacity,
             fwm.netPlanCapacity             AS netPlanCapacity,
             fwm.demolition                  AS demolition,
             fwm.mutationKind                AS mutationKind,
             fov.ownershipValue              AS ownershipValueList,
             fwgp.noPermissionOwner          AS noPermissionOwner,
             fwgp.intentionPermissionOwner   AS intentionPermissionOwner,
             fwgp.formalPermissionOwner      AS formalPermissionOwner,
             fwf.tussenwoning                AS tussenwoning,
             fwf.tweeondereenkap             AS tweeondereenkap,
             fwf.portiekflat                 AS portiekflat,
             fwf.hoekwoning                  AS hoekwoning,
             fwf.vrijstaand                  AS vrijstaand,
             fwf.gallerijflat                AS gallerijflat,
             fwf.meergezinswoning            AS meergezinswoning,
             fwf.eengezinswoning             AS eengezinswoning,
             fwdg.regular                    AS regular,
             fwdg.youth                      AS youth,
             fwdg.student                    AS student,
             fwdg.elderly                    AS elderly,
             fwdg.GHZ                        AS GHZ,
             fwdg.largeFamilies              AS largeFamilies

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
                pwm.grossPlanCapacity           AS grossPlanCapacity,
                pwm.netPlanCapacity             AS netPlanCapacity,
                pwm.demolition                  AS demolition,
                pwm.mutationKind                AS mutationKind,
                pov.ownershipValue              AS ownershipValueList,
                pwgp.noPermissionOwner          AS noPermissionOwner,
                pwgp.intentionPermissionOwner   AS intentionPermissionOwner,
                pwgp.formalPermissionOwner      AS formalPermissionOwner,
                pwf.tussenwoning                AS tussenwoning,
                pwf.tweeondereenkap             AS tweeondereenkap,
                pwf.portiekflat                 AS portiekflat,
                pwf.hoekwoning                  AS hoekwoning,
                pwf.vrijstaand                  AS vrijstaand,
                pwf.gallerijflat                AS gallerijflat,
                pwf.meergezinswoning            AS meergezinswoning,
                pwf.eengezinswoning             AS eengezinswoning,
                pwdg.regular                    AS regular,
                pwdg.youth                      AS youth,
                pwdg.student                    AS student,
                pwdg.elderly                    AS elderly,
                pwdg.GHZ                        AS GHZ,
                pwdg.largeFamilies              AS largeFamilies

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

    ORDER BY q.startDate, q.endDate;

END;$$
