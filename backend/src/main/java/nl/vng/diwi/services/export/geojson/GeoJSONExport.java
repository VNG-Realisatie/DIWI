package nl.vng.diwi.services.export.geojson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.jackson.CrsType;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus.BooleanPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus.CategoryPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus.NumericPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus.TextPropertyModel;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.ExportUtil;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.BasicProjectData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.GeoJsonHouseblock;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.GeoJsonProject;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.MutationData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.OwnershipValueData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.ProjectData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.ProjectDuration;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.ProjectLocation;

public class GeoJSONExport {

    @Data
    static public class CustomProps {

        private List<PropertyModel> customProps;
        private Map<UUID, String> optionsMap;
        private Map<UUID, PropertyModel> customPropsMap;

        public CustomProps(List<PropertyModel> customProps) {
            this.customProps = customProps;

            optionsMap = customProps.stream()
                    .flatMap(cp -> cp.getCategories() != null ? cp.getCategories().stream() : Stream.empty())
                    .collect(Collectors.toMap(option -> option.getId(), option -> option.getName()));

            customPropsMap = customProps.stream().collect(Collectors.toMap(PropertyModel::getId, Function.identity()));

        }

        public PropertyModel get(UUID id) {
            return customPropsMap.get(id);
        }

        public PropertyModel get(String propName) {
            return customProps.stream()
                    .filter(pfp -> pfp.getName().equals(propName))
                    .findFirst()
                    .orElse(null);
        }

        public String getOption(UUID id) {
            return optionsMap.get(id);
        }

        public List<String> getOptions(List<UUID> optionIds) {
            if (optionIds == null) {
                return List.of();
            } else {
                return optionIds.stream()
                        .map(optionId -> optionsMap.get(optionId))
                        .toList();
            }
        }

        public Map<String, String> getCustomProps(
                List<TextPropertyModel> projectTextCustomProps,
                List<NumericPropertyModel> projectNumericCustomProps,
                List<BooleanPropertyModel> projectBooleanCustomProps,
                List<CategoryPropertyModel> projectCategoricalCustomProps) {

            Map<String, String> customProps = new HashMap<>();
            for (var prop : projectTextCustomProps) {
                PropertyModel propertyModel = get(prop.getPropertyId());
                if (propertyModel != null) {
                    customProps.put(propertyModel.getName(), prop.getTextValue());
                }
            }
            for (var prop : projectNumericCustomProps) {
                PropertyModel propertyModel = get(prop.getPropertyId());
                if (propertyModel != null) {
                    customProps.put(propertyModel.getName(), prop.getValue().toString());
                }
            }
            for (var prop : projectBooleanCustomProps) {
                PropertyModel propertyModel = get(prop.getPropertyId());
                if (propertyModel != null) {
                    customProps.put(propertyModel.getName(), prop.getBooleanValue().toString());
                }
            }
            for (var prop : projectCategoricalCustomProps) {
                PropertyModel propertyModel = get(prop.getPropertyId());
                if (propertyModel != null) {
                    String values = prop.getOptionValues()
                            .stream()
                            .map(optionId -> getOption(optionId))
                            .collect(Collectors.joining(","));
                    customProps.put(propertyModel.getName(), values);
                }
            }
            return customProps;
        }

    }

    static public FeatureCollection buildExportObject(
            ConfigModel configModel,
            List<ProjectExportSqlModelPlus> projects,
            List<PropertyModel> customProps,
            Map<String, DataExchangePropertyModel> dxPropertiesMap,
            LocalDate exportDate,
            Confidentiality minConfidentiality,
            List<DataExchangeExportError> errors) {

        FeatureCollection exportObject = new FeatureCollection();
        Crs crs = new Crs();
        crs.setType(CrsType.name);
        String targetCrs = "EPSG:28992";
        crs.getProperties().put("name", targetCrs);
        exportObject.setCrs(crs);

        var customPropsTool = new CustomProps(customProps);
        PropertyModel priceRangeBuyFixedProp = customPropsTool.get(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY);
        PropertyModel priceRangeRentFixedProp = customPropsTool.get(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT);
        PropertyModel municipalityFixedProp = customPropsTool.get(Constants.FIXED_PROPERTY_MUNICIPALITY);

        projects.forEach(project -> exportObject.add(getProjectFeature(
                configModel,
                project,
                priceRangeBuyFixedProp,
                priceRangeRentFixedProp,
                municipalityFixedProp,
                dxPropertiesMap,
                minConfidentiality,
                exportDate,
                errors,
                targetCrs,
                customPropsTool)));

        return exportObject;
    }

    static private Feature getProjectFeature(
            ConfigModel configModel,
            ProjectExportSqlModelPlus project,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            PropertyModel municipalityFixedProp,
            Map<String, DataExchangePropertyModel> dxPropertiesMap,
            Confidentiality minConfidentiality, LocalDate exportDate,
            List<DataExchangeExportError> errors,
            String targetCrs,
            CustomProps customPropTool) {
        var projectFeature = new Feature();

        var multiPolygon = ExportUtil.createPolygonForProject(project.getGeometries(), targetCrs, project.getProjectId());
        if (!multiPolygon.getCoordinates().isEmpty()) {
            projectFeature.setGeometry(multiPolygon);
        }

        Map<ProjectPhase, LocalDate> phases = new HashMap<>();
        phases.put(ProjectPhase._6_REALIZATION, project.getRealizationPhaseDate());

        Map<String, String> customProps = customPropTool.getCustomProps(
                project.getTextProperties(),
                project.getNumericProperties(),
                project.getBooleanProperties(),
                project.getCategoryProperties());

        Map<UUID, List<UUID>> projectCategoricalCustomProps = project.getCategoryProperties().stream()
                .collect(Collectors.toMap(ProjectExportSqlModelPlus.CategoryPropertyModel::getPropertyId,
                        ProjectExportSqlModelPlus.CategoryPropertyModel::getOptionValues));

        List<String> gemeente = customPropTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropTool.get(Constants.FIXED_PROPERTY_MUNICIPALITY).getId()));

        List<String> buurt = customPropTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropTool.get(Constants.FIXED_PROPERTY_NEIGHBOURHOOD).getId()));

        List<String> wijk = customPropTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropTool.get(Constants.FIXED_PROPERTY_DISTRICT).getId()));

        LocalDate today = LocalDate.now();

        final var geoJsonProject = GeoJsonProject.builder()
                .diwiId(project.getProjectId())
                .basicProjectData(BasicProjectData.builder()
                        .identificationNo(null) // Seems to be only used for error messages in the import
                        .name(project.getName())
                        .build())
                .projectData(ProjectData.builder()
                        .planType(project.getPlanType().isEmpty() ? null : project.getPlanType().get(0))
                        // .in_programmering() // Is a block property, move to block
                        // .priority() // This is a custom property in the importer
                        // .municipalityRole() // This is a custom property
                        .status(project.getEndDate().isBefore(today) ? ProjectStatus.REALIZED : ProjectStatus.ACTIVE) // Need to guess based on
                                                                                                                      // future/pastness.
                                                                                                                      // Do in SQL
                        // .owner()// Needs adding to the model
                        .confidentialityLevel(project.getConfidentiality())
                        .build())
                .projectDuration(ProjectDuration.builder()
                        .startDate(project.getStartDate())
                        .endDate(project.getEndDate())
                        .build())
                .projectPhasesMap(phases)
                .projectLocation(ProjectLocation.builder()
                        .municipality(gemeente)
                        .district(wijk)
                        .neighbourhood(buurt)
                        .build())
                .customPropertiesMap(customProps)
                .build();

        final var geoJsonBlocks = project.getHouseblocks().stream()
                .map(block -> {

                    Map<String, String> blockCustomProps = customPropTool.getCustomProps(
                            block.getTextProperties(),
                            block.getNumericProperties(),
                            block.getBooleanProperties(),
                            block.getCategoryProperties());

                    var mutationData = MutationData.builder()
                            .amount(block.getMutationAmount())
                            .mutationType(block.getMutationKind())
                            .build();

                    var ownerShipValue = block.getOwnershipValueList().stream()
                            .map(ov -> {
                                // TODO global categories
                                Double max = null;
                                if (ov.getOwnershipType() == OwnershipType.KOOPWONING && ov.getOwnershipValueRangeMax() != null) {
                                    max = (double) ov.getOwnershipValueRangeMax() / 100;

                                } else if (ov.getOwnershipRentalValueRangeMax() != null) {
                                    max = (double) ov.getOwnershipRentalValueRangeMax() / 100;
                                }

                                Double min = null;
                                if (ov.getOwnershipType() == OwnershipType.KOOPWONING && ov.getOwnershipValueRangeMax() != null) {
                                    min = (double) ov.getOwnershipValueRangeMin() / 100;
                                } else if (ov.getOwnershipRentalValueRangeMin() != null) {
                                    min = (double) ov.getOwnershipRentalValueRangeMin() / 100;
                                }

                                Double value = null;
                                if (ov.getOwnershipType() == OwnershipType.KOOPWONING && ov.getOwnershipValue() != null) {
                                    value = (double) ov.getOwnershipValue() / 100;
                                } else if (ov.getOwnershipRentalValue() != null) {
                                    value = (double) ov.getOwnershipRentalValue() / 100;
                                }

                                return OwnershipValueData.builder()
                                        .ownershipType(ov.getOwnershipType().toString())
                                        .max(max)
                                        .min(min)
                                        .value(value)
                                        .build();
                            })
                            .toList();

                    Map<GroundPosition, Integer> groundPositions = new HashMap<>();
                    groundPositions.put(GroundPosition.GEEN_TOESTEMMING_GRONDEIGENAAR, block.getNoPermissionOwner());
                    groundPositions.put(GroundPosition.INTENTIE_MEDEWERKING_GRONDEIGENAAR, block.getIntentionPermissionOwner());
                    groundPositions.put(GroundPosition.FORMELE_TOESTEMMING_GRONDEIGENAAR, block.getFormalPermissionOwner());
                    return GeoJsonHouseblock.builder()
                            .diwiId(block.getHouseblockId())
                            .name(block.getName())
                            .endDate(block.getEndDate())
                            .mutationData(mutationData)
                            .groundPositionsMap(groundPositions)
                            .ownershipValue(ownerShipValue)
                            .customPropertiesMap(blockCustomProps)
                            .programming(block.getProgramming())
                            .build();
                })
                .toList();

        projectFeature.setProperty("projectgegevens", geoJsonProject);
        projectFeature.setProperty("woning_blokken", geoJsonBlocks);

        return projectFeature;
    }

}
