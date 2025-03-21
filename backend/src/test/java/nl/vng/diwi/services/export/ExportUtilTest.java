package nl.vng.diwi.services.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.PriceCategory;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.PriceCategoryPeriod;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.zuidholland.ZuidHollandConstants;

public class ExportUtilTest {
    private static Stream<Arguments> ranges() {
        return Stream.of(
                Arguments.of(OwnershipType.KOOPWONING, null, null, OwnershipCategory.KOOP_ONB),
                Arguments.of(OwnershipType.KOOPWONING, 0L, 28000000L - 1, OwnershipCategory.KOOP1),
                Arguments.of(OwnershipType.KOOPWONING, 28000000L, 39000000L - 1, OwnershipCategory.KOOP2),
                Arguments.of(OwnershipType.KOOPWONING, 39000000L, 60000000L - 1, OwnershipCategory.KOOP3),
                Arguments.of(OwnershipType.KOOPWONING, 60000000L, null, OwnershipCategory.KOOP4),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, null, null, OwnershipCategory.HUUR_ONB),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 0L, 69400L - 1, OwnershipCategory.HUUR1),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 69400L, 88000L - 1, OwnershipCategory.HUUR2),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 88000L, 112300L - 1, OwnershipCategory.HUUR3),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 112300L, null, OwnershipCategory.HUUR4),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, null, null, OwnershipCategory.HUUR_ONB),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 0L, 69400L - 1, OwnershipCategory.HUUR1),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 69400L, 88000L - 1, OwnershipCategory.HUUR2),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 88000L, 112300L - 1, OwnershipCategory.HUUR3),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 112300L, null, OwnershipCategory.HUUR4));
    }

    @ParameterizedTest
    @MethodSource("ranges")
    void getOwnershipCategoryRange(OwnershipType ownershipType, Long min, Long max, OwnershipCategory expected) throws Exception {
        var uuid = new UUID(0, 0);
        var errors = new ArrayList<DataExchangeExportError>();

        var cat = ExportUtil.getOwnershipCategory(
                uuid,
                uuid,
                ownershipType,
                min,
                max,
                ZuidHollandConstants.priceRangeMap,
                errors);

        assertThat(cat).isEqualTo(expected);
        assertThat(errors).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("ranges")
    void getOwnershipCategoryRangePcp(OwnershipType ownershipType, Long min, Long max, OwnershipCategory expected) throws Exception {

        var uuid = new UUID(0, 0);
        var errors = new ArrayList<DataExchangeExportError>();

        // Values are based on the Zuid-Holland categories, but this format uses a max value
        // for the range instead of a min so we need a -1 to compensate
        PriceCategoryPeriod pcp = PriceCategoryPeriod.builder()
                .categoriesBuy(List.of(
                        new PriceCategory(OwnershipCategory.KOOP1, 28000000L - 1),
                        new PriceCategory(OwnershipCategory.KOOP2, 39000000L - 1),
                        new PriceCategory(OwnershipCategory.KOOP3, 60000000L - 1),
                        new PriceCategory(OwnershipCategory.KOOP4, null)))

                .categoriesRent(List.of(
                        new PriceCategory(OwnershipCategory.HUUR1, 69400L - 1),
                        new PriceCategory(OwnershipCategory.HUUR2, 88000L - 1),
                        new PriceCategory(OwnershipCategory.HUUR3, 112300L - 1),
                        new PriceCategory(OwnershipCategory.HUUR4, null)))
                .build();
        var cat = ExportUtil.getOwnershipCategory(
                uuid,
                uuid,
                ownershipType,
                min,
                max,
                pcp,
                errors);

        assertThat(cat).isEqualTo(expected);
        assertThat(errors).isEmpty();
    }

}
