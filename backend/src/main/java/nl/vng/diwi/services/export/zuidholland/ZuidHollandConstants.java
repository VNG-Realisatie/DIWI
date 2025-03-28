package nl.vng.diwi.services.export.zuidholland;

import java.util.HashMap;
import java.util.Map;

import nl.vng.diwi.dal.entities.enums.OwnershipCategory;

public class ZuidHollandConstants {
    public static Map<OwnershipCategory, Long> priceRangeMap = new HashMap<>();
    static {
        priceRangeMap.put(OwnershipCategory.KOOP1, 0L); // lower range limit in cents
        priceRangeMap.put(OwnershipCategory.KOOP2, 28000000L);
        priceRangeMap.put(OwnershipCategory.KOOP3, 39000000L);
        priceRangeMap.put(OwnershipCategory.KOOP4, 60000000L);

        priceRangeMap.put(OwnershipCategory.HUUR1, 0L);
        priceRangeMap.put(OwnershipCategory.HUUR2, 69400L);
        priceRangeMap.put(OwnershipCategory.HUUR3, 88000L);
        priceRangeMap.put(OwnershipCategory.HUUR4, 112300L);
    }
}
