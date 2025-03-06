package nl.vng.diwi.services.export.gelderland;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.jackson.CrsType;

import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.DataExchangeExportError;

public class GdbGelderlandExport {

    public static StreamingOutput buildExportObject(
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

        projects.forEach(project -> {

            exportObject.add(getProjectFeature(configModel, project, customPropsMap, priceRangeBuyFixedProp, priceRangeRentFixedProp,
                municipalityFixedProp, dxPropertiesMap, minConfidentiality, exportDate, errors, targetCrs));
        });

        // Convert GeoJSON and CSV to GDB here

        throw new UnsupportedOperationException("Unimplemented method 'buildExportObject'");
    }

    private static Feature getProjectFeature(ConfigModel configModel, ProjectExportSqlModel project, Map<UUID, PropertyModel> customPropsMap,
            PropertyModel priceRangeBuyFixedProp, PropertyModel priceRangeRentFixedProp, PropertyModel municipalityFixedProp,
            Map<String, DataExchangePropertyModel> dxPropertiesMap, Confidentiality minConfidentiality, LocalDate exportDate,
            List<DataExchangeExportError> errors, String targetCrs) {
        throw new UnsupportedOperationException("Unimplemented method 'buildExportObject'");

    }

}
