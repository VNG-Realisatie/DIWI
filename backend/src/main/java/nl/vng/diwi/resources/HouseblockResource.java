package nl.vng.diwi.resources;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.services.HouseblockService;

import java.util.UUID;

import static nl.vng.diwi.security.SecurityRoleConstants.Admin;

@Path("/houseblock")
@RolesAllowed({Admin})
public class HouseblockResource {

    private final VngRepository repo;
    private final HouseblockService houseblockService;

    @Inject
    public HouseblockResource(
        GenericRepository genericRepository,
        HouseblockService houseblockService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.houseblockService = houseblockService;
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public HouseblockSnapshotModel getCurrentHouseblockSnapshot(@PathParam("uuid") UUID houseblockUuid) throws VngNotFoundException {

        return houseblockService.getHouseblockSnapshot(repo, houseblockUuid);

    }
}
