package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Plan;
import nl.vng.diwi.dal.entities.PlanCategory;
import nl.vng.diwi.dal.entities.PlanCategoryState;
import nl.vng.diwi.dal.entities.PlanState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.models.PlanModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.security.LoggedUser;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class GoalService {

    public GoalService() {
    }

    public PropertyModel getProperty(VngRepository repo, UUID propertyUuid) {
        return repo.getPropertyDAO().getPropertyById(propertyUuid);
    }


    public SelectModel createGoalCategory(VngRepository repo, SelectModel goalCategoryModel, LoggedUser loggedUser) {
        PlanCategory newCategory = new PlanCategory();
        repo.persist(newCategory);

        PlanCategoryState newCategoryState = new PlanCategoryState();
        newCategoryState.setPlanCategory(newCategory);
        newCategoryState.setLabel(goalCategoryModel.getName());
        newCategoryState.setCreateUser(repo.getReferenceById(User.class, loggedUser.getUuid()));
        newCategoryState.setChangeStartDate(ZonedDateTime.now());
        repo.persist(newCategoryState);

        goalCategoryModel.setId(newCategory.getId());
        return goalCategoryModel;
    }

    public SelectModel updateGoalCategory(VngRepository repo, SelectModel goalCategoryModel, LoggedUser loggedUser) throws VngNotFoundException {

        PlanCategory category = repo.findById(PlanCategory.class, goalCategoryModel.getId());
        if (category == null) {
            throw new VngNotFoundException();
        }

        ZonedDateTime now = ZonedDateTime.now();
        User currentUser = repo.getReferenceById(User.class, loggedUser.getUuid());

        category.getStates().forEach(s -> {
            if (s.getChangeEndDate() == null) {
                s.setChangeEndDate(now);
                s.setChangeUser(currentUser);
                repo.persist(s);
            }
        });

        PlanCategoryState newCategoryState = new PlanCategoryState();
        newCategoryState.setPlanCategory(category);
        newCategoryState.setLabel(goalCategoryModel.getName());
        newCategoryState.setCreateUser(currentUser);
        newCategoryState.setChangeStartDate(now);
        repo.persist(newCategoryState);

        return goalCategoryModel;
    }


    public void deleteGoalCategory(VngRepository repo, UUID categoryId, LoggedUser loggedUser) throws VngNotFoundException {

        PlanCategory category = repo.findById(PlanCategory.class, categoryId);
        if (category == null) {
            throw new VngNotFoundException();
        }

        ZonedDateTime now = ZonedDateTime.now();
        User currentUser = repo.getReferenceById(User.class, loggedUser.getUuid());

        category.getStates().forEach(s -> {
            if (s.getChangeEndDate() == null) {
                s.setChangeEndDate(now);
                s.setChangeUser(currentUser);
                repo.persist(s);
            }
        });

        List<PlanState> planStates = repo.getGoalDAO().getActivePlanStatesByCategoryId(categoryId);

        planStates.forEach(ps -> {
            ps.setChangeEndDate(now);
            ps.setChangeUser(currentUser);
            repo.persist(ps);

            PlanState newPlanState = new PlanState();
            newPlanState.setPlan(ps.getPlan());
            newPlanState.setCreateUser(currentUser);
            newPlanState.setChangeStartDate(now);
            newPlanState.setName(ps.getName());
            newPlanState.setStartDate(ps.getStartDate());
            newPlanState.setDeadline(ps.getDeadline());
            newPlanState.setGoalValue(ps.getGoalValue());
            newPlanState.setGoalDirection(ps.getGoalDirection());
            newPlanState.setGoalType(ps.getGoalType());
            repo.persist(newPlanState);
        });

    }


    public UUID createGoal(VngRepository repo, PlanModel planModel, ZonedDateTime createTime, UUID loggedUserUuid) {

        Plan goal = new Plan();
        repo.persist(goal);

        PlanState goalState = new PlanState();
        goalState.setPlan(goal);
        goalState.setName(planModel.getName());
        goalState.setStartDate(planModel.getStartDate());
        goalState.setDeadline(planModel.getEndDate());
        goalState.setGoalType(planModel.getGoalType());
        goalState.setGoalDirection(planModel.getGoalDirection());
        goalState.setGoalValue(planModel.getGoalValue());
        goalState.setChangeStartDate(createTime);
        goalState.setCreateUser(repo.findById(User.class, loggedUserUuid));
        goalState.setCategory(repo.getReferenceById(PlanCategory.class, planModel.getCategory().getId()));
        repo.persist(goalState);

        return goal.getId();
    }

}
