package nl.vng.diwi.services.export;

import java.io.IOException;
import java.util.Objects;

import org.geojson.FeatureCollection;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport;

public class ExportUtil {
    static final CRSFactory crsFactory = new CRSFactory();
    static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

    public static MultiPolygon createPolygonForProject(ProjectExportSqlModel project, String targetCrs) {
        MultiPolygon multiPolygon = new MultiPolygon();

        CoordinateReferenceSystem target = crsFactory.createFromName(targetCrs);

        for (String geometryString : project.getGeometries()) {
            FeatureCollection geometryObject;

            try {
                geometryObject = EsriZuidHollandExport.MAPPER.readValue(geometryString, FeatureCollection.class);

                String sourceCrsName = getCompatibleCrsName(geometryObject);

                CoordinateReferenceSystem source = crsFactory.createFromName(sourceCrsName);
                CoordinateTransform transform = ctFactory.createTransform(source, target);

                geometryObject.getFeatures().forEach(f -> {
                    if (f.getGeometry() instanceof Polygon) {
                        Polygon polygon = (Polygon) f.getGeometry();

                        for (var ring : polygon.getCoordinates()) {
                            for (var coordinate : ring) {
                                ProjCoordinate result = new ProjCoordinate();

                                transform.transform(new ProjCoordinate(coordinate.getLongitude(), coordinate.getLatitude()), result);

                                coordinate.setLatitude(result.y);
                                coordinate.setLongitude(result.x);
                            }
                        }

                        multiPolygon.add(polygon);
                    } else {
                        EsriZuidHollandExport.logger.error("Geometry for project id {} is not instance of Polygon: {}", project.getProjectId(), geometryString);
                    }
                });
            } catch (IOException e) {
                EsriZuidHollandExport.logger.error("Geometry for project id {} could not be deserialized into a FeatureCollection: {}", project.getProjectId(),
                        geometryString);
            }
        }
        return multiPolygon;
    }

    private static String getCompatibleCrsName(FeatureCollection geometryObject) {
        String sourceCrsName = (String) geometryObject.getCrs().getProperties().get("name");
        String urnPrefix = "urn:ogc:def:crs:";
        if (sourceCrsName.startsWith(urnPrefix)) {
            sourceCrsName =  sourceCrsName.substring(urnPrefix.length());
        }

        String epsgDoubleColonPrefix = "EPSG::";
        if (sourceCrsName.startsWith(epsgDoubleColonPrefix)){
            sourceCrsName = "EPSG:" + sourceCrsName.substring(epsgDoubleColonPrefix.length());
        }
        return sourceCrsName;
    }

}
