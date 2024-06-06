package nl.vng.diwi.services;

import lombok.Data;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.HouseblockAppearanceAndTypeChangelog;
import nl.vng.diwi.dal.entities.HouseblockBooleanCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockCategoryCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockCategoryCustomPropertyChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockDeliveryDateChangelog;
import nl.vng.diwi.dal.entities.HouseblockDurationChangelog;
import nl.vng.diwi.dal.entities.HouseblockGroundPositionChangelog;
import nl.vng.diwi.dal.entities.HouseblockGroundPositionChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockHouseTypeChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockMutatieChangelog;
import nl.vng.diwi.dal.entities.HouseblockNameChangelog;
import nl.vng.diwi.dal.entities.HouseblockNumericCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockOrdinalCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockOwnershipValueChangelog;
import nl.vng.diwi.dal.entities.HouseblockPhysicalAppearanceChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockProgrammingChangelog;
import nl.vng.diwi.dal.entities.HouseblockState;
import nl.vng.diwi.dal.entities.HouseblockTargetGroupChangelog;
import nl.vng.diwi.dal.entities.HouseblockTargetGroupChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockTextCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectBooleanCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectCategoryPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectCategoryPropertyChangelogValue;
import nl.vng.diwi.dal.entities.ProjectDurationChangelog;
import nl.vng.diwi.dal.entities.ProjectFaseChangelog;
import nl.vng.diwi.dal.entities.ProjectNameChangelog;
import nl.vng.diwi.dal.entities.ProjectNumericCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectOrdinalPropertyChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelogValue;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelogValue;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.ProjectTextPropertyChangelog;
import nl.vng.diwi.dal.entities.Property;
import nl.vng.diwi.dal.entities.PropertyCategoryValue;
import nl.vng.diwi.dal.entities.PropertyOrdinalValue;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.UserGroup;
import nl.vng.diwi.dal.entities.UserGroupToProject;
import nl.vng.diwi.dal.entities.UserState;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.ImportError;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.security.UserAction;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
public class ProjectImportModel {

    private Integer id;
    private String projectName;
    private PlanType planType;
    private Boolean programming;
    private Confidentiality confidentialityLevel;
    private String ownerEmail;
    private UUID ownerUserGroupUuid;

    private ProjectStatus projectStatus;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    private Map<ProjectPhase, LocalDate> projectPhasesMap = new HashMap<>();
    private Map<PlanStatus, LocalDate> projectPlanStatusesMap = new HashMap<>();

    private Map<UUID, UUID> projectCategoryPropsMap = new HashMap<>();
    private Map<UUID, String> projectStringPropsMap = new HashMap<>();
    private Map<UUID, Boolean> projectBooleanPropsMap = new HashMap<>();
    private Map<UUID, Double> projectNumericPropsMap = new HashMap<>();
    private Map<UUID, UUID> projectOrdinalPropsMap = new HashMap<>();

    private boolean hasMunicipality;
    private boolean hasDistrict;
    private boolean hasNeighbourhood;

    private List<HouseblockImportModel> houseblocks = new ArrayList<>();

    public boolean hasSameProjectLevelData(ProjectImportModel other) {
        return Objects.equals(this.projectName, other.projectName) &&
            Objects.equals(this.planType, other.planType) &&
            Objects.equals(this.programming, other.programming) &&
            Objects.equals(this.projectStatus, other.projectStatus) &&
            Objects.equals(this.projectStartDate, other.projectStartDate) &&
            Objects.equals(this.projectPhasesMap, other.projectPhasesMap) &&
            Objects.equals(this.projectCategoryPropsMap, other.projectCategoryPropsMap) &&
            Objects.equals(this.projectPlanStatusesMap, other.projectPlanStatusesMap) &&
            Objects.equals(this.projectStringPropsMap, other.projectStringPropsMap) &&
            Objects.equals(this.projectBooleanPropsMap, other.projectBooleanPropsMap) &&
            Objects.equals(this.projectNumericPropsMap, other.projectNumericPropsMap) &&
            Objects.equals(this.projectOrdinalPropsMap, other.projectOrdinalPropsMap);
    }

    @Data
    public static class HouseblockImportModel {

        private Integer mutation;
        private MutationType mutationType;
        private String name;

        private Map<HouseType, Integer> houseTypeMap = new HashMap<>();
        private Map<OwnershipType, Integer> ownershipTypeMap = new HashMap<>();
        private List<HouseblockSnapshotModel.OwnershipValue> ownershipValues = new ArrayList<>();
        private Map<UUID, Integer> physicalAppearanceMap = new HashMap<>();
        private Map<UUID, Integer> targetGroupMap = new HashMap<>();
        private Map<GroundPosition, Integer> groundPositionMap = new HashMap<>();

        private Map<UUID, UUID> houseblockCategoryPropsMap = new HashMap<>();
        private Map<UUID, String> houseblockStringPropsMap = new HashMap<>();
        private Map<UUID, Boolean> houseblockBooleanPropsMap = new HashMap<>();
        private Map<UUID, Double> houseblockNumericPropsMap = new HashMap<>();
        private Map<UUID, UUID> houseblockOrdinalPropsMap = new HashMap<>();

        private LocalDate latestDeliveryDate;
        private LocalDate earliestDeliveryDate;
        private Map<LocalDate, Integer> deliveryDateMap = new HashMap<>();

        public HouseblockImportModel(MutationType mutationType, Integer mutation, String name) {
            this.mutationType = mutationType;
            this.mutation = mutation;
            if (name != null) {
                this.name = name;
            } else if (mutationType == MutationType.CONSTRUCTION) {
                this.name = "Bouw";
            } else {
                this.name = "Sloop";
            }
        }

        public void validate(ProjectImportModel projectRowModel, Integer excelRowNo, List<ImportError> rowErrors) {

            if (latestDeliveryDate == null) {
                latestDeliveryDate = deliveryDateMap.keySet().stream().max(LocalDate::compareTo).orElse(projectRowModel.projectEndDate);
            }
            if (earliestDeliveryDate == null) {
                earliestDeliveryDate = deliveryDateMap.keySet().stream().min(LocalDate::compareTo).orElse(projectRowModel.projectEndDate);
            }

            if (latestDeliveryDate.isAfter(projectRowModel.projectEndDate)) {
                rowErrors.add(new ImportError(excelRowNo, projectRowModel.getId(), this.name, ImportError.ERROR.HOUSEBLOCK_DELIVERY_DATE_AFTER_PROJECT_END_DATE));
            }

            if (mutation <= 0) {
                rowErrors.add(new ImportError(excelRowNo, projectRowModel.getId(), this.name, ImportError.ERROR.HOUSEBLOCK_HOUSING_NUMBER_NOT_POSITIVE));
            }

            validateMapTotals(projectRowModel.getId(), deliveryDateMap, mutation, excelRowNo, ImportError.ERROR.HOUSEBLOCK_DELIVERY_TOTAL_INCORRECT, rowErrors);
            if (!deliveryDateMap.isEmpty()) {
                LocalDate projectDeliveryDate = projectRowModel.getProjectPhasesMap().get(ProjectPhase._6_REALIZATION);
                if (projectDeliveryDate != null) {
                    deliveryDateMap.keySet().forEach(dd -> {
                        if (dd.isBefore(projectDeliveryDate)) {
                            rowErrors.add(new ImportError(excelRowNo, projectRowModel.getId(), this.name, ImportError.ERROR.HOUSEBLOCK_DELIVERY_DATE_BEFORE_PROJECT_DELIVERY_PHASE));
                        }
                    });
                }
            }

            //grotte validations

            validateMapTotals(projectRowModel.getId(), ownershipTypeMap, mutation, excelRowNo, ImportError.ERROR.HOUSEBLOCK_OWNERSHIP_TYPE_TOTAL_INCORRECT, rowErrors);
            if (!ownershipTypeMap.isEmpty()) {
                Map<OwnershipType, Integer> ownershipValueMap = ownershipValues.stream()
                    .collect(Collectors.toMap(HouseblockSnapshotModel.OwnershipValue::getType, HouseblockSnapshotModel.OwnershipValue::getAmount, Integer::sum));

                if (ownershipValueMap.containsKey(OwnershipType.KOOPWONING)) {
                    if (!Objects.equals(ownershipTypeMap.get(OwnershipType.KOOPWONING), ownershipValueMap.get(OwnershipType.KOOPWONING))) {
                        rowErrors.add(new ImportError(excelRowNo, projectRowModel.getId(), this.name, ImportError.ERROR.HOUSEBLOCK_OWNERSHIP_OWNER_TOTAL_INCORRECT));
                    }
                }
                if (ownershipValueMap.containsKey(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER)) {
                    if (!Objects.equals(ownershipTypeMap.get(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER), ownershipValueMap.get(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER))) {
                        rowErrors.add(new ImportError(excelRowNo, projectRowModel.getId(), this.name, ImportError.ERROR.HOUSEBLOCK_OWNERSHIP_LANDLORD_TOTAL_INCORRECT));
                    }
                }
                if (ownershipValueMap.containsKey(OwnershipType.HUURWONING_WONINGCORPORATIE)) {
                    if (!Objects.equals(ownershipTypeMap.get(OwnershipType.HUURWONING_WONINGCORPORATIE), ownershipValueMap.get(OwnershipType.HUURWONING_WONINGCORPORATIE))) {
                        rowErrors.add(new ImportError(excelRowNo, projectRowModel.getId(), this.name, ImportError.ERROR.HOUSEBLOCK_OWNERSHIP_HOUSING_ASSOCIATION_TOTAL_INCORRECT));
                    }
                }
            }

            validateMapTotals(projectRowModel.getId(), houseTypeMap, mutation, excelRowNo, ImportError.ERROR.HOUSEBLOCK_HOUSE_TYPE_TOTAL_INCORRECT, rowErrors);
            validateMapTotals(projectRowModel.getId(), physicalAppearanceMap, mutation, excelRowNo, ImportError.ERROR.HOUSEBLOCK_PHYSICAL_APPEARANCE_TOTAL_INCORRECT, rowErrors);
            validateMapTotals(projectRowModel.getId(), targetGroupMap, mutation, excelRowNo, ImportError.ERROR.HOUSEBLOCK_TARGET_GROUP_TOTAL_INCORRECT, rowErrors);
            validateMapTotals(projectRowModel.getId(), groundPositionMap, mutation, excelRowNo, ImportError.ERROR.HOUSEBLOCK_GROUND_POSITION_TOTAL_INCORRECT, rowErrors);

        }

        private <T> void validateMapTotals(Integer projectId, Map<T, Integer> map, Integer referenceTotal, Integer excelRowNo, ImportError.ERROR error, List<ImportError> rowErrors) {
            if (!map.isEmpty()) {
                Integer mapTotal = map.values().stream().reduce(0, Integer::sum);
                if (!Objects.equals(referenceTotal, mapTotal)) {
                    rowErrors.add(new ImportError(excelRowNo, projectId, this.name, error));
                }
            }
        }
    }

    public void validate(VngRepository repo, Integer excelRowNo, List<ImportError> rowErrors, LocalDate importTime) {

        if (ownerEmail != null) {
            UserState userState = repo.getUserDAO().getUserByEmail(ownerEmail);
            if (userState != null && userState.getUserRole().allowedActions.contains(UserAction.CAN_OWN_PROJECTS)) {
                ownerUserGroupUuid = repo.getUsergroupDAO().findSingleUserUserGroup(userState.getUser().getId());
            }
        }
        if (ownerUserGroupUuid == null) {
            rowErrors.add(new ImportError(excelRowNo, ownerEmail, ImportError.ERROR.PROJECT_OWNER_UNKNOWN));
        }

        //business logic validations - all individual values are valid by this point

        if (!projectEndDate.isAfter(projectStartDate)) {
            rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_START_DATE_AFTER_END_DATE));
        }
        switch (projectStatus) {
            case NEW -> {
                if (importTime.isAfter(projectStartDate) || importTime.isAfter(projectEndDate)) {
                    rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_DATES_WRONG_FOR_PROJECT_STATUS));
                }
            }
            case ACTIVE -> {
                if (importTime.isBefore(projectStartDate) || importTime.isAfter(projectEndDate)) {
                    rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_DATES_WRONG_FOR_PROJECT_STATUS));
                }
            }
            case REALIZED, TERMINATED -> {
                if (importTime.isBefore(projectStartDate) || importTime.isBefore(projectEndDate)) {
                    rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_DATES_WRONG_FOR_PROJECT_STATUS));
                }
            }
        }

        if (!projectPhasesMap.isEmpty()) {
            LocalDate phaseEndDate = projectEndDate;
            List<ProjectPhase> projectPhasesList = Arrays.stream(ProjectPhase.values()).sorted(Collections.reverseOrder()).toList();
            for (ProjectPhase phase : projectPhasesList) {
                if (projectPhasesMap.containsKey(phase)) {
                    LocalDate phaseStartDate = projectPhasesMap.get(phase);
                    if (phaseStartDate.isBefore(projectStartDate) || !phaseStartDate.isBefore(phaseEndDate)) {
                        rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_WRONG_PHASE_DATES));
                    }
                    phaseEndDate = phaseStartDate;
                }
            }
        }

        if (!projectPlanStatusesMap.isEmpty()) {
            LocalDate planStatusEndDate = projectEndDate;
            List<PlanStatus> planStatusesList = Arrays.stream(PlanStatus.values()).sorted(Collections.reverseOrder()).toList();
            for (PlanStatus planStatus : planStatusesList) {
                if (projectPlanStatusesMap.containsKey(planStatus)) {
                    LocalDate planStatusStartDate = projectPlanStatusesMap.get(planStatus);
                    if (planStatusStartDate.isBefore(projectStartDate) || !planStatusStartDate.isBefore(planStatusEndDate)) {
                        rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_WRONG_PLAN_STATUS_DATES));
                    }
                    planStatusEndDate = planStatusStartDate;
                }
            }
        }

        if (hasNeighbourhood && (!hasMunicipality || !hasDistrict)) {
            rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_LOCATION_INCOMPLETE));
        }
        if (hasDistrict && !hasMunicipality) {
            rowErrors.add(new ImportError(excelRowNo, id, null, ImportError.ERROR.PROJECT_LOCATION_INCOMPLETE));
        }

        houseblocks.forEach(h -> h.validate(this, excelRowNo, rowErrors));
    }

    private MilestoneStatus getMilestoneStatus(LocalDate milestoneTime, ZonedDateTime importTime) {
        if (milestoneTime.isAfter(importTime.toLocalDate())) {
            return MilestoneStatus.GEREALISEERD;
        } else {
            return MilestoneStatus.GEPLAND;
        }
    }

    private Milestone getOrCreateProjectMilestone(VngRepository repo, List<MilestoneState> milestoneStates, Project project, LocalDate milestoneDate,
                                                  MilestoneStatus milestoneStatus, User user, ZonedDateTime importTime) {

        MilestoneState existingMilestone = milestoneStates.stream().filter(ms -> ms.getDate().equals(milestoneDate)).findFirst().orElse(null);
        if (existingMilestone == null) {
            var milestone = new Milestone();
            milestone.setProject(project);
            repo.persist(milestone);

            var milestoneState = new MilestoneState();
            milestoneState.setDate(milestoneDate);
            milestoneState.setMilestone(milestone);
            milestoneState.setCreateUser(user);
            milestoneState.setChangeStartDate(importTime);
            milestoneState.setState(milestoneStatus == null ? getMilestoneStatus(milestoneDate, importTime) : milestoneStatus);
            repo.persist(milestoneState);

            milestoneStates.add(milestoneState);
            return milestone;
        } else {
            return existingMilestone.getMilestone();
        }
    }

    public SelectModel persistProjectAndHouseblocks(VngRepository repo, User user, ZonedDateTime importTime) {

        var project = new Project();
        repo.persist(project);

        List<MilestoneState> projectMilestones = new ArrayList<>();

        var startMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, projectStartDate, null, user, importTime);

        var endMilestoneStatus = projectStatus == ProjectStatus.TERMINATED ? MilestoneStatus.AFGEBROKEN : getMilestoneStatus(projectEndDate, importTime);
        var endMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, projectEndDate, endMilestoneStatus, user, importTime);

        Consumer<MilestoneChangeDataSuperclass> setChangelogValues = (MilestoneChangeDataSuperclass entity) -> {
            entity.setStartMilestone(startMilestone);
            entity.setEndMilestone(endMilestone);
            entity.setCreateUser(user);
            entity.setChangeStartDate(importTime);
        };

        var duration = new ProjectDurationChangelog();
        setChangelogValues.accept(duration);
        duration.setProject(project);
        repo.persist(duration);

        var name = new ProjectNameChangelog();
        name.setProject(project);
        name.setName(projectName);
        setChangelogValues.accept(name);
        repo.persist(name);

        var state = new ProjectState();
        state.setProject(project);
        state.setCreateUser(user);
        state.setChangeStartDate(importTime);
        state.setConfidentiality(confidentialityLevel);
        state.setColor("#000000");
        repo.persist(state);

        UserGroupToProject ugtp = new UserGroupToProject();
        ugtp.setProject(project);
        ugtp.setChangeStartDate(importTime);
        ugtp.setCreateUser(user);
        ugtp.setUserGroup(repo.findById(UserGroup.class, ownerUserGroupUuid));
        repo.persist(ugtp);

        if (planType != null) {
            var planTypeChangelog = new ProjectPlanTypeChangelog();
            planTypeChangelog.setProject(project);
            setChangelogValues.accept(planTypeChangelog);
            repo.persist(planTypeChangelog);
            var planTypeValue = new ProjectPlanTypeChangelogValue();
            planTypeValue.setPlanTypeChangelog(planTypeChangelog);
            planTypeValue.setPlanType(planType);
            repo.persist(planTypeValue);
        }

        if (!projectPhasesMap.isEmpty()) {
            Milestone phaseEndMilestone = endMilestone;
            List<ProjectPhase> projectPhasesList = Arrays.stream(ProjectPhase.values()).sorted(Collections.reverseOrder()).toList();
            for (ProjectPhase phase : projectPhasesList) {
                if (projectPhasesMap.containsKey(phase)) {
                    LocalDate phaseStartDate = projectPhasesMap.get(phase);
                    Milestone phaseStartMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, phaseStartDate, null, user, importTime);

                    var phaseChangelog = new ProjectFaseChangelog();
                    phaseChangelog.setProject(project);
                    phaseChangelog.setStartMilestone(phaseStartMilestone);
                    phaseChangelog.setEndMilestone(phaseEndMilestone);
                    phaseChangelog.setChangeStartDate(importTime);
                    phaseChangelog.setCreateUser(user);
                    phaseChangelog.setProjectPhase(phase);

                    repo.persist(phaseChangelog);
                    phaseEndMilestone = phaseStartMilestone;
                }
            }
        }
        if (!projectPlanStatusesMap.isEmpty()) {
            Milestone planStatusEndMilestone = endMilestone;
            List<PlanStatus> planStatusesList = Arrays.stream(PlanStatus.values()).sorted(Collections.reverseOrder()).toList();
            for (PlanStatus planStatus : planStatusesList) {
                if (projectPlanStatusesMap.containsKey(planStatus)) {
                    LocalDate planStatusStartDate = projectPlanStatusesMap.get(planStatus);
                    Milestone planStatusStartMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, planStatusStartDate, null, user, importTime);

                    var planStatusChangelog = new ProjectPlanologischePlanstatusChangelog();
                    planStatusChangelog.setProject(project);
                    planStatusChangelog.setStartMilestone(planStatusStartMilestone);
                    planStatusChangelog.setEndMilestone(planStatusEndMilestone);
                    planStatusChangelog.setChangeStartDate(importTime);
                    planStatusChangelog.setCreateUser(user);
                    repo.persist(planStatusChangelog);

                    var planStatusChangelogValue = new ProjectPlanologischePlanstatusChangelogValue();
                    planStatusChangelogValue.setPlanStatusChangelog(planStatusChangelog);
                    planStatusChangelogValue.setPlanStatus(planStatus);
                    repo.persist(planStatusChangelogValue);

                    planStatusEndMilestone = planStatusStartMilestone;
                }
            }
        }

        if (!projectCategoryPropsMap.isEmpty()) {
            for (Map.Entry<UUID, UUID> categoryEntry : projectCategoryPropsMap.entrySet()) {
                var projectPropertyChangelog = new ProjectCategoryPropertyChangelog();
                projectPropertyChangelog.setProject(project);
                setChangelogValues.accept(projectPropertyChangelog);
                projectPropertyChangelog.setProperty(repo.getReferenceById(Property.class, categoryEntry.getKey()));
                repo.persist(projectPropertyChangelog);

                var projectPropertyChangelogValue = new ProjectCategoryPropertyChangelogValue();
                projectPropertyChangelogValue.setCategoryChangelog(projectPropertyChangelog);
                projectPropertyChangelogValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, categoryEntry.getValue()));
                repo.persist(projectPropertyChangelogValue);
            }
        }

        if (!projectStringPropsMap.isEmpty()) {
            for (Map.Entry<UUID, String> stringEntry : projectStringPropsMap.entrySet()) {
                var projectTextChangelog = new ProjectTextPropertyChangelog();
                projectTextChangelog.setProject(project);
                setChangelogValues.accept(projectTextChangelog);
                projectTextChangelog.setProperty(repo.getReferenceById(Property.class, stringEntry.getKey()));
                projectTextChangelog.setValue(stringEntry.getValue());
                repo.persist(projectTextChangelog);
            }
        }

        if (!projectBooleanPropsMap.isEmpty()) {
            for (Map.Entry<UUID, Boolean> booleanEntry : projectBooleanPropsMap.entrySet()) {
                var projectBooleanChangelog = new ProjectBooleanCustomPropertyChangelog();
                projectBooleanChangelog.setProject(project);
                setChangelogValues.accept(projectBooleanChangelog);
                projectBooleanChangelog.setProperty(repo.getReferenceById(Property.class, booleanEntry.getKey()));
                projectBooleanChangelog.setValue(booleanEntry.getValue());
                repo.persist(projectBooleanChangelog);
            }
        }


        if (!projectNumericPropsMap.isEmpty()) {
            for (Map.Entry<UUID, Double> numericEntry : projectNumericPropsMap.entrySet()) {
                var projectNumericChangelog = new ProjectNumericCustomPropertyChangelog();
                projectNumericChangelog.setProject(project);
                setChangelogValues.accept(projectNumericChangelog);
                projectNumericChangelog.setProperty(repo.getReferenceById(Property.class, numericEntry.getKey()));
                projectNumericChangelog.setValue(numericEntry.getValue());
                projectNumericChangelog.setValueType(ValueType.SINGLE_VALUE);
                repo.persist(projectNumericChangelog);
            }
        }

        if (!projectOrdinalPropsMap.isEmpty()) {
            for (Map.Entry<UUID, UUID> ordinalEntry : projectOrdinalPropsMap.entrySet()) {
                var projectOrdinalChangelog = new ProjectOrdinalPropertyChangelog();
                projectOrdinalChangelog.setProject(project);
                setChangelogValues.accept(projectOrdinalChangelog);
                projectOrdinalChangelog.setProperty(repo.getReferenceById(Property.class, ordinalEntry.getKey()));
                projectOrdinalChangelog.setValue(repo.getReferenceById(PropertyOrdinalValue.class, ordinalEntry.getValue()));
                projectOrdinalChangelog.setValueType(ValueType.SINGLE_VALUE);
                repo.persist(projectOrdinalChangelog);
            }
        }

        houseblocks.forEach(houseblock -> {
            Milestone houseblockEndMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, houseblock.getLatestDeliveryDate(), null, user, importTime);
            persistHouseblocks(repo, houseblock, project, startMilestone, houseblockEndMilestone, user, importTime);
        });

        return new SelectModel(project.getId(), projectName);

    }

    public void persistHouseblocks(VngRepository repo, HouseblockImportModel houseblockRowModel, Project project, Milestone startMilestone, Milestone endMilestone,
                                   User user, ZonedDateTime importTime) {

        Houseblock houseblock = new Houseblock();
        houseblock.setProject(project);
        repo.persist(houseblock);

        HouseblockState houseblockState = new HouseblockState();
        houseblockState.setHouseblock(houseblock);
        houseblockState.setCreateUser(user);
        houseblockState.setChangeStartDate(importTime);
        repo.persist(houseblockState);

        Consumer<MilestoneChangeDataSuperclass> setChangelogValues = (MilestoneChangeDataSuperclass entity) -> {
            entity.setStartMilestone(startMilestone);
            entity.setEndMilestone(endMilestone);
            entity.setCreateUser(user);
            entity.setChangeStartDate(importTime);
        };

        var durationChangelog = new HouseblockDurationChangelog();
        setChangelogValues.accept(durationChangelog);
        durationChangelog.setHouseblock(houseblock);
        repo.persist(durationChangelog);

        var deliveryDateChangelog = new HouseblockDeliveryDateChangelog();
        setChangelogValues.accept(deliveryDateChangelog);
        deliveryDateChangelog.setHouseblock(houseblock);
        deliveryDateChangelog.setLatestDeliveryDate(houseblockRowModel.latestDeliveryDate);
        deliveryDateChangelog.setEarliestDeliveryDate(houseblockRowModel.earliestDeliveryDate);
        repo.persist(deliveryDateChangelog);

        var nameChangelog = new HouseblockNameChangelog();
        setChangelogValues.accept(nameChangelog);
        nameChangelog.setName(houseblockRowModel.getName());
        nameChangelog.setHouseblock(houseblock);
        repo.persist(nameChangelog);

        var mutationChangelog = new HouseblockMutatieChangelog();
        setChangelogValues.accept(mutationChangelog);
        mutationChangelog.setHouseblock(houseblock);
        mutationChangelog.setAmount(houseblockRowModel.getMutation());
        mutationChangelog.setMutationType(houseblockRowModel.getMutationType());
        repo.persist(mutationChangelog);

        if (programming != null) {
            var programmingChangelog = new HouseblockProgrammingChangelog();
            setChangelogValues.accept(programmingChangelog);
            programmingChangelog.setHouseblock(houseblock);
            programmingChangelog.setProgramming(programming);
            repo.persist(programmingChangelog);
        }


        if (!houseblockRowModel.getGroundPositionMap().isEmpty()) {
            var groundPositionChangelog = new HouseblockGroundPositionChangelog();
            setChangelogValues.accept(groundPositionChangelog);
            groundPositionChangelog.setHouseblock(houseblock);
            repo.persist(groundPositionChangelog);

            for (GroundPosition groundPos : GroundPosition.values()) {
                if (houseblockRowModel.getGroundPositionMap().containsKey(groundPos)) {
                    var groundPosChangelogValue = new HouseblockGroundPositionChangelogValue();
                    groundPosChangelogValue.setGroundPositionChangelog(groundPositionChangelog);
                    groundPosChangelogValue.setGroundPosition(groundPos);
                    groundPosChangelogValue.setAmount(houseblockRowModel.getGroundPositionMap().get(groundPos));
                    repo.persist(groundPosChangelogValue);
                }
            }
        }

        if (houseblockRowModel.ownershipValues != null) {
            houseblockRowModel.ownershipValues.forEach(ov -> {
                var ownershipValue = new HouseblockOwnershipValueChangelog();
                setChangelogValues.accept(ownershipValue);
                ownershipValue.setHouseblock(houseblock);
                if (ov.getValue() != null) {
                    ownershipValue.setValueRange(ov.getValue().toRange());
                    ownershipValue.setValueType(ValueType.RANGE);
                }
                if (ov.getRentalValue() != null) {
                    ownershipValue.setRentalValueRange(ov.getRentalValue().toRange());
                    ownershipValue.setRentalValueType(ValueType.RANGE);
                }
                ownershipValue.setAmount(ov.getAmount());
                ownershipValue.setOwnershipType(ov.getType());
                repo.persist(ownershipValue);
            });
        }

        if (!houseblockRowModel.getPhysicalAppearanceMap().isEmpty() || !houseblockRowModel.getHouseTypeMap().isEmpty()) {
            var appearanceAndTypeChangelog = new HouseblockAppearanceAndTypeChangelog();
            setChangelogValues.accept(appearanceAndTypeChangelog);
            appearanceAndTypeChangelog.setHouseblock(houseblock);
            repo.persist(appearanceAndTypeChangelog);

            for (Map.Entry<UUID, Integer> physicalAppEntry : houseblockRowModel.getPhysicalAppearanceMap().entrySet()) {
                var physicalAppearanceValue = new HouseblockPhysicalAppearanceChangelogValue();
                physicalAppearanceValue.setAmount(physicalAppEntry.getValue());
                physicalAppearanceValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, physicalAppEntry.getKey()));
                physicalAppearanceValue.setAppearanceAndTypeChangelog(appearanceAndTypeChangelog);
                repo.persist(physicalAppearanceValue);
            }

            for (HouseType houseType : HouseType.values()) {
                if (houseblockRowModel.getHouseTypeMap().containsKey(houseType)) {
                    var houseTypeValue = new HouseblockHouseTypeChangelogValue();
                    houseTypeValue.setAmount(houseblockRowModel.getHouseTypeMap().get(houseType));
                    houseTypeValue.setHouseType(houseType);
                    houseTypeValue.setAppearanceAndTypeChangelog(appearanceAndTypeChangelog);
                    repo.persist(houseTypeValue);
                }
            }
        }

        if (!houseblockRowModel.getTargetGroupMap().isEmpty()) {
            var targetGroupChangelog = new HouseblockTargetGroupChangelog();
            setChangelogValues.accept(targetGroupChangelog);
            targetGroupChangelog.setHouseblock(houseblock);
            repo.persist(targetGroupChangelog);

            for (Map.Entry<UUID, Integer> targetGroupEntry : houseblockRowModel.getTargetGroupMap().entrySet()) {
                var targetGroupValue = new HouseblockTargetGroupChangelogValue();
                targetGroupValue.setAmount(targetGroupEntry.getValue());
                targetGroupValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, targetGroupEntry.getKey()));
                targetGroupValue.setTargetGroupChangelog(targetGroupChangelog);
                repo.persist(targetGroupValue);
            }
        }

        if (!houseblockRowModel.houseblockCategoryPropsMap.isEmpty()) {
            for (Map.Entry<UUID, UUID> categoryEntry : houseblockRowModel.houseblockCategoryPropsMap.entrySet()) {
                var houseblockPropertyChangelog = new HouseblockCategoryCustomPropertyChangelog();
                houseblockPropertyChangelog.setHouseblock(houseblock);
                setChangelogValues.accept(houseblockPropertyChangelog);
                houseblockPropertyChangelog.setProperty(repo.getReferenceById(Property.class, categoryEntry.getKey()));
                repo.persist(houseblockPropertyChangelog);

                var houseblockPropertyChangelogValue = new HouseblockCategoryCustomPropertyChangelogValue();
                houseblockPropertyChangelogValue.setCategoryChangelog(houseblockPropertyChangelog);
                houseblockPropertyChangelogValue.setCategoryValue(repo.getReferenceById(PropertyCategoryValue.class, categoryEntry.getValue()));
                repo.persist(houseblockPropertyChangelogValue);
            }
        }

        if (!houseblockRowModel.houseblockStringPropsMap.isEmpty()) {
            for (Map.Entry<UUID, String> stringEntry : houseblockRowModel.houseblockStringPropsMap.entrySet()) {
                var houseblockTextChangelog = new HouseblockTextCustomPropertyChangelog();
                houseblockTextChangelog.setHouseblock(houseblock);
                setChangelogValues.accept(houseblockTextChangelog);
                houseblockTextChangelog.setProperty(repo.getReferenceById(Property.class, stringEntry.getKey()));
                houseblockTextChangelog.setValue(stringEntry.getValue());
                repo.persist(houseblockTextChangelog);
            }
        }

        if (!houseblockRowModel.houseblockBooleanPropsMap.isEmpty()) {
            for (Map.Entry<UUID, Boolean> booleanEntry : houseblockRowModel.houseblockBooleanPropsMap.entrySet()) {
                var houseblockBooleanChangelog = new HouseblockBooleanCustomPropertyChangelog();
                houseblockBooleanChangelog.setHouseblock(houseblock);
                setChangelogValues.accept(houseblockBooleanChangelog);
                houseblockBooleanChangelog.setProperty(repo.getReferenceById(Property.class, booleanEntry.getKey()));
                houseblockBooleanChangelog.setValue(booleanEntry.getValue());
                repo.persist(houseblockBooleanChangelog);
            }
        }

        if (!houseblockRowModel.houseblockNumericPropsMap.isEmpty()) {
            for (Map.Entry<UUID, Double> numericEntry : houseblockRowModel.houseblockNumericPropsMap.entrySet()) {
                var houseblockNumericChangelog = new HouseblockNumericCustomPropertyChangelog();
                houseblockNumericChangelog.setHouseblock(houseblock);
                setChangelogValues.accept(houseblockNumericChangelog);
                houseblockNumericChangelog.setProperty(repo.getReferenceById(Property.class, numericEntry.getKey()));
                houseblockNumericChangelog.setValue(numericEntry.getValue());
                houseblockNumericChangelog.setValueType(ValueType.SINGLE_VALUE);
                repo.persist(houseblockNumericChangelog);
            }
        }

        if (!houseblockRowModel.houseblockOrdinalPropsMap.isEmpty()) {
            for (Map.Entry<UUID, UUID> ordinalEntry : houseblockRowModel.houseblockOrdinalPropsMap.entrySet()) {
                var houseblockOrdinalChangelog = new HouseblockOrdinalCustomPropertyChangelog();
                houseblockOrdinalChangelog.setHouseblock(houseblock);
                setChangelogValues.accept(houseblockOrdinalChangelog);
                houseblockOrdinalChangelog.setProperty(repo.getReferenceById(Property.class, ordinalEntry.getKey()));
                houseblockOrdinalChangelog.setValue(repo.getReferenceById(PropertyOrdinalValue.class, ordinalEntry.getValue()));
                houseblockOrdinalChangelog.setValueType(ValueType.SINGLE_VALUE);
                repo.persist(houseblockOrdinalChangelog);
            }
        }
    }

}
