package com.vng.dal;

import java.util.Arrays;
import java.util.List;

import com.vng.dal.entities.Organization;
import com.vng.dal.entities.OrganizationState;
import com.vng.dal.entities.User;

public class GenericRepository implements AutoCloseable {

    public static List<Class<? extends Object>> getEntities() {
        List<Class<? extends Object>> entities =
            Arrays.asList(Organization.class,
                OrganizationState.class,
                User.class);
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
