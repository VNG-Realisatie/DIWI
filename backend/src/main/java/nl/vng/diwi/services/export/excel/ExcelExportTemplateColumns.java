package nl.vng.diwi.services.export.excel;

import nl.vng.diwi.services.ExcelTableHeader;

import java.util.ArrayList;
import java.util.List;

import static nl.vng.diwi.services.ExcelTableHeader.Column;
import static nl.vng.diwi.services.ExcelTableHeader.Section;

public class ExcelExportTemplateColumns {

    public static final Integer PROJECT_CUSTOM_PROPS_COLUMNS_DEFAULT = 6;

    public List<ExcelTableHeader> templateTableHeaders = new ArrayList<>();

    {
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_ID, 1));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_NAME, 2));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_OWNER, 3));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CONFIDENTIALITY, 4));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_TYPE, 5));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PROGRAMMING, 6));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PRIORITY, 7));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_MUNICIPALITY_ROLE, 8));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_STATUS, 12));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_START_DATE, 13));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_END_DATE, 14));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_1_CONCEPT, 15));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_2_INITIATIVE, 16));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_3_DEFINITION, 17));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_4_DESIGN, 18));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_5_PREPARATION, 19));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_6_REALIZATION, 20));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PHASE_7_AFTERCARE, 21));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_4A_OPGENOMEN_IN_VISIE, 22));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_4B_NIET_OPGENOMEN_IN_VISIE, 23));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_3_IN_VOORBEREIDING, 24));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_2A_VASTGESTELD, 25));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_2B_VASTGESTELD_MET_UITWERKING_NODIG, 26));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_2C_VASTGESTELD_MET_BW_NODIG, 27));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK, 28));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG, 29));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG, 30));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_MUNICIPALITY, 31));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_DISTRICT, 32));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_NEIGHBOURHOOD, 33));

        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 34));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 35));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 36));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 37));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 38));
        templateTableHeaders.add(new ExcelTableHeader(Section.PROJECT_DATA, Column.PROJECT_CUSTOM_PROPERTY, 39));

        templateTableHeaders.add(new ExcelTableHeader(Section.HOUSE_NUMBERS, Column.HOUSEBLOCK_MUTATION_BUILD, 41));
        templateTableHeaders.add(new ExcelTableHeader(Section.HOUSE_NUMBERS, Column.HOUSEBLOCK_MUTATION_DEMOLISH, 42));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 44));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 45));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 46));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 47));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 48));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 49));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 50));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 51));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 52));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 53));

        //Grotte - 54 - 60

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_OWNER, 61));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_LANDLORD, 62));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION, 63));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_UNKNOWN, 64));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 65));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 66));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 67));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 68));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 69));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 70));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 71));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 72));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 73));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 74));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 75));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 76));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 77));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 78));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 79));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 80));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 81));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 82));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 83));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 84));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 85));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 86));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 87));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 88));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TYPE_SINGLE_FAMILY, 89));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TYPE_MULTI_FAMILY, 90));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TYPE_UNKNOWN, 91));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 92));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 93));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 94));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 95));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 96));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 97));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 98));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 99));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 100));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 101));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 102));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 103));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION, 104));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION, 105));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION, 106));

        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 107));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 108));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 109));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 110));
        templateTableHeaders.add(new ExcelTableHeader(Section.CONSTRUCTION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 111));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 113));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 114));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 115));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 116));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 117));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 118));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 119));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 120));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 121));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_DELIVERY_DATE, 122));

        //Grotte - 123 - 129

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_OWNER, 130));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_LANDLORD, 131));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION, 132));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_TYPE_UNKNOWN, 133));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 134));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 135));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 136));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY, 137));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 138));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 139));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 140));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, 141));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 142));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 143));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 144));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY, 145));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 146));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 147));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 148));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE, 149));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 150));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 151));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 152));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY, 153));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 154));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 155));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 156));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE, 157));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TYPE_SINGLE_FAMILY, 158));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TYPE_MULTI_FAMILY, 159));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TYPE_UNKNOWN, 160));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 161));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 162));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 163));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 164));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 165));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_PHYSICAL_APPEARANCE, 166));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 167));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 168));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 169));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 170));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 171));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_TARGET_GROUP, 172));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION, 173));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION, 174));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION, 175));

        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 176));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 177));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 178));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 179));
        templateTableHeaders.add(new ExcelTableHeader(Section.DEMOLITION_DATA, Column.HOUSEBLOCK_CUSTOM_PROPERTY, 180));

    }

    public int insertColumn(Column column) {
        List<ExcelTableHeader> tempHeaders = new ArrayList<>();

        int insertIndex = -1;
        boolean found = false;

        for (var header : templateTableHeaders) {
            if (!found && header.getColumn().equals(column)) {
                found = true;
                insertIndex = header.getColumnIndex() + 1;
                tempHeaders.add(new ExcelTableHeader(header.getSection(), header.getColumn(), header.getColumnIndex()));
            }

            if (found) {
                header.setColumnIndex(header.getColumnIndex() + 1);
            }
            tempHeaders.add(header);
        }

        templateTableHeaders.clear();
        templateTableHeaders.addAll(tempHeaders);

        return insertIndex;
    }
}

