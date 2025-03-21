package nl.vng.diwi.services;

import static nl.vng.diwi.dal.entities.enums.OwnershipCategory.HUUR_ONB;
import static nl.vng.diwi.dal.entities.enums.OwnershipCategory.KOOP1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.DataExchange;
import nl.vng.diwi.dal.entities.DataExchangePriceCategoryMapping;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;

public class DataExchangeServiceTest {
    @Test
    void createDataExchangeFromTemplate() {
        var repo = mock(VngRepository.class);
        var dx = new DataExchange();
        var template = DataExchangeTemplate.builder()
                .priceCategoryMappings(List.of(KOOP1, HUUR_ONB))
                .build();

        new DataExchangeService(null)
                .createDataExchangeFromTemplate(repo, dx, template);

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
}
