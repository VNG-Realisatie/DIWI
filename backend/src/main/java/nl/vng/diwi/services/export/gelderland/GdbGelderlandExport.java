package nl.vng.diwi.services.export.gelderland;

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
import org.geojson.MultiPolygon;
import org.geojson.jackson.CrsType;

import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.ExportUtil;

public class GdbGelderlandExport {
    public static StreamingOutput buildExportObject(
            ConfigModel configModel,
            List<ProjectExportSqlModelExtended> projects,
            List<PropertyModel> customProps,
            Map<String, DataExchangePropertyModel> dxPropertiesMap,
            LocalDate exportDate,
            Confidentiality minConfidentiality,
            List<DataExchangeExportError> errors,
            LoggedUser user) {
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

        int i = 0;
        for (var project : projects) {
            exportObject.add(getProjectFeature(configModel, project, customPropsMap, priceRangeBuyFixedProp, priceRangeRentFixedProp,
                    municipalityFixedProp, dxPropertiesMap, minConfidentiality, exportDate, errors, targetCrs, ++i, user));
        }

        //TODO: write .geojson file to disk(gdb_download_working_dir) and then call GdbConversionService.processGeoJsonToGdb();
        //TODO: write .csv file to disk(gdb_download_working_dir) and gthen call GdbConversionService.processCsvToGdb();

        //TODO: create .zip file from .gdb and place it inside the gdb_download_working_dir : GdbConversionService.createZip();
        //TODO: delete .zip file after download : GdbConversionService.deleteFile(zipFile);

        // For now just return the json
        return output -> {
            Json.mapper.writeValue(output, exportObject);
            output.flush();
        };
    }

    public static Feature getProjectFeature(
            ConfigModel configModel,
            ProjectExportSqlModelExtended project,
            Map<UUID, PropertyModel> customPropsMap,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            PropertyModel municipalityFixedProp,
            Map<String, DataExchangePropertyModel> dxPropertiesMap,
            Confidentiality minConfidentiality,
            LocalDate exportDate,
            List<DataExchangeExportError> errors,
            String targetCrs,
            Integer objectId,
            LoggedUser user) {

        Feature feature = new Feature();

        MultiPolygon multiPolygon = ExportUtil.createPolygonForProject(project.getGeometries(), targetCrs, project.getProjectId());
        if (!multiPolygon.getCoordinates().isEmpty()) {
            feature.setGeometry(multiPolygon);
        }

        feature.setProperty("OBJECTID", objectId);
        feature.setProperty("GlobalID", project.getProjectId());

        // feature.setProperty("Created", project.getCreationDate()); // TODO add to query
        feature.setProperty("Editor", user.getFirstName() + " " + user.getLastName());
        // feature.setProperty("Edited", project.getCreationDate()); // TODO add to query

        feature.setProperty("plannaam", project.getName());
        feature.setProperty("provincie", "Gelderland");

        // regio // custom prop
        // gemeente // custom prop
        // woonplaats // custom prop

        feature.setProperty("vertrouwelijkheid", mapConfidentiality(project.getConfidentiality()));

        // opdrachtgever_type // custom prop
        // opdrachtgever_naam // custom prop
        feature.setProperty("oplevering_eerste", getFirstDelivery(project));
        feature.setProperty("oplevering_laatste", getLastDelivery(project));
        // opmerkingen_basis // custom prop
        feature.setProperty("plantype", mapPlanType(project.getPlanType()));
        // masterplan // custom prop - text
        // bestemmingsplan // custom prop - text
        // zoekgebied // custom prop
        feature.setProperty("projectfase", mapProjectPhase(project.getProjectPhase()));
        // status_planologisch
        // opmerkingen_status // custom prop - text
        // beoogd_woonmilieu_ABF5 // custom prop - cat
        // beoogd_woonmilieu_ABF13 // custom prop - cat
        // knelpunten _meerkeuze // custom prop
        // toelichting_knelpunten // custom prop
        // verhuurder_type // custom prop
        // opmerkingen_kwalitatief // custom prop
        feature.setProperty("koppelid", project.getProjectId());

        // klopt_geom // has note check with province
        // SHAPE_Length // Auto generated?
        // SHAPE_Area // Auto generated?
        var sums = getSums(project, exportDate);
        for (var sum : sums.entrySet()) {
            feature.setProperty(sum.getKey(), sum.getValue());
        }

        feature.setProperty("overkoepelende_plan_id", null);
        feature.setProperty("overkoepelende_plan_naam", null);
        // aantal_tijdelijke_woningen // custom prop
        // aantal_nultreden_woningen // custom prop
        // aantal_geclusterde_woningen // custom prop
        // aantal_zorggeschikte_woningen // custom prop
        // Totaal_koop1
        // Totaal_koop2
        // Totaal_koop3
        // Totaal_koop4
        // Totaal_koop_onbekend
        // Totaal_huur1
        // Totaal_huur2
        // Totaal_huur3
        // Totaal_huur4
        // Totaal_huur_onbekend
        // Totaal_eigendom_onbekend
        // energieconcept // custom prop
        // tapwatervoorziening // custom prop
        // realisatiekans // custom prop
        // aandachtsgroepen// custom prop
        // sleutelproject // custom prop
        // aantal_middenhuur_corporatie
        // onzelfstandige_wooneenheden // custom prop

        return feature;
    }

    private static Map<String, Object> getSums(ProjectExportSqlModelExtended project, LocalDate exportDate) {
        int Totaal_aantal_huurwoningen_corporatie = 0;
        int Totaal_bouw = 0;
        int Totaal_gerealiseerd = 0;
        int Totaal_resterend = 0;
        int Totaal_sloop = 0;
        int Totaal_sloop_gerealiseerd = 0;
        int Totaal_sloop_resterend = 0;
        int Totaal_netto = 0;
        int Totaal_netto_gerealiseerd = 0;
        int Totaal_netto_resterend = 0;
        int Totaal_eengezins_resterend = 0;
        int Totaal_meergezins_resterend = 0;
        int Totaal_type_onbekend_resterend = 0;
        int Totaal_koop_resterend = 0;
        int Totaal_huur_resterend = 0;
        int Totaal_koop_huur_onbekend_resterend = 0;

        for (var b : project.getHouseblocks()) {
            final boolean gerealiseerd = b.getEndDate().isBefore(exportDate);

            int resterend = 0;
            boolean bouw = b.getMutationKind() == MutationType.CONSTRUCTION;

            if (bouw) {
                Totaal_bouw += b.getMutationAmount();
                if (gerealiseerd) {
                    Totaal_gerealiseerd += b.getMutationAmount();
                } else {
                    resterend += b.getMutationAmount();
                    Totaal_meergezins_resterend += b.getMeergezinswoning();
                    Totaal_eengezins_resterend += b.getEengezinswoning();
                    Totaal_type_onbekend_resterend = b.getMutationAmount() - b.getMeergezinswoning() - b.getEengezinswoning();
                }
            } else {
                Totaal_sloop += b.getMutationAmount();
                if (gerealiseerd) {
                    Totaal_sloop_gerealiseerd += b.getMutationAmount();
                } else {
                    Totaal_sloop_resterend += b.getMutationAmount();
                }
            }

            int huur_resterend = 0;
            int koop_resterend = 0;
            for (var o : b.getOwnershipValueList()) {
                if (o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE) {
                    Totaal_aantal_huurwoningen_corporatie += o.getOwnershipAmount();
                    if (!gerealiseerd) {
                        if (bouw) {
                            huur_resterend += o.getOwnershipAmount();
                        } else {
                            huur_resterend -= o.getOwnershipAmount();
                        }
                    }
                } else if (o.getOwnershipType() == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER) {
                    if (!gerealiseerd) {
                        if (bouw) {
                            huur_resterend += o.getOwnershipAmount();
                        } else {
                            huur_resterend -= o.getOwnershipAmount();
                        }
                    }

                } else if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
                    if (!gerealiseerd) {
                        if (bouw) {
                            koop_resterend += o.getOwnershipAmount();
                        } else {
                            koop_resterend -= o.getOwnershipAmount();
                        }
                    }
                }
            }

            Totaal_koop_huur_onbekend_resterend += resterend - huur_resterend - koop_resterend;
            Totaal_koop_resterend += koop_resterend;
            Totaal_huur_resterend += huur_resterend;
            Totaal_resterend += resterend;
        }

        Totaal_netto = Totaal_bouw - Totaal_sloop;
        Totaal_netto_gerealiseerd = Totaal_gerealiseerd - Totaal_sloop_gerealiseerd;
        Totaal_netto_resterend = Totaal_resterend - Totaal_sloop_resterend;

        var map = new HashMap<String, Object>();
        map.put("aantal_huurwoningen_corporatie", Totaal_aantal_huurwoningen_corporatie);
        map.put("Totaal_bouw", Totaal_bouw);
        map.put("Totaal_gerealiseerd", Totaal_gerealiseerd);
        map.put("Totaal_resterend", Totaal_resterend);
        map.put("Totaal_sloop", Totaal_sloop);
        map.put("Totaal_sloop_gerealiseerd", Totaal_sloop_gerealiseerd);
        map.put("Totaal_sloop_resterend", Totaal_sloop_resterend);
        map.put("Totaal_netto", Totaal_netto);
        map.put("Totaal_netto_gerealiseerd", Totaal_netto_gerealiseerd);
        map.put("Totaal_netto_resterend", Totaal_netto_resterend);
        map.put("Totaal_eengezins_resterend", Totaal_eengezins_resterend);
        map.put("Totaal_meergezins_resterend", Totaal_meergezins_resterend);
        map.put("Totaal_type_onbekend_resterend", Totaal_type_onbekend_resterend);
        map.put("Totaal_koop_resterend", Totaal_koop_resterend);
        map.put("Totaal_huur_resterend", Totaal_huur_resterend);
        map.put("Totaal_koop_huur_onbekend_resterend", Totaal_koop_huur_onbekend_resterend);
        return map;

    }

    private static String mapProjectPhase(ProjectPhase projectPhase) {
        if (projectPhase == null) {
            return null;
        }
        return switch (projectPhase) {
        case _2_INITIATIVE -> "1. Initiatief";
        case _3_DEFINITION -> "2. Definitie";
        case _4_DESIGN -> "3. Ontwerp";
        case _5_PREPARATION -> "4. Voorbereiding";
        case _6_REALIZATION -> "5. Realisatie";
        case _7_AFTERCARE -> "6. Nazorg";
        // case -> "7. Afgerond"; // TODO can we find this another way?
        case _1_CONCEPT -> "Onbekend";
        };
    }

    private static String mapPlanType(List<PlanType> planType) {
        if (planType.isEmpty()) {
            return null;
        }
        return switch (planType.get(0)) {
        case HERSTRUCTURERING -> "Herstructurering";
        case PAND_TRANSFORMATIE -> "Pand transformatie";
        case TRANSFORMATIEGEBIED -> "Transformatiegebied";
        case UITBREIDING_OVERIG -> "Uitbreiding overig";
        case UITBREIDING_UITLEG -> "Uitbreiding uitleg";
        case VERDICHTING -> "Verdichting";
        };
    }

    private static LocalDate getFirstDelivery(ProjectExportSqlModelExtended project) {
        return project.getHouseblocks().stream().map(b -> b.getEndDate()).min(LocalDate::compareTo).orElse(null);
    }

    private static LocalDate getLastDelivery(ProjectExportSqlModelExtended project) {
        return project.getHouseblocks().stream().map(b -> b.getEndDate()).max(LocalDate::compareTo).orElse(null);
    }

    private static String mapConfidentiality(Confidentiality confidentiality) {
        if (confidentiality == null) {
            return "";
        }
        return switch (confidentiality) {
        case INTERNAL_CIVIL, INTERNAL_MANAGEMENT, INTERNAL_COUNCIL -> "Gemeente";
        case EXTERNAL_REGIONAL -> "Regio";
        case EXTERNAL_GOVERNMENTAL -> "ProvincieGemeente";
        case PUBLIC -> "Openbaar";
        case PRIVATE -> throw new RuntimeException("Can't export private projects");
        };
    }

}
