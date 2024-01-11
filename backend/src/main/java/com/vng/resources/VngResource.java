package com.vng.resources;

import static com.vng.security.SecurityRoleConstants.Admin;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import com.vng.dal.GenericRepository;
import com.vng.dal.VngRepository;
import com.vng.models.SelectModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vng.security.LoggedUser;
import com.vng.services.VngService;

@Path("/vng")
@RolesAllowed({Admin})
public class VngResource {
    private static final Logger logger = LogManager.getLogger();

    private final VngRepository repo;
    private final VngService vngService;

    @Inject
    public VngResource(
        GenericRepository genericRepository,
        VngService vngService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.vngService = vngService;
    }

    @GET
    @Path("/organizations/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelectModel> getAllOrganizationStates(@Context LoggedUser loggedUser) {

        return vngService.getAllOrganizationStates(repo);

    }

}
