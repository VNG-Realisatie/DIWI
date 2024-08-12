package nl.vng.diwi.services;

import lombok.Data;
import nl.vng.diwi.models.PropertyModel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
public class ExcelTableHeader {

    private Section section;
    private Column column;
    private String subheader;

    private PropertyModel propertyModel;
    private LocalDate subheaderDateValue;

    public enum Section {
        PROJECT_DATA("Projectgegevens"),
        HOUSE_NUMBERS("Woning aantallen"),
        CONSTRUCTION_DATA("Bouw gegevens"),
        DEMOLITION_DATA("Sloop gegevens"),
        CONTROL_CONSTRUCTION("Controle Bouw totalen"),
        CONTROL_DEMOLITION("Controle Sloop totalen");

        private static final Map<String, Section> map = new HashMap<>();

        static {
            Arrays.stream(Section.values()).forEach(v -> map.put(v.name, v));
        }

        public final String name;

        Section(String name) {
            this.name = name;
        }

        public static Section findByName(String name) {
            return map.get(name);
        }
    }

    public enum Column {

        PROJECT_ID("Identificatie nr", false),
        PROJECT_NAME("Naam", false),
        PROJECT_PLAN_TYPE("Plan soort", false),
        PROJECT_PROGRAMMING("In programmering", false),
        PROJECT_PRIORITY("Priorisering", false),
        PROJECT_MUNICIPALITY_ROLE("Rol gemeente", false),
        PROJECT_ROLE("Rol type", true),
        PROJECT_STATUS("Project status", false),
        PROJECT_START_DATE("Start project", false),
        PROJECT_END_DATE("Eind project", false),
        PROJECT_CONFIDENTIALITY("Vertrouwelijkheid", false),
        PROJECT_OWNER("Eigenaar", false),
        PROJECT_PHASE_1_CONCEPT("1 Concept", false),
        PROJECT_PHASE_2_INITIATIVE("2 Initiatief", false),
        PROJECT_PHASE_3_DEFINITION("3 Definitie", false),
        PROJECT_PHASE_4_DESIGN("4 Ontwerp", false),
        PROJECT_PHASE_5_PREPARATION("5 Voorbereiding", false),
        PROJECT_PHASE_6_REALIZATION("6 Realisatie", false),
        PROJECT_PHASE_7_AFTERCARE("7 Nazorg", false),
        PROJECT_PLAN_STATUS_4A_OPGENOMEN_IN_VISIE("4a Opgenomen in de visie", false),
        PROJECT_PLAN_STATUS_4B_NIET_OPGENOMEN_IN_VISIE("4b Niet opgenomen in de visie", false),
        PROJECT_PLAN_STATUS_3_IN_VOORBEREIDING("3 In voorbereiding", false),
        PROJECT_PLAN_STATUS_2A_VASTGESTELD("2a Vastgesteld", false),
        PROJECT_PLAN_STATUS_2B_VASTGESTELD_MET_UITWERKING_NODIG("2b Vastgesteld uitwerking nodig", false),
        PROJECT_PLAN_STATUS_2C_VASTGESTELD_MET_BW_NODIG("2c Vastgesteld B&W nodig", false),
        PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK("1a Onherroepelijk", false),
        PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG("1b Onherroepelijk uitwerking nodig", false),
        PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG("1c Onherroepelijk B&W nodig", false),
        PROJECT_MUNICIPALITY("Gemeente", false),
        PROJECT_DISTRICT("Wijk", false),
        PROJECT_NEIGHBOURHOOD("Buurt", false),
        PROJECT_CUSTOM_PROPERTY("Maatwerk project eigenschap", true),
        HOUSEBLOCK_MUTATION_BUILD("Bouw", false),
        HOUSEBLOCK_MUTATION_DEMOLISH("Sloop", false),
        HOUSEBLOCK_DELIVERY_DATE("Opleverdatum", true),
        //Grootte - ignore section for now
        HOUSEBLOCK_PROPERTY_TYPE_OWNER("Koopwoning", false),
        HOUSEBLOCK_PROPERTY_TYPE_LANDLORD("Particuliere verhuurder", false),
        HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION("Huur woningcorporatie", false),
        HOUSEBLOCK_PROPERTY_TYPE_UNKNOWN("Eigendom type onbekend",false),
        HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY("Koopprijs categorie", true),
        HOUSEBLOCK_PROPERTY_PURCHASE_PRICE("Koopprijs waarde", true),
        HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY("Huurprijs particuliere verhuurder categorie", true),
        HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE("Huurprijs particuliere verhuurder", true),
        HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY("Huurprijs woningcorporatie categorie", true),
        HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE("Huurprijs woningcorporatie", true),
        HOUSEBLOCK_TYPE_SINGLE_FAMILY("Eengezins woning", false),
        HOUSEBLOCK_TYPE_MULTI_FAMILY("Meergezins woning", false),
        HOUSEBLOCK_TYPE_UNKNOWN("Woning type onbekend", false),
        HOUSEBLOCK_PHYSICAL_APPEARANCE("Fysiek voorkomen", true),
        HOUSEBLOCK_TARGET_GROUP("Doelgroep categorie", true),
        HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION("Geen toestemming eigenaar", false),
        HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION("Intentie medewerking eigenaar", false),
        HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION("Formele toestemming eigenaar", false),
        HOUSEBLOCK_CUSTOM_PROPERTY("Maatwerk woning eigenschap", true);

        private static final Map<String, Column> map = new HashMap<>();

        static {
            Arrays.stream(Column.values()).forEach(v -> map.put(v.name, v));
        }

        public final String name;
        public final boolean hasSubheader;


        Column(String name, boolean hasSubheader) {
            this.name = name;
            this.hasSubheader = hasSubheader;
        }

        public static Column findByName(String name) {
            return map.get(name);
        }

    }

}
