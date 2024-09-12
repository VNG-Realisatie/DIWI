package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Blueprint;
import nl.vng.diwi.dal.entities.BlueprintState;
import nl.vng.diwi.dal.entities.BlueprintToElement;
import nl.vng.diwi.dal.entities.BlueprintToUserGroup;
import nl.vng.diwi.dal.entities.MultiProjectDashboardSqlModel;
import nl.vng.diwi.dal.entities.MultiProjectPolicyGoalSqlModel;
import nl.vng.diwi.dal.entities.ProjectDashboardSqlModel;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.enums.GoalType;
import nl.vng.diwi.models.BlueprintModel;
import nl.vng.diwi.models.MultiProjectDashboardModel;
import nl.vng.diwi.models.ProjectDashboardModel;
import nl.vng.diwi.rest.VngBadRequestException;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class DashboardService {
    private static final Logger logger = LogManager.getLogger();

    public DashboardService() {
    }

    public ProjectDashboardModel getProjectDashboardSnapshot(VngRepository repo, UUID projectUuid, LocalDate snapshotDate, LoggedUser loggedUser) throws VngNotFoundException {

        ProjectDashboardSqlModel projectModel = repo.getProjectsDAO().getProjectDashboardSnapshot(projectUuid, snapshotDate, loggedUser);

        if (projectModel == null) {
            logger.error("Project with uuid {} was not found.", projectUuid);
            throw new VngNotFoundException();
        }

        return new ProjectDashboardModel(projectModel);
    }

    public MultiProjectDashboardModel getMultiProjectDashboardSnapshot(VngRepository repo, LocalDate snapshotDate, LoggedUser loggedUser) throws VngNotFoundException {

        MultiProjectDashboardSqlModel projectModel = repo.getProjectsDAO().getMultiProjectDashboardSnapshot(snapshotDate, loggedUser);

        return new MultiProjectDashboardModel(projectModel);
    }

    public List<MultiProjectPolicyGoalSqlModel> getMultiProjectPolicyGoals(VngRepository repo, LocalDate snapshotDate, LoggedUser loggedUser) {

        List<MultiProjectPolicyGoalSqlModel> result = repo.getProjectsDAO().getMultiProjectPolicyGoals(snapshotDate, loggedUser);
        result.forEach(r -> {
            if (r.getGoalType() == GoalType.PERCENTAGE) {
                if (r.getAmount() != null && r.getTotalAmount() != null) {
                    if (r.getTotalAmount() > 0) {
                        r.setPercentage(new BigDecimal(r.getAmount()).multiply(new BigDecimal(100))
                            .divide(new BigDecimal(r.getTotalAmount()), 4, RoundingMode.HALF_UP));
                    } else {
                        r.setPercentage(BigDecimal.valueOf(100));
                    }
                }
            }
        });
        return result;
    }

    public UUID createBlueprint(VngRepository repo, BlueprintModel blueprintModel, ZonedDateTime zdtNow, UUID loggedUserUuid) throws VngBadRequestException {

        if (checkBlueprintNameExists(repo, blueprintModel.getName(), null)) {
            throw new VngBadRequestException("Blueprint name already exists");
        }

        Blueprint blueprint = new Blueprint();
        repo.persist(blueprint);

        createBlueprintStateWithElementsAndUserGroups(repo, blueprint, blueprintModel, zdtNow, loggedUserUuid);

        return blueprint.getId();

    }

    private boolean checkBlueprintNameExists(VngRepository repo, String name, UUID currentBlueprintUuid) {
        BlueprintState state = repo.getBlueprintDAO().getActiveBlueprintStateByName(name);
        if (state != null && !state.getBlueprint().getId().equals(currentBlueprintUuid)) {
            return true;
        }
        return false;
    }

    private void createBlueprintStateWithElementsAndUserGroups(VngRepository repo, Blueprint blueprint, BlueprintModel blueprintModel, ZonedDateTime zdtNow, UUID loggedUserUuid) {
        BlueprintState blueprintState = new BlueprintState();
        blueprintState.setBlueprint(blueprint);
        blueprintState.setName(blueprintModel.getName());
        blueprintState.setChangeStartDate(zdtNow);
        blueprintState.setCreateUser(repo.getReferenceById(User.class, loggedUserUuid));
        repo.persist(blueprintState);

        if (blueprintModel.getElements() != null) {
            blueprintModel.getElements().forEach(e -> {
                BlueprintToElement bte = new BlueprintToElement();
                bte.setBlueprintState(blueprintState);
                bte.setElement(e);
                repo.persist(bte);
            });
        }


        if (blueprintModel.getUserGroups() != null) {
            blueprintModel.getUserGroups().forEach(ug -> {
                BlueprintToUserGroup btug = new BlueprintToUserGroup();
                btug.setBlueprintState(blueprintState);
                btug.setUserGroup(repo.getReferenceById(UserGroup.class, ug.getUuid()));
                repo.persist(btug);
            });
        }
    }

    public void deleteBlueprint(VngRepository repo, UUID blueprintUuid, UUID loggedUserUuid) throws VngNotFoundException {

        BlueprintState state = repo.getBlueprintDAO().getActiveBlueprintStateByBlueprintUuid(blueprintUuid);

        if (state == null) {
            throw new VngNotFoundException();
        }

        state.setChangeEndDate(ZonedDateTime.now());
        state.setChangeUser(repo.getReferenceById(User.class, loggedUserUuid));
        repo.persist(state);

    }

    public void updateBlueprint(VngRepository repo, BlueprintModel blueprintModel, ZonedDateTime zdtNow, UUID loggedUserUuid)
        throws VngNotFoundException, VngBadRequestException {

        if (checkBlueprintNameExists(repo, blueprintModel.getName(), blueprintModel.getUuid())) {
            throw new VngBadRequestException("Blueprint name already exists");
        }

        deleteBlueprint(repo, blueprintModel.getUuid(), loggedUserUuid);

        Blueprint blueprint = repo.getReferenceById(Blueprint.class, blueprintModel.getUuid());
        createBlueprintStateWithElementsAndUserGroups(repo, blueprint, blueprintModel, zdtNow, loggedUserUuid);

    }
}
