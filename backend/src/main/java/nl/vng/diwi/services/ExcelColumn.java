package nl.vng.diwi.services;

import nl.vng.diwi.dal.entities.enums.ObjectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static nl.vng.diwi.dal.entities.enums.ObjectType.PROJECT;
import static nl.vng.diwi.dal.entities.enums.ObjectType.WONINGBLOK;

public enum ExcelColumn {

    PROJECT_ID("Identificatie nr", false, PROJECT),
    PROJECT_NAME("Naam", false, PROJECT),
    PROJECT_PLAN_TYPE("Plan soort", false, PROJECT),
    PROJECT_PROGRAMMING("In programmering", false, PROJECT),
    PROJECT_PRIORITY("Priorisering", false, PROJECT),
    PROJECT_MUNICIPALITY_ROLE("Rol gemeente", false, PROJECT),

    PROJECT_ROLE("Rol type", true, PROJECT),

    PROJECT_STATUS("Project status", false, PROJECT),
    PROJECT_START_DATE("Start project", false, PROJECT),
    PROJECT_END_DATE("Eind project", false, PROJECT),

    PROJECT_PHASE_1_CONCEPT("1 Concept", false, PROJECT),
    PROJECT_PHASE_2_INITIATIVE("2 Initiatief", false, PROJECT),
    PROJECT_PHASE_3_DEFINITION("3 Definitie", false, PROJECT),
    PROJECT_PHASE_4_DESIGN("4 Ontwerp", false, PROJECT),
    PROJECT_PHASE_5_PREPARATION("5 Voorbereiding", false, PROJECT),
    PROJECT_PHASE_6_REALIZATION("6 Realisatie", false, PROJECT),
    PROJECT_PHASE_7_AFTERCARE("7 Nazorg", false, PROJECT),

    PROJECT_PLAN_STATUS_4A_OPGENOMEN_IN_VISIE("4a Niet opgenomen in de visie", false, PROJECT),
    PROJECT_PLAN_STATUS_4B_NIET_OPGENOMEN_IN_VISIE("4b Opgenomen in de visie", false, PROJECT),
    PROJECT_PLAN_STATUS_3_IN_VOORBEREIDING("3 In voorbereiding", false, PROJECT),
    PROJECT_PLAN_STATUS_2A_VASTGESTELD("2a Vastgesteld", false, PROJECT),
    PROJECT_PLAN_STATUS_2B_VASTGESTELD_MET_UITWERKING_NODIG("2b Vastgesteld uitwerking nodig", false, PROJECT),
    PROJECT_PLAN_STATUS_2C_VASTGESTELD_MET_BW_NODIG("2c Vastgesteld B&W nodig", false, PROJECT),
    PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK("1a Onherroepelijk", false, PROJECT),
    PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG("1b Onherroepelijk uitwerking nodig", false, PROJECT),
    PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG("1c Onherroepelijk B&W nodig", false, PROJECT),

    PROJECT_MUNICIPALITY("Gemeente", false, PROJECT),
    PROJECT_DISTRICT("Wijk", false, PROJECT),
    PROJECT_NEIGHBOURHOOD("Buurt", false, PROJECT),

    PROJECT_CUSTOM_PROPERTY("Maatwerk project eigenschap", true, PROJECT),

    HOUSEBLOCK_MUTATION_BUILD("Bouw", false, WONINGBLOK),
    HOUSEBLOCK_MUTATION_DEMOLISH("Sloop", false, WONINGBLOK),

    HOUSEBLOCK_DELIVERY_DATE("Opleverdatum", true, WONINGBLOK),

    //Grootte - ignore section for now
    HOUSEBLOCK_PROPERTY_TYPE_OWNER("Koopwoning", false, WONINGBLOK),
    HOUSEBLOCK_PROPERTY_TYPE_LANDLORD("Particuliere verhuurder", false, WONINGBLOK),
    HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION("Huur woningcorporatie", false, WONINGBLOK),

    HOUSEBLOCK_PROPERTY_PURCHASE_PRICE("Koopprijs categorie", true, WONINGBLOK),
    HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE("Huurprijs particulier", true, WONINGBLOK),
    HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE("Huurprijs woningcorporatie", true, WONINGBLOK),

    HOUSEBLOCK_TYPE_SINGLE_FAMILY("Eengezins woning", false, WONINGBLOK),
    HOUSEBLOCK_TYPE_MULTI_FAMILY("Meergezins woning", false, WONINGBLOK),
    HOUSEBLOCK_PHYSICAL_APPEARANCE("Fysiek voorkomen", true, WONINGBLOK),
    HOUSEBLOCK_TARGET_GROUP("Doelgroep categorie", true, WONINGBLOK),

    HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION("Geen toestemming eigenaar", false, WONINGBLOK),
    HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION("Intentie medewerking eigenaar", false, WONINGBLOK),
    HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION("Formele toestemming eigenaar", false, WONINGBLOK),

    HOUSEBLOCK_CUSTOM_PROPERTY("Maatwerk woning eigenschap", true, WONINGBLOK);

    private static final Map<String, ExcelColumn> map = new HashMap<>();

    static {
        Arrays.stream(ExcelColumn.values()).forEach(v -> map.put(v.name, v));
    }

    public final String name;
    public final boolean hasSubheader;
    public final ObjectType type;

    ExcelColumn(String name, boolean hasSubheader, ObjectType objectType) {
        this.name = name;
        this.hasSubheader = hasSubheader;
        this.type = objectType;
    }

    public static ExcelColumn findByName(String name) {
        return map.get(name);
    }

}
