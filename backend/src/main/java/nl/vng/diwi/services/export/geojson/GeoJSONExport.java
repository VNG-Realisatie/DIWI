package nl.vng.diwi.services.export.geojson;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.generic.Json;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.jackson.CrsType;

import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.export.CustomPropsTool;
import nl.vng.diwi.services.export.ExportUtil;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.BasicProjectData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.BlockTypeData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.GeoJsonHouseblock;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.GeoJsonProject;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.MutationData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.OwnershipValueData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.ProjectData;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.ProjectDuration;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.ProjectLocation;
import nl.vng.diwi.services.export.geojson.GeoJsonExportModel.SizeData;

public class GeoJSONExport {
    static public StreamingOutput buildExportObject(
            List<ProjectExportSqlModelExtended> projects,
            List<PropertyModel> customProps) {

        FeatureCollection exportObject = new FeatureCollection();
        Crs crs = new Crs();
        crs.setType(CrsType.name);
        String targetCrs = "EPSG:28992";
        crs.getProperties().put("name", targetCrs);
        exportObject.setCrs(crs);

        var customPropsTool = new CustomPropsTool(customProps);

        projects.forEach(project -> exportObject.add(getProjectFeature(
                project,
                targetCrs,
                customPropsTool)));

        return output -> {
            Json.mapper.writeValue(output, exportObject);
            output.flush();
        };
    }

    static private Feature getProjectFeature(
            ProjectExportSqlModelExtended project,
            String targetCrs,
            CustomPropsTool customPropsTool) {
        var projectFeature = new Feature();

        Map<String, String> customProps = customPropsTool.getCustomPropertyMap(
                project.getTextProperties(),
                project.getNumericProperties(),
                project.getBooleanProperties(),
                project.getCategoryProperties(),
                project.getOrdinalProperties());

        List<String> geometries = new ArrayList<>();
        if (project.getGeometries() != null) {
            geometries.addAll(project.getGeometries());
        }
        var importGeometry = customProps.get(Constants.FIXED_PROPERTY_GEOMETRY);
        if (importGeometry != null) {
            geometries.add(importGeometry);
        }

        var multiPolygon = ExportUtil.createPolygonForProject(geometries, targetCrs, project.getProjectId());
        if (!multiPolygon.getCoordinates().isEmpty()) {
            projectFeature.setGeometry(multiPolygon);
        }

        Map<String, LocalDate> phases = project.getProjectPhaseStartDateList()
                .stream()
                .collect(Collectors.toMap(ph -> translate(ph.getProjectPhase()), ph -> ph.getStartDate()));

        Map<String, LocalDate> planstatuses = project.getProjectPlanStatusStartDateList()
                .stream()
                .collect(Collectors.toMap(ps -> translate(ps.getPlanStatus()), ps -> ps.getStartDate()));

        Map<UUID, List<UUID>> projectCategoricalCustomProps = project.getCategoryProperties().stream()
                .collect(Collectors.toMap(ProjectExportSqlModelExtended.CategoryPropertyModel::getPropertyId,
                        ProjectExportSqlModelExtended.CategoryPropertyModel::getOptionValues));

        List<String> municipalities = customPropsTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropsTool.get(Constants.FIXED_PROPERTY_MUNICIPALITY).getId()));

        List<String> neighbourhoods = customPropsTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropsTool.get(Constants.FIXED_PROPERTY_NEIGHBOURHOOD).getId()));

        List<String> districts = customPropsTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropsTool.get(Constants.FIXED_PROPERTY_DISTRICT).getId()));

        LocalDate today = LocalDate.now();

        List<String> municipalityRole = customPropsTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropsTool.get(Constants.FIXED_PROPERTY_MUNICIPALITY_ROLE).getId()));

        List<String> priority = customPropsTool.getOptions(
                projectCategoricalCustomProps.get(
                        customPropsTool.get(Constants.FIXED_PROPERTY_PRIORITY).getId()));

        final var geoJsonProject = GeoJsonProject.builder()
                .diwiId(project.getProjectId())
                .basicProjectData(BasicProjectData.builder()
                        .identificationNo(null) // Seems to be only used for error messages in the import
                        .name(project.getName())
                        .build())
                .projectData(ProjectData.builder()
                        .planType(project.getPlanType().isEmpty() ? null : project.getPlanType().get(0))
                        .priority(priority) // This is a custom property in the importer
                        .municipalityRole(municipalityRole) // This is a custom property
                        .status(project.getEndDate().isBefore(today) ? ProjectStatus.REALIZED : ProjectStatus.ACTIVE) // Need to guess based on
                                                                                                                      // future/pastness.
                                                                                                                      // Do in SQL
                        // .owner()// Needs adding to the model
                        .confidentialityLevel(translate(project.getConfidentiality()))
                        .build())
                .projectDuration(ProjectDuration.builder()
                        .startDate(project.getStartDate())
                        .endDate(project.getEndDate())
                        .build())
                .projectPhasesMap(phases)
                .projectPlanStatusesMap(planstatuses)
                .projectLocation(ProjectLocation.builder()
                        .municipality(municipalities)
                        .district(districts)
                        .neighbourhood(neighbourhoods)
                        .build())
                .customPropertiesMap(customProps)
                .build();

        final var geoJsonBlocks = project.getHouseblocks().stream()
                .map(block -> {

                    Map<String, String> blockCustomProps = customPropsTool.getCustomPropertyMap(
                            block.getTextProperties(),
                            block.getNumericProperties(),
                            block.getBooleanProperties(),
                            block.getCategoryProperties(),
                            block.getOrdinalProperties());

                    var mutationData = MutationData.builder()
                            .amount(block.getMutationAmount())
                            .mutationType(translate(block.getMutationKind()))
                            .build();

                    var ownerShipValue = block.getOwnershipValueList().stream()
                            .map(ov -> {
                                var builder = OwnershipValueData.builder()
                                        .ownershipType(ov.getOwnershipType().toString())
                                        .amount(ov.getOwnershipAmount());

                                // First check if it is a global range
                                var buyRangeId = ov.getOwnershipRangeCategoryId();
                                var rentRangeId = ov.getOwnershipRentalRangeCategoryId();
                                var rangeId = buyRangeId != null ? buyRangeId : rentRangeId;
                                var range = rangeId != null ? customPropsTool.getRange(rangeId) : null;
                                if (range != null) {
                                    return builder
                                            .min(range.getMin() != null ? range.getMin().doubleValue() / 100 : null)
                                            .max(range.getMax() != null ? range.getMax().doubleValue() / 100 : null)
                                            .categorie(range.getName())
                                            .build();
                                } else {
                                    // Otherwise use block specific values
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

                                    return builder
                                            .min(min)
                                            .max(max)
                                            .value(value)
                                            .build();
                                }
                            })
                            .toList();

                    var size = block.getSize();

                    Map<GroundPosition, Integer> groundPositions = new LinkedHashMap<>();
                    groundPositions.put(GroundPosition.GEEN_TOESTEMMING_GRONDEIGENAAR, block.getNoPermissionOwner());
                    groundPositions.put(GroundPosition.INTENTIE_MEDEWERKING_GRONDEIGENAAR, block.getIntentionPermissionOwner());
                    groundPositions.put(GroundPosition.FORMELE_TOESTEMMING_GRONDEIGENAAR, block.getFormalPermissionOwner());

                    Integer unknownBlockType = block.getHouseTypeUnknownAmount();

                    return GeoJsonHouseblock.builder()
                            .diwiId(block.getHouseblockId())
                            .name(block.getName())
                            .size(size != null ? SizeData.builder()
                                    .min(toDouble(size.getMin()))
                                    .max(toDouble(size.getMax()))
                                    .value(toDouble(size.getValue()))
                                    .build() : null)
                            .type(BlockTypeData.builder()
                                    .singleFamily(block.getEengezinswoning())
                                    .multipleFamily(block.getMeergezinswoning())
                                    .unknown(unknownBlockType)
                                    .build())
                            .endDate(block.getEndDate())
                            .mutationData(mutationData)
                            .groundPositionsMap(groundPositions)
                            .ownershipValue(ownerShipValue)
                            .customPropertiesMap(blockCustomProps)
                            .programming(block.getProgramming())
                            .build();
                }).toList();

        projectFeature.setProperty("projectgegevens", geoJsonProject);
        projectFeature.setProperty("woning_blokken", geoJsonBlocks);

        return projectFeature;

    }

    private static Double toDouble(BigDecimal min) {
        return min != null ? min.doubleValue() : null;
    }

    public static String translate(Confidentiality confidentiality) {
        return switch (confidentiality) {
        case PRIVATE -> "Prive";
        case INTERNAL_CIVIL -> "Intern ambtelijk";
        case INTERNAL_MANAGEMENT -> "Intern bestuurlijk";
        case INTERNAL_COUNCIL -> "Intern raad";
        case EXTERNAL_REGIONAL -> "Extern woonregio";
        case EXTERNAL_GOVERNMENTAL -> "Extern mede-overheden";
        case PUBLIC -> "Openbaar";
        };
    }

    public static String translate(ProjectPhase in) {
        if (in == null) {
            return null;
        }
        return switch (in) {
        case _1_CONCEPT -> "Concept";
        case _2_INITIATIVE -> "Initiatief";
        case _3_DEFINITION -> "Definitie";
        case _4_DESIGN -> "Ontwerp";
        case _5_PREPARATION -> "Voorbereiding";
        case _6_REALIZATION -> "Realisatie";
        case _7_AFTERCARE -> "Nazorg";
        };
    }

    public static String translate(PlanStatus in) {
        if (in == null) {
            return null;
        }
        return switch (in) {
        case _1A_ONHERROEPELIJK -> "1A Onherroepelijk";
        case _1B_ONHERROEPELIJK_MET_UITWERKING_NODIG -> "1B Onherroepelijk met uitwerking nodig";
        case _1C_ONHERROEPELIJK_MET_BW_NODIG -> "1C Onherroepelijk met bw nodig";
        case _2A_VASTGESTELD -> "2A Vastgesteld";
        case _2B_VASTGESTELD_MET_UITWERKING_NODIG -> "2B Vastgesteld met uitwerking nodig";
        case _2C_VASTGESTELD_MET_BW_NODIG -> "2C Vastgesteld met bw nodig";
        case _3_IN_VOORBEREIDING -> "3 In voorbereiding";
        case _4A_OPGENOMEN_IN_VISIE -> "4A Opgenomen in visie";
        case _4B_NIET_OPGENOMEN_IN_VISIE -> "4B Niet opgenomen in visie";
        };
    }

    public static String translate(MutationType in) {
        if (in == null) {
            return null;
        }
        return switch (in) {
        case DEMOLITION -> "Sloop";
        case CONSTRUCTION -> "Bouw";
        };
    }
}
