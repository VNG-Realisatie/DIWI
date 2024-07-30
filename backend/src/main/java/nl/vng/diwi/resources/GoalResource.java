package nl.vng.diwi.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.PlanCategory;
import nl.vng.diwi.dal.entities.PlanCategoryState;
import nl.vng.diwi.models.PlanModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.GoalService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Path("/goals")
//@RolesAllowed("BLOCKED_BY_DEFAULT") // This forces us to make sure each end-point has action(s) assigned, so we never have things open by default.
public class GoalResource {

    private final VngRepository repo;
    private final GoalService goalService;

    @Inject
    public GoalResource(GenericRepository genericRepository, GoalService goalService) {
        this.repo = new VngRepository(genericRepository.getDal().getSession());
        this.goalService = goalService;
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PlanModel> getAllGoals() {

        return goalService.getAllGoals(repo);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public PlanModel getGoalById(@PathParam("id") UUID planId) throws VngNotFoundException {

        PlanModel planModel = goalService.getGoal(repo, planId);
        if (planModel == null) {
            throw new VngNotFoundException();
        }
        return planModel;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PlanModel createGoal(PlanModel planModel, @Context LoggedUser loggedUser) throws VngBadRequestException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            String validationError = planModel.validate(repo);
            if (validationError != null) {
                throw new VngBadRequestException(validationError);
            }

            UUID newPlanUuid = goalService.createGoal(repo, planModel, ZonedDateTime.now(), loggedUser.getUuid());
            transaction.commit();

            return goalService.getGoal(repo, newPlanUuid);
        }
    }


    @DELETE
    @Path("/{id}")
    public void deleteGoal(@PathParam("id") UUID planId, ContainerRequestContext requestContext) throws VngNotFoundException {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            goalService.deleteGoal(repo, planId, loggedUser);
            transaction.commit();
        }
    }

    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<SelectModel> getAllGoalCategories() {

        return repo.getGoalDAO().getAllGoalCategories();
    }

    @GET
    @Path("/categories/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SelectModel getGoalCategory(@PathParam("id") UUID categoryId) throws VngNotFoundException {

        PlanCategory category = repo.findById(PlanCategory.class, categoryId);
        if (category == null) {
            throw new VngNotFoundException();
        }
        PlanCategoryState ps = category.getStates().stream().filter(s -> s.getChangeEndDate() == null).findFirst().orElseThrow(VngNotFoundException::new);

        return new SelectModel(categoryId, ps.getLabel());
    }


    @POST
    @Path("/categories")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SelectModel createGoalCategory(SelectModel goalCategoryModel, @Context LoggedUser loggedUser) {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            goalCategoryModel = goalService.createGoalCategory(repo, goalCategoryModel, loggedUser);
            transaction.commit();
        }

        return goalCategoryModel;
    }

    @PUT
    @Path("/categories/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SelectModel updateGoalCategory(@PathParam("id") UUID categoryId, SelectModel goalCategoryModel, @Context LoggedUser loggedUser) throws VngNotFoundException {

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            goalCategoryModel.setId(categoryId);
            goalCategoryModel = goalService.updateGoalCategory(repo, goalCategoryModel, loggedUser);
            transaction.commit();
        }

        return goalCategoryModel;
    }


    @DELETE
    @Path("/categories/{id}")
    public void deleteGoalCategory(@PathParam("id") UUID categoryId, ContainerRequestContext requestContext) throws VngNotFoundException {

        var loggedUser = (LoggedUser) requestContext.getProperty("loggedUser");

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {
            goalService.deleteGoalCategory(repo, categoryId, loggedUser);
            transaction.commit();
        }
    }
}
