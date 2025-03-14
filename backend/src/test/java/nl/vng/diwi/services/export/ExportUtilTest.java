package nl.vng.diwi.services.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.PriceCategory;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.PriceCategoryPeriod;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.zuidholland.ZuidHollandConstants;

public class ExportUtilTest {
    private static Stream<Arguments> ranges() {
        return Stream.of(
                Arguments.of(OwnershipType.KOOPWONING, null, null, OwnershipCategory.koop_onb),
                Arguments.of(OwnershipType.KOOPWONING, 0L, 28000000L - 1, OwnershipCategory.koop1),
                Arguments.of(OwnershipType.KOOPWONING, 28000000L, 39000000L - 1, OwnershipCategory.koop2),
                Arguments.of(OwnershipType.KOOPWONING, 39000000L, 60000000L - 1, OwnershipCategory.koop3),
                Arguments.of(OwnershipType.KOOPWONING, 60000000L, null, OwnershipCategory.koop4),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, null, null, OwnershipCategory.huur_onb),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 0L, 69400L - 1, OwnershipCategory.huur1),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 69400L, 88000L - 1, OwnershipCategory.huur2),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 88000L, 112300L - 1, OwnershipCategory.huur3),
                Arguments.of(OwnershipType.HUURWONING_WONINGCORPORATIE, 112300L, null, OwnershipCategory.huur4),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, null, null, OwnershipCategory.huur_onb),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 0L, 69400L - 1, OwnershipCategory.huur1),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 69400L, 88000L - 1, OwnershipCategory.huur2),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 88000L, 112300L - 1, OwnershipCategory.huur3),
                Arguments.of(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER, 112300L, null, OwnershipCategory.huur4));
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
                        new PriceCategory(OwnershipCategory.koop1, 28000000L - 1),
                        new PriceCategory(OwnershipCategory.koop2, 39000000L - 1),
                        new PriceCategory(OwnershipCategory.koop3, 60000000L - 1),
                        new PriceCategory(OwnershipCategory.koop4, null)))

                .categoriesRent(List.of(
                        new PriceCategory(OwnershipCategory.huur1, 69400L - 1),
                        new PriceCategory(OwnershipCategory.huur2, 88000L - 1),
                        new PriceCategory(OwnershipCategory.huur3, 112300L - 1),
                        new PriceCategory(OwnershipCategory.huur4, null)))
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
