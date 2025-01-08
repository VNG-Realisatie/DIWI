package nl.vng.diwi.services.export.geojson;

import static nl.vng.diwi.util.Json.MAPPER;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.jackson.CrsType;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.GeoJsonImportModel;
import nl.vng.diwi.services.GeoJsonImportModel.BasicProjectData;
import nl.vng.diwi.services.GeoJsonImportModel.GeoJsonProject;
import nl.vng.diwi.services.GeoJsonImportModel.ProjectData;
import nl.vng.diwi.services.export.ExportUtil;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandHouseblockExportModel;

public class GeoJSONExport {
    static public FeatureCollection buildExportObject(
            ConfigModel configModel,
            List<ProjectExportSqlModel> projects,
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
            ProjectExportSqlModel project,
            Map<UUID, PropertyModel> customPropsMap,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            PropertyModel municipalityFixedProp,
            Map<String, DataExchangePropertyModel> dxPropertiesMap,
            Confidentiality minConfidentiality, LocalDate exportDate,
            List<DataExchangeExportError> errors,
            String targetCrs) {
        var projectFeature = new Feature();

        var multiPolygon = ExportUtil.createPolygonForProject(project, targetCrs);
        if (!multiPolygon.getCoordinates().isEmpty()) {
            projectFeature.setGeometry(multiPolygon);
        }

        List<EsriZuidHollandHouseblockExportModel> houseblockExportModels = project.getHouseblocks().stream()
                .map(h -> new EsriZuidHollandHouseblockExportModel(project.getProjectId(), h, priceRangeBuyFixedProp, priceRangeRentFixedProp, errors))
                .toList();

        var model = GeoJsonImportModel.builder()
                .project(GeoJsonProject.builder()
                        .basicProjectData(BasicProjectData.builder()
                                .identificationNo(null) // Seems to be only used for error messages in the import
                                .name(project.getName())
                                .build())
                        .projectData(ProjectData.builder()
                                .planType(project.getPlanType().isEmpty() ? null : project.getPlanType().get(0))
                                // .in_programmering() // Is a block property
                                // .priority() // Needs adding to the model
                                // .municipalityRole() // Needs adding to the model
                                // .status()) // Need to guess based on future/pastness. Do in SQL
                                // .owner()// Needs adding to the model
                                .confidentialityLevel(project.getConfidentiality())
                                .build())
                        .build())
                .build();

        projectFeature.setProperty("projectgegevens", model.getProject());
        return projectFeature;
    }
}
