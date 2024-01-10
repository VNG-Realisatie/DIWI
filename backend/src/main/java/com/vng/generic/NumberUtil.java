package com.vng.generic;

import java.math.BigDecimal;

public class NumberUtil {

    public static BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static long zeroIfNull(Long value) {
        return value == null ? 0 : value;
    }

    public static double zeroIfNull(Double value) {
        return value == null ? 0 : value;
    }

}
