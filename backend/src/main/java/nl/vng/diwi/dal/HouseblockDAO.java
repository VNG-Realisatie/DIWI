package nl.vng.diwi.dal;

import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.HouseblockAppearanceAndTypeChangelog;
import nl.vng.diwi.dal.entities.HouseblockBooleanCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockCategoryCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockDeliveryDateChangelog;
import nl.vng.diwi.dal.entities.HouseblockDurationChangelog;
import nl.vng.diwi.dal.entities.HouseblockGroundPositionChangelog;
import nl.vng.diwi.dal.entities.HouseblockMutatieChangelog;
import nl.vng.diwi.dal.entities.HouseblockNameChangelog;
import nl.vng.diwi.dal.entities.HouseblockNumericCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockOrdinalCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockOwnershipValueChangelog;
import nl.vng.diwi.dal.entities.HouseblockProgrammingChangelog;
import nl.vng.diwi.dal.entities.HouseblockTargetGroupChangelog;
import nl.vng.diwi.dal.entities.HouseblockSizeChangelog;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.dal.entities.HouseblockTextCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.ProjectHouseblockCustomPropertySqlModel;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HouseblockDAO extends AbstractRepository {

    public static Map<Class<? extends HouseblockMilestoneChangeDataSuperclass>, String> houseblockChangelogs = new HashMap<>();

    static {
        houseblockChangelogs.put(HouseblockTargetGroupChangelog.class, "woningblok_doelgroep_changelog");
        houseblockChangelogs.put(HouseblockDurationChangelog.class, "woningblok_duration_changelog");
        houseblockChangelogs.put(HouseblockOwnershipValueChangelog.class, "woningblok_eigendom_en_waarde_changelog");
        houseblockChangelogs.put(HouseblockGroundPositionChangelog.class, "woningblok_grondpositie_changelog");
        houseblockChangelogs.put(HouseblockSizeChangelog.class, "woningblok_grootte_changelog");
        houseblockChangelogs.put(HouseblockBooleanCustomPropertyChangelog.class, "woningblok_maatwerk_boolean_changelog");
        houseblockChangelogs.put(HouseblockCategoryCustomPropertyChangelog.class, "woningblok_maatwerk_categorie_changelog");
        houseblockChangelogs.put(HouseblockNumericCustomPropertyChangelog.class, "woningblok_maatwerk_numeriek_changelog");
        houseblockChangelogs.put(HouseblockOrdinalCustomPropertyChangelog.class, "woningblok_maatwerk_ordinaal_changelog");
        houseblockChangelogs.put(HouseblockTextCustomPropertyChangelog.class, "woningblok_maatwerk_text_changelog");
        houseblockChangelogs.put(HouseblockMutatieChangelog.class, "woningblok_mutatie_changelog");
        houseblockChangelogs.put(HouseblockNameChangelog.class, "woningblok_naam_changelog");
        houseblockChangelogs.put(HouseblockDeliveryDateChangelog.class, "woningblok_deliverydate_changelog");
        houseblockChangelogs.put(HouseblockProgrammingChangelog.class, "woningblok_programmering_changelog");
        houseblockChangelogs.put(HouseblockAppearanceAndTypeChangelog.class, "woningblok_type_en_fysiek_changelog");
    }

    public HouseblockDAO(Session session) {
        super(session);
    }

    public HouseblockSnapshotSqlModel getHouseblockByUuid(UUID houseblockUuid) {
        return session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_houseblock_snapshots(null, :houseblockUuid, :now) ", GenericRepository.VNG_SCHEMA_NAME), HouseblockSnapshotSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("houseblockUuid", houseblockUuid)
            .getSingleResultOrNull();
    }

    public List<HouseblockSnapshotSqlModel> getHouseblocksByProjectUuid(UUID projectUuid) {
        return session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_houseblock_snapshots(:projectUuid, null, :now) ", GenericRepository.VNG_SCHEMA_NAME), HouseblockSnapshotSqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("projectUuid", projectUuid)
            .list();
    }

    public Houseblock getCurrentHouseblock(UUID houseblockId) {
        session.enableFilter(GenericRepository.CURRENT_DATA_FILTER);
        String statement = "FROM Houseblock H WHERE H.id = :uuid";
        SelectionQuery<Houseblock> query = session
            .createSelectionQuery(statement, Houseblock.class)
            .setParameter("uuid", houseblockId);
        return query.getSingleResultOrNull();
    }

    public List<ProjectHouseblockCustomPropertySqlModel> getHouseblockCustomProperties(UUID houseblockUuid) {
        List<ProjectHouseblockCustomPropertySqlModel> result = session.createNativeQuery(String.format(
                "SELECT * FROM %s.get_houseblock_custom_properties(:houseblockUuid, :now) ", GenericRepository.VNG_SCHEMA_NAME), ProjectHouseblockCustomPropertySqlModel.class)
            .setParameter("now", LocalDate.now())
            .setParameter("houseblockUuid", houseblockUuid)
            .list();

        return result;
    }

    public Set<MilestoneState> getHouseblockActiveMilestones(UUID houseblockUuid) {

        Set<MilestoneState> houseblockActiveMilestones = new HashSet<>();

        for (String houseblockChangelog : houseblockChangelogs.values()) {
            List<MilestoneState> acitveMilestones = session.createNativeQuery(String.format("""
                        SELECT DISTINCT sms.* from
                            %1$s.%2$s c
                            LEFT JOIN %1$s.milestone_state sms ON c.start_milestone_id = sms.milestone_id AND sms.change_end_date IS NULL
                            WHERE c.woningblok_id = :houseblockUuid AND c.change_end_date IS NULL
                        UNION
                        SELECT DISTINCT ems.* from
                            %1$s.%2$s c
                            LEFT JOIN %1$s.milestone_state ems ON c.end_milestone_id = ems.milestone_id AND ems.change_end_date IS NULL
                            WHERE c.woningblok_id = :houseblockUuid AND c.change_end_date IS NULL """,
                    GenericRepository.VNG_SCHEMA_NAME, houseblockChangelog), MilestoneState.class)
                .setParameter("houseblockUuid", houseblockUuid)
                .list();

            houseblockActiveMilestones.addAll(acitveMilestones);
        }
        return houseblockActiveMilestones;
    }

    public <T extends MilestoneChangeDataSuperclass> List<T> findActiveHouseblockChangelogByStartMilestone(Class<T> clazz, UUID houseblockUuid, UUID startMilestoneUuid) {
        Query<T> query = session.createQuery(" FROM " + clazz.getName() + " c WHERE c.houseblock.id = :houseblockUuid" +
            " AND c.startMilestone.id = :startMilestoneUuid AND c.changeEndDate IS NULL ", clazz);
        query.setParameter("houseblockUuid", houseblockUuid);
        query.setParameter("startMilestoneUuid", startMilestoneUuid);
        return query.getResultList();
    }

    public <T extends MilestoneChangeDataSuperclass> List<T> findActiveHouseblockChangelogByEndMilestone(Class<T> clazz, UUID houseblockUuid, UUID endMilestoneUuid) {
        Query<T> query = session.createQuery(" FROM " + clazz.getName() + " c WHERE c.houseblock.id = :houseblockUuid" +
            " AND c.endMilestone.id = :endMilestoneUuid AND c.changeEndDate IS NULL ", clazz);
        query.setParameter("houseblockUuid", houseblockUuid);
        query.setParameter("endMilestoneUuid", endMilestoneUuid);
        return query.getResultList();
    }
}
