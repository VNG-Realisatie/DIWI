package nl.vng.diwi.services.export.gelderland;

import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MISSING_DATAEXCHANGE_MAPPING;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MISSING_MANDATORY_VALUE;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MULTIPLE_SINGLE_SELECT_VALUES;
import static nl.vng.diwi.services.export.ExportUtil.getOwnershipCategory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.MultiPolygon;
import org.geojson.jackson.CrsType;

import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.TemplateProperty;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.ExportUtil;
import nl.vng.diwi.services.export.ExportUtil.OwnershipValueModel;
import nl.vng.diwi.services.export.OwnershipCategory;

public class GdbGelderlandExport {
    public static StreamingOutput buildExportObject(
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
            exportObject.add(getProjectFeature(project, customPropsMap, priceRangeBuyFixedProp, priceRangeRentFixedProp,
                    municipalityFixedProp, dxPropertiesMap, minConfidentiality, exportDate, errors, targetCrs, ++i, user));
        }

        // TODO: write .geojson file to disk(gdb_download_working_dir) and then call GdbConversionService.processGeoJsonToGdb();
        // TODO: write .csv file to disk(gdb_download_working_dir) and gthen call GdbConversionService.processCsvToGdb();

        // TODO: create .zip file from .gdb and place it inside the gdb_download_working_dir : GdbConversionService.createZip();
        // TODO: delete .zip file after download : GdbConversionService.deleteFile(zipFile);

        try {
            var tempDir = Files.createTempDirectory("GdbGelderlandExport");
            var geojsonFile = new File(tempDir.toFile(), "geojson.geojson");
            Json.mapper.writeValue(geojsonFile, exportObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // For now just return the json
        return output -> {
            Json.mapper.writeValue(output, exportObject);
            output.flush();
        };
    }

    public static Feature getProjectFeature(
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

        feature.setProperty("vertrouwelijkheid", mapConfidentiality(project.getConfidentiality()));

        feature.setProperty("oplevering_eerste", getFirstDelivery(project));
        feature.setProperty("oplevering_laatste", getLastDelivery(project));
        feature.setProperty("plantype", mapPlanType(project.getPlanType()));
        feature.setProperty("projectfase", mapProjectPhase(project.getProjectPhase()));
        // status_planologisch

        feature.setProperty("koppelid", project.getProjectId());

        // klopt_geom // has note check with province
        // SHAPE_Length // Auto generated?
        // SHAPE_Area // Auto generated?
        var sums = calculateAggregations(
                project,
                exportDate,
                priceRangeBuyFixedProp,
                priceRangeRentFixedProp,
                errors);
        for (var sum : sums.entrySet()) {
            feature.setProperty(sum.getKey(), sum.getValue());
        }

        feature.setProperty("overkoepelende_plan_id", null);
        feature.setProperty("overkoepelende_plan_naam", null);

        // custom props
        Map<UUID, String> projectTextCustomProps = project.getTextProperties().stream()
                .collect(Collectors.toMap(
                        ProjectExportSqlModelExtended.TextPropertyModel::getPropertyId,
                        ProjectExportSqlModelExtended.TextPropertyModel::getTextValue));
        Map<UUID, SingleValueOrRangeModel<BigDecimal>> projectNumericCustomProps = project.getNumericProperties().stream()
                .collect(Collectors.toMap(
                        ProjectExportSqlModelExtended.NumericPropertyModel::getPropertyId,
                        ProjectExportSqlModelExtended.NumericPropertyModel::getSingleValueOrRangeModel));
        Map<UUID, Boolean> projectBooleanCustomProps = project.getBooleanProperties().stream()
                .collect(Collectors.toMap(
                        ProjectExportSqlModelExtended.BooleanPropertyModel::getPropertyId,
                        ProjectExportSqlModelExtended.BooleanPropertyModel::getBooleanValue));
        Map<UUID, List<UUID>> projectCategoricalCustomProps = project.getCategoryProperties().stream()
                .collect(Collectors.toMap(
                        ProjectExportSqlModelExtended.CategoryPropertyModel::getPropertyId,
                        ProjectExportSqlModelExtended.CategoryPropertyModel::getOptionValues));

        for (var templateProperty : DataExchangeTemplate.templates.get(DataExchangeType.GDB_GELDERLAND).getProperties()) {
            var prop = templateProperty.getName();
            DataExchangePropertyModel dxPropertyModel = dxPropertiesMap.get(prop);
            addMappedProperty(
                    project,
                    customPropsMap,
                    dxPropertiesMap,
                    errors,
                    feature,
                    projectTextCustomProps,
                    projectCategoricalCustomProps,
                    prop,
                    templateProperty,
                    dxPropertyModel);
        }

        // regio // custom prop
        // gemeente // custom prop
        // woonplaats // custom prop
        // opdrachtgever_type // custom prop
        // opdrachtgever_naam // custom prop
        // opmerkingen_basis // custom prop
        // masterplan // custom prop - text
        // bestemmingsplan // custom prop - text
        // zoekgebied // custom prop

        // opmerkingen_status // custom prop - text
        // beoogd_woonmilieu_ABF5 // custom prop - cat
        // beoogd_woonmilieu_ABF13 // custom prop - cat
        // knelpunten _meerkeuze // custom prop
        // toelichting_knelpunten // custom prop
        // verhuurder_type // custom prop
        // opmerkingen_kwalitatief // custom prop

        // aantal_tijdelijke_woningen // custom prop
        // aantal_nultreden_woningen // custom prop
        // aantal_geclusterde_woningen // custom prop
        // aantal_zorggeschikte_woningen // custom prop

        // energieconcept // custom prop
        // tapwatervoorziening // custom prop
        // realisatiekans // custom prop
        // aandachtsgroepen// custom prop
        // sleutelproject // custom prop
        // aantal_middenhuur_corporatie
        // onzelfstandige_wooneenheden // custom prop

        return feature;
    }

    private static Map<String, Object> calculateAggregations(
            ProjectExportSqlModelExtended project,
            LocalDate exportDate,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            List<DataExchangeExportError> errors) {
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
        int Totaal_koop1 = 0;
        int Totaal_koop2 = 0;
        int Totaal_koop3 = 0;
        int Totaal_koop4 = 0;
        int Totaal_koop_onbekend = 0;
        int Totaal_huur1 = 0;
        int Totaal_huur2 = 0;
        int Totaal_huur3 = 0;
        int Totaal_huur4 = 0;
        int Totaal_huur_onbekend = 0;
        int Totaal_eigendom_onbekend = 0;

        for (var b : project.getHouseblocks()) {
            final boolean gerealiseerd = b.getEndDate().isBefore(exportDate);
            final boolean bouw = b.getMutationKind() == MutationType.CONSTRUCTION;
            final int bouwFactor = bouw ? 1 : -1;

            // totals for construction and for house types (single family, etc)
            if (bouw) {
                Totaal_bouw += b.getMutationAmount();
                if (gerealiseerd) {
                    Totaal_gerealiseerd += b.getMutationAmount();
                } else {
                    Totaal_resterend += b.getMutationAmount();
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
            // totals for ownership
            for (var o : b.getOwnershipValueList()) {
                var model = createOwnershipValueModel(
                        project.getProjectId(),
                        b,
                        priceRangeBuyFixedProp,
                        priceRangeRentFixedProp,
                        errors,
                        GelderlandConstants.priceRangeMap,
                        o);

                switch (model.getOwnershipCategory()) {
                case huur1 -> Totaal_huur1 += bouwFactor * model.getAmount();
                case huur2 -> Totaal_huur2 += bouwFactor * model.getAmount();
                case huur3 -> Totaal_huur3 += bouwFactor * model.getAmount();
                case huur4 -> Totaal_huur4 += bouwFactor * model.getAmount();
                case huur_onb -> Totaal_huur_onbekend += bouwFactor * model.getAmount();

                case koop1 -> Totaal_koop1 += bouwFactor * model.getAmount();
                case koop2 -> Totaal_koop2 += bouwFactor * model.getAmount();
                case koop3 -> Totaal_koop3 += bouwFactor * model.getAmount();
                case koop4 -> Totaal_koop4 += bouwFactor * model.getAmount();
                case koop_onb -> Totaal_koop_onbekend += bouwFactor * model.getAmount();
                }

                if (o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE) {
                    Totaal_aantal_huurwoningen_corporatie += o.getOwnershipAmount();
                }

                if (o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE
                        || o.getOwnershipType() == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER) {
                    if (!gerealiseerd) {
                        huur_resterend += bouwFactor * o.getOwnershipAmount();
                        Totaal_huur_resterend += bouwFactor * o.getOwnershipAmount();
                    }
                } else if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
                    if (!gerealiseerd) {
                        koop_resterend += bouwFactor * o.getOwnershipAmount();
                        Totaal_koop_resterend += bouwFactor * o.getOwnershipAmount();
                    }
                }
            }
            if (!gerealiseerd) {
                int resterend = bouwFactor * b.getMutationAmount();
                Totaal_koop_huur_onbekend_resterend += resterend - huur_resterend - koop_resterend;
            }
        }

        // Totaal_koop_huur_onbekend_resterend += Totaal_resterend - Totaal_huur_resterend - Totaal_koop_resterend;
        // Totaal_koop_resterend += koop_resterend;
        // Totaal_huur_resterend += huur_resterend;
        // Totaal_resterend += resterend;

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
        map.put("Totaal_koop1", Totaal_koop1);
        map.put("Totaal_koop2", Totaal_koop2);
        map.put("Totaal_koop3", Totaal_koop3);
        map.put("Totaal_koop4", Totaal_koop4);
        map.put("Totaal_koop_onbekend", Totaal_koop_onbekend);
        map.put("Totaal_huur1", Totaal_huur1);
        map.put("Totaal_huur2", Totaal_huur2);
        map.put("Totaal_huur3", Totaal_huur3);
        map.put("Totaal_huur4", Totaal_huur4);
        map.put("Totaal_huur_onbekend", Totaal_huur_onbekend);
        map.put("Totaal_eigendom_onbekend", Totaal_eigendom_onbekend);
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

    static private OwnershipValueModel createOwnershipValueModel(
            UUID projectUuid,
            ProjectExportSqlModelExtended.HouseblockExportSqlModel sqlModel,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            List<DataExchangeExportError> errors,
            Map<OwnershipCategory, Long> priceCategoryMap,
            ProjectExportSqlModelExtended.OwnershipValueSqlModel o) {
        OwnershipValueModel oModel = new OwnershipValueModel();
        oModel.setOwnershipType(o.getOwnershipType());
        oModel.setAmount(o.getOwnershipAmount());
        if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
            if (o.getOwnershipValue() != null) {
                oModel.setOwnershipCategory(
                        getOwnershipCategory(o.getOwnershipType(), o.getOwnershipValue(), priceCategoryMap));
            } else if (o.getOwnershipValueRangeMin() != null) {
                oModel.setOwnershipCategory(ExportUtil.getOwnershipCategory(
                        projectUuid,
                        sqlModel.getHouseblockId(),
                        o.getOwnershipType(),
                        o.getOwnershipValueRangeMin(),
                        o.getOwnershipValueRangeMax(),
                        priceCategoryMap,
                        errors));
            } else if (o.getOwnershipRangeCategoryId() != null) {
                RangeSelectDisabledModel rangeOption = priceRangeBuyFixedProp.getRanges().stream()
                        .filter(r -> r.getDisabled() == Boolean.FALSE
                                && r.getId().equals(o.getOwnershipRangeCategoryId()))
                        .findFirst().orElse(null);
                if (rangeOption == null) {
                    oModel.setOwnershipCategory(OwnershipCategory.koop_onb);
                } else {
                    oModel.setOwnershipCategory(ExportUtil.getOwnershipCategory(
                            projectUuid,
                            sqlModel.getHouseblockId(),
                            o.getOwnershipType(),
                            rangeOption.getMin().longValue(),
                            rangeOption.getMax().longValue(),
                            priceCategoryMap,
                            errors));
                }
            } else {
                oModel.setOwnershipCategory(OwnershipCategory.koop_onb);
            }
        } else {
            if (o.getOwnershipRentalValue() != null) {
                oModel.setOwnershipCategory(getOwnershipCategory(o.getOwnershipType(), o.getOwnershipRentalValue(), priceCategoryMap));
            } else if (o.getOwnershipRentalValueRangeMin() != null) {
                oModel.setOwnershipCategory(
                        ExportUtil.getOwnershipCategory(
                                projectUuid,
                                sqlModel.getHouseblockId(),
                                o.getOwnershipType(),
                                o.getOwnershipRentalValueRangeMin(),
                                o.getOwnershipRentalValueRangeMax(),
                                priceCategoryMap,
                                errors));
            } else if (o.getOwnershipRentalRangeCategoryId() != null) {
                RangeSelectDisabledModel rangeOption = priceRangeRentFixedProp.getRanges().stream()
                        .filter(r -> r.getDisabled() == Boolean.FALSE
                                && r.getId().equals(o.getOwnershipRentalRangeCategoryId()))
                        .findFirst().orElse(null);
                if (rangeOption == null) {
                    oModel.setOwnershipCategory(OwnershipCategory.huur_onb);
                } else {
                    oModel.setOwnershipCategory(
                            ExportUtil.getOwnershipCategory(projectUuid,
                                    sqlModel.getHouseblockId(),
                                    o.getOwnershipType(),
                                    rangeOption.getMin().longValue(),
                                    rangeOption.getMax().longValue(),
                                    priceCategoryMap,
                                    errors));
                }
            } else {
                oModel.setOwnershipCategory(OwnershipCategory.huur_onb);
            }
        }
        return oModel;
    }

    /*
     * TODO: Needs to be deduplicated with the version in pzh
     */
    public static void addMappedProperty(
            ProjectExportSqlModelExtended project,
            Map<UUID, PropertyModel> customPropsMap,
            Map<String, DataExchangePropertyModel> dxPropertiesMap,
            List<DataExchangeExportError> errors,
            Feature projectFeature,
            Map<UUID, String> projectTextCustomProps,
            Map<UUID, List<UUID>> projectCategoricalCustomProps,
            String propName,
            TemplateProperty templateProperty,
            DataExchangePropertyModel dxPropertyModel) {
        if (dxPropertyModel == null) {
            // TODO: Should we allow it not being set?
            projectFeature.getProperties().put(templateProperty.getName(), "");
        } else if (templateProperty.getPropertyTypes().contains(PropertyType.TEXT)) {
            // If it accepts a text property it should be able to map a category to it
            if (projectTextCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
                addProjectTextCustomProperty(project.getProjectId(), projectFeature, templateProperty, dxPropertiesMap, projectTextCustomProps, errors);
            } else if (projectCategoricalCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
                addProjectCategoricalCustomPropertyAsText(project.getProjectId(), projectFeature, templateProperty, dxPropertiesMap,
                        projectCategoricalCustomProps, customPropsMap, errors);
            } else {
                projectFeature.getProperties().put(propName, null);
            }
        } else if (templateProperty.getPropertyTypes().containsAll(List.of(PropertyType.CATEGORY))) {
            addProjectCategoricalCustomProperty(
                    project.getProjectId(),
                    projectFeature,
                    templateProperty,
                    dxPropertiesMap,
                    projectCategoricalCustomProps,
                    errors);
        } else {
            throw new NotImplementedException("Combination of types not implemented");
        }
    }

    private static void addProjectTextCustomProperty(UUID projectUuid, Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
            Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, String> projectTextCustomProps,
            List<DataExchangeExportError> errors) {
        UUID customPropUuid = dxPropertiesMap.get(templateProperty.getName()).getCustomPropertyId();
        String ezhValue = null;
        if (customPropUuid == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectTextCustomProps.containsKey(customPropUuid)) {
            ezhValue = projectTextCustomProps.get(customPropUuid);
        } else if (templateProperty.getMandatory()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);
    }

    private static void addProjectCategoricalCustomPropertyAsText(UUID projectUuid, Feature projectFeature, TemplateProperty templateProperty,
            Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, List<UUID>> projectCategoricalCustomProps,
            Map<UUID, PropertyModel> customPropsMap, List<DataExchangeExportError> errors) {
        UUID customPropUuid = dxPropertiesMap.get(templateProperty.getName()).getCustomPropertyId();
        String ezhValue = null;
        if (customPropUuid == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectCategoricalCustomProps.containsKey(customPropUuid)) {
            UUID optionUuid = projectCategoricalCustomProps.get(customPropUuid).get(0);
            PropertyModel propertyModel = customPropsMap.get(customPropUuid);
            ezhValue = propertyModel.getCategories().stream().filter(o -> o.getDisabled() == Boolean.FALSE && o.getId().equals(optionUuid))
                    .map(SelectModel::getName).findFirst().orElse(null);
        } else if (templateProperty.getMandatory()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);

    }

    private static void addProjectCategoricalCustomProperty(UUID projectUuid, Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
            Map<String, DataExchangePropertyModel> dxPropertiesMap, Map<UUID, List<UUID>> projectCategoricalCustomProps,
            List<DataExchangeExportError> errors) {
        DataExchangePropertyModel dxPropertyModel = dxPropertiesMap.get(templateProperty.getName());
        List<String> ezhValue = new ArrayList<>();
        if (dxPropertyModel == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectCategoricalCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
            List<UUID> projectCategoryOptions = projectCategoricalCustomProps.get(dxPropertyModel.getCustomPropertyId());
            for (UUID option : projectCategoryOptions) {
                dxPropertyModel.getOptions().forEach(dxOption -> {
                    if (dxOption.getPropertyCategoryValueIds().contains(option)) {
                        ezhValue.add(dxOption.getName());
                    }
                });
            }
        }

        if (templateProperty.getMandatory() && ezhValue.isEmpty()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        if (templateProperty.getSingleSelect() && ezhValue.size() > 1) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MULTIPLE_SINGLE_SELECT_VALUES));
        }

        if (templateProperty.getSingleSelect()) {
            projectFeature.getProperties().put(templateProperty.getName(), ezhValue.isEmpty() ? null : ezhValue.get(0));
        } else {
            projectFeature.getProperties().put(templateProperty.getName(), ezhValue.isEmpty() ? null : ezhValue);
        }
    }
}
