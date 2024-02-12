package com.vng.dal;

import java.util.Arrays;
import java.util.List;

import com.vng.dal.entities.*;
import com.vng.models.ProjectListModel;

public class GenericRepository implements AutoCloseable {

    public static final String VNG_SCHEMA_NAME = "diwi_testset";

    public static List<Class<? extends Object>> getEntities() {
        List<Class<? extends Object>> entities =
            Arrays.asList(
                Milestone.class,
                Organization.class,
                OrganizationState.class,
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
                ProjectListModel.class);
        return entities;
    }

    private Dal dal;

    public GenericRepository(Dal dal) {
        this.dal = dal;
    }

    public Dal getDal() {
        return dal;
    }

    @Override
    public void close() {
        dal.close();
    }
}
