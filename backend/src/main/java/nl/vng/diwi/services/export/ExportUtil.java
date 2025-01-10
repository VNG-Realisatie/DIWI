package nl.vng.diwi.services.export;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.geojson.FeatureCollection;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport;

@Log4j2
public class ExportUtil {
    static final CRSFactory crsFactory = new CRSFactory();
    static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

    public static MultiPolygon createPolygonForProject(List<String> geometries, String targetCrs, UUID projectId) {
        MultiPolygon multiPolygon = new MultiPolygon();

        CoordinateReferenceSystem target = crsFactory.createFromName(targetCrs);

        for (String geometryString : geometries) {
            FeatureCollection geometryObject;

            try {
                geometryObject = Json.mapper.readValue(geometryString, FeatureCollection.class);

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
                        log.error("Geometry for project id {} is not instance of Polygon: {}", projectId, geometryString);
                    }
                });
            } catch (IOException e) {
                log.error("Geometry for project id {} could not be deserialized into a FeatureCollection: {}", projectId,
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
