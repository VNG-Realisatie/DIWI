package nl.vng.diwi.services;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Plan;
import nl.vng.diwi.dal.entities.PlanCategory;
import nl.vng.diwi.dal.entities.PlanCategoryState;
import nl.vng.diwi.dal.entities.PlanCondition;
import nl.vng.diwi.dal.entities.PlanConditionAppearanceAndType;
import nl.vng.diwi.dal.entities.PlanConditionBooleanCustomProperty;
import nl.vng.diwi.dal.entities.PlanConditionCategoryCustomProperty;
import nl.vng.diwi.dal.entities.PlanConditionCategoryCustomPropertyValue;
import nl.vng.diwi.dal.entities.PlanConditionGroundPosition;
import nl.vng.diwi.dal.entities.PlanConditionGroundPositionValue;
import nl.vng.diwi.dal.entities.PlanConditionHouseTypeValue;
import nl.vng.diwi.dal.entities.PlanConditionOrdinalCustomProperty;
import nl.vng.diwi.dal.entities.PlanConditionOwnershipValue;
import nl.vng.diwi.dal.entities.PlanConditionPhysicalAppearanceValue;
import nl.vng.diwi.dal.entities.PlanConditionProgramming;
import nl.vng.diwi.dal.entities.PlanConditionRegistryLink;
import nl.vng.diwi.dal.entities.PlanConditionRegistryLinkValue;
import nl.vng.diwi.dal.entities.PlanConditionState;
import nl.vng.diwi.dal.entities.PlanConditionTargetGroup;
import nl.vng.diwi.dal.entities.PlanConditionTargetGroupValue;
import nl.vng.diwi.dal.entities.PlanState;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.PropertyOrdinalValue;
import nl.vng.diwi.dal.entities.PropertyRangeCategoryValue;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.ConditionType;
import nl.vng.diwi.dal.entities.enums.GoalType;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.PlanModel;
import nl.vng.diwi.models.PlanSqlModel;
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

    public List<PlanModel> getAllGoals(VngRepository repo) {

        List<PlanSqlModel> sqlModels = repo.getGoalDAO().getGoals();
        List<PlanModel> result = sqlModels.stream().map(PlanModel::new).toList();
        return result;
    }

    public PlanModel getGoal(VngRepository repo, UUID planId) {

        PlanSqlModel sqlModel = repo.getGoalDAO().getGoalById(planId);
        if (sqlModel == null) {
            return null;
        }
        return new PlanModel(sqlModel);
    }

    private PlanCondition createPlanCondition(VngRepository repo, Plan plan, ConditionType type, ZonedDateTime createTime, User createUser) {
        PlanCondition planCondition = new PlanCondition();
        planCondition.setPlan(plan);
        repo.persist(planCondition);

        PlanConditionState planConditionState = new PlanConditionState();
        planConditionState.setPlanCondition(planCondition);
        planConditionState.setConditionType(type);
        planConditionState.setCreateUser(createUser);
        planConditionState.setChangeStartDate(createTime);
        repo.persist(planConditionState);

        return planCondition;
    }

    public UUID createGoal(VngRepository repo, PlanModel planModel, ZonedDateTime createTime, UUID loggedUserUuid) {

        ZonedDateTime now = ZonedDateTime.now();
        User currentUser = repo.getReferenceById(User.class, loggedUserUuid);

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
        goalState.setChangeStartDate(now);
        goalState.setCreateUser(currentUser);
        if (planModel.getCategory() != null) {
            goalState.setCategory(repo.getReferenceById(PlanCategory.class, planModel.getCategory().getId()));
        }
        repo.persist(goalState);

        if (planModel.getGeography() != null && !planModel.getGeography().getOptions().isEmpty()) {
            PlanCondition condition = createPlanCondition(repo, goal, ConditionType.PLAN_CONDITION, now, currentUser);

            PlanConditionRegistryLink registryLink = new PlanConditionRegistryLink();
            registryLink.setPlanCondition(condition);
            registryLink.setCreateUser(currentUser);
            registryLink.setChangeStartDate(now);
            repo.persist(registryLink);

            planModel.getGeography().getOptions().forEach(g -> {
                PlanConditionRegistryLinkValue registryLinkValue = new PlanConditionRegistryLinkValue();
                registryLinkValue.setRegistryLink(registryLink);
                registryLinkValue.setBrkGemeenteCode(g.getBrkGemeenteCode());
                registryLinkValue.setBrkSectie(g.getBrkSectie());
                registryLinkValue.setBrkPerceelNummer(g.getBrkPerceelNummer());
                repo.persist(registryLinkValue);
            });
        }

        if (planModel.getConditions() != null && !planModel.getConditions().isEmpty()) {
            ConditionType conditionType = (planModel.getGoalType() == GoalType.NUMBER) ? ConditionType.PLAN_CONDITION : ConditionType.GOAL_CONDITION;
            for (PlanModel.PlanConditionModel c : planModel.getConditions()) {
                PlanCondition condition = createPlanCondition(repo, goal, conditionType, now, currentUser);

                if (c.getConditionFieldType() == PlanModel.ConditionFieldType.GROUND_POSITION) {
                    PlanConditionGroundPosition gp = new PlanConditionGroundPosition();
                    gp.setPlanCondition(condition);
                    gp.setCreateUser(currentUser);
                    gp.setChangeStartDate(now);
                    repo.persist(gp);

                    c.getListOptions().forEach(s -> {
                        PlanConditionGroundPositionValue gpValue = new PlanConditionGroundPositionValue();
                        gpValue.setConditionGroundPosition(gp);
                        gpValue.setGroundPosition(GroundPosition.valueOf(s));
                        repo.persist(gpValue);
                    });

                } else if (c.getConditionFieldType() == PlanModel.ConditionFieldType.HOUSE_TYPE) {
                    PlanConditionAppearanceAndType ht = new PlanConditionAppearanceAndType();
                    ht.setPlanCondition(condition);
                    ht.setCreateUser(currentUser);
                    ht.setChangeStartDate(now);
                    repo.persist(ht);

                    c.getListOptions().forEach(s -> {
                        PlanConditionHouseTypeValue htValue = new PlanConditionHouseTypeValue();
                        htValue.setAppearanceAndTypeCondition(ht);
                        htValue.setHouseType(HouseType.valueOf(s));
                        repo.persist(htValue);
                    });

                } else if (c.getConditionFieldType() == PlanModel.ConditionFieldType.PROGRAMMING) {
                    PlanConditionProgramming p = new PlanConditionProgramming();
                    p.setPlanCondition(condition);
                    p.setProgramming(c.getBooleanValue());
                    p.setCreateUser(currentUser);
                    p.setChangeStartDate(now);
                    repo.persist(p);

                } else if (c.getConditionFieldType() == PlanModel.ConditionFieldType.OWNERSHIP) {
                    PlanConditionOwnershipValue o = new PlanConditionOwnershipValue();
                    o.setPlanCondition(condition);
                    o.setCreateUser(currentUser);
                    o.setChangeStartDate(now);
                    o.setOwnershipType(c.getOwnershipOption().getType());
                    if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
                        o.setValue(c.getOwnershipOption().getValue().getValue());
                        o.setValueRange(c.getOwnershipOption().getValue().toRange());
                        if (c.getOwnershipOption().getRangeCategoryOption() != null) {
                            o.setOwnershipRangeCategoryValue(repo.getReferenceById(PropertyRangeCategoryValue.class, c.getOwnershipOption().getRangeCategoryOption().getId()));
                        }
                    } else {
                        o.setRentalValue(c.getOwnershipOption().getValue().getValue());
                        o.setRentalValueRange(c.getOwnershipOption().getValue().toRange());
                        if (c.getOwnershipOption().getRangeCategoryOption() != null) {
                            o.setRentalRangeCategoryValue(repo.getReferenceById(PropertyRangeCategoryValue.class, c.getOwnershipOption().getRangeCategoryOption().getId()));
                        }
                    }
                    repo.persist(o);

                } else if (c.getConditionFieldType() == PlanModel.ConditionFieldType.PROPERTY) {
                    if (c.getPropertyName().equals(Constants.FIXED_PROPERTY_PHYSICAL_APPEARANCE)) {
                        var at = new PlanConditionAppearanceAndType();
                        at.setPlanCondition(condition);
                        at.setCreateUser(currentUser);
                        at.setChangeStartDate(now);
                        repo.persist(at);

                        c.getCategoryOptions().forEach(s -> {
                            var paValue = new PlanConditionPhysicalAppearanceValue();
                            paValue.setAppearanceAndTypeCondition(at);
                            paValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, s.getId()));
                            repo.persist(paValue);
                        });

                    } else if (c.getPropertyName().equals(Constants.FIXED_PROPERTY_TARGET_GROUP)) {
                        var tg = new PlanConditionTargetGroup();
                        tg.setPlanCondition(condition);
                        tg.setCreateUser(currentUser);
                        tg.setChangeStartDate(now);
                        repo.persist(tg);

                        c.getCategoryOptions().forEach(s -> {
                            var tgValue = new PlanConditionTargetGroupValue();
                            tgValue.setTargetGroupCondition(tg);
                            tgValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, s.getId()));
                            repo.persist(tgValue);
                        });

                    } else {
                        if (c.getPropertyType() == PropertyType.BOOLEAN) {
                            var bcp = new PlanConditionBooleanCustomProperty();
                            bcp.setPlanCondition(condition);
                            bcp.setCreateUser(currentUser);
                            bcp.setChangeStartDate(now);
                            bcp.setValue(c.getBooleanValue());
                            repo.persist(bcp);

                        } else if (c.getPropertyType() == PropertyType.CATEGORY) {
                            var ccp = new PlanConditionCategoryCustomProperty();
                            ccp.setPlanCondition(condition);
                            ccp.setCreateUser(currentUser);
                            ccp.setChangeStartDate(now);
                            ccp.setProperty(repo.getReferenceById(Property.class, c.getPropertyId()));
                            repo.persist(ccp);

                            c.getCategoryOptions().forEach(co -> {
                                var ccpv = new PlanConditionCategoryCustomPropertyValue();
                                ccpv.setCategoryCondition(ccp);
                                ccpv.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, co.getId()));
                                repo.persist(ccpv);
                            });

                        } else if (c.getPropertyType() == PropertyType.ORDINAL) {
                            var cop = new PlanConditionOrdinalCustomProperty();
                            cop.setPlanCondition(condition);
                            cop.setCreateUser(currentUser);
                            cop.setChangeStartDate(now);
                            cop.setProperty(repo.getReferenceById(Property.class, c.getPropertyId()));
                            if (c.getOrdinalOptions().getValue() != null) {
                                cop.setValue(repo.getReferenceById(PropertyOrdinalValue.class, c.getOrdinalOptions().getValue().getId()));
                                cop.setValueType(ValueType.SINGLE_VALUE);
                            } else {
                                cop.setValueType(ValueType.RANGE);
                                cop.setMinValue(repo.getReferenceById(PropertyOrdinalValue.class, c.getOrdinalOptions().getMin().getId()));
                                cop.setMaxValue(repo.getReferenceById(PropertyOrdinalValue.class, c.getOrdinalOptions().getMax().getId()));
                            }
                            repo.persist(cop);

                        }
                    }
                }
            }
        }

        return goal.getId();
    }

    public void deleteGoal(VngRepository repo, UUID planId, LoggedUser loggedUser) throws VngNotFoundException {

        Plan plan = repo.findById(Plan.class, planId);
        if (plan == null) {
            throw new VngNotFoundException();
        }

        ZonedDateTime now = ZonedDateTime.now();
        User currentUser = repo.getReferenceById(User.class, loggedUser.getUuid());

        plan.getStates().forEach(s -> {
            if (s.getChangeEndDate() == null) {
                s.setChangeEndDate(now);
                s.setChangeUser(currentUser);
                repo.persist(s);
            }
        });

    }
}
