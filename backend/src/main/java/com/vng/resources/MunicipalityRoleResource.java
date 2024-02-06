package com.vng.resources;

import com.vng.dal.GenericRepository;
import com.vng.dal.VngRepository;
import com.vng.models.SelectModel;
import com.vng.security.LoggedUser;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import static com.vng.security.SecurityRoleConstants.Admin;

@Path("/municipalityrole")
@RolesAllowed({Admin})
public class MunicipalityRoleResource {
    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;

    @Inject
    public MunicipalityRoleResource(GenericRepository genericRepository) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelectModel> getAllMunicipalityRoles(@Context LoggedUser loggedUser) {

        return repo.getMunicipalityRoles();

    }

}
