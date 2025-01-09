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

import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
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

        PropertyModel priceRangeBuyFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY)).findFirst().orElse(null);
        PropertyModel priceRangeRentFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT)).findFirst().orElse(null);
        PropertyModel municipalityFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY)).findFirst().orElse(null);

        Map<UUID, PropertyModel> customPropsMap = customProps.stream().collect(Collectors.toMap(PropertyModel::getId, Function.identity()));

        var optionsMap = customProps.stream()
                .flatMap(cp -> cp.getCategories() != null ? cp.getCategories().stream() : Stream.empty())
                .collect(Collectors.toMap(option -> option.getId(), option -> option.getName()));

        projects.forEach(project -> exportObject.add(getProjectFeature(
                configModel,
                project,
                customPropsMap,
                priceRangeBuyFixedProp,
                priceRangeRentFixedProp,
                municipalityFixedProp,
                dxPropertiesMap,
                minConfidentiality,
                exportDate,
                errors,
                targetCrs,
                optionsMap)));

        return exportObject;
    }

    static private Feature getProjectFeature(
            ConfigModel configModel,
            ProjectExportSqlModelPlus project,
            Map<UUID, PropertyModel> customPropsMap,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            PropertyModel municipalityFixedProp,
            Map<String, DataExchangePropertyModel> dxPropertiesMap,
            Confidentiality minConfidentiality, LocalDate exportDate,
            List<DataExchangeExportError> errors,
            String targetCrs,
            Map<UUID, String> optionsMap) {
        var projectFeature = new Feature();

        var multiPolygon = ExportUtil.createPolygonForProject(project.getGeometries(), targetCrs, project.getProjectId());
        if (!multiPolygon.getCoordinates().isEmpty()) {
            projectFeature.setGeometry(multiPolygon);
        }

        Map<ProjectPhase, LocalDate> phases = new HashMap<>();
        phases.put(ProjectPhase._6_REALIZATION, project.getRealizationPhaseDate());

        Map<UUID, String> projectTextCustomProps = project.getTextProperties().stream()
                .collect(Collectors.toMap(ProjectExportSqlModelPlus.TextPropertyModel::getPropertyId,
                        ProjectExportSqlModelPlus.TextPropertyModel::getTextValue));
        Map<UUID, SingleValueOrRangeModel<BigDecimal>> projectNumericCustomProps = project.getNumericProperties().stream()
                .collect(Collectors.toMap(ProjectExportSqlModelPlus.NumericPropertyModel::getPropertyId,
                        ProjectExportSqlModelPlus.NumericPropertyModel::getSingleValueOrRangeModel));
        Map<UUID, Boolean> projectBooleanCustomProps = project.getBooleanProperties().stream()
                .collect(Collectors.toMap(ProjectExportSqlModelPlus.BooleanPropertyModel::getPropertyId,
                        ProjectExportSqlModelPlus.BooleanPropertyModel::getBooleanValue));
        Map<UUID, List<UUID>> projectCategoricalCustomProps = project.getCategoryProperties().stream()
                .collect(Collectors.toMap(ProjectExportSqlModelPlus.CategoryPropertyModel::getPropertyId,
                        ProjectExportSqlModelPlus.CategoryPropertyModel::getOptionValues));

        Map<String, String> customProps = new HashMap<>();
        for (var prop : projectTextCustomProps.entrySet()) {
            customProps.put(customPropsMap.get(prop.getKey()).getName(), prop.getValue());
        }
        for (var prop : projectNumericCustomProps.entrySet()) {
            customProps.put(customPropsMap.get(prop.getKey()).getName(), prop.getValue().getValue().toString());
        }

        List<String> woonplaatsName;
        List<UUID> optionIds = projectCategoricalCustomProps.get(municipalityFixedProp.getId());
        if (optionIds == null || optionIds.isEmpty()) {
            woonplaatsName = List.of();
        } else {
            woonplaatsName = optionIds.stream()
                    .map(optionId -> optionsMap.get(optionId))
                    .toList();
        }

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
                        .status(project.getEndDate().isBefore(today) ? ProjectStatus.REALIZED : ProjectStatus.ACTIVE) // Need to guess based on future/pastness.
                                                                                                                      // Do in SQL
                        // .owner()// Needs adding to the model
                        .confidentialityLevel(project.getConfidentiality())
                        .build())
                // .roles(Map.of("projectleider", )) This is a custom property
                .projectDuration(ProjectDuration.builder()
                        .startDate(project.getStartDate())
                        .endDate(project.getEndDate())
                        .build())
                .projectPhasesMap(phases)
                .projectLocation(ProjectLocation.builder()
                        .municipality(woonplaatsName)
                        // .district()
                        // .neighbourhood()
                        .build())
                .customPropertiesMap(customProps)
                .build();

        final var geoJsonBlocks = project.getHouseblocks().stream()
                .map(block -> {
                    var mutationData = MutationData.builder()
                            .amount(block.getMutationAmount())
                            .mutationType(block.getMutationKind())
                            .build();

                    var ownerShipValue = block.getOwnershipValueList().stream()
                            .map(ov -> {
                                return OwnershipValueData.builder()
                                        .categoryName(ov.getOwnershipType().toString())
                                        .max(ov.getOwnershipType() == OwnershipType.KOOPWONING ? (double) ov.getOwnershipValueRangeMax() / 100
                                                : (double) ov.getOwnershipRentalValueRangeMax() / 100)
                                        .min(ov.getOwnershipType() == OwnershipType.KOOPWONING ? (double) ov.getOwnershipValueRangeMin() / 100
                                                : (double) ov.getOwnershipRentalValueRangeMin() / 100)
                                        .build();
                            })
                            .toList();

                    return GeoJsonHouseblock.builder()
                            .diwiId(block.getHouseblockId())
                            .name(block.getName())
                            .endDate(block.getEndDate())
                            .mutationData(mutationData)
                            // .groundPositionsMap(null)

                            .ownershipValue(ownerShipValue)
                            .build();
                })
                .toList();

        projectFeature.setProperty("projectgegevens", geoJsonProject);
        projectFeature.setProperty("woning_blokken", geoJsonBlocks);

        return projectFeature;
    }
}
