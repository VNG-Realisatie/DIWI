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

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.*;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.generic.Json;
import nl.vng.diwi.generic.ResourceUtil;
import nl.vng.diwi.models.ConfigModel;
import nl.vng.diwi.models.DataExchangePropertyModel;
import nl.vng.diwi.models.DataExchangePropertyModel.DataExchangePropertyModelBuilder;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.security.LoggedUser;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.testutil.ProjectsUtil;

public class GdbGelderlandExportTest {
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
        List<PropertyModel> customProps = List.of(PropertyModel.builder()
                .id(UUID.randomUUID())
                .name("text")
                .objectType(ObjectType.PROJECT)
                .propertyType(PropertyType.TEXT)
                .build());

        // Make it easy to find the custom prop
        var customPropMap = customProps.stream()
                .collect(Collectors.toMap(PropertyModel::getName, p -> p));

        // Create a project with some blocks
        ProjectExportSqlModelExtended project = ProjectExportSqlModelExtended.builder()
                .projectId(UUID.fromString("fdd87435-025f-48ed-a2b4-d765246040cd"))
                .name("project name")
                .geometries(List.of(ProjectsUtil.PLOT_JSON_STRING))
                .confidentiality(Confidentiality.EXTERNAL_GOVERNMENTAL)
                .projectPhase(ProjectPhase._5_PREPARATION)
                .planType(List.of(PlanType.TRANSFORMATIEGEBIED))
                .textProperties(List.of(new TextPropertyModel(customPropMap.get("text").getId(), "text_value")))
                .houseblocks(List.of(
                        HouseblockExportSqlModel.builder()
                                .name("block1")
                                .mutationAmount(4)
                                .mutationKind(MutationType.CONSTRUCTION)
                                .ownershipValueList(List.of(
                                        OwnershipValueSqlModel.builder()
                                                .ownershipAmount(4)
                                                .ownershipType(OwnershipType.HUURWONING_WONINGCORPORATIE)
                                                .ownershipRentalValue(100l)
                                                .build()))
                                .meergezinswoning(4)
                                .eengezinswoning(0)
                                .endDate(LocalDate.of(2025, 3, 1))
                                .build(),
                        HouseblockExportSqlModel.builder()
                                .name("block2")
                                .mutationAmount(2)
                                .mutationKind(MutationType.CONSTRUCTION)
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
                                .mutationAmount(1)
                                .mutationKind(MutationType.DEMOLITION)
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

        // Create the mapping from custom props to dx props
        // Map<String, DataExchangePropertyModel> dxPropertiesMap = new HashMap<>();
        // dxPropertiesMap.put("opdrachtgever_naam", DataExchangePropertyModel.builder()
        //         .customPropertyId(customPropMap.get("text").getId())
        //         .name("opdrachtgever_naam")
        //         .build());

        var template = DataExchangeTemplate.templates.get(DataExchangeType.GDB_GELDERLAND);
        Map<String, DataExchangePropertyModel> dxPropertiesMap = template.getProperties().stream()
                .map(dxProp -> {
                    DataExchangePropertyModelBuilder builder = DataExchangePropertyModel.builder()
                            .name(dxProp.getName());
                    if (dxProp.getPropertyTypes().contains(PropertyType.TEXT)) {
                        return builder.customPropertyId(customPropMap.get("text").getId())
                                .build();
                    }
                    return null;
                })
                .filter(dxProp -> dxProp != null)
                .collect(Collectors.toMap(DataExchangePropertyModel::getName, d->d));

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
                17,
                user);

        assertThat(errors).isEmpty();

        JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .build()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writerWithDefaultPrettyPrinter()
                .writeValue(new File("src/test/resources/GdbGelderlandTest/feature.actual.json"), result);
        var expected = ResourceUtil.getResourceAsString("GdbGelderlandTest/feature.expected.json");

        JSONAssert.assertEquals(expected, Json.mapper.writeValueAsString(result), JSONCompareMode.NON_EXTENSIBLE);
    }

}
