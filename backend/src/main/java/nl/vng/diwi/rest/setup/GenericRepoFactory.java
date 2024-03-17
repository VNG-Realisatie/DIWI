package nl.vng.diwi.rest.setup;

import org.glassfish.hk2.api.Factory;

import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.DalFactory;
import nl.vng.diwi.dal.GenericRepository;

public class GenericRepoFactory implements Factory<GenericRepository> {
    private DalFactory dalFactory;

    public GenericRepoFactory(DalFactory factory) {
        this.dalFactory = factory;
    }


    @Override
    public GenericRepository provide() {
        Dal dal = dalFactory.constructDal();
        return new GenericRepository(dal);
    }

    @Override
    public void dispose(GenericRepository instance) {
        instance.close();

    }
}
