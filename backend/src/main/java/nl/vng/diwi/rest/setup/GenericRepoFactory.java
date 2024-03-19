package nl.vng.diwi.rest.setup;

import org.glassfish.hk2.api.Factory;

import jakarta.inject.Inject;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.dal.Dal;
import nl.vng.diwi.dal.GenericRepository;

@Log4j2
public class GenericRepoFactory implements Factory<GenericRepository> {

    private Dal dal;

    @Inject
    public GenericRepoFactory(Dal dal) {
        this.dal = dal;
    }

    @Override
    public GenericRepository provide() {
        log.debug("Creating GenericRepository");
        return new GenericRepository(dal);
    }

    @Override
    public void dispose(GenericRepository instance) {
        instance.close();

    }
}
