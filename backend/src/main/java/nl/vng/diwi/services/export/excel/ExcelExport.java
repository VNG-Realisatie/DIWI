package nl.vng.diwi.services.export.excel;

import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.models.SelectDisabledModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.services.ExcelStrings;
import nl.vng.diwi.services.ExcelTableHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ExcelExport {

    private static final Logger logger = LogManager.getLogger();

    public static String EXCEL_TEMPLATE_NAME  = "Excel_export_template.xlsx";
    public static String EXCEL_TEMPLATE_PATH = "templates/" + EXCEL_TEMPLATE_NAME;

    public static String PROJECT_SHEET_NAME = "Data";
    public static BigDecimal HOUSING_PRICE_DIVIDE_FACTOR = BigDecimal.valueOf(100L);
    public static String UNKNOWN = "Onbekend";

    public static StreamingOutput buildExportObject(
        List<ProjectExportSqlModelExtended> projects,
        List<PropertyModel> customProps) {

        try {
            File excelFile = new File(ExcelExport.class.getClassLoader().getResource(EXCEL_TEMPLATE_PATH).getPath());

            FileInputStream inputStream = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheet(PROJECT_SHEET_NAME);

            ExcelExportCellStyles styles = new ExcelExportCellStyles(workbook);

            ExcelExportTemplateColumns header = new ExcelExportTemplateColumns();
            addExcelColumnsAndAssignHeaderProperties(projects, customProps, header, sheet, styles);

            int rowCount = 5;
            for (var project : projects) {
                if (project.getHouseblocks() == null || project.getHouseblocks().isEmpty()) {
                    Row row = sheet.getRow(rowCount);
                    if (row == null) {
                        row = sheet.createRow(rowCount);
                    }
                    createProjectCells(header, row, project, styles);
                    rowCount++;
                } else {
                    for (var houseblock : project.getHouseblocks()) {
                        Row row = sheet.getRow(rowCount);
                        if (row == null) {
                            row = sheet.createRow(rowCount);
                        }
                        createProjectCells(header, row, project, styles);
                        createHouseblockCells(header, row, houseblock, styles);
                        rowCount++;
                    }
                }
            }

            inputStream.close();

            StreamingOutput stream = output -> {
                try {
                    workbook.write(output);
                    workbook.close();
                } catch (IOException e) {
                    logger.error("Excel download error while writing workbook to StreamingOutput", e);
                    throw e;
                }
            };
            return stream;

        } catch (Exception ex) {
            logger.error("Excel download Exception", ex.getCause());
            logger.error(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }

    private static void addExcelColumnsAndAssignHeaderProperties(List<ProjectExportSqlModelExtended> projects, List<PropertyModel> customProps,
                                                                 ExcelExportTemplateColumns header, Sheet sheet, ExcelExportCellStyles styles) {

        Set<UUID> projectCustomPropIds = new HashSet<>();
        Set<LocalDate> hhDeliveryDatesSet = new HashSet<>();
        Set<UUID> hbCustomPropIds = new HashSet<>();
        Set<SingleValueOrRangeModel<BigDecimal>> hbSizes = new HashSet<>();
        Set<SingleValueOrRangeModel<BigDecimal>> hbPurchasePrices = new HashSet<>();
        Set<SingleValueOrRangeModel<BigDecimal>> hbLandlordRentPrices = new HashSet<>();
        Set<SingleValueOrRangeModel<BigDecimal>> hbHousingRentPrices = new HashSet<>();
        projects.forEach(p -> {
            p.getBooleanProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
            p.getTextProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
            p.getCategoryProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
            p.getNumericProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
            p.getOrdinalProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
            p.getHouseblocks().forEach(h -> {
                hhDeliveryDatesSet.add(h.getEndDate());
                h.getBooleanProperties().forEach(prop -> hbCustomPropIds.add(prop.getPropertyId()));
                h.getTextProperties().forEach(prop -> hbCustomPropIds.add(prop.getPropertyId()));
                h.getCategoryProperties().forEach(prop -> hbCustomPropIds.add(prop.getPropertyId()));
                h.getNumericProperties().forEach(prop -> hbCustomPropIds.add(prop.getPropertyId()));
                h.getOrdinalProperties().forEach(prop -> hbCustomPropIds.add(prop.getPropertyId()));
                if (h.getSize() != null && (h.getSize().getValue() != null || h.getSize().getMin() != null)) {
                    hbSizes.add(h.getSize());
                }
                h.getOwnershipValueList().forEach(o -> {
                    if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
                        hbPurchasePrices.add(o.getSingleValueOrRangeValue(false, HOUSING_PRICE_DIVIDE_FACTOR));
                    }
                    if (o.getOwnershipType() == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER) {
                        hbLandlordRentPrices.add(o.getSingleValueOrRangeValue(true, HOUSING_PRICE_DIVIDE_FACTOR));
                    }
                    if (o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE) {
                        hbHousingRentPrices.add(o.getSingleValueOrRangeValue(true, HOUSING_PRICE_DIVIDE_FACTOR));
                    }
                });
            });
        });

        List<PropertyModel> projectCustomProps = customProps.stream().filter(cp -> cp.getType() == PropertyKind.CUSTOM && projectCustomPropIds.contains(cp.getId()))
            .sorted(Comparator.comparing(PropertyModel::getName)).toList();
        List<PropertyModel> hbCustomProps = customProps.stream().filter(cp -> cp.getType() == PropertyKind.CUSTOM && hbCustomPropIds.contains(cp.getId()))
            .sorted(Comparator.comparing(PropertyModel::getName)).toList();
        List<LocalDate> hbDeliveryDates = hhDeliveryDatesSet.stream().sorted().toList();
        List<SingleValueOrRangeModel<BigDecimal>> hbSizesList = hbSizes.stream().sorted().toList();
        List<SingleValueOrRangeModel<BigDecimal>> hbPurchasePricesList = hbPurchasePrices.stream().filter(Objects::nonNull).sorted().toList();
        List<SingleValueOrRangeModel<BigDecimal>> hbLandlordRentPricesList = hbLandlordRentPrices.stream().filter(Objects::nonNull).sorted().toList();
        List<SingleValueOrRangeModel<BigDecimal>> hbHousingRentPricesList = hbHousingRentPrices.stream().filter(Objects::nonNull).sorted().toList();

        PropertyModel targetGroupProp = customProps.stream().filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_TARGET_GROUP))
            .findFirst().orElse(null);
        List<SelectDisabledModel> targetGroupPropOptions = new ArrayList<>();
        if (targetGroupProp.getCategories() != null) {
            targetGroupPropOptions.addAll(targetGroupProp.getCategories().stream().filter(c -> c.getDisabled() == Boolean.FALSE)
                .sorted(Comparator.comparing(SelectDisabledModel::getName)).toList());
        }

        PropertyModel physicalAppearanceProp = customProps.stream().filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_PHYSICAL_APPEARANCE))
            .findFirst().orElse(null);
        List<SelectDisabledModel> physicalAppPropOptions = new ArrayList<>();
        if (physicalAppearanceProp.getCategories() != null) {
            physicalAppPropOptions.addAll(physicalAppearanceProp.getCategories().stream().filter(c -> c.getDisabled() == Boolean.FALSE)
                .sorted(Comparator.comparing(SelectDisabledModel::getName)).toList());
        }

        PropertyModel priceRangeBuy = customProps.stream().filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_BUY))
            .findFirst().orElse(null);
        List<RangeSelectDisabledModel> priceRangeBuyOptions = new ArrayList<>();
        if (priceRangeBuy.getRanges() != null) {
            priceRangeBuyOptions.addAll(priceRangeBuy.getRanges().stream().filter(o -> o.getDisabled() == Boolean.FALSE)
                .sorted(Comparator.comparing(RangeSelectDisabledModel::getMin)).toList());
        }

        PropertyModel priceRangeRent = customProps.stream().filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_PRICE_RANGE_RENT))
            .findFirst().orElse(null);
        List<RangeSelectDisabledModel> priceRangeRentOptions = new ArrayList<>();
        if (priceRangeRent.getRanges() != null) {
            priceRangeRentOptions.addAll(priceRangeRent.getRanges().stream().filter(o -> o.getDisabled() == Boolean.FALSE)
                .sorted(Comparator.comparing(RangeSelectDisabledModel::getMin)).toList());
        }

        int newColumnIndex;
        for (int i = ExcelExportTemplateColumns.PROJECT_CUSTOM_PROPS_COLUMNS_DEFAULT; i < projectCustomProps.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.PROJECT_DATA, ExcelTableHeader.Column.PROJECT_CUSTOM_PROPERTY);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_DELIVERY_DATES_COLUMNS_DEFAULT; i < hbDeliveryDates.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_DELIVERY_DATE);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_DELIVERY_DATE);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_PRICE_RANGE_COLUMNS_DEFAULT - 1; i < priceRangeBuyOptions.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_PRICE_RANGE_COLUMNS_DEFAULT - 1; i < priceRangeRentOptions.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_PRICE_VALUES_COLUMNS_DEFAULT - 1; i < hbPurchasePricesList.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_PURCHASE_PRICE);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_PRICE_VALUES_COLUMNS_DEFAULT - 1; i < hbLandlordRentPricesList.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_PRICE_VALUES_COLUMNS_DEFAULT - 1; i < hbHousingRentPricesList.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_TARGET_GROUP_COLUMNS_DEFAULT; i < targetGroupPropOptions.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_TARGET_GROUP);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_TARGET_GROUP);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_PHYSICAL_APPEARANCE_COLUMNS_DEFAULT; i < physicalAppPropOptions.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PHYSICAL_APPEARANCE);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_PHYSICAL_APPEARANCE);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_CUSTOM_PROPS_COLUMNS_DEFAULT; i < hbCustomProps.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_CUSTOM_PROPERTY);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_CUSTOM_PROPERTY);
            insertNewColumnBefore(sheet, newColumnIndex);
        }
        for (int i = ExcelExportTemplateColumns.HOUSEBLOCK_SIZE_COLUMNS_DEFAULT - 1; i < hbSizesList.size(); i++) {
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.CONSTRUCTION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_SIZE);
            insertNewColumnBefore(sheet, newColumnIndex);
            newColumnIndex = header.insertColumn(ExcelTableHeader.Section.DEMOLITION_DATA, ExcelTableHeader.Column.HOUSEBLOCK_SIZE);
            insertNewColumnBefore(sheet, newColumnIndex);
        }

        Row suhheaderRow = sheet.getRow(4);
        int projectCpCount = 0;
        int hbConstructionDDCount = 0;
        int hbDemolitionDDCount = 0;
        int hbConstructionTargetGroupCount = 0;
        int hbDemolitionTargetGroupCount = 0;
        int hbConstructionPhysicalAppCount = 0;
        int hbDemolitionPhysicalAppCount = 0;
        int hbConstructionCustomPropsCount = 0;
        int hbDemolitionCustomPropsCount = 0;
        int hbConstructionPurchasePriceRangeCount = 0;
        int hbDemolitionPurchasePriceRangeCount = 0;
        int hbConstructionLandlordPriceRangeCount = 0;
        int hbDemolitionLandlordPriceRangeCount = 0;
        int hbConstructionHousingAssocPriceRangeCount = 0;
        int hbDemolitionHousingAssocPriceRangeCount = 0;
        int hbConstructionSizeCount = 0;
        int hbDemolitionSizeCount = 0;
        int hbConstructionPurchasePriceCount = 0;
        int hbDemolitionPurchasePriceCount = 0;
        int hbConstructionLandlordPriceCount = 0;
        int hbDemolitionLandlordPriceCount = 0;
        int hbConstructionHousingAssocPriceCount = 0;
        int hbDemolitionHousingAssocPriceCount = 0;

        for (var h : header.templateTableHeaders) {
            switch (h.getColumn()) {
                case PROJECT_CUSTOM_PROPERTY -> {
                    if (projectCpCount < projectCustomProps.size()) {
                        h.setPropertyModel(projectCustomProps.get(projectCpCount));
                        h.setSubheader(h.getPropertyModel().getName());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        projectCpCount++;
                    }
                }
                case PROJECT_MUNICIPALITY ->
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY))
                        .findFirst().orElse(null));

                case PROJECT_DISTRICT ->
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_DISTRICT))
                        .findFirst().orElse(null));
                case PROJECT_NEIGHBOURHOOD ->
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_NEIGHBOURHOOD))
                        .findFirst().orElse(null));
                case PROJECT_MUNICIPALITY_ROLE ->
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY_ROLE))
                        .findFirst().orElse(null));
                case PROJECT_PRIORITY ->
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_PRIORITY))
                        .findFirst().orElse(null));

                case HOUSEBLOCK_DELIVERY_DATE -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionDDCount < hbDeliveryDates.size()) {
                        h.setSubheaderDateValue(hbDeliveryDates.get(hbConstructionDDCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheaderDateValue(), styles, null);
                        hbConstructionDDCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionDDCount < hbDeliveryDates.size()) {
                        h.setSubheaderDateValue(hbDeliveryDates.get(hbDemolitionDDCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheaderDateValue(), styles, null);
                        hbDemolitionDDCount++;
                    }
                }
                case HOUSEBLOCK_SIZE -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionSizeCount < hbSizesList.size()) {
                        h.setSubheaderRangeValue(hbSizesList.get(hbConstructionSizeCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbConstructionSizeCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionSizeCount < hbSizesList.size()) {
                        h.setSubheaderRangeValue(hbSizesList.get(hbDemolitionSizeCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbDemolitionSizeCount++;
                    } else if (UNKNOWN.equalsIgnoreCase(getCellStringValue(suhheaderRow.getCell(h.getColumnIndex())))) {
                        h.setSubheader(UNKNOWN);
                    }
                }
                case HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionPurchasePriceRangeCount < priceRangeBuyOptions.size()) {
                        RangeSelectDisabledModel rsm = priceRangeBuyOptions.get(hbConstructionPurchasePriceRangeCount);
                        h.setSubheaderUuid(rsm.getId());
                        h.setSubheader(getRangeSubheaderName(rsm));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbConstructionPurchasePriceRangeCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionPurchasePriceRangeCount < priceRangeBuyOptions.size()) {
                        RangeSelectDisabledModel rsm = priceRangeBuyOptions.get(hbDemolitionPurchasePriceRangeCount);
                        h.setSubheaderUuid(rsm.getId());
                        h.setSubheader(getRangeSubheaderName(rsm));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbDemolitionPurchasePriceRangeCount++;
                    } else if (UNKNOWN.equalsIgnoreCase(getCellStringValue(suhheaderRow.getCell(h.getColumnIndex())))) {
                        h.setSubheader(UNKNOWN);
                    }
                }
                case HOUSEBLOCK_PROPERTY_PURCHASE_PRICE -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionPurchasePriceCount < hbPurchasePricesList.size()) {
                        h.setSubheaderRangeValue(hbPurchasePricesList.get(hbConstructionPurchasePriceCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbConstructionPurchasePriceCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionPurchasePriceCount < hbPurchasePricesList.size()) {
                        h.setSubheaderRangeValue(hbPurchasePricesList.get(hbDemolitionPurchasePriceCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbDemolitionPurchasePriceCount++;
                    }
                }

                case HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionLandlordPriceRangeCount < priceRangeRentOptions.size()) {
                        RangeSelectDisabledModel rsm = priceRangeRentOptions.get(hbConstructionLandlordPriceRangeCount);
                        h.setSubheaderUuid(rsm.getId());
                        h.setSubheader(getRangeSubheaderName(rsm));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbConstructionLandlordPriceRangeCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionLandlordPriceRangeCount < priceRangeRentOptions.size()) {
                        RangeSelectDisabledModel rsm = priceRangeRentOptions.get(hbDemolitionLandlordPriceRangeCount);
                        h.setSubheaderUuid(rsm.getId());
                        h.setSubheader(getRangeSubheaderName(rsm));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbDemolitionLandlordPriceRangeCount++;
                    } else if (UNKNOWN.equalsIgnoreCase(getCellStringValue(suhheaderRow.getCell(h.getColumnIndex())))) {
                        h.setSubheader(UNKNOWN);
                    }
                }
                case HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionLandlordPriceCount < hbLandlordRentPricesList.size()) {
                        h.setSubheaderRangeValue(hbLandlordRentPricesList.get(hbConstructionLandlordPriceCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbConstructionLandlordPriceCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionLandlordPriceCount < hbLandlordRentPricesList.size()) {
                        h.setSubheaderRangeValue(hbLandlordRentPricesList.get(hbDemolitionLandlordPriceCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbDemolitionLandlordPriceCount++;
                    }
                }

                case HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionHousingAssocPriceRangeCount < priceRangeRentOptions.size()) {
                        RangeSelectDisabledModel rsm = priceRangeRentOptions.get(hbConstructionHousingAssocPriceRangeCount);
                        h.setSubheaderUuid(rsm.getId());
                        h.setSubheader(getRangeSubheaderName(rsm));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbConstructionHousingAssocPriceRangeCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionHousingAssocPriceRangeCount < priceRangeRentOptions.size()) {
                        RangeSelectDisabledModel rsm = priceRangeRentOptions.get(hbDemolitionHousingAssocPriceRangeCount);
                        h.setSubheaderUuid(rsm.getId());
                        h.setSubheader(getRangeSubheaderName(rsm));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbDemolitionHousingAssocPriceRangeCount++;
                    } else if (UNKNOWN.equalsIgnoreCase(getCellStringValue(suhheaderRow.getCell(h.getColumnIndex())))) {
                        h.setSubheader(UNKNOWN);
                    }
                }
                case HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionHousingAssocPriceCount < hbHousingRentPricesList.size()) {
                        h.setSubheaderRangeValue(hbHousingRentPricesList.get(hbConstructionHousingAssocPriceCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbConstructionHousingAssocPriceCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionHousingAssocPriceCount < hbHousingRentPricesList.size()) {
                        h.setSubheaderRangeValue(hbHousingRentPricesList.get(hbDemolitionHousingAssocPriceCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), getSingleValueOrRangeSubheaderName(h.getSubheaderRangeValue()), styles, null);
                        hbDemolitionHousingAssocPriceCount++;
                    }
                }

                case HOUSEBLOCK_TARGET_GROUP -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionTargetGroupCount < targetGroupPropOptions.size()) {
                        h.setSubheaderUuid(targetGroupPropOptions.get(hbConstructionTargetGroupCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), targetGroupPropOptions.get(hbConstructionTargetGroupCount).getName(), styles, null);
                        hbConstructionTargetGroupCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionTargetGroupCount < targetGroupPropOptions.size()) {
                        h.setSubheaderUuid(targetGroupPropOptions.get(hbDemolitionTargetGroupCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), targetGroupPropOptions.get(hbDemolitionTargetGroupCount).getName(), styles, null);
                        hbDemolitionTargetGroupCount++;
                    }
                }
                case HOUSEBLOCK_PHYSICAL_APPEARANCE -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionPhysicalAppCount < physicalAppPropOptions.size()) {
                        h.setSubheaderUuid(physicalAppPropOptions.get(hbConstructionPhysicalAppCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), physicalAppPropOptions.get(hbConstructionPhysicalAppCount).getName(), styles, null);
                        hbConstructionPhysicalAppCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionPhysicalAppCount < physicalAppPropOptions.size()) {
                        h.setSubheaderUuid(physicalAppPropOptions.get(hbDemolitionPhysicalAppCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), physicalAppPropOptions.get(hbDemolitionPhysicalAppCount).getName(), styles, null);
                        hbDemolitionPhysicalAppCount++;
                    }
                }
                case HOUSEBLOCK_CUSTOM_PROPERTY -> {
                    if (h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA && hbConstructionCustomPropsCount < hbCustomProps.size()) {
                        h.setPropertyModel(hbCustomProps.get(hbConstructionCustomPropsCount));
                        h.setSubheader(h.getPropertyModel().getName());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbConstructionCustomPropsCount++;
                    } else if (h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA && hbDemolitionCustomPropsCount < hbCustomProps.size()) {
                        h.setPropertyModel(hbCustomProps.get(hbDemolitionCustomPropsCount));
                        h.setSubheader(h.getPropertyModel().getName());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        hbDemolitionCustomPropsCount++;
                    }
                }
            }
        }
    }

    private static String getCellStringValue(Cell cell) {
        if (cell != null) {
            return cell.getStringCellValue();
        }
        return "";
    }

    private static String getRangeSubheaderName(RangeSelectDisabledModel rsm) {
        var min = rsm.getMin() != null
            ? rsm.getMin().divide(HOUSING_PRICE_DIVIDE_FACTOR, 2, RoundingMode.UNNECESSARY).toString()
            : "0";
        var max = rsm.getMax() != null
            ? rsm.getMax().divide(HOUSING_PRICE_DIVIDE_FACTOR, 2, RoundingMode.UNNECESSARY).toString()
            : "Inf";

        return StringUtils.replace(min + " - " + max, ".", ",");
    }

    private static String getSingleValueOrRangeSubheaderName(SingleValueOrRangeModel<BigDecimal> svrm) {
        StringBuilder sb = new StringBuilder();
        if (svrm.getValue() != null) {
            sb.append(isIntegerValue(svrm.getValue()) ? svrm.getValue().intValue() : svrm.getValue());
        } else {
            sb.append(isIntegerValue(svrm.getMin()) ? svrm.getMin().intValue() : svrm.getMin());
            sb.append(" - ");
            if (svrm.getMax() != null) {
                sb.append(isIntegerValue(svrm.getMax()) ? svrm.getMax().intValue() : svrm.getMax());
            } else {
                sb.append("Inf");
            }
        }
        return sb.toString();
    }

    private static void createProjectCells(ExcelExportTemplateColumns header, Row row, ProjectExportSqlModelExtended project, ExcelExportCellStyles styles) {

            for (var columnHeader : header.templateTableHeaders) {
                switch (columnHeader.getColumn()) {
                    case PROJECT_ID -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getProjectId().toString(), styles,
                        CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                    case PROJECT_NAME -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getName(), styles,
                        CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                    case PROJECT_OWNER -> {
                        Set<String> ownerEmails = new HashSet<>();
                        project.getOwnerGroupList().forEach(group -> group.getUsers().forEach(u -> ownerEmails.add(u.getUserEmail())));
                        if (!ownerEmails.isEmpty()) {
                            List<String> emails = ownerEmails.stream().filter(Objects::nonNull).sorted().toList();
                            createCellWithValue(row, columnHeader.getColumnIndex(), String.join(", ", emails), styles,
                                CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                        }
                    }
                    case PROJECT_CONFIDENTIALITY -> createCellWithValue(row, columnHeader.getColumnIndex(), ExcelStrings.getExcelStringFromEnumValue(project.getConfidentiality().name()),
                       styles, CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                    case PROJECT_PLAN_TYPE -> {
                        if (project.getPlanType() != null && !project.getPlanType().isEmpty()) {
                            List<String> planTypes = project.getPlanType().stream().map(pt -> ExcelStrings.getExcelStringFromEnumValue(pt.name())).toList();
                            createCellWithValue(row, columnHeader.getColumnIndex(), String.join(", ", planTypes), styles,
                                CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                        }
                    }
                    case PROJECT_STATUS -> {
                        String status;
                        if (project.getEndDate().isBefore(LocalDate.now())) {
                            status = ExcelStrings.getExcelStringFromEnumValue(ProjectStatus.REALIZED.name());
                        } else {
                            status = ExcelStrings.getExcelStringFromEnumValue(ProjectStatus.ACTIVE.name());
                        }
                        createCellWithValue(row, columnHeader.getColumnIndex(), status, styles,
                            CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                    }
                    case PROJECT_START_DATE -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getStartDate(), styles,
                        CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle()));
                    case PROJECT_END_DATE -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getEndDate(), styles,
                        CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle()));

                    case PROJECT_PHASE_1_CONCEPT -> project.getProjectPhaseStartDateList().stream()
                            .filter(pph -> pph.getStartDate() != null && pph.getProjectPhase() == ProjectPhase._1_CONCEPT).findFirst()
                            .ifPresent(pph -> createCellWithValue(row, columnHeader.getColumnIndex(), pph.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PHASE_2_INITIATIVE -> project.getProjectPhaseStartDateList().stream()
                            .filter(pph -> pph.getStartDate() != null && pph.getProjectPhase() == ProjectPhase._2_INITIATIVE).findFirst()
                            .ifPresent(pph -> createCellWithValue(row, columnHeader.getColumnIndex(), pph.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PHASE_3_DEFINITION -> project.getProjectPhaseStartDateList().stream()
                            .filter(pph -> pph.getStartDate() != null && pph.getProjectPhase() == ProjectPhase._3_DEFINITION).findFirst()
                            .ifPresent(pph -> createCellWithValue(row, columnHeader.getColumnIndex(), pph.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PHASE_4_DESIGN -> project.getProjectPhaseStartDateList().stream()
                            .filter(pph -> pph.getStartDate() != null && pph.getProjectPhase() == ProjectPhase._4_DESIGN).findFirst()
                            .ifPresent(pph -> createCellWithValue(row, columnHeader.getColumnIndex(), pph.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PHASE_5_PREPARATION -> project.getProjectPhaseStartDateList().stream()
                            .filter(pph -> pph.getStartDate() != null && pph.getProjectPhase() == ProjectPhase._5_PREPARATION).findFirst()
                            .ifPresent(pph -> createCellWithValue(row, columnHeader.getColumnIndex(), pph.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PHASE_6_REALIZATION -> project.getProjectPhaseStartDateList().stream()
                            .filter(pph -> pph.getStartDate() != null && pph.getProjectPhase() == ProjectPhase._6_REALIZATION).findFirst()
                            .ifPresent(pph -> createCellWithValue(row, columnHeader.getColumnIndex(), pph.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PHASE_7_AFTERCARE -> project.getProjectPhaseStartDateList().stream()
                            .filter(pph -> pph.getStartDate() != null && pph.getProjectPhase() == ProjectPhase._7_AFTERCARE).findFirst()
                            .ifPresent(pph -> createCellWithValue(row, columnHeader.getColumnIndex(), pph.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));

                    case PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._1A_ONHERROEPELIJK).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._1B_ONHERROEPELIJK_MET_UITWERKING_NODIG).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._1C_ONHERROEPELIJK_MET_BW_NODIG).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_2A_VASTGESTELD -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._2A_VASTGESTELD).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_2B_VASTGESTELD_MET_UITWERKING_NODIG -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._2B_VASTGESTELD_MET_UITWERKING_NODIG).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_2C_VASTGESTELD_MET_BW_NODIG -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._2C_VASTGESTELD_MET_BW_NODIG).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_3_IN_VOORBEREIDING -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._3_IN_VOORBEREIDING).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_4A_OPGENOMEN_IN_VISIE -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._4A_OPGENOMEN_IN_VISIE).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));
                    case PROJECT_PLAN_STATUS_4B_NIET_OPGENOMEN_IN_VISIE -> project.getProjectPlanStatusStartDateList().stream()
                            .filter(pps -> pps.getStartDate() != null && pps.getPlanStatus() == PlanStatus._4B_NIET_OPGENOMEN_IN_VISIE).findFirst()
                            .ifPresent(pps -> createCellWithValue(row, columnHeader.getColumnIndex(), pps.getStartDate(), styles,
                                CellStyleType.getCellStyleType(CellContentType.DATE, columnHeader.getBorderStyle())));

                    case PROJECT_MUNICIPALITY_ROLE, PROJECT_MUNICIPALITY, PROJECT_DISTRICT, PROJECT_NEIGHBOURHOOD, PROJECT_CUSTOM_PROPERTY, PROJECT_PRIORITY -> {
                        if (columnHeader.getPropertyModel() != null) {
                            switch (columnHeader.getPropertyModel().getPropertyType()) {
                                case TEXT -> project.getTextProperties().stream()
                                    .filter(tp -> tp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(tpm -> createCellWithValue(row, columnHeader.getColumnIndex(), tpm.getTextValue(), styles,
                                        CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle())));
                                case CATEGORY -> project.getCategoryProperties().stream()
                                    .filter(cp -> cp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(cpm -> {
                                        List<String> cats = columnHeader.getPropertyModel().getCategories().stream()
                                            .filter(c -> c.getDisabled() == Boolean.FALSE && cpm.getOptionValues().contains(c.getId()))
                                            .map(SelectModel::getName).sorted().toList();
                                        createCellWithValue(row, columnHeader.getColumnIndex(), String.join(", ", cats), styles,
                                            CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                                    });
                                case NUMERIC -> project.getNumericProperties().stream()
                                    .filter(np -> np.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(npm -> {
                                        if (npm.getValue() != null) {
                                            if (isIntegerValue(npm.getValue())) {
                                                createCellWithValue(row, columnHeader.getColumnIndex(), npm.getValue().longValue(), styles,
                                                    CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                                            } else {
                                                createCellWithValue(row, columnHeader.getColumnIndex(), npm.getValue().doubleValue(), styles,
                                                    CellStyleType.getCellStyleType(CellContentType.DOUBLE, columnHeader.getBorderStyle()));
                                            }
                                        }
                                    });
                                case BOOLEAN -> project.getBooleanProperties().stream()
                                    .filter(bp -> bp.getBooleanValue() != null && bp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(bpm -> createCellWithValue(row, columnHeader.getColumnIndex(), bpm.getBooleanValue() == Boolean.TRUE ? 1 : 0, styles,
                                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));
                                case ORDINAL -> project.getOrdinalProperties().stream()
                                    .filter(op -> op.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(opm -> createCellWithValue(row, columnHeader.getColumnIndex(), createOrdinalCellValue(opm, columnHeader.getPropertyModel()), styles,
                                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));
                            }
                        }
                    }
                }
            }
    }

    private static String createOrdinalCellValue(ProjectExportSqlModelExtended.OrdinalPropertyModel opm, PropertyModel prop) {
        StringBuilder sb = new StringBuilder();
        if (opm.getPropertyValueId() != null) {
            prop.getOrdinals().stream().filter(o -> o.getDisabled() == Boolean.FALSE && o.getId().equals(opm.getPropertyValueId())).findFirst()
                .ifPresent(o -> sb.append(o.getOrdinalValue()));
        } else {
            prop.getOrdinals().stream().filter(o -> o.getDisabled() == Boolean.FALSE && o.getId().equals(opm.getMinPropertyValueId())).findFirst()
                .ifPresent(o -> sb.append(o.getOrdinalValue()));
            sb.append(" - ");
        }
        prop.getOrdinals().stream().filter(o -> o.getDisabled() == Boolean.FALSE && o.getId().equals(opm.getMaxPropertyValueId())).findFirst()
            .ifPresent(o -> sb.append(o.getOrdinalValue()));
        return sb.toString();
    }

    private static void createHouseblockCells(ExcelExportTemplateColumns header, Row row, ProjectExportSqlModelExtended.HouseblockExportSqlModel houseblock,
                                              ExcelExportCellStyles styles) {
        for (var columnHeader : header.templateTableHeaders) {

            MutationType sectionMutationKind = null;
            if (columnHeader.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA) {
                sectionMutationKind = MutationType.CONSTRUCTION;
            } else if (columnHeader.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA) {
                sectionMutationKind = MutationType.DEMOLITION;
            }

            if (houseblock.getMutationKind() == sectionMutationKind || columnHeader.getSection() == ExcelTableHeader.Section.HOUSE_NUMBERS ||
                columnHeader.getColumn() == ExcelTableHeader.Column.PROJECT_PROGRAMMING) {
                switch (columnHeader.getColumn()) {
                    case PROJECT_PROGRAMMING -> {
                        if (houseblock.getProgramming() != null) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getProgramming() == Boolean.TRUE ? 1 : 0, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_MUTATION_BUILD -> {
                        if (houseblock.getMutationKind() == MutationType.CONSTRUCTION) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(), styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_MUTATION_DEMOLISH -> {
                        if (houseblock.getMutationKind() == MutationType.DEMOLITION) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(), styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_DELIVERY_DATE -> {
                        if (Objects.equals(columnHeader.getSubheaderDateValue(), houseblock.getEndDate())) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(), styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_SIZE -> {
                        if (columnHeader.getSubheaderRangeValue() != null && Objects.equals(columnHeader.getSubheaderRangeValue(), houseblock.getSize())) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(), styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        } else if (UNKNOWN.equalsIgnoreCase(columnHeader.getSubheader()) &&
                            (houseblock.getSize() == null || (houseblock.getSize().getValue() == null && houseblock.getSize().getMin() == null))) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(), styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_PROPERTY_TYPE_OWNER -> {
                        int htOwnerAmount = houseblock.getOwnershipValueList().stream()
                            .filter(o -> o.getOwnershipType() == OwnershipType.KOOPWONING)
                            .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        if (htOwnerAmount != 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), htOwnerAmount, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_PROPERTY_TYPE_LANDLORD -> {
                        int htLandlordAmount = houseblock.getOwnershipValueList().stream()
                            .filter(o -> o.getOwnershipType() == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER)
                            .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        if (htLandlordAmount != 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), htLandlordAmount, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION -> {
                        int htHousingAssocAmount = houseblock.getOwnershipValueList().stream()
                            .filter(o -> o.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE)
                            .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        if (htHousingAssocAmount != 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), htHousingAssocAmount, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_PROPERTY_TYPE_UNKNOWN -> {
                        int htKnown = houseblock.getOwnershipValueList().stream()
                            .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        if (htKnown != houseblock.getMutationAmount()) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount() - htKnown, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_PROPERTY_PURCHASE_PRICE_RANGE_CATEGORY -> {
                        int amount = 0;
                        if (columnHeader.getSubheaderUuid() != null) {
                            amount = houseblock.getOwnershipValueList().stream()
                                .filter(ov -> ov.getOwnershipType() == OwnershipType.KOOPWONING &&
                                    columnHeader.getSubheaderUuid().equals(ov.getOwnershipRangeCategoryId())
                                    && Objects.equals(columnHeader.getSubheaderRangeValue(), ov.getSingleValueOrRangeValue(false, HOUSING_PRICE_DIVIDE_FACTOR)))
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        } else if (UNKNOWN.equalsIgnoreCase(columnHeader.getSubheader())) {
                            amount = houseblock.getOwnershipValueList().stream().filter(ov -> ov.getOwnershipType() == OwnershipType.KOOPWONING &&
                                    ov.getOwnershipRangeCategoryId() == null && ov.getOwnershipValue() == null && ov.getOwnershipValueRangeMin() == null)
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        }
                        if (amount > 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), amount, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_PROPERTY_PURCHASE_PRICE -> {
                        if (columnHeader.getSubheaderRangeValue() != null) {
                            int amount = houseblock.getOwnershipValueList().stream()
                                .filter(ov -> ov.getOwnershipType() == OwnershipType.KOOPWONING)
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                            if (amount > 0) {
                                createCellWithValue(row, columnHeader.getColumnIndex(), amount, styles,
                                    CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                            }
                        }
                    }

                    case HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE_RANGE_CATEGORY -> {
                        int amount = 0;
                        if (columnHeader.getSubheaderUuid() != null) {
                            amount = houseblock.getOwnershipValueList().stream().filter(ov -> ov.getOwnershipType() == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER &&
                                    columnHeader.getSubheaderUuid().equals(ov.getOwnershipRentalRangeCategoryId()))
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        } else if (UNKNOWN.equalsIgnoreCase(columnHeader.getSubheader())) {
                            amount = houseblock.getOwnershipValueList().stream().filter(ov -> ov.getOwnershipType() == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER &&
                                    ov.getOwnershipRentalRangeCategoryId() == null && ov.getOwnershipRentalValue() == null && ov.getOwnershipRentalValueRangeMin() == null)
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        }
                        if (amount > 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), amount, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE -> {
                        if (columnHeader.getSubheaderRangeValue() != null) {
                            int amount = houseblock.getOwnershipValueList().stream()
                                .filter(ov -> ov.getOwnershipType() == OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER &&
                                    Objects.equals(columnHeader.getSubheaderRangeValue(), ov.getSingleValueOrRangeValue(true, HOUSING_PRICE_DIVIDE_FACTOR)))
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                            if (amount > 0) {
                                createCellWithValue(row, columnHeader.getColumnIndex(), amount, styles,
                                    CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                            }
                        }
                    }

                    case HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE_RANGE_CATEGORY -> {
                        int amount = 0;
                        if (columnHeader.getSubheaderUuid() != null) {
                            amount = houseblock.getOwnershipValueList().stream()
                                .filter(ov -> ov.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE &&
                                    columnHeader.getSubheaderUuid().equals(ov.getOwnershipRentalRangeCategoryId()) &&
                                    Objects.equals(columnHeader.getSubheaderRangeValue(), ov.getSingleValueOrRangeValue(true, HOUSING_PRICE_DIVIDE_FACTOR)))
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        } else if (UNKNOWN.equalsIgnoreCase(columnHeader.getSubheader())) {
                            amount = houseblock.getOwnershipValueList().stream().filter(ov -> ov.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE &&
                                    ov.getOwnershipRentalRangeCategoryId() == null && ov.getOwnershipRentalValue() == null && ov.getOwnershipRentalValueRangeMin() == null)
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                        }
                        if (amount > 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), amount, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }
                    case HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE -> {
                        if (columnHeader.getSubheaderRangeValue() != null) {
                            int amount = houseblock.getOwnershipValueList().stream()
                                .filter(ov -> ov.getOwnershipType() == OwnershipType.HUURWONING_WONINGCORPORATIE)
                                .mapToInt(ProjectExportSqlModelExtended.OwnershipValueSqlModel::getOwnershipAmount).sum();
                            if (amount > 0) {
                                createCellWithValue(row, columnHeader.getColumnIndex(), amount, styles,
                                    CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                            }
                        }
                    }

                    case HOUSEBLOCK_TYPE_SINGLE_FAMILY -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getEengezinswoning(), styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_TYPE_MULTI_FAMILY ->  createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMeergezinswoning(), styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_TYPE_UNKNOWN -> {
                        Integer unknownVal = houseblock.getHouseTypeUnknownAmount();
                        if (unknownVal != 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), unknownVal, styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getIntentionPermissionOwner(), styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getFormalPermissionOwner(), styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getNoPermissionOwner(), styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));

                    case HOUSEBLOCK_PHYSICAL_APPEARANCE -> houseblock.getPhysicalAppearances().stream()
                        .filter(pa -> pa.getPropertyValueId().equals(columnHeader.getSubheaderUuid())).findFirst()
                        .ifPresent(pa -> createCellWithValue(row, columnHeader.getColumnIndex(), pa.getAmount(), styles,
                            CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));

                    case HOUSEBLOCK_TARGET_GROUP -> houseblock.getTargetGroups().stream()
                        .filter(tg -> tg.getPropertyValueId().equals(columnHeader.getSubheaderUuid())).findFirst()
                        .ifPresent(tg -> createCellWithValue(row, columnHeader.getColumnIndex(), tg.getAmount(), styles,
                            CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));

                    case HOUSEBLOCK_CUSTOM_PROPERTY -> {
                        if (columnHeader.getPropertyModel() != null) {
                            switch (columnHeader.getPropertyModel().getPropertyType()) {
                                case TEXT -> houseblock.getTextProperties().stream()
                                    .filter(tp -> tp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(tpm -> createCellWithValue(row, columnHeader.getColumnIndex(), tpm.getTextValue(), styles,
                                        CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle())));
                                case CATEGORY -> houseblock.getCategoryProperties().stream()
                                    .filter(cp -> cp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(cpm -> {
                                        List<String> cats = columnHeader.getPropertyModel().getCategories().stream()
                                            .filter(c -> c.getDisabled() == Boolean.FALSE && cpm.getOptionValues().contains(c.getId()))
                                            .map(SelectModel::getName).sorted().toList();
                                        createCellWithValue(row, columnHeader.getColumnIndex(), String.join(", ", cats), styles,
                                            CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                                    });
                                case NUMERIC -> houseblock.getNumericProperties().stream()
                                    .filter(np -> np.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(npm -> {
                                        if (npm.getValue() != null) {
                                            if (isIntegerValue(npm.getValue())) {
                                                createCellWithValue(row, columnHeader.getColumnIndex(), npm.getValue().longValue(), styles,
                                                    CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                                            } else {
                                                createCellWithValue(row, columnHeader.getColumnIndex(), npm.getValue().doubleValue(), styles,
                                                    CellStyleType.getCellStyleType(CellContentType.DOUBLE, columnHeader.getBorderStyle()));
                                            }
                                        }
                                    });
                                case BOOLEAN -> houseblock.getBooleanProperties().stream()
                                    .filter(bp -> bp.getBooleanValue() != null && bp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(bpm -> createCellWithValue(row, columnHeader.getColumnIndex(), bpm.getBooleanValue() == Boolean.TRUE ? 1 : 0, styles,
                                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));
                                case ORDINAL -> houseblock.getOrdinalProperties().stream()
                                    .filter(op -> op.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                    .ifPresent(opm -> createCellWithValue(row, columnHeader.getColumnIndex(), createOrdinalCellValue(opm, columnHeader.getPropertyModel()), styles,
                                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));
                            }
                        }
                    }
                }
            }
        }
    }


    private static boolean isIntegerValue(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }

    private static void createCellWithValue(Row row, int columnIndex, Object value, ExcelExportCellStyles styles, CellStyleType styleType) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        if (styleType != null) {
            cell.setCellStyle(styles.getCellStyle(styleType, row.getRowNum() == 5));
        }
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof LocalDate) {
                cell.setCellValue((LocalDate) value);
            } else if (value instanceof Integer) {
                cell.setCellValue((Integer) value);
            } else if (value instanceof Long) {
                cell.setCellValue((Long) value);
            } else if (value instanceof Double) {
                cell.setCellValue((Double) value);
            }
        }
    }


    public static void insertNewColumnBefore(Sheet sheet, int columnIndex) {

        int nrRows = sheet.getLastRowNum() + 1;;
        int nrCols = sheet.getRow(0).getLastCellNum();

        for (int row = 0; row < nrRows; row++) {
            Row r = sheet.getRow(row);

            if (r == null) {
                continue;
            }

            for (int col = nrCols; col > columnIndex; col--) {
                Cell rightCell = r.getCell(col);
                if (rightCell != null) {
                    r.removeCell(rightCell);
                }

                Cell leftCell = r.getCell(col - 1);
                if (leftCell != null) {
                    Cell newCell = r.createCell(col, leftCell.getCellType());
                    cloneCell(newCell, leftCell);
                }
            }

            Cell oldCell = r.getCell(columnIndex + 1);
            if (oldCell != null) {
                Cell currentEmptyCell = r.getCell(columnIndex);
                if (currentEmptyCell == null) {
                    currentEmptyCell = r.createCell(columnIndex);
                }
                cloneCell(currentEmptyCell, r.getCell(columnIndex + 1));
            }
        }

        for (int col = nrCols; col > columnIndex; col--) {
            sheet.setColumnWidth(col, sheet.getColumnWidth(col - 1));
        }

    }

    private static void cloneCell(Cell cNew, Cell cOld) {
        cNew.setCellComment(cOld.getCellComment());
        cNew.setCellStyle(cOld.getCellStyle());

        switch (cOld.getCellType()) {
            case BOOLEAN -> cNew.setCellValue(cOld.getBooleanCellValue());
            case NUMERIC -> cNew.setCellValue(cOld.getNumericCellValue());
            case STRING -> cNew.setCellValue(cOld.getStringCellValue());
            case ERROR -> cNew.setCellValue(cOld.getErrorCellValue());
            case FORMULA -> cNew.setCellFormula(cOld.getCellFormula());
        }
    }


    public enum CellContentType {
        STRING,
        DATE,
        INTEGER,
        DOUBLE;
    }

    public enum CellStyleType {
        STRING_LEFT_BORDER,
        STRING_RIGHT_BORDER,
        STRING_NO_BORDER,

        INTEGER_LEFT_BORDER,
        INTEGER_RIGHT_BORDER,
        INTEGER_NO_BORDER,

        DOUBLE_LEFT_BORDER,
        DOUBLE_RIGHT_BORDER,
        DOUBLE_NO_BORDER,

        DATE_LEFT_BORDER,
        DATE_RIGHT_BORDER,
        DATE_NO_BORDER;

        public static CellStyleType getCellStyleType(CellContentType cellContentType, ExcelTableHeader.Border border) {
            if (cellContentType == CellContentType.STRING) {
                if (border == ExcelTableHeader.Border.LEFT) {
                    return CellStyleType.STRING_LEFT_BORDER;
                } else if (border == ExcelTableHeader.Border.RIGHT) {
                    return CellStyleType.STRING_RIGHT_BORDER;
                } else {
                    return CellStyleType.STRING_NO_BORDER;
                }
            } else if (cellContentType == CellContentType.DATE) {
                if (border == ExcelTableHeader.Border.LEFT) {
                    return CellStyleType.DATE_LEFT_BORDER;
                } else if (border == ExcelTableHeader.Border.RIGHT) {
                    return CellStyleType.DATE_RIGHT_BORDER;
                } else {
                    return CellStyleType.DATE_NO_BORDER;
                }
            }
            return null;
        }
    }

    public static class ExcelExportCellStyles {

        private Map<CellStyleType, CellStyle> styles = new HashMap<>();
        private Map<CellStyleType, CellStyle> topBorderStyles = new HashMap<>();

        public ExcelExportCellStyles(Workbook workbook) {
            CreationHelper createHelper = workbook.getCreationHelper();

            styles.put(CellStyleType.STRING_LEFT_BORDER, createCellStyle(createHelper, workbook, "General", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            styles.put(CellStyleType.STRING_RIGHT_BORDER, createCellStyle(createHelper, workbook, "General", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            styles.put(CellStyleType.STRING_NO_BORDER, createCellStyle(createHelper, workbook, "General", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));

            topBorderStyles.put(CellStyleType.STRING_LEFT_BORDER, createCellStyle(createHelper, workbook, "General", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            topBorderStyles.put(CellStyleType.STRING_RIGHT_BORDER, createCellStyle(createHelper, workbook, "General", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            topBorderStyles.put(CellStyleType.STRING_NO_BORDER, createCellStyle(createHelper, workbook, "General", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));

            styles.put(CellStyleType.INTEGER_LEFT_BORDER, createCellStyle(createHelper, workbook, "0", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            styles.put(CellStyleType.INTEGER_RIGHT_BORDER, createCellStyle(createHelper, workbook, "0", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            styles.put(CellStyleType.INTEGER_NO_BORDER, createCellStyle(createHelper, workbook, "0", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));

            topBorderStyles.put(CellStyleType.INTEGER_LEFT_BORDER, createCellStyle(createHelper, workbook, "0.00", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            topBorderStyles.put(CellStyleType.INTEGER_RIGHT_BORDER, createCellStyle(createHelper, workbook, "0.00", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            topBorderStyles.put(CellStyleType.INTEGER_NO_BORDER, createCellStyle(createHelper, workbook, "0.00", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));

            styles.put(CellStyleType.DOUBLE_LEFT_BORDER, createCellStyle(createHelper, workbook, "0.00", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            styles.put(CellStyleType.DOUBLE_RIGHT_BORDER, createCellStyle(createHelper, workbook, "0.00", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            styles.put(CellStyleType.DOUBLE_NO_BORDER, createCellStyle(createHelper, workbook, "0.00", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));

            topBorderStyles.put(CellStyleType.DOUBLE_LEFT_BORDER, createCellStyle(createHelper, workbook, "0", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            topBorderStyles.put(CellStyleType.DOUBLE_RIGHT_BORDER, createCellStyle(createHelper, workbook, "0", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            topBorderStyles.put(CellStyleType.DOUBLE_NO_BORDER, createCellStyle(createHelper, workbook, "0", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));

            styles.put(CellStyleType.DATE_LEFT_BORDER, createCellStyle(createHelper, workbook, "mm/dd/yyyy", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            styles.put(CellStyleType.DATE_RIGHT_BORDER, createCellStyle(createHelper, workbook, "mm/dd/yyyy", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            styles.put(CellStyleType.DATE_NO_BORDER, createCellStyle(createHelper, workbook, "mm/dd/yyyy", BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));

            topBorderStyles.put(CellStyleType.DATE_LEFT_BORDER, createCellStyle(createHelper, workbook, "mm/dd/yyyy", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THIN));
            topBorderStyles.put(CellStyleType.DATE_RIGHT_BORDER, createCellStyle(createHelper, workbook, "mm/dd/yyyy", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.MEDIUM));
            topBorderStyles.put(CellStyleType.DATE_NO_BORDER, createCellStyle(createHelper, workbook, "mm/dd/yyyy", BorderStyle.MEDIUM, BorderStyle.THIN, BorderStyle.THIN, BorderStyle.THIN));
        }

        private static CellStyle createCellStyle(CreationHelper creationHelper, Workbook workbook, String format,
                                                 BorderStyle top, BorderStyle bottom, BorderStyle left, BorderStyle right) {
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
            cellStyle.setBorderBottom(bottom);
            cellStyle.setBorderTop(top);
            cellStyle.setBorderLeft(left);
            cellStyle.setBorderRight(right);
            return cellStyle;
        }

        public CellStyle getCellStyle(CellStyleType styleType, boolean topBorder) {
            if (topBorder) {
                return topBorderStyles.get(styleType);
            } else {
               return styles.get(styleType);
            }
        }
    }
}


