package nl.vng.diwi.services;

import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;

import java.util.HashMap;
import java.util.Map;

public class ExcelStrings {

    public static final Map<String, String> map = new HashMap<>();

    public static final String DELIVERY_DATE = "Opleverdatum";

    static {
        map.put("Pand Transformatie", PlanType.PAND_TRANSFORMATIE.name());
        map.put("Transformatiegebied", PlanType.TRANSFORMATIEGEBIED.name());
        map.put("Herstructurering", PlanType.HERSTRUCTURERING.name());
        map.put("Verdichting", PlanType.VERDICHTING.name());
        map.put("Uitbreiding uitleg", PlanType.UITBREIDING_UITLEG.name());
        map.put("Uitbreiding overig", PlanType.UITBREIDING_OVERIG.name());

        map.put("Nieuw", ProjectStatus.NEW.name());
        map.put("Actief", ProjectStatus.ACTIVE.name());
        map.put("Afgerond", ProjectStatus.REALIZED.name());
        map.put("Afgebroken", ProjectStatus.TERMINATED.name());
    }


}
