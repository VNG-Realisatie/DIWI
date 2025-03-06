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
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.DataExchangeExportError;

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

        // Convert GeoJSON and CSV to GDB here

        throw new UnsupportedOperationException("Unimplemented method 'buildExportObject'");
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

        feature.setProperty("OBJECTID", objectId);
        feature.setProperty("GlobalID", project.getProjectId());

        // feature.setProperty("Created", project.getCreationDate()); // TODO
        feature.setProperty("Editor", user.getFirstName() + " " + user.getLastName());
        // feature.setProperty("Edited", project.getCreationDate()); // TODO

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
        // knelpunten _meerkeuze
        // toelichting_knelpunten
        // verhuurder_type
        // aantal_huurwoningen_corporatie
        // opmerkingen_kwalitatief
        // koppelid
        // klopt_geom
        // SHAPE_Length
        // SHAPE_Area
        // Totaal_bouw
        // Totaal_gerealiseerd
        // Totaal_resterend
        // Totaal_sloop
        // Totaal_sloop_gerealiseerd
        // Totaal_sloop_resterend
        // Totaal_netto
        // Totaal_netto_gerealiseerd
        // Totaal_netto_resterend
        // Totaal_eengezins_resterend
        // Totaal_meergezins_resterend
        // Totaal_type_onbekend_resterend
        // Totaal_koop_resterend
        // Totaal_huur_resterend
        // Totaal_koop_huur_onbekend_resterend
        // overkoepelende_plan_id
        // overkoepelende_plan_naam
        // aantal_tijdelijke_woningen
        // aantal_nultreden_woningen
        // aantal_geclusterde_woningen
        // aantal_zorggeschikte_woningen
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
        // energieconcept
        // tapwatervoorziening
        // realisatiekans
        // aandachtsgroepen
        // sleutelproject
        // aantal_middenhuur_corporatie
        // onzelfstandige_wooneenheden

        return feature;
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
