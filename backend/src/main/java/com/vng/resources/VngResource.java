package com.vng.resources;

import static com.vng.security.SecurityRoleConstants.Admin;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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
