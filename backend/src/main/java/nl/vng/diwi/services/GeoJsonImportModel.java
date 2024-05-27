package nl.vng.diwi.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.ImportError;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class GeoJsonImportModel {

    @JsonProperty("projectgegevens")
    private GeoJsonProject project;

    @JsonProperty("woning_blokken")
    private List<GeoJsonHouseblock> houseblocks;

    private static Boolean getBooleanValue(String booleanValueStr, Integer errorRow, List<ImportError> errors) {
        if (booleanValueStr.equalsIgnoreCase("true") || booleanValueStr.equals("1")) {
            return Boolean.TRUE;
        } else if (booleanValueStr.equalsIgnoreCase("false") || booleanValueStr.equals("0")) {
            return Boolean.FALSE;
        } else {
            errors.add(new ImportError(errorRow, booleanValueStr, ImportError.ERROR.WRONG_TYPE_NOT_BOOLEAN));
            return null;
        }
    }

    private static Double getNumericValue(String numericValueStr, Integer errorRow, List<ImportError> errors) {
        try {
            return Double.parseDouble(numericValueStr);
        } catch (NumberFormatException ex) {
            errors.add(new ImportError(errorRow, numericValueStr, ImportError.ERROR.WRONG_TYPE_NOT_NUMERIC));
            return null;
        }
    }

    private static UUID getCategoryValue(String categoryValueStr, PropertyModel propertyModel, Integer errorRow, List<ImportError> errors) {
        SelectModel categoryValue = propertyModel.getActiveCategoryValue(categoryValueStr);
        if (categoryValue == null) {
            errors.add(new ImportError(errorRow, categoryValueStr, ImportError.ERROR.UNKNOWN_PROPERTY_VALUE));
            return null;
        }
        return categoryValue.getId();
    }

    private static UUID getOrdinalValue(String ordinalValueStr, PropertyModel propertyModel, Integer errorRow, List<ImportError> errors) {
        SelectModel ordinalValue = propertyModel.getActiveOrdinalValue(ordinalValueStr);
        if (ordinalValue == null) {
            errors.add(new ImportError(errorRow, ordinalValueStr, ImportError.ERROR.UNKNOWN_PROPERTY_VALUE));
            return null;
        }
        return ordinalValue.getId();
    }

    private static boolean addFixedCategoryValue(String stringValue, Map<UUID, UUID> categoryPropertiesMap, String fixedPropertyName, Map<String, PropertyModel> propertyModelMap,
                                                 Integer errorRow, List<ImportError> errors) {
        PropertyModel propertyModel = propertyModelMap.get(fixedPropertyName);
        if (propertyModel == null || propertyModel.getType() != PropertyKind.FIXED || propertyModel.getPropertyType() != PropertyType.CATEGORY) {
            errors.add(new ImportError(errorRow, fixedPropertyName, ImportError.ERROR.MISSING_FIXED_PROPERTY));
        } else if (stringValue != null && !stringValue.isBlank()) {
            SelectModel categoryValue = propertyModel.getActiveCategoryValue(stringValue);
            if (categoryValue == null) {
                errors.add(new ImportError(errorRow, stringValue, ImportError.ERROR.UNKNOWN_PROPERTY_VALUE));
                return false;
            } else {
                categoryPropertiesMap.put(propertyModel.getId(), categoryValue.getId());
                return true;
            }
        }
        return false;
    }

    public ProjectImportModel toProjectImportModel(Map<String, PropertyModel> activePropertiesMap, List<ImportError> importErrors) {

        if (project != null) {

            BasicProjectData basicProjectData = project.getBasicProjectData();
            if (basicProjectData == null || basicProjectData.identificationNo == null) {
                importErrors.add(new ImportError(null, ImportError.ERROR.MISSING_PROJECT_ID));
                return null;
            }
            Integer id = basicProjectData.identificationNo;

            if (basicProjectData.name == null || basicProjectData.name.isBlank()) {
                importErrors.add(new ImportError(id, ImportError.ERROR.MISSING_PROJECT_NAME));
            }

            ProjectDuration projectDuration = project.getProjectDuration();
            if (projectDuration == null) {
                importErrors.add(new ImportError(id, ImportError.ERROR.MISSING_PROJECT_START_DATE));
                importErrors.add(new ImportError(id, ImportError.ERROR.MISSING_PROJECT_END_DATE));
            } else if (projectDuration.getStartDate() == null) {
                importErrors.add(new ImportError(id, ImportError.ERROR.MISSING_PROJECT_START_DATE));
            } else if (projectDuration.getEndDate() == null) {
                importErrors.add(new ImportError(id, ImportError.ERROR.MISSING_PROJECT_END_DATE));
            }

            ProjectData projectData = project.getProjectData();
            if (projectData == null || projectData.getStatus() == null) {
                importErrors.add(new ImportError(id, ImportError.ERROR.MISSING_PROJECT_STATUS));
            } else if (projectData.getConfidentialityLevel() == null) {
                importErrors.add(new ImportError(id, ImportError.ERROR.MISSING_PROJECT_CONFIDENTIALITY));
            }

            if (!importErrors.isEmpty()) {
                return null;
            }

            ProjectImportModel projectImportModel = new ProjectImportModel();
            projectImportModel.setId(basicProjectData.identificationNo);
            projectImportModel.setProjectName(basicProjectData.name);
            projectImportModel.setProjectStartDate(projectDuration.startDate);
            projectImportModel.setProjectEndDate(projectDuration.endDate);
            projectImportModel.setProjectStatus(projectData.status);
            projectImportModel.setProgramming(projectData.programming);
            projectImportModel.setConfidentialityLevel(projectData.confidentialityLevel);
            projectImportModel.setOwnerEmail(projectData.owner);
            addFixedCategoryValue(projectData.municipalityRole, projectImportModel.getProjectCategoryPropsMap(), Constants.FIXED_PROPERTY_MUNICIPALITY_ROLE,
                activePropertiesMap, id, importErrors);
            addFixedOrdinalValue(projectData.priority, projectImportModel.getProjectOrdinalPropsMap(), Constants.FIXED_PROPERTY_PRIORITY,
                activePropertiesMap, id, importErrors);

            projectImportModel.setPlanType(projectData.planType);
            projectImportModel.setProjectPhasesMap(project.getProjectPhasesMap().entrySet().stream().filter(e -> e.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            projectImportModel.setProjectPlanStatusesMap(project.getProjectPlanStatusesMap().entrySet().stream().filter(e -> e.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            for (Map.Entry<String, String> rolesEntry : project.roles.entrySet()) {
                String roleValue = rolesEntry.getValue();
                if (roleValue != null && !roleValue.isBlank()) {
                    PropertyModel propertyModel = activePropertiesMap.get(rolesEntry.getKey());
                    if (propertyModel == null || propertyModel.getObjectType() != ObjectType.PROJECT || propertyModel.getType() != PropertyKind.CUSTOM ||
                        propertyModel.getPropertyType() != PropertyType.CATEGORY) {
                        importErrors.add(new ImportError(id, roleValue, ImportError.ERROR.UNKNOWN_PROJECT_CATEGORY_PROPERTY));
                    } else {
                        projectImportModel.getProjectCategoryPropsMap().put(propertyModel.getId(), getCategoryValue(roleValue, propertyModel, id, importErrors));
                    }
                }
            }

            ProjectLocation projectLocation = project.projectLocation;
            if (projectLocation != null) {
                projectImportModel.setHasMunicipality(addFixedCategoryValue(projectLocation.municipality, projectImportModel.getProjectCategoryPropsMap(),
                    Constants.FIXED_PROPERTY_MUNICIPALITY, activePropertiesMap, id, importErrors));
                projectImportModel.setHasDistrict(addFixedCategoryValue(projectLocation.district, projectImportModel.getProjectCategoryPropsMap(),
                    Constants.FIXED_PROPERTY_DISTRICT, activePropertiesMap, id, importErrors));
                projectImportModel.setHasNeighbourhood(addFixedCategoryValue(projectLocation.neighbourhood, projectImportModel.getProjectCategoryPropsMap(),
                    Constants.FIXED_PROPERTY_NEIGHBOURHOOD, activePropertiesMap, id, importErrors));
            }

            for (Map.Entry<String, String> customPropEntry : project.customPropertiesMap.entrySet()) {
                String customPropValue = customPropEntry.getValue();
                if (customPropValue != null && !customPropValue.isBlank()) {
                    PropertyModel propertyModel = activePropertiesMap.get(customPropEntry.getKey());
                    if (propertyModel == null || propertyModel.getObjectType() != ObjectType.PROJECT || propertyModel.getType() != PropertyKind.CUSTOM) {
                        importErrors.add(new ImportError(id, customPropEntry.getKey(), ImportError.ERROR.UNKNOWN_PROJECT_PROPERTY));
                    } else {
                        switch (propertyModel.getPropertyType()) {
                            case TEXT -> projectImportModel.getProjectStringPropsMap().put(propertyModel.getId(), customPropEntry.getValue());
                            case BOOLEAN ->
                                projectImportModel.getProjectBooleanPropsMap().put(propertyModel.getId(), getBooleanValue(customPropValue, id, importErrors));
                            case NUMERIC ->
                                projectImportModel.getProjectNumericPropsMap().put(propertyModel.getId(), getNumericValue(customPropValue, id, importErrors));
                            case CATEGORY ->
                                projectImportModel.getProjectCategoryPropsMap().put(propertyModel.getId(), getCategoryValue(customPropValue, propertyModel, id, importErrors));
                            case ORDINAL ->
                                projectImportModel.getProjectOrdinalPropsMap().put(propertyModel.getId(), getOrdinalValue(customPropValue, propertyModel, id, importErrors));
                        }
                    }
                }
            }

            if (houseblocks != null) {
                houseblocks.forEach(h -> {
                    var houseblockImportModel = h.toHouseblockImportModel(id, activePropertiesMap, importErrors);
                    if (houseblockImportModel != null) {
                        projectImportModel.getHouseblocks().add(houseblockImportModel);
                    }
                });
            }

            if (importErrors.isEmpty()) {
                return projectImportModel;
            }
        }

        return null;
    }

    private void addFixedOrdinalValue(String stringValue, Map<UUID, UUID> ordinalPropertiesMap, String fixedPropertyName, Map<String, PropertyModel> propertyModelMap,
                                      Integer errorRow, List<ImportError> errors) {
        PropertyModel propertyModel = propertyModelMap.get(fixedPropertyName);
        if (propertyModel == null || propertyModel.getType() != PropertyKind.FIXED || propertyModel.getPropertyType() != PropertyType.ORDINAL) {
            errors.add(new ImportError(errorRow, fixedPropertyName, ImportError.ERROR.MISSING_FIXED_PROPERTY));
        } else if (stringValue != null && !stringValue.isBlank()) {
            SelectModel ordinalValue = propertyModel.getActiveOrdinalValue(stringValue);
            if (ordinalValue == null) {
                errors.add(new ImportError(errorRow, stringValue, ImportError.ERROR.UNKNOWN_PROPERTY_VALUE));
            } else {
                ordinalPropertiesMap.put(propertyModel.getId(), ordinalValue.getId());
            }
        }
    }

    @Data
    public static class GeoJsonProject {
        @JsonProperty("basisgegevens")
        private BasicProjectData basicProjectData;
        @JsonProperty("projectgegevens")
        private ProjectData projectData;
        @JsonProperty("projectduur")
        private ProjectDuration projectDuration;
        @JsonProperty("locatie")
        private ProjectLocation projectLocation;
        @JsonProperty("rollen")
        private Map<String, String> roles = new HashMap<>();
        @JsonProperty("projectfasen")
        private Map<ProjectPhase, LocalDate> projectPhasesMap = new HashMap<>();
        @JsonProperty("planologische_planstatus")
        private Map<PlanStatus, LocalDate> projectPlanStatusesMap = new HashMap<>();
        @JsonProperty("maatwerk_projecteigenschappen")
        private Map<String, String> customPropertiesMap = new HashMap<>();
    }

    @Data
    public static class BasicProjectData {
        @JsonProperty("identificatie_nr")
        private Integer identificationNo;
        @JsonProperty("naam")
        private String name;
    }

    @Data
    public static class ProjectData {
        @JsonProperty("plan_soort")
        private PlanType planType;
        @JsonProperty("in_programmering")
        private Boolean programming;
        @JsonProperty("prioritering")
        private String priority;
        @JsonProperty("rol_gemeente")
        private String municipalityRole;
        @JsonProperty("status")
        private ProjectStatus status;
        @JsonProperty("eigenaar")
        private String owner;
        @JsonProperty("vertrouwelijkheid")
        private Confidentiality confidentialityLevel;
    }

    @Data
    public static class ProjectDuration {
        @JsonProperty("start_project")
        private LocalDate startDate;
        @JsonProperty("eind_project")
        private LocalDate endDate;
    }

    @Data
    public static class ProjectLocation {
        @JsonProperty("gemeente")
        private String municipality;
        @JsonProperty("wijk")
        private String district;
        @JsonProperty("buurt")
        private String neighbourhood;
    }

    @Data
    public static class GeoJsonHouseblock {

        @JsonProperty("name")
        private String name;
        @JsonProperty("mutatiegegevens")
        private MutationData mutationData;
        @JsonProperty("einddatum")
        private LocalDate endDate;
        @JsonProperty("grootte")
        private List<Object> size; //unused
        @JsonProperty("waarde")
        private OwnershipValueData ownershipValue;
        @JsonProperty("fysiek_voorkomen")
        private List<PhysicalAppearanceData> physicalAppearanceList = new ArrayList<>();
        @JsonProperty("doelgroep")
        private List<TargetGroupData> targetGroupList = new ArrayList<>();
        @JsonProperty("grondpositie")
        private Map<GroundPosition, Integer> groundPositionsMap = new HashMap<>();
        @JsonProperty("maatwerk_woningeigenschappen")
        private Map<String, String> customPropertiesMap = new HashMap<>();

        public ProjectImportModel.HouseblockImportModel toHouseblockImportModel(Integer projectNo, Map<String, PropertyModel> activePropertiesMap, List<ImportError> errors) {

            if (mutationData == null || mutationData.mutationType == null || mutationData.amount == null) {
                errors.add(new ImportError(projectNo, ImportError.ERROR.MISSING_HOUSEBLOCK_MUTATION));
                return null;
            }
            if (name == null) {
                errors.add(new ImportError(projectNo, ImportError.ERROR.MISSING_HOUSEBLOCK_NAME));
                return null;
            }

            ProjectImportModel.HouseblockImportModel houseblockImportModel = new ProjectImportModel.HouseblockImportModel(mutationData.mutationType, mutationData.amount, name);

            if (mutationData.houseType != null) {
                houseblockImportModel.getHouseTypeMap().put(mutationData.houseType, mutationData.amount);
            }

            if (endDate != null) {
                houseblockImportModel.setLatestDeliveryDate(endDate);
            }

            if (mutationData.ownershipType != null) {
                houseblockImportModel.getOwnershipTypeMap().put(mutationData.ownershipType, mutationData.amount);
                HouseblockSnapshotModel.OwnershipValue ov = new HouseblockSnapshotModel.OwnershipValue();
                houseblockImportModel.getOwnershipValues().add(ov);
                ov.setType(mutationData.ownershipType);
                ov.setAmount(mutationData.getAmount());
                if (ownershipValue != null) {
                    SingleValueOrRangeModel<Integer> valueRange = new SingleValueOrRangeModel<>();
                    if (ownershipValue.min != null) {
                        valueRange.setMin((int) (ownershipValue.min * 100));
                    }
                    if (ownershipValue.max != null) {
                        valueRange.setMax((int) (ownershipValue.max * 100));
                    }
                    if (!valueRange.isValid(true)) {
                        errors.add(new ImportError(projectNo, ownershipValue.toString(), ImportError.ERROR.INVALID_RANGE));
                    } else {
                        if (mutationData.ownershipType == OwnershipType.KOOPWONING) {
                            ov.setValue(valueRange);
                        } else {
                            ov.setRentalValue(valueRange);
                        }
                    }
                }
            }

            physicalAppearanceList.forEach(pa -> {
                if (pa.getAmount() != null) {
                    SelectModel categoryValue = activePropertiesMap.get(Constants.FIXED_PROPERTY_PHYSICAL_APPEARANCE).getActiveCategoryValue(pa.categoryName);
                    if (categoryValue == null) {
                        errors.add(new ImportError(projectNo, pa.categoryName, ImportError.ERROR.UNKNOWN_PROPERTY_VALUE));
                    } else {
                        houseblockImportModel.getPhysicalAppearanceMap().put(categoryValue.getId(), pa.amount);
                    }
                }
            });

            targetGroupList.forEach(tg -> {
                if (tg.getAmount() != null) {
                    SelectModel categoryValue = activePropertiesMap.get(Constants.FIXED_PROPERTY_TARGET_GROUP).getActiveCategoryValue(tg.categoryName);
                    if (categoryValue == null) {
                        errors.add(new ImportError(projectNo, tg.categoryName, ImportError.ERROR.UNKNOWN_PROPERTY_VALUE));
                    } else {
                        houseblockImportModel.getTargetGroupMap().put(categoryValue.getId(), tg.amount);
                    }
                }
            });

            houseblockImportModel.setGroundPositionMap(groundPositionsMap.entrySet().stream().filter(e -> e.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            for (Map.Entry<String, String> customPropEntry : customPropertiesMap.entrySet()) {
                String customPropValue = customPropEntry.getValue();
                if (customPropValue != null && !customPropValue.isBlank()) {
                    PropertyModel propertyModel = activePropertiesMap.get(customPropEntry.getKey());
                    if (propertyModel == null || propertyModel.getObjectType() != ObjectType.WONINGBLOK || propertyModel.getType() != PropertyKind.CUSTOM) {
                        errors.add(new ImportError(projectNo, customPropEntry.getKey(), ImportError.ERROR.UNKNOWN_PROJECT_PROPERTY));
                    } else {
                        switch (propertyModel.getPropertyType()) {
                            case TEXT -> houseblockImportModel.getHouseblockStringPropsMap().put(propertyModel.getId(), customPropEntry.getValue());
                            case BOOLEAN ->
                                houseblockImportModel.getHouseblockBooleanPropsMap().put(propertyModel.getId(), getBooleanValue(customPropValue, projectNo, errors));
                            case NUMERIC ->
                                houseblockImportModel.getHouseblockNumericPropsMap().put(propertyModel.getId(), getNumericValue(customPropValue, projectNo, errors));
                            case CATEGORY ->
                                houseblockImportModel.getHouseblockCategoryPropsMap().put(propertyModel.getId(), getCategoryValue(customPropValue, propertyModel, projectNo, errors));
                            case ORDINAL ->
                                houseblockImportModel.getHouseblockOrdinalPropsMap().put(propertyModel.getId(), getOrdinalValue(customPropValue, propertyModel, projectNo, errors));
                        }
                    }
                }
            }

            if (!errors.isEmpty()) {
                return null;
            }
            return houseblockImportModel;
        }
    }

    @Data
    public static class MutationData {
        @JsonProperty("mutatie_type")
        private MutationType mutationType;
        @JsonProperty("eigendom_type")
        private OwnershipType ownershipType;
        @JsonProperty("woning_type")
        private HouseType houseType;
        @JsonProperty("status")
        private ProjectStatus houseblockStatus; //unused
        @JsonProperty("contract_type")
        private String contractType; //unused
        @JsonProperty("aantal")
        private Integer amount;
    }

    @Data
    public static class TargetGroupData {
        @JsonProperty("doelgroep_categorie")
        private String categoryName;
        @JsonProperty("aantal")
        private Integer amount;
    }

    @Data
    public static class PhysicalAppearanceData {
        @JsonProperty("fysiek_voorkomen")
        private String categoryName;
        @JsonProperty("aantal")
        private Integer amount;
    }

    @Data
    public static class OwnershipValueData {
        @JsonProperty("laag")
        private Double min;
        @JsonProperty("hoog")
        private Double max;
    }

}
