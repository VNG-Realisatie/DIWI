package nl.vng.diwi.services.export;

import java.io.IOException;

import org.geojson.FeatureCollection;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport;

public class ExportUtil {

    public static MultiPolygon createPolygonForProject(ProjectExportSqlModel project) {
        MultiPolygon multiPolygon = new MultiPolygon();
        for (String geometryString : project.getGeometries()) {
            FeatureCollection geometryObject;
            try {
                geometryObject = EsriZuidHollandExport.MAPPER.readValue(geometryString, FeatureCollection.class);
                geometryObject.getFeatures().forEach(f -> {
                    if (f.getGeometry() instanceof Polygon) {
                        multiPolygon.add((Polygon) f.getGeometry());
                    } else {
                        EsriZuidHollandExport.logger.error("Geometry for project id {} is not instance of Polygon: {}", project.getProjectId(), geometryString);
                    }
                });
            } catch (IOException e) {
                EsriZuidHollandExport.logger.error("Geometry for project id {} could not be deserialized into a FeatureCollection: {}", project.getProjectId(), geometryString);
            }
        }
        return multiPolygon;
    }

}
