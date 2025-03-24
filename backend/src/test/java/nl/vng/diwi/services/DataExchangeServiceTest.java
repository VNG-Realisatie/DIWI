package nl.vng.diwi.services;

import static nl.vng.diwi.dal.entities.enums.OwnershipCategory.HUUR_ONB;
import static nl.vng.diwi.dal.entities.enums.OwnershipCategory.KOOP1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import nl.vng.diwi.dal.DataExchangeDAO;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMapping;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMappingState;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;
import nl.vng.diwi.dal.entities.PropertyRangeCategoryValue;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.models.DataExchangeModel.PriceCategories;
import nl.vng.diwi.models.DataExchangeModel.PriceCategoryMapping;

public class DataExchangeServiceTest {
    private VngRepository repo;
    private DataExchangeDAO dataExchangeDAO;
    private DataExchangeService dataExchangeService;

    @BeforeEach
    void beforeEach() throws Exception {
        repo = mock(VngRepository.class);
        dataExchangeDAO = mock(DataExchangeDAO.class);
        when(repo.getDataExchangeDAO()).thenReturn(dataExchangeDAO);

        dataExchangeService = new DataExchangeService(null);
    }

    @Test
    void createDataExchangeFromTemplate() {

        var dx = new DataExchange();
        var template = DataExchangeTemplate.builder()
                .priceCategoryMappings(List.of(KOOP1, HUUR_ONB))
                .build();

        dataExchangeService.createDataExchangeFromTemplate(repo, dx, template);

        var captor = ArgumentCaptor.forClass(DataExchangePriceCategoryMapping.class);
        verify(repo, times(2)).persist(captor.capture());

        DataExchangePriceCategoryMapping expected1 = new DataExchangePriceCategoryMapping();
        expected1.setDataExchange(dx);
        expected1.setOwnershipCategory(KOOP1);

        DataExchangePriceCategoryMapping expected2 = new DataExchangePriceCategoryMapping();
        expected2.setDataExchange(dx);
        expected2.setOwnershipCategory(HUUR_ONB);

        assertThat(captor.getAllValues())
                .usingRecursiveComparison()
                .isEqualTo(List.of(expected1, expected2));
    }

    @Test
    void getDataExchangeModel() throws Exception {
        final var dxId = UUID.fromString("0000000-0000-0000-0000-00000000000");
        final var type = DataExchangeType.GDB_GELDERLAND;

        final var buyCategoryId1 = UUID.fromString("0000000-0000-0001-0000-00000000001");
        final var buyCategoryId2 = UUID.fromString("0000000-0000-0001-0000-00000000002");

        var buyCategory1 = new PropertyRangeCategoryValue();
        buyCategory1.setId(buyCategoryId1);

        var buyCategory2 = new PropertyRangeCategoryValue();
        buyCategory2.setId(buyCategoryId2);

        final var rentCategoryId1 = UUID.fromString("0000000-0000-0002-0000-00000000001");
        final var rentCategoryId2 = UUID.fromString("0000000-0000-0002-0000-00000000002");

        var rentCategory1 = new PropertyRangeCategoryValue();
        rentCategory1.setId(rentCategoryId1);

        var rentCategory2 = new PropertyRangeCategoryValue();
        rentCategory2.setId(rentCategoryId2);

        var dataExchange = new DataExchange();
        dataExchange.setId(dxId);

        var dxState = new DataExchangeState();
        dxState.setDataExchange(dataExchange);
        dxState.setType(type);

        var buyMapping = new DataExchangePriceCategoryMapping();
        buyMapping.setOwnershipCategory(KOOP1);
        buyMapping.setMappings(List.of(
                new DataExchangePriceCategoryMappingState()
                        .withPriceRange(buyCategory1),
                new DataExchangePriceCategoryMappingState()
                        .withPriceRange(buyCategory2)));

        var rentMapping = new DataExchangePriceCategoryMapping();
        rentMapping.setOwnershipCategory(HUUR_ONB);
        rentMapping.setMappings(List.of(
                new DataExchangePriceCategoryMappingState()
                        .withPriceRange(rentCategory1),
                new DataExchangePriceCategoryMappingState()
                        .withPriceRange(rentCategory2)));

        when(dataExchangeDAO.getActiveDataExchangeStateByDataExchangeUuid(dxId)).thenReturn(dxState);
        when(dataExchangeDAO.getDataExchangePriceMappings(dxId)).thenReturn(List.of(
                buyMapping, rentMapping));

        var result = dataExchangeService.getDataExchangeModel(repo, dxId, false);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(new DataExchangeModel()
                        .withId(dxId)
                        .withType(type)
                        .withMinimumConfidentiality(Confidentiality.EXTERNAL_REGIONAL)
                        .withPriceCategories(new PriceCategories()
                                .withRent(List.of(new PriceCategoryMapping()
                                        .withName(HUUR_ONB)
                                        .withCategoryValueIds(List.of(rentCategoryId1, rentCategoryId2))))
                                .withBuy(List.of(new PriceCategoryMapping()
                                        .withName(KOOP1)
                                        .withCategoryValueIds(List.of(buyCategoryId1, buyCategoryId2))))));
    }
}
