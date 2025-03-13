package nl.vng.diwi.services.export.gelderland;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.HouseblockExportSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.NumericPropertyModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.OwnershipValueSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.TextPropertyModel;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.generic.ResourceUtil;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.DataExchangePropertyModel.DataExchangePropertyModelBuilder;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.OwnershipCategory;
import nl.vng.diwi.testutil.ProjectsUtil;

public class GdbGelderlandExportTest {
    @Test
    void createGdb() throws Exception {
        // Some constants
        LocalDate exportDate = LocalDate.of(2025, 3, 2); // Same as middle house block so we have future and past blocks
        Confidentiality minConfidentiality = Confidentiality.EXTERNAL_REGIONAL;
        LoggedUser user = LoggedUser.builder()
                .firstName("first")
                .lastName("last")
                .build();

        // Create some custom props
        List<PropertyModel> customProps = List.of(
                PropertyModel.builder()
                        .id(UUID.randomUUID())
                        .name("text")
                        .objectType(ObjectType.PROJECT)
                        .propertyType(PropertyType.TEXT)
                        .build(),
                PropertyModel.builder()
                        .id(UUID.randomUUID())
                        .name(PropertyType.NUMERIC.name())
                        .objectType(ObjectType.PROJECT)
                        .propertyType(PropertyType.NUMERIC)
                        .build());

        // Make it easy to find the custom prop
        Map<String, PropertyModel> customPropMap = customProps.stream()
                .collect(Collectors.toMap(PropertyModel::getName, p -> p));

        // Create a project with some blocks
        ProjectExportSqlModelExtended project = ProjectExportSqlModelExtended.builder()
                .projectId(UUID.fromString("fdd87435-025f-48ed-a2b4-d765246040cd"))
                .name("project name")
                .creation_date(LocalDate.of(2025, 1, 1))
                .last_edit_date(LocalDate.of(2025, 2, 1))
                .geometries(List.of(ProjectsUtil.PLOT_JSON_STRING))
                .confidentiality(Confidentiality.EXTERNAL_GOVERNMENTAL)
                .projectPhase(ProjectPhase._5_PREPARATION)
                .planType(List.of(PlanType.TRANSFORMATIEGEBIED))
                .planningPlanStatus(List.of(PlanStatus._2B_VASTGESTELD_MET_UITWERKING_NODIG))
                .textProperties(List.of(
                        new TextPropertyModel(customPropMap.get("text").getId(), "text_value")))
                .numericProperties(List.of(
                        new NumericPropertyModel(customPropMap.get(PropertyType.NUMERIC.name()).getId(), BigDecimal.valueOf(17), null, null)))
                .houseblocks(List.of(
                        HouseblockExportSqlModel.builder()
                                .name("block2")
                                .houseblockId(UUID.randomUUID())
                                .mutationAmount(2)
                                .mutationKind(MutationType.CONSTRUCTION)
                                .deliveryYear(2025)
                                .ownershipValueList(List.of(
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(2)
                                                .ownershipType(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER)
                                                .ownershipRentalValue(100l)
                                                .build()))
                                .meergezinswoning(0)
                                .eengezinswoning(2)
                                .endDate(LocalDate.of(2025, 3, 2))
                                .build()))
                .build();

        var template = DataExchangeTemplate.templates.get(DataExchangeType.GDB_GELDERLAND);
        Map<String, DataExchangePropertyModel> dxPropertiesMap = template.getProperties().stream()
                .map(dxProp -> {
                    DataExchangePropertyModelBuilder builder = DataExchangePropertyModel.builder()
                            .name(dxProp.getName());
                    if (dxProp.getPropertyTypes().contains(PropertyType.TEXT)) {
                        return builder.customPropertyId(customPropMap.get("text").getId())
                                .build();
                    } else if (dxProp.getPropertyTypes().contains(PropertyType.NUMERIC)) {
                        return builder.customPropertyId(customPropMap.get(PropertyType.NUMERIC.name()).getId())
                                .build();
                    }
                    return null;
                })
                .filter(dxProp -> dxProp != null)
                .collect(Collectors.toMap(DataExchangePropertyModel::getName, d -> d));

        List<DataExchangeExportError> errors = new ArrayList<>();

        var result = GdbGelderlandExport.buildExportObject(List.of(project), customProps, dxPropertiesMap, exportDate, minConfidentiality, errors, user);

        File outputFile = new File("src/test/resources/GdbGelderlandTest/result.gdb.zip");
        try (FileOutputStream output = new FileOutputStream(outputFile)) {
            result.write(output);
        }
        assertThat(errors).isEmpty();

        var process = new ProcessBuilder(List.of("/usr/bin/ogrinfo", outputFile.getAbsolutePath()))
                .start();

        process.waitFor();

        assertThat(new String(process.getInputStream().readAllBytes()))
                .isEqualTo("""
                        INFO: Open of `%s'
                              using driver `OpenFileGDB' successful.
                        Layer: Planregistratie (Multi Polygon)
                        Layer: DetailPlanning (None)
                        """.formatted(outputFile.getAbsolutePath()));
    }

    @Test
    void testGetProjectFeature() throws Exception {
        // Some constants
        LocalDate exportDate = LocalDate.of(2025, 3, 2); // Same as middle house block so we have future and past blocks
        Confidentiality minConfidentiality = Confidentiality.EXTERNAL_REGIONAL;
        String targetCrs = "EPSG:28992";
        LoggedUser user = LoggedUser.builder()
                .firstName("first")
                .lastName("last")
                .build();

        // Create some custom props
        List<PropertyModel> customProps = List.of(
                PropertyModel.builder()
                        .id(UUID.randomUUID())
                        .name("text")
                        .objectType(ObjectType.PROJECT)
                        .propertyType(PropertyType.TEXT)
                        .build(),
                PropertyModel.builder()
                        .id(UUID.randomUUID())
                        .name(PropertyType.NUMERIC.name())
                        .objectType(ObjectType.PROJECT)
                        .propertyType(PropertyType.NUMERIC)
                        .build());

        // Make it easy to find the custom prop
        Map<String, PropertyModel> customPropMap = customProps.stream()
                .collect(Collectors.toMap(PropertyModel::getName, p -> p));

        // Create a project with some blocks
        ProjectExportSqlModelExtended project = ProjectExportSqlModelExtended.builder()
                .projectId(UUID.fromString("fdd87435-025f-48ed-a2b4-d765246040cd"))
                .name("project name")
                .creation_date(LocalDate.of(2025, 1, 1))
                .last_edit_date(LocalDate.of(2025, 2, 1))
                .geometries(List.of(ProjectsUtil.PLOT_JSON_STRING))
                .confidentiality(Confidentiality.EXTERNAL_GOVERNMENTAL)
                .projectPhase(ProjectPhase._5_PREPARATION)
                .planType(List.of(PlanType.TRANSFORMATIEGEBIED))
                .planningPlanStatus(List.of(PlanStatus._2B_VASTGESTELD_MET_UITWERKING_NODIG))
                .textProperties(List.of(
                        new TextPropertyModel(customPropMap.get("text").getId(), "text_value")))
                .numericProperties(List.of(
                        new NumericPropertyModel(customPropMap.get(PropertyType.NUMERIC.name()).getId(), BigDecimal.valueOf(17), null, null)))
                .houseblocks(List.of(
                        HouseblockExportSqlModel.builder()
                                .name("block1")
                                .houseblockId(UUID.fromString("0000000-0000-0000-0001-000000000001"))
                                .mutationAmount(4)
                                .mutationKind(MutationType.CONSTRUCTION)
                                .deliveryYear(2025)
                                .ownershipValueList(List.of(
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(1)
                                                .ownershipType(OwnershipType.HUURWONING_WONINGCORPORATIE)
                                                .ownershipRentalValue(100l)
                                                .build(),
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(1)
                                                .ownershipType(OwnershipType.HUURWONING_WONINGCORPORATIE)
                                                .ownershipRentalValueRangeMin(GelderlandConstants.priceRangeMap.get(OwnershipCategory.huur4))
                                                .build(),
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(1)
                                                .ownershipType(OwnershipType.HUURWONING_WONINGCORPORATIE)
                                                .ownershipRentalValueRangeMax(100l)
                                                .build(),
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(1)
                                                .ownershipType(OwnershipType.HUURWONING_WONINGCORPORATIE)
                                                .ownershipRentalValueRangeMin(100l)
                                                .ownershipRentalValueRangeMax(120l)
                                                .build()))
                                .meergezinswoning(4)
                                .eengezinswoning(0)
                                .endDate(LocalDate.of(2025, 3, 1))
                                .build(),
                        HouseblockExportSqlModel.builder()
                                .name("block2")
                                .houseblockId(UUID.fromString("0000000-0000-0000-0001-000000000002"))
                                .mutationAmount(2)
                                .mutationKind(MutationType.CONSTRUCTION)
                                .deliveryYear(2025)
                                .ownershipValueList(List.of(
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(2)
                                                .ownershipType(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER)
                                                .ownershipRentalValue(100l)
                                                .build()))
                                .meergezinswoning(0)
                                .eengezinswoning(2)
                                .endDate(LocalDate.of(2025, 3, 2))
                                .build(),
                        HouseblockExportSqlModel.builder()
                                .name("block3")
                                .houseblockId(UUID.fromString("0000000-0000-0000-0001-000000000003"))
                                .mutationAmount(1)
                                .mutationKind(MutationType.DEMOLITION)
                                .deliveryYear(2026)
                                .ownershipValueList(List.of(
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(1)
                                                .ownershipType(OwnershipType.KOOPWONING)
                                                .ownershipValue(100l)
                                                .build()))
                                .meergezinswoning(0)
                                .eengezinswoning(1)
                                .endDate(LocalDate.of(2025, 3, 3))
                                .build()))
                .build();

        var template = DataExchangeTemplate.templates.get(DataExchangeType.GDB_GELDERLAND);
        Map<String, DataExchangePropertyModel> dxPropertiesMap = template.getProperties().stream()
                .map(dxProp -> {
                    DataExchangePropertyModelBuilder builder = DataExchangePropertyModel.builder()
                            .name(dxProp.getName());
                    if (dxProp.getPropertyTypes().contains(PropertyType.TEXT)) {
                        return builder.customPropertyId(customPropMap.get("text").getId())
                                .build();
                    } else if (dxProp.getPropertyTypes().contains(PropertyType.NUMERIC)) {
                        return builder.customPropertyId(customPropMap.get(PropertyType.NUMERIC.name()).getId())
                                .build();
                    }
                    return null;
                })
                .filter(dxProp -> dxProp != null)
                .collect(Collectors.toMap(DataExchangePropertyModel::getName, d -> d));

        List<DataExchangeExportError> errors = new ArrayList<>();

        PropertyModel priceRangeBuyFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY)).findFirst().orElse(null);
        PropertyModel priceRangeRentFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT)).findFirst().orElse(null);
        PropertyModel municipalityFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY)).findFirst().orElse(null);
        Map<UUID, PropertyModel> customPropsMap = customProps.stream().collect(Collectors.toMap(PropertyModel::getId, Function.identity()));

        var result = GdbGelderlandExport.getProjectFeature(
                project,
                customPropsMap,
                priceRangeBuyFixedProp,
                priceRangeRentFixedProp,
                municipalityFixedProp,
                dxPropertiesMap,
                minConfidentiality,
                exportDate,
                errors,
                targetCrs,
                user);

        assertThat(errors).isEmpty();

        // Use JSON for comparing the large objects as that makes comparing easier. e.g. using an external diff tool.
        Json.SORTED_MAPPER
                .writerWithDefaultPrettyPrinter()
                .writeValue(new File("src/test/resources/GdbGelderlandTest/feature.actual.json"), result.getPlanRegistration());
        var expected = ResourceUtil.getResourceAsString("GdbGelderlandTest/feature.expected.json");

        JSONAssert.assertEquals(expected, Json.SORTED_MAPPER.writeValueAsString(result.getPlanRegistration()), JSONCompareMode.NON_EXTENSIBLE);

        Json.SORTED_MAPPER
                .writerWithDefaultPrettyPrinter()
                .writeValue(new File("src/test/resources/GdbGelderlandTest/detailPlanning.actual.json"), result.getDetailPlanning());
        var expectedDetailPlanningJson = ResourceUtil.getResourceAsString("GdbGelderlandTest/detailPlanning.expected.json");
        var actualDetailPlanningJson = Json.SORTED_MAPPER.writeValueAsString(result.getDetailPlanning());

        JSONAssert.assertEquals(expectedDetailPlanningJson, actualDetailPlanningJson, JSONCompareMode.NON_EXTENSIBLE);
    }
}
