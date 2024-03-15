package nl.vng.diwi.dal;

import java.util.Arrays;
import java.util.List;

import nl.vng.diwi.dal.entities.*;

public class GenericRepository implements AutoCloseable {

    public static final String VNG_SCHEMA_NAME = "diwi_testset";
    public static final String CURRENT_DATA_FILTER = "current";
    private Dal dal;

    public GenericRepository(Dal dal) {
        this.dal = dal;
    }

    public static List<Class<? extends Object>> getEntities() {
        List<Class<? extends Object>> entities =
            Arrays.asList(
                Houseblock.class,
                HouseblockAppearanceAndTypeChangelog.class,
                HouseblockPhysicalAppearanceChangelogValue.class,
                HouseblockHouseTypeChangelogValue.class,
                HouseblockDurationChangelog.class,
                HouseblockGroundPositionChangelog.class,
                HouseblockGroundPositionChangelogValue.class,
                HouseblockMutatieChangelog.class,
                HouseblockMutatieChangelogTypeValue.class,
                HouseblockNameChangelog.class,
                HouseblockOwnershipValueChangelog.class,
                HouseblockProgrammingChangelog.class,
                HouseblockPurposeChangelog.class,
                HouseblockPurposeChangelogValue.class,
                HouseblockSizeChangelog.class,
                Milestone.class,
                MilestoneState.class,
                Organization.class,
                OrganizationProjectRole.class,
                OrganizationState.class,
                OrganizationToProject.class,
                Project.class,
                ProjectDurationChangelog.class,
                ProjectFaseChangelog.class,
                ProjectGemeenteRolChangelog.class,
                ProjectGemeenteRolValue.class,
                ProjectGemeenteRolValueState.class,
                ProjectNameChangelog.class,
                ProjectPlanologischePlanstatusChangelog.class,
                ProjectPlanologischePlanstatusChangelogValue.class,
                ProjectPlanTypeChangelog.class,
                ProjectPlanTypeChangelogValue.class,
                ProjectPrioriseringChangelog.class,
                ProjectPrioriseringValue.class,
                ProjectPrioriseringValueState.class,
                ProjectState.class,
                User.class,
                UserState.class,
                UserToOrganization.class,
                ProjectListSqlModel.class,
                HouseblockSnapshotSqlModel.class);
        return entities;
    }

    public Dal getDal() {
        return dal;
    }

    @Override
    public void close() {
        dal.close();
    }
}
