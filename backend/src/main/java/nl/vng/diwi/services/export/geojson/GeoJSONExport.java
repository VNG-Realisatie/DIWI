package nl.vng.diwi.services.export.geojson;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.jackson.CrsType;

import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.GeoJsonExportModel.BasicProjectData;
import nl.vng.diwi.services.GeoJsonExportModel.GeoJsonHouseblock;
import nl.vng.diwi.services.GeoJsonExportModel.GeoJsonProject;
import nl.vng.diwi.services.GeoJsonExportModel.ProjectData;
import nl.vng.diwi.services.GeoJsonExportModel.ProjectDuration;
import nl.vng.diwi.services.GeoJsonExportModel.ProjectLocation;
import nl.vng.diwi.services.export.ExportUtil;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandHouseblockExportModel;

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

        projects.forEach(project -> exportObject.add(getProjectFeature(configModel, project, customPropsMap, priceRangeBuyFixedProp, priceRangeRentFixedProp,
                municipalityFixedProp, dxPropertiesMap, minConfidentiality, exportDate, errors, targetCrs)));

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
            String targetCrs) {
        var projectFeature = new Feature();

        var multiPolygon = ExportUtil.createPolygonForProject(project.getGeometries(), targetCrs, project.getProjectId());
        if (!multiPolygon.getCoordinates().isEmpty()) {
            projectFeature.setGeometry(multiPolygon);
        }

        List<GeoJsonHouseblockExportModel> houseblockExportModels = project
                .getHouseblocks()
                .stream()
                .map(h -> new GeoJsonHouseblockExportModel(project.getProjectId(), h, priceRangeBuyFixedProp, priceRangeRentFixedProp, errors))
                .toList();

        Map<ProjectPhase, LocalDate> phases = new HashMap<>();
        phases.put(ProjectPhase._6_REALIZATION, project.getRealizationPhaseDate());

        final var geoJsonProject = GeoJsonProject.builder()
                .basicProjectData(BasicProjectData.builder()
                        .identificationNo(null) // Seems to be only used for error messages in the import
                        .name(project.getName())
                        .build())
                .projectData(ProjectData.builder()
                        .planType(project.getPlanType().isEmpty() ? null : project.getPlanType().get(0))
                        // .in_programmering() // Is a block property, move to block
                        // .priority() // This is a custom property in the importer
                        // .municipalityRole() // This is a custom property
                        .status(project.getStatus()) // Need to guess based on future/pastness. Do in SQL
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
                        // .municipality()
                        // .district()
                        // .neighbourhood()
                        .build())
                .build();

        final var geoJsonBlocks = houseblockExportModels.stream()
                .map(blockModel -> GeoJsonHouseblock.builder()
                        // .name(blockModel.getName())
                        .build())
                .toList();

        projectFeature.setProperty("projectgegevens", geoJsonProject);
        projectFeature.setProperty("woning_blokken", geoJsonBlocks);

        return projectFeature;
    }
}
