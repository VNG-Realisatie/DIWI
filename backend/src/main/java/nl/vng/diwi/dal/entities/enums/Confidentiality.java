package nl.vng.diwi.dal.entities.enums;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public enum Confidentiality {
    PRIVATE,
    INTERNAL_CIVIL,
    INTERNAL_MANAGEMENT,
    INTERNAL_COUNCIL,
    EXTERNAL_REGIONAL,
    EXTERNAL_GOVERNMENTAL,
    PUBLIC;

    public static final Map<Confidentiality, Integer> confidentialityMap = ImmutableMap.<Confidentiality, Integer>builder()
        .put(PRIVATE, 1)
        .put(INTERNAL_CIVIL, 2)
        .put(INTERNAL_MANAGEMENT, 3)
        .put(INTERNAL_COUNCIL, 4)
        .put(EXTERNAL_REGIONAL, 5)
        .put(EXTERNAL_GOVERNMENTAL, 6)
        .put(PUBLIC, 7)
        .build();
}
