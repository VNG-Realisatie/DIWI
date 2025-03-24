package nl.vng.diwi.services.export;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.geojson.FeatureCollection;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.PriceCategory;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.PriceCategoryPeriod;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR;

@Log4j2
public class ExportUtil {
    @Data
    public static class OwnershipValueModel {
        private OwnershipType ownershipType;
        private Integer amount;
        private OwnershipCategory ownershipCategory;
    }

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
            sourceCrsName = sourceCrsName.substring(urnPrefix.length());
        }

        String epsgDoubleColonPrefix = "EPSG::";
        if (sourceCrsName.startsWith(epsgDoubleColonPrefix)) {
            sourceCrsName = "EPSG:" + sourceCrsName.substring(epsgDoubleColonPrefix.length());
        }
        return sourceCrsName;
    }

    public static OwnershipCategory getOwnershipCategory(
            OwnershipType ownershipType,
            Long priceValue,
            Map<OwnershipCategory, Long> priceCategoryMap) {
        if (ownershipType == OwnershipType.KOOPWONING) {
            if (priceValue == null) {
                return OwnershipCategory.KOOP_ONB;
            } else if (priceValue < priceCategoryMap.get(OwnershipCategory.KOOP2)) {
                return OwnershipCategory.KOOP1;
            } else if (priceValue < priceCategoryMap.get(OwnershipCategory.KOOP3)) {
                return OwnershipCategory.KOOP2;
            } else if (priceValue < priceCategoryMap.get(OwnershipCategory.KOOP4)) {
                return OwnershipCategory.KOOP3;
            } else {
                return OwnershipCategory.KOOP4;
            }
        } else {
            if (priceValue == null) {
                return OwnershipCategory.HUUR_ONB;
            } else if (priceValue < priceCategoryMap.get(OwnershipCategory.HUUR2)) {
                return OwnershipCategory.HUUR1;
            } else if (priceValue < priceCategoryMap.get(OwnershipCategory.HUUR3)) {
                return OwnershipCategory.HUUR2;
            } else if (priceValue < priceCategoryMap.get(OwnershipCategory.HUUR4)) {
                return OwnershipCategory.HUUR3;
            } else {
                return OwnershipCategory.HUUR4;
            }
        }
    }

    public static OwnershipCategory getOwnershipCategory(
            UUID projectUuid,
            UUID houseblockUuid,
            OwnershipType ownershipType,
            Long priceValueMin,
            Long priceValueMax,
            Map<OwnershipCategory, Long> priceCategoryMap,
            List<DataExchangeExportError> errors) {
        OwnershipCategory cat1 = getOwnershipCategory(ownershipType, priceValueMin, priceCategoryMap);
        OwnershipCategory cat2 = getOwnershipCategory(ownershipType, priceValueMax, priceCategoryMap);

        if (cat1 == cat2) {
            return cat1;
        } else if (ownershipType == OwnershipType.KOOPWONING &&
                cat1 == OwnershipCategory.KOOP4 &&
                cat2 == OwnershipCategory.KOOP_ONB) {
            return cat1;
        } else if ((ownershipType == OwnershipType.HUURWONING_WONINGCORPORATIE || ownershipType == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER) &&
                cat1 == OwnershipCategory.HUUR4 &&
                cat2 == OwnershipCategory.HUUR_ONB) {
            return cat1;
        } else {
            errors.add(new DataExchangeExportError(projectUuid, houseblockUuid, EXPORT_ERROR.OWNERSHIP_RANGE_MAPPING_ERROR, cat1, cat2, priceValueMin,
                    priceValueMax));
            return ownershipType == OwnershipType.KOOPWONING ? OwnershipCategory.KOOP_ONB : OwnershipCategory.HUUR_ONB;
        }
    }

    public static OwnershipCategory getOwnershipCategory(
            OwnershipType ownershipType,
            Long priceValue,
            PriceCategoryPeriod pcp) {
        if (ownershipType == OwnershipType.KOOPWONING) {
            return getOwnershipCategoryFromList(priceValue, pcp.getCategoriesBuy(), OwnershipCategory.KOOP_ONB);
        } else {
            return getOwnershipCategoryFromList(priceValue, pcp.getCategoriesRent(), OwnershipCategory.HUUR_ONB);
        }
    }

    private static OwnershipCategory getOwnershipCategoryFromList(Long priceValue, List<PriceCategory> categoryList, OwnershipCategory unknownCategory) {
        if (priceValue == null) {
            return unknownCategory;
        }

        for (var cat : categoryList) {
            if (cat.getMaxValue() == null) {
                return cat.getCategory();
            }
            if (cat.getMaxValue() >= priceValue) {
                return cat.getCategory();
            }
        }
        return unknownCategory;
    }

    public static OwnershipCategory getOwnershipCategory(
            UUID projectUuid,
            UUID houseblockUuid,
            OwnershipType ownershipType,
            Long priceValueMin,
            Long priceValueMax,
            PriceCategoryPeriod pcp,
            List<DataExchangeExportError> errors) {
        OwnershipCategory cat1 = getOwnershipCategory(ownershipType, priceValueMin, pcp);
        OwnershipCategory cat2 = getOwnershipCategory(ownershipType, priceValueMax, pcp);

        if (cat1 == cat2) {
            return cat1;
        } else if (ownershipType == OwnershipType.KOOPWONING &&
                cat1 == OwnershipCategory.KOOP4 &&
                cat2 == OwnershipCategory.KOOP_ONB) {
            return cat1;
        } else if ((ownershipType == OwnershipType.HUURWONING_WONINGCORPORATIE || ownershipType == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER) &&
                cat1 == OwnershipCategory.HUUR4 &&
                cat2 == OwnershipCategory.HUUR_ONB) {
            return cat1;
        } else {
            errors.add(new DataExchangeExportError(
                    projectUuid,
                    houseblockUuid,
                    EXPORT_ERROR.OWNERSHIP_RANGE_MAPPING_ERROR,
                    cat1,
                    cat2,
                    priceValueMin,
                    priceValueMax));
            return ownershipType == OwnershipType.KOOPWONING ? OwnershipCategory.KOOP_ONB : OwnershipCategory.HUUR_ONB;
        }
    }
}
