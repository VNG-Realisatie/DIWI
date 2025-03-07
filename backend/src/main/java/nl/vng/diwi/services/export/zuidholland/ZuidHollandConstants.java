package nl.vng.diwi.services.export.zuidholland;

import java.util.HashMap;
import java.util.Map;

import nl.vng.diwi.services.export.OwnershipCategory;

public class ZuidHollandConstants {
    public static Map<OwnershipCategory, Long> priceRangeMap = new HashMap<>();
    static {
        priceRangeMap.put(OwnershipCategory.koop1, 0L); // lower range limit in cents
        priceRangeMap.put(OwnershipCategory.koop2, 28000000L);
        priceRangeMap.put(OwnershipCategory.koop3, 39000000L);
        priceRangeMap.put(OwnershipCategory.koop4, 60000000L);

        priceRangeMap.put(OwnershipCategory.huur1, 0L);
        priceRangeMap.put(OwnershipCategory.huur2, 69400L);
        priceRangeMap.put(OwnershipCategory.huur3, 88000L);
        priceRangeMap.put(OwnershipCategory.huur4, 112300L);
    }
}
