package nl.vng.diwi.services.export.geojson;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.geojson.Crs;
import org.geojson.FeatureCollection;
import org.geojson.jackson.CrsType;

import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.DataExchangeExportError;

public class GeoJSONExport {
    public static FeatureCollection buildExportObject(
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
        crs.getProperties().put("name", "EPSG:3857");
        exportObject.setCrs(crs);
        return exportObject;
    }
}
