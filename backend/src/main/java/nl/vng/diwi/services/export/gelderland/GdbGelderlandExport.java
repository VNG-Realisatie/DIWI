package nl.vng.diwi.services.export.gelderland;

import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MISSING_DATAEXCHANGE_MAPPING;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MISSING_MANDATORY_VALUE;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.MULTIPLE_SINGLE_SELECT_VALUES;
import static nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR.NUMERIC_RANGE_VALUE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.jackson.CrsType;

import jakarta.ws.rs.core.StreamingOutput;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
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
import nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR;
import nl.vng.diwi.services.export.CustomPropsTool;
import nl.vng.diwi.services.export.DataExchangeConfigForExport;
import nl.vng.diwi.services.export.ExportUtil;
import nl.vng.diwi.services.export.gelderland.GelderlandConstants.DetailPlanningHeaders;
import nl.vng.diwi.services.export.zuidholland.EsriZuidHollandExport.EsriZuidHollandHouseblockProps;

public class GdbGelderlandExport {

    private static final List<String> HOUSE_TYPES = List.of("meergezins", "eengezins", "onbekend");

    private static final List<String> HOUSE_CATEGORIES = List.of(
            "koop1",
            "koop2",
            "koop3",
            "koop4",
            "koop_onb",
            "huur1",
            "huur2",
            "huur3",
            "huur4",
            "huur_onb",
            "onbekend");
    private static final String GELDERLAND = "Gelderland";

    static List<String> detailPlanningHeaders;
    static {
        detailPlanningHeaders = new ArrayList<>();
        detailPlanningHeaders.addAll(List.of(
                "GlobalID",
                "Creator",
                "Created",
                "Editor",
                "Edited",
                "parent_globalid",
                "gemeente",
                "regio",
                "vertrouwelijkheid",
                "jaartal"));
        for (var type : HOUSE_TYPES) {
            for (var cat : HOUSE_CATEGORIES) {
                detailPlanningHeaders.add(type + "_" + cat);
            }

        }
        detailPlanningHeaders.add("bouw_gerealiseerd");
        for (var type : HOUSE_TYPES) {
            for (var cat : HOUSE_CATEGORIES) {
                detailPlanningHeaders.add("sloop_" + type + "_" + cat);
            }

        }
        detailPlanningHeaders.add("sloop_gerealiseerd");
    }

    static Map<String, Object> createEmptyDetailPlanningMap(Integer year) {
        var map = new LinkedHashMap<String, Object>();
        for (var header : GelderlandConstants.DetailPlanningHeaders.values()) {
            map.put(header.name(), "");
        }

        map.put("jaartal", year == null ? "" : year.toString());
        return map;
    }

    @AllArgsConstructor
    @Data
    public static class ProjectExportData {
        Feature planRegistration;
        List<Map<String, Object>> detailPlanning;
    }

    public static StreamingOutput buildExportObject(
            List<ProjectExportSqlModelExtended> projects,
            List<PropertyModel> customProps,
            DataExchangeConfigForExport dataExchangeConfigForExport,
            DataExchangeTemplate template,
            LocalDate exportDate,
            List<DataExchangeExportError> errors,
            LoggedUser user) {
        FeatureCollection exportObject = new FeatureCollection();
        Crs crs = new Crs();
        crs.setType(CrsType.name);
        String targetCrs = "EPSG:28992";
        crs.getProperties().put("name", targetCrs);
        exportObject.setCrs(crs);

        var customPropsTool = new CustomPropsTool(customProps);

        PropertyModel priceRangeBuyFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY)).findFirst().orElse(null);
        PropertyModel priceRangeRentFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT)).findFirst().orElse(null);
        var ranges = new ArrayList<RangeSelectDisabledModel>();
        if (priceRangeBuyFixedProp != null) {
            ranges.addAll(priceRangeBuyFixedProp.getRanges());
        }
        if (priceRangeRentFixedProp != null) {
            ranges.addAll(priceRangeRentFixedProp.getRanges());
        }

        PropertyModel municipalityFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY)).findFirst().orElse(null);

        try {
            var tempDir = Files.createTempDirectory("GdbGelderlandExport");
            var csvFile = new File(tempDir.toFile(), "DetailPlanning.csv");

            try (CSVPrinter writer = new CSVPrinter(new FileWriter(csvFile), CSVFormat.DEFAULT)) {
                writer.printRecord(Arrays.asList(GelderlandConstants.DetailPlanningHeaders.values()));

                for (var project : projects) {
                    ProjectExportData projectExportData = getProjectFeature(
                            project,
                            customPropsTool,
                            ranges,
                            municipalityFixedProp,
                            dataExchangeConfigForExport,
                            template,
                            exportDate,
                            errors,
                            targetCrs,
                            user);

                    exportObject.add(projectExportData.planRegistration);

                    for (var row : projectExportData.detailPlanning) {
                        writer.printRecord(row.values());
                    }
                }
            }

            var geojsonFile = new File(tempDir.toFile(), "Planregistratie.geojson");
            Json.mapper.writeValue(geojsonFile, exportObject);
            var gdbFile = GdbConversionService.convertToGdb(geojsonFile, csvFile);

            return output -> {
                try (FileInputStream fileInputStream = new FileInputStream(gdbFile)) {
                    fileInputStream.transferTo(output);
                    output.flush();
                }
            };

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static ProjectExportData getProjectFeature(
            ProjectExportSqlModelExtended project,
            CustomPropsTool customPropsTool,
            List<RangeSelectDisabledModel> ranges,
            PropertyModel municipalityFixedProp,
            DataExchangeConfigForExport dxConfig,
            DataExchangeTemplate template,
            LocalDate exportDate,
            List<DataExchangeExportError> errors,
            String targetCrs,
            LoggedUser user) {

        Feature feature = new Feature();

        Map<String, String> customProps = customPropsTool.getCustomPropertyMap(
                project.getTextProperties(),
                project.getNumericProperties(),
                project.getBooleanProperties(),
                project.getCategoryProperties(),
                project.getOrdinalProperties());

        List<String> geometries = new ArrayList<>();
        if (project.getGeometries() != null) {
            geometries.addAll(project.getGeometries());
        }
        var importGeometry = customProps.get(Constants.FIXED_PROPERTY_GEOMETRY);
        if (importGeometry != null) {
            geometries.add(importGeometry);
        }
        var multiPolygon = ExportUtil.createPolygonForProject(geometries, targetCrs, project.getProjectId());
        if (!multiPolygon.getCoordinates().isEmpty()) {
            feature.setGeometry(multiPolygon);
        } else {
            errors.add(DataExchangeExportError.builder()
                    .projectId(project.getProjectId())
                    .error(EXPORT_ERROR.PROJECT_DOES_NOT_HAVE_GEOMETRY)
                    .build());

        }

        feature.setProperty("GlobalID", project.getProjectId());

        feature.setProperty("Created", project.getCreation_date());
        feature.setProperty("Editor", user.getFirstName() + " " + user.getLastName());
        feature.setProperty("Edited", project.getLast_edit_date());

        feature.setProperty("plannaam", project.getName());
        feature.setProperty("provincie", GELDERLAND);

        feature.setProperty("vertrouwelijkheid", mapConfidentiality(project.getConfidentiality()));

        feature.setProperty("oplevering_eerste", getFirstDelivery(project));
        feature.setProperty("oplevering_laatste", getLastDelivery(project));
        feature.setProperty("plantype", mapPlanType(project.getPlanType()));
        feature.setProperty("projectfase", mapProjectPhase(project, exportDate));
        feature.setProperty("status_planologisch", mapPlanStatus(project.getPlanningPlanStatus()));
        feature.setProperty("knelpunten_meerkeuze", project.getCategoryProperties());

        // The following props are deliberately empty
        feature.setProperty("koppelid", null);
        feature.setProperty("klopt_geom", null);

        var detailYearMap = new TreeMap<Integer, Map<String, Object>>();
        var sums = calculateAggregations(
                project,
                exportDate,
                ranges,
                detailYearMap,
                dxConfig,
                template,
                errors);

        // Add project level sums to object
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
        Map<UUID, List<UUID>> projectCategoricalCustomProps = project.getCategoryProperties().stream()
                .collect(Collectors.toMap(
                        ProjectExportSqlModelExtended.CategoryPropertyModel::getPropertyId,
                        ProjectExportSqlModelExtended.CategoryPropertyModel::getOptionValues));

        for (var templateProperty : DataExchangeTemplate.templates.get(DataExchangeType.GDB_GELDERLAND).getProperties()) {
            var prop = templateProperty.getName();
            var dxPropertyModel = dxConfig.getDxProp(prop);
            addMappedProperty(
                    project,
                    customPropsTool.getCustomPropsMap(),
                    dxConfig,
                    errors,
                    feature,
                    projectTextCustomProps,
                    projectCategoricalCustomProps,
                    projectNumericCustomProps,
                    prop,
                    templateProperty,
                    dxPropertyModel);
        }

        // Add detail planning pzh style. With the houseblocks combined per year.
        List<GdbGelderlandHouseblockExportModel> houseblockExportModels = project
                .getHouseblocks()
                .stream()
                .map(h -> new GdbGelderlandHouseblockExportModel(
                        project.getProjectId(),
                        h,
                        ranges,
                        dxConfig,
                        template,
                        errors))
                .toList();

        List<Map<String, Object>> houseblockProperties = getHouseblockProperties(houseblockExportModels);

        for (var ba : houseblockProperties) {
            var map = detailYearMap.get(ba.get(DetailPlanningHeaders.jaartal.name()));
            for (var value : ba.entrySet()) {
                map.put(value.getKey(), value.getValue());
            }
        }

        // Add project level info to detail planning
        for (var detailPlanning : detailYearMap.values()) {
            detailPlanning.put("Creator", user.getFirstName() + " " + user.getLastName());
            detailPlanning.put("Created", project.getCreation_date().toString());
            detailPlanning.put("Editor", user.getFirstName() + " " + user.getLastName());
            detailPlanning.put("Edited", project.getLast_edit_date().toString());
            detailPlanning.put("parent_globalid", project.getProjectId().toString());
            detailPlanning.put("gemeente", feature.getProperty("gemeente"));
            detailPlanning.put("regio", feature.getProperty("regio"));
            detailPlanning.put("vertrouwelijkheid", mapConfidentiality(project.getConfidentiality()));
        }

        return new ProjectExportData(feature, detailYearMap.values().stream().toList());
    }

    private static Map<String, Object> calculateAggregations(
            ProjectExportSqlModelExtended project,
            LocalDate exportDate,
            List<RangeSelectDisabledModel> ranges,
            Map<Integer, Map<String, Object>> detailYearMap,
            DataExchangeConfigForExport dxConfig,
            DataExchangeTemplate template,
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
        int aantal_middenhuur_corporatie = 0;

        for (var block : project.getHouseblocks()) {
            var detailMap = detailYearMap.computeIfAbsent(block.getDeliveryYear(), GdbGelderlandExport::createEmptyDetailPlanningMap);
            detailMap.put("GlobalID", block.getHouseblockId().toString());

            final boolean gerealiseerd = block.getEndDate().isBefore(exportDate);
            final boolean bouw = block.getMutationKind() == MutationType.CONSTRUCTION;
            final int bouwFactor = bouw ? 1 : -1;

            // totals for construction and for house types (single family, etc)
            if (bouw) {
                Totaal_bouw += block.getMutationAmount();
                if (gerealiseerd) {
                    Totaal_gerealiseerd += block.getMutationAmount();
                } else {
                    int eengezinswoning = block.getEengezinswoning() == null ? 0 : block.getEengezinswoning();
                    int meergezinswoning = block.getMeergezinswoning() == null ? 0 : block.getMeergezinswoning();

                    Totaal_resterend += block.getMutationAmount();
                    Totaal_meergezins_resterend += meergezinswoning;
                    Totaal_eengezins_resterend += eengezinswoning;
                    Totaal_type_onbekend_resterend = block.getMutationAmount() - meergezinswoning - eengezinswoning;
                }
            } else {
                Totaal_sloop += block.getMutationAmount();
                if (gerealiseerd) {
                    Totaal_sloop_gerealiseerd += block.getMutationAmount();
                } else {
                    Totaal_sloop_resterend += block.getMutationAmount();
                }
            }

            int huur_resterend = 0;
            int koop_resterend = 0;

            // This assumes the periods are ordered from old to new
            var priceCategoriesForPeriod = template.getPriceCategoryPeriods()
                    .stream()
                    .filter(pcp -> pcp.getValidUntil() == null || pcp.getValidUntil().isAfter(block.getEndDate()))
                    .findFirst()
                    .orElseThrow();

            // totals for ownership
            for (var o : block.getOwnershipValueList()) {
                var model = ExportUtil.createOwnershipValueModel(
                        project.getProjectId(),
                        block,
                        ranges,
                        errors,
                        priceCategoriesForPeriod,
                        dxConfig,
                        o);

                switch (model.getOwnershipCategory()) {
                case HUUR1 -> Totaal_huur1 += bouwFactor * model.getAmount();
                case HUUR2 -> Totaal_huur2 += bouwFactor * model.getAmount();
                case HUUR3 -> Totaal_huur3 += bouwFactor * model.getAmount();
                case HUUR4 -> Totaal_huur4 += bouwFactor * model.getAmount();
                case HUUR_ONB -> Totaal_huur_onbekend += bouwFactor * model.getAmount();

                case KOOP1 -> Totaal_koop1 += bouwFactor * model.getAmount();
                case KOOP2 -> Totaal_koop2 += bouwFactor * model.getAmount();
                case KOOP3 -> Totaal_koop3 += bouwFactor * model.getAmount();
                case KOOP4 -> Totaal_koop4 += bouwFactor * model.getAmount();
                case KOOP_ONB -> Totaal_koop_onbekend += bouwFactor * model.getAmount();
                }

                if (o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE) {
                    Totaal_aantal_huurwoningen_corporatie += o.getOwnershipAmount();

                    if (model.getOwnershipCategory() == OwnershipCategory.HUUR3) {
                        aantal_middenhuur_corporatie += bouwFactor * model.getAmount();
                    }
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
                int resterend = bouwFactor * block.getMutationAmount();
                Totaal_koop_huur_onbekend_resterend += resterend - huur_resterend - koop_resterend;
            }
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
        map.put("aantal_middenhuur_corporatie", aantal_middenhuur_corporatie);

        return map;
    }

    private static String mapPlanStatus(List<PlanStatus> planningPlanStatus) {
        if (planningPlanStatus.isEmpty()) {
            return "Onbekend";
        }
        var status = planningPlanStatus.get(0);
        return switch (status) {
        case _1A_ONHERROEPELIJK -> "1A. Onherroepelijk";
        case _1B_ONHERROEPELIJK_MET_UITWERKING_NODIG -> "1B. Onherroepelijk, uitwerkingsplicht";
        case _1C_ONHERROEPELIJK_MET_BW_NODIG -> "1C. Onherroepelijk, wijzigingsbevoegdheid";
        case _2A_VASTGESTELD -> "2A. Vastgesteld";
        case _2B_VASTGESTELD_MET_UITWERKING_NODIG -> "2B. Vastgesteld, uitwerkingsplicht";
        case _2C_VASTGESTELD_MET_BW_NODIG -> "2C. Vastgesteld, wijzigingsbevoegdheid";
        case _3_IN_VOORBEREIDING -> "3. In voorbereiding";
        case _4A_OPGENOMEN_IN_VISIE -> "4A. Visie";
        case _4B_NIET_OPGENOMEN_IN_VISIE -> "4B. Idee";
        };
    }

    private static String mapProjectPhase(ProjectExportSqlModelExtended project, LocalDate exportDate) {
        var projectPhase = project.getProjectPhase();
        if (project.getEndDate().isBefore(exportDate)) {
            return "7. Afgerond";
        }

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

    /*
     * TODO: Needs to be deduplicated with the version in pzh
     */
    public static void addMappedProperty(
            ProjectExportSqlModelExtended project,
            Map<UUID, PropertyModel> customPropsMap,
            DataExchangeConfigForExport dataExchangeConfigForExport,
            List<DataExchangeExportError> errors,
            Feature projectFeature,
            Map<UUID, String> projectTextCustomProps,
            Map<UUID, List<UUID>> projectCategoricalCustomProps,
            Map<UUID, SingleValueOrRangeModel<BigDecimal>> projectNumericCustomProps,
            String propName,
            TemplateProperty templateProperty,
            DataExchangePropertyModel dxPropertyModel) {
        if (dxPropertyModel == null) {
            // TODO: Should we allow it not being set?
            projectFeature.getProperties().put(templateProperty.getName(), "");
        } else if (templateProperty.getPropertyTypes().contains(PropertyType.TEXT)) {
            // If it accepts a text property it should be able to map a category to it
            if (projectTextCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
                addProjectTextCustomProperty(project.getProjectId(), projectFeature, templateProperty, dataExchangeConfigForExport, projectTextCustomProps,
                        errors);
            } else if (projectCategoricalCustomProps.containsKey(dxPropertyModel.getCustomPropertyId())) {
                addProjectCategoricalCustomPropertyAsText(project.getProjectId(), projectFeature, templateProperty, dataExchangeConfigForExport,
                        projectCategoricalCustomProps, customPropsMap, errors);
            } else {
                projectFeature.getProperties().put(propName, null);
            }
        } else if (templateProperty.getPropertyTypes().containsAll(List.of(PropertyType.NUMERIC))) {
            addProjectNumericCustomProperty(
                    project.getProjectId(),
                    projectFeature,
                    templateProperty,
                    dataExchangeConfigForExport,
                    projectNumericCustomProps,
                    errors);
        } else if (templateProperty.getPropertyTypes().containsAll(List.of(PropertyType.CATEGORY))) {
            addProjectCategoricalCustomProperty(
                    project.getProjectId(),
                    projectFeature,
                    templateProperty,
                    dataExchangeConfigForExport,
                    projectCategoricalCustomProps,
                    errors);
        } else {
            throw new NotImplementedException("Combination of types not implemented");
        }
    }

    private static void addProjectTextCustomProperty(UUID projectUuid, Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
            DataExchangeConfigForExport dataExchangeConfigForExport, Map<UUID, String> projectTextCustomProps,
            List<DataExchangeExportError> errors) {
        DataExchangePropertyModel dataExchangePropertyModel = dataExchangeConfigForExport.getDxProp(templateProperty.getName());
        UUID customPropUuid = dataExchangePropertyModel.getCustomPropertyId();
        String ezhValue = null;
        if (customPropUuid == null) {
            if (dataExchangePropertyModel.getMandatory()) {
                errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
            }
        } else if (projectTextCustomProps.containsKey(customPropUuid)) {
            ezhValue = projectTextCustomProps.get(customPropUuid);
        } else if (templateProperty.getMandatory()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);
    }

    private static void addProjectCategoricalCustomPropertyAsText(UUID projectUuid, Feature projectFeature, TemplateProperty templateProperty,
            DataExchangeConfigForExport dataExchangeConfigForExport, Map<UUID, List<UUID>> projectCategoricalCustomProps,
            Map<UUID, PropertyModel> customPropsMap, List<DataExchangeExportError> errors) {
        DataExchangePropertyModel dataExchangePropertyModel = dataExchangeConfigForExport.getDxProp(templateProperty.getName());
        UUID customPropUuid = dataExchangePropertyModel.getCustomPropertyId();
        String ezhValue = null;
        if (customPropUuid == null) {
            if (dataExchangePropertyModel.getMandatory()) {
                errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
            }
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
            DataExchangeConfigForExport dataExchangeConfigForExport, Map<UUID, List<UUID>> projectCategoricalCustomProps,
            List<DataExchangeExportError> errors) {
        DataExchangePropertyModel dataExchangePropertyModel = dataExchangeConfigForExport.getDxProp(templateProperty.getName());
        List<String> ezhValue = new ArrayList<>();
        if (dataExchangePropertyModel == null) {
            errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
        } else if (projectCategoricalCustomProps.containsKey(dataExchangePropertyModel.getCustomPropertyId())) {
            List<UUID> projectCategoryOptions = projectCategoricalCustomProps.get(dataExchangePropertyModel.getCustomPropertyId());
            for (UUID option : projectCategoryOptions) {
                dataExchangePropertyModel.getOptions().forEach(dxOption -> {
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

    private static BigDecimal addProjectNumericCustomProperty(UUID projectUuid, Feature projectFeature, DataExchangeTemplate.TemplateProperty templateProperty,
            DataExchangeConfigForExport dataExchangeConfigForExport, Map<UUID, SingleValueOrRangeModel<BigDecimal>> projectNumericCustomProps,
            List<DataExchangeExportError> errors) {
        DataExchangePropertyModel dataExchangePropertyModel = dataExchangeConfigForExport.getDxProp(templateProperty.getName());
        UUID customPropUuid = dataExchangePropertyModel.getCustomPropertyId();
        BigDecimal ezhValue = null;
        if (customPropUuid == null) {
            if (dataExchangePropertyModel.getMandatory()) {
                errors.add(new DataExchangeExportError(null, templateProperty.getName(), MISSING_DATAEXCHANGE_MAPPING));
            }
        } else if (projectNumericCustomProps.containsKey(customPropUuid)) {
            var numericVal = projectNumericCustomProps.get(customPropUuid);
            if (numericVal.getValue() != null) {
                ezhValue = numericVal.getValue();
            } else if (numericVal.getMin() != null) {
                errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), NUMERIC_RANGE_VALUE));
            }
        } else if (templateProperty.getMandatory()) {
            errors.add(new DataExchangeExportError(projectUuid, templateProperty.getName(), MISSING_MANDATORY_VALUE));
        }
        projectFeature.getProperties().put(templateProperty.getName(), ezhValue);

        return ezhValue;
    }

    private static List<Map<String, Object>> getHouseblockProperties(List<GdbGelderlandHouseblockExportModel> houseblocks) {

        Map<Integer, List<GdbGelderlandHouseblockExportModel>> houseblocksByDeliveryYear = new HashMap<>();
        houseblocks.forEach(houseblock -> {
            if (!houseblocksByDeliveryYear.containsKey(houseblock.getDeliveryYear())) {
                houseblocksByDeliveryYear.put(houseblock.getDeliveryYear(), new ArrayList<>());
            }
            houseblocksByDeliveryYear.get(houseblock.getDeliveryYear()).add(houseblock);
        });

        List<Map<String, Object>> allHouseblockProperties = new ArrayList<>();

        List<Integer> deliveryYears = houseblocksByDeliveryYear.keySet().stream().sorted().toList();
        deliveryYears.forEach(deliveryYear -> {
            allHouseblockProperties.add(getHouseblockPropertiesForDeliveryYear(houseblocksByDeliveryYear.get(deliveryYear)));
        });

        return allHouseblockProperties;
    }

    private static Map<String, Object> getHouseblockPropertiesForDeliveryYear(List<GdbGelderlandHouseblockExportModel> houseblockExportModels) {

        Map<String, Object> hbPropsByDeliveryYear = new LinkedHashMap<>();

        GdbGelderlandHouseblockModel constructionHbTotals = new GdbGelderlandHouseblockModel();
        GdbGelderlandHouseblockModel demolitionHbTotals = new GdbGelderlandHouseblockModel();

        houseblockExportModels.forEach(h -> {
            if ((h.getMutationKind() == MutationType.CONSTRUCTION)) {
                constructionHbTotals.addHouseblockData(h);
            } else {
                demolitionHbTotals.addHouseblockData(h);
            }
        });

        Map<EsriZuidHollandHouseblockProps, Integer> constructionValuesMap = constructionHbTotals.calculateHouseTypeOwnershipValuesMap();
        Map<EsriZuidHollandHouseblockProps, Integer> demolitionValuesMap = demolitionHbTotals.calculateHouseTypeOwnershipValuesMap();

        for (var prop : EsriZuidHollandHouseblockProps.values()) {
            switch (prop) {
            case jaartal -> hbPropsByDeliveryYear.put(prop.name(), houseblockExportModels.get(0).getDeliveryYear());

            case meergezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop1));
            case meergezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop2));
            case meergezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop3));
            case meergezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop4));
            case meergezins_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop_onb));
            case meergezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur1));
            case meergezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur2));
            case meergezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur3));
            case meergezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur4));
            case meergezins_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur_onb));
            case meergezins_onbekend -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_onbekend));

            case eengezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop1));
            case eengezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop2));
            case eengezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop3));
            case eengezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop4));
            case eengezins_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop_onb));
            case eengezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur1));
            case eengezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur2));
            case eengezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur3));
            case eengezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur4));
            case eengezins_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur_onb));
            case eengezins_onbekend -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_onbekend));

            case onbekend_koop1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop1));
            case onbekend_koop2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop2));
            case onbekend_koop3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop3));
            case onbekend_koop4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop4));
            case onbekend_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop_onb));
            case onbekend_huur1 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur1));
            case onbekend_huur2 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur2));
            case onbekend_huur3 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur3));
            case onbekend_huur4 -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur4));
            case onbekend_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur_onb));
            case onbekend_onbekend -> hbPropsByDeliveryYear.put(prop.name(), constructionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_onbekend));

            case bouw_gerealiseerd ->
                hbPropsByDeliveryYear.put(prop.name(), LocalDate.now().getYear() < houseblockExportModels.get(0).getDeliveryYear() ? "Ja" : "Nee");

            case sloop_meergezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop1));
            case sloop_meergezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop2));
            case sloop_meergezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop3));
            case sloop_meergezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop4));
            case sloop_meergezins_koop_onb ->
                hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_koop_onb));
            case sloop_meergezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur1));
            case sloop_meergezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur2));
            case sloop_meergezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur3));
            case sloop_meergezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur4));
            case sloop_meergezins_huur_onb ->
                hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_huur_onb));
            case sloop_meergezins_onbekend ->
                hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.meergezins_onbekend));

            case sloop_eengezins_koop1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop1));
            case sloop_eengezins_koop2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop2));
            case sloop_eengezins_koop3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop3));
            case sloop_eengezins_koop4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop4));
            case sloop_eengezins_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_koop_onb));
            case sloop_eengezins_huur1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur1));
            case sloop_eengezins_huur2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur2));
            case sloop_eengezins_huur3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur3));
            case sloop_eengezins_huur4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur4));
            case sloop_eengezins_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_huur_onb));
            case sloop_eengezins_onbekend -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.eengezins_onbekend));

            case sloop_onbekend_koop1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop1));
            case sloop_onbekend_koop2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop2));
            case sloop_onbekend_koop3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop3));
            case sloop_onbekend_koop4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop4));
            case sloop_onbekend_koop_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_koop_onb));
            case sloop_onbekend_huur1 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur1));
            case sloop_onbekend_huur2 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur2));
            case sloop_onbekend_huur3 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur3));
            case sloop_onbekend_huur4 -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur4));
            case sloop_onbekend_huur_onb -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_huur_onb));
            case sloop_onbekend_onbekend -> hbPropsByDeliveryYear.put(prop.name(), demolitionValuesMap.get(EsriZuidHollandHouseblockProps.onbekend_onbekend));

            case sloop_gerealiseerd ->
                hbPropsByDeliveryYear.put(prop.name(), LocalDate.now().getYear() < houseblockExportModels.get(0).getDeliveryYear() ? "Ja" : "Nee");
            }
        }

        return hbPropsByDeliveryYear;

    }
}
