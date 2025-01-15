package nl.vng.diwi.services.export.excel;

import nl.vng.diwi.services.ExcelTableHeader;

import java.util.ArrayList;
import java.util.List;

import static nl.vng.diwi.services.ExcelTableHeader.Column;
import static nl.vng.diwi.services.ExcelTableHeader.Section;

public class ExcelExportTemplateColumns {

    public static final Integer PROJECT_CUSTOM_PROPS_COLUMNS_DEFAULT = 6;
    public static final Integer HOUSEBLOCK_DELIVERY_DATES_COLUMNS_DEFAULT = 10;
    public static final Integer HOUSEBLOCK_TARGET_GROUP_COLUMNS_DEFAULT = 6;
    public static final Integer HOUSEBLOCK_PHYSICAL_APPEARANCE_COLUMNS_DEFAULT = 6;

    public List<ExcelTableHeader> templateTableHeaders = new ArrayList<>();

    {
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_ID, 1, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_NAME, 2, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_OWNER, 3, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CONFIDENTIALITY, 4, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_TYPE, 5, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PROGRAMMING, 6, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PRIORITY, 7, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_MUNICIPALITY_ROLE, 8, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_STATUS, 12, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_START_DATE, 13, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_END_DATE, 14, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_1_CONCEPT, 15, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_2_INITIATIVE, 16, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_3_DEFINITION, 17, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_4_DESIGN, 18, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_5_PREPARATION, 19, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_6_REALIZATION, 20, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_7_AFTERCARE, 21, ExcelTableHeader.Border.NONE));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_4A_OPGENOMEN_IN_VISIE, 22, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_4B_NIET_OPGENOMEN_IN_VISIE, 23, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_3_IN_VOORBEREIDING, 24, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_2A_VASTGESTELD, 25, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_2B_VASTGESTELD_MET_UITWERKING_NODIG, 26, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_2C_VASTGESTELD_MET_BW_NODIG, 27, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK, 28, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG, 29, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG, 30, ExcelTableHeader.Border.NONE));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_MUNICIPALITY, 31, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_DISTRICT, 32, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_NEIGHBOURHOOD, 33, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 34, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 35, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 36, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 37, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 38, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 39, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.HOUSE_NUMBERS, Column.HOUSEBLOCK_MUTATION_BUILD, 41, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.HOUSE_NUMBERS, Column.HOUSEBLOCK_MUTATION_DEMOLISH, 42, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 44, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 45, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 46, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 47, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 48, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 49, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 50, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 51, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 52, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 53, ExcelTableHeader.Border.RIGHT));

        //Grotte - 54 - 60

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_OWNER, 61, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_LANDLORD, 62, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION, 63, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_UNKNOWN, 64, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 65, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 66, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 67, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 68, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 69, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 70, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 71, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 72, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 73, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 74, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 75, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 76, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 77, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 78, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 79, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 80, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 81, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 82, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 83, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 84, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 85, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 86, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 87, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 88, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TYPE_SINGLE_FAMILY, 89, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TYPE_MULTI_FAMILY, 90, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TYPE_UNKNOWN, 91, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 92, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 93, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 94, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 95, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 96, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 97, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 98, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 99, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 100, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 101, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 102, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 103, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION, 104, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION, 105, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION, 106, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 107, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 108, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 109, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 110, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 111, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 113, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 114, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 115, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 116, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 117, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 118, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 119, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 120, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 121, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 122, ExcelTableHeader.Border.RIGHT));

        //Grotte - 123 - 129

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_OWNER, 130, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_LANDLORD, 131, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION, 132, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_UNKNOWN, 133, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 134, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 135, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 136, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 137, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 138, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 139, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 140, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 141, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 142, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 143, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 144, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 145, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 146, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 147, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 148, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 149, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 150, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 151, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 152, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 153, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 154, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 155, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 156, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 157, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TYPE_SINGLE_FAMILY, 158, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TYPE_MULTI_FAMILY, 159, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TYPE_UNKNOWN, 160, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 161, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 162, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 163, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 164, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 165, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 166, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 167, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 168, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 169, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 170, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 171, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 172, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION, 173, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION, 174, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION, 175, ExcelTableHeader.Border.RIGHT));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 176, ExcelTableHeader.Border.LEFT));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 177, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 178, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 179, ExcelTableHeader.Border.NONE));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 180, ExcelTableHeader.Border.RIGHT));

    }

    public int insertColumn(Section section, Column column) {
        List<ExcelTableHeader> tempHeaders = new ArrayList<>();

        int insertIndex = -1;
        boolean found = false;

        for (var header : templateTableHeaders) {
            if (!found && header.getColumn().equals(column) && header.getSection().equals(section)) {
                found = true;
                insertIndex = header.getColumnIndex() + 1;
                tempHeaders.add(header);
                tempHeaders.add(new ExcelTableHeader(header.getSection(), header.getColumn(), insertIndex, ExcelTableHeader.Border.NONE));
            } else if (found) {
                header.setColumnIndex(header.getColumnIndex() + 1);
                tempHeaders.add(header);
            } else {
                tempHeaders.add(header);
            }
        }

        templateTableHeaders.clear();
        templateTableHeaders.addAll(tempHeaders);

        return insertIndex;
    }
}

