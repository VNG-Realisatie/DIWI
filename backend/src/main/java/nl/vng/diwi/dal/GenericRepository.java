package nl.vng.diwi.dal;

import java.util.Arrays;
import java.util.List;

import jakarta.inject.Inject;
import nl.vng.diwi.dal.entities.*;
import nl.vng.diwi.models.PropertyModel;

public class GenericRepository implements AutoCloseable {

    public static final String VNG_SCHEMA_NAME = "diwi";
    public static final String CURRENT_DATA_FILTER = "current";
    private Dal dal;

    @Inject
    public GenericRepository(Dal dal) {
        this.dal = dal;
    }

    public static List<Class<? extends Object>> getEntities() {
        List<Class<? extends Object>> entities =
            Arrays.asList(
                PropertyCategoryValue.class,
                PropertyCategoryValueState.class,
                PropertyOrdinalValue.class,
                PropertyOrdinalValueState.class,
                Property.class,
                PropertyState.class,
                Houseblock.class,
                HouseblockState.class,
                HouseblockAppearanceAndTypeChangelog.class,
                HouseblockPhysicalAppearanceChangelogValue.class,
                HouseblockHouseTypeChangelogValue.class,
                HouseblockDeliveryDateChangelog.class,
                HouseblockDurationChangelog.class,
                HouseblockGroundPositionChangelog.class,
                HouseblockGroundPositionChangelogValue.class,
                HouseblockMutatieChangelog.class,
                HouseblockNameChangelog.class,
                HouseblockOwnershipValueChangelog.class,
                HouseblockProgrammingChangelog.class,
                HouseblockTargetGroupChangelog.class,
                HouseblockTargetGroupChangelogValue.class,
                HouseblockSizeChangelog.class,
                HouseblockBooleanCustomPropertyChangelog.class,
                HouseblockNumericCustomPropertyChangelog.class,
                HouseblockTextCustomPropertyChangelog.class,
                HouseblockCategoryCustomPropertyChangelog.class,
                HouseblockCategoryCustomPropertyChangelogValue.class,
                HouseblockOrdinalCustomPropertyChangelog.class,
                Milestone.class,
                MilestoneState.class,
                Organization.class,
                OrganizationState.class,
                OrganizationToProject.class,
                Project.class,
                ProjectDurationChangelog.class,
                ProjectFaseChangelog.class,
                ProjectNameChangelog.class,
                ProjectPlanologischePlanstatusChangelog.class,
                ProjectPlanologischePlanstatusChangelogValue.class,
                ProjectPlanTypeChangelog.class,
                ProjectPlanTypeChangelogValue.class,
                ProjectRegistryLinkChangelog.class,
                ProjectRegistryLinkChangelogValue.class,
                ProjectState.class,
                ProjectBooleanCustomPropertyChangelog.class,
                ProjectNumericCustomPropertyChangelog.class,
                ProjectTextPropertyChangelog.class,
                ProjectCategoryPropertyChangelog.class,
                ProjectCategoryPropertyChangelogValue.class,
                ProjectOrdinalPropertyChangelog.class,
                User.class,
                UserState.class,
                UserToOrganization.class,
                ProjectListSqlModel.class,
                HouseblockSnapshotSqlModel.class,
                PropertyModel.class,
                ProjectHouseblockCustomPropertySqlModel.class);
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
