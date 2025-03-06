package nl.vng.diwi.services.export.gelderland;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.util.UuidUtil;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.databind.ObjectWriter;

import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.generic.ResourceUtil;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.security.LoggedUser;

public class GdbGelderlandExportTest {
    @Test
    void testGetProjectFeature() throws Exception {
        ConfigModel configModel = new ConfigModel();
        ProjectExportSqlModelExtended project = ProjectExportSqlModelExtended.builder()
                .projectId(UUID.fromString("fdd87435-025f-48ed-a2b4-d765246040cd"))
                .name("project name")
                .confidentiality(Confidentiality.EXTERNAL_GOVERNMENTAL)
                .houseblocks(List.of(
                        ProjectExportSqlModelExtended.HouseblockExportSqlModel.builder()
                                .name("block1")
                                .endDate(LocalDate.of(2025, 3, 1))
                                .build(),
                        ProjectExportSqlModelExtended.HouseblockExportSqlModel.builder()
                                .name("block2")
                                .endDate(LocalDate.of(2025, 3, 2))
                                .build(),
                        ProjectExportSqlModelExtended.HouseblockExportSqlModel.builder()
                                .name("block3")
                                .endDate(LocalDate.of(2025, 3, 3))
                                .build()))
                .build();

        List<PropertyModel> customProps = List.of(PropertyModel.builder()
                .name("text")
                .objectType(ObjectType.PROJECT)
                .propertyType(PropertyType.TEXT)
                .build());

        Map<String, DataExchangePropertyModel> dxPropertiesMap = new HashMap<>();
        LocalDate exportDate = LocalDate.of(2025, 3, 6);
        Confidentiality minConfidentiality = Confidentiality.EXTERNAL_REGIONAL;
        List<DataExchangeExportError> errors = new ArrayList<>();
        String targetCrs = "EPSG:28992";
        LoggedUser user = LoggedUser.builder()
                .firstName("first")
                .lastName("last")
                .build();

        PropertyModel priceRangeBuyFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY)).findFirst().orElse(null);
        PropertyModel priceRangeRentFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT)).findFirst().orElse(null);
        PropertyModel municipalityFixedProp = customProps.stream()
                .filter(pfp -> pfp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY)).findFirst().orElse(null);
        Map<UUID, PropertyModel> customPropsMap = customProps.stream().collect(Collectors.toMap(PropertyModel::getId, Function.identity()));

        var result = GdbGelderlandExport.getProjectFeature(
                configModel,
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
                17,
                user);

        Json.writerWithDefaultPrettyPrinter.writeValue(new File("src/test/resources/GdbGelderlandTest/feature.actual.json"), result);
        var actualTree = Json.mapper.readTree(new File("src/test/resources/GdbGelderlandTest/feature.actual.json"));

        var expected = ResourceUtil.getResourceAsString("GdbGelderlandTest/feature.expected.json");
        var expectedTree = Json.mapper.readTree(expected);

        JSONAssert.assertEquals(Json.mapper.writeValueAsString(result), expected, JSONCompareMode.NON_EXTENSIBLE);

        // assertThat(
        // Json.writerWithDefaultPrettyPrinter
        // .writeValueAsString(result)
        // .lines()
        // .filter(l -> !l.contains("diwi_id"))
        // .collect(Collectors.joining("\n")))
        // .isEqualToIgnoringWhitespace(expectedTree.toPrettyString());
    }
}
