package nl.vng.diwi.services.export.excel;

import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectDisabledModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.ExcelStrings;
import nl.vng.diwi.services.ExcelTableHeader;
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
import java.time.LocalDate;
import java.util.ArrayList;
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

        static public StreamingOutput buildExportObject(
            List<ProjectExportSqlModelExtended> projects,
            List<PropertyModel> customProps,
            Confidentiality minConfidentiality,
            List<DataExchangeExportError> errors) {


        try {
            File excelFile = new File(ExcelExport.class.getClassLoader().getResource(EXCEL_TEMPLATE_PATH).getPath());

            FileInputStream inputStream = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheet(PROJECT_SHEET_NAME);

            ExcelExportCellStyles styles = new ExcelExportCellStyles(workbook);

            ExcelExportTemplateColumns header = new ExcelExportTemplateColumns();

            Set<UUID> projectCustomPropIds = new HashSet<>();
            Set<LocalDate> hhDeliveryDatesSet = new HashSet<>();
            projects.forEach(p -> {
                p.getBooleanProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getTextProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getCategoryProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getNumericProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getOrdinalProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getHouseblocks().forEach(h -> {
                    hhDeliveryDatesSet.add(h.getEndDate());
                });
            });

            List<PropertyModel> projectCustomProps = customProps.stream().filter(cp -> cp.getType() == PropertyKind.CUSTOM && projectCustomPropIds.contains(cp.getId()))
                .sorted(Comparator.comparing(PropertyModel::getName)).toList();
            List<LocalDate> hbDeliveryDates = hhDeliveryDatesSet.stream().sorted().toList();

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

            Row suhheaderRow = sheet.getRow(4);
            int projectCpCount = 0;
            int hbConstructionDDCount = 0;
            int hbDemolitionDDCount = 0;
            int hbConstructionTargetGroupCount = 0;
            int hbDemolitionTargetGroupCount = 0;
            int hbConstructionPhysicalAppCount = 0;
            int hbDemolitionPhysicalAppCount = 0;

            for (var h : header.templateTableHeaders) {
                if (h.getColumn() == ExcelTableHeader.Column.PROJECT_CUSTOM_PROPERTY) {
                    if (projectCpCount < projectCustomProps.size()) {
                        h.setPropertyModel(projectCustomProps.get(projectCpCount));
                        h.setSubheader(h.getPropertyModel().getName());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), styles, null);
                        projectCpCount++;
                    }
                } else if (h.getColumn() == ExcelTableHeader.Column.PROJECT_MUNICIPALITY) {
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY))
                        .findFirst().orElse(null));
                } else if (h.getColumn() == ExcelTableHeader.Column.PROJECT_DISTRICT) {
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_DISTRICT))
                        .findFirst().orElse(null));
                } else if (h.getColumn() == ExcelTableHeader.Column.PROJECT_NEIGHBOURHOOD) {
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_NEIGHBOURHOOD))
                        .findFirst().orElse(null));
                } else if (h.getColumn() == ExcelTableHeader.Column.PROJECT_MUNICIPALITY_ROLE) {
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY_ROLE))
                        .findFirst().orElse(null));
                } else if (h.getColumn() == ExcelTableHeader.Column.PROJECT_PRIORITY) {
                    h.setPropertyModel(customProps.stream()
                        .filter(cp -> cp.getType() == PropertyKind.FIXED && cp.getName().equals(Constants.FIXED_PROPERTY_PRIORITY))
                        .findFirst().orElse(null));
                } else if (h.getColumn() == ExcelTableHeader.Column.HOUSEBLOCK_DELIVERY_DATE && h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA) {
                    if (hbConstructionDDCount < hbDeliveryDates.size()) {
                        h.setSubheaderDateValue(hbDeliveryDates.get(hbConstructionDDCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheaderDateValue(), styles, null);
                        hbConstructionDDCount++;
                    }
                } else if (h.getColumn() == ExcelTableHeader.Column.HOUSEBLOCK_DELIVERY_DATE && h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA) {
                    if (hbDemolitionDDCount < hbDeliveryDates.size()) {
                        h.setSubheaderDateValue(hbDeliveryDates.get(hbDemolitionDDCount));
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheaderDateValue(), styles, null);
                        hbDemolitionDDCount++;
                    }
                } else if (h.getColumn() == ExcelTableHeader.Column.HOUSEBLOCK_TARGET_GROUP && h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA) {
                    if (hbConstructionTargetGroupCount < targetGroupPropOptions.size()) {
                        h.setSubheaderUuid(targetGroupPropOptions.get(hbConstructionTargetGroupCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), targetGroupPropOptions.get(hbConstructionTargetGroupCount).getName(), styles, null);
                        hbConstructionTargetGroupCount++;
                    }
                } else if (h.getColumn() == ExcelTableHeader.Column.HOUSEBLOCK_TARGET_GROUP && h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA) {
                    if (hbDemolitionTargetGroupCount < targetGroupPropOptions.size()) {
                        h.setSubheaderUuid(targetGroupPropOptions.get(hbDemolitionTargetGroupCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), targetGroupPropOptions.get(hbDemolitionTargetGroupCount).getName(), styles, null);
                        hbDemolitionTargetGroupCount++;
                    }
                } else if (h.getColumn() == ExcelTableHeader.Column.HOUSEBLOCK_PHYSICAL_APPEARANCE && h.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA) {
                    if (hbConstructionPhysicalAppCount < physicalAppPropOptions.size()) {
                        h.setSubheaderUuid(physicalAppPropOptions.get(hbConstructionPhysicalAppCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), physicalAppPropOptions.get(hbConstructionPhysicalAppCount).getName(), styles, null);
                        hbConstructionPhysicalAppCount++;
                    }
                } else if (h.getColumn() == ExcelTableHeader.Column.HOUSEBLOCK_PHYSICAL_APPEARANCE && h.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA) {
                    if (hbDemolitionPhysicalAppCount < physicalAppPropOptions.size()) {
                        h.setSubheaderUuid(physicalAppPropOptions.get(hbDemolitionPhysicalAppCount).getId());
                        createCellWithValue(suhheaderRow, h.getColumnIndex(), physicalAppPropOptions.get(hbDemolitionPhysicalAppCount).getName(), styles, null);
                        hbDemolitionPhysicalAppCount++;
                    }
                }
            }

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
            logger.error("Excel download Exception", ex);
        }
        return null;
    }

    private static void createProjectCells(ExcelExportTemplateColumns header, Row row, ProjectExportSqlModelExtended project, ExcelExportCellStyles styles) {

            for (var columnHeader : header.templateTableHeaders) {
                switch (columnHeader.getColumn()) {
                    case PROJECT_ID -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getProjectId().toString(), styles,
                        CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                    case PROJECT_NAME -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getName(), styles,
                        CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
//                    case PROJECT_OWNER -> //TODO
                    case PROJECT_CONFIDENTIALITY -> createCellWithValue(row, columnHeader.getColumnIndex(), ExcelStrings.getExcelStringFromEnumValue(project.getConfidentiality().name()),
                        styles, CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                    case PROJECT_PLAN_TYPE -> {
                        if (project.getPlanType() != null && !project.getPlanType().isEmpty()) {
                            List<String> planTypes = project.getPlanType().stream().map(pt -> ExcelStrings.getExcelStringFromEnumValue(pt.name())).toList();
                            createCellWithValue(row, columnHeader.getColumnIndex(), String.join(", ", planTypes), styles,
                                CellStyleType.getCellStyleType(CellContentType.STRING, columnHeader.getBorderStyle()));
                        }
                    }
//                    case PROJECT_PRIORITY -> TODO
//                    case PROJECT_STATUS -> createCellWithValue(row, columnHeader.getColumnIndex(), ExcelStrings.getExcelStringFromEnumValue(project.getStatus().name()), (short) 0);
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

                    case PROJECT_MUNICIPALITY_ROLE, PROJECT_MUNICIPALITY, PROJECT_DISTRICT, PROJECT_NEIGHBOURHOOD, PROJECT_CUSTOM_PROPERTY -> {
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
//                            case ORDINAL -> TODO
                        }
                    }
                }
            }
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
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(),  styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_DELIVERY_DATE -> {
                        if (Objects.equals(columnHeader.getSubheaderDateValue(), houseblock.getEndDate())) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(),  styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_TYPE_SINGLE_FAMILY -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getEengezinswoning(),  styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_TYPE_MULTI_FAMILY ->  createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMeergezinswoning(),  styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_TYPE_UNKNOWN -> {
                        Integer unknownVal = houseblock.getMutationAmount();
                        if (houseblock.getEengezinswoning() != null) {
                            unknownVal -= houseblock.getEengezinswoning();
                        }
                        if (houseblock.getMeergezinswoning() != null) {
                            unknownVal -= houseblock.getMeergezinswoning();
                        }
                        if (unknownVal != 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), unknownVal,  styles,
                                CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                        }
                    }

                    case HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getIntentionPermissionOwner(),  styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getFormalPermissionOwner(),  styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));
                    case HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getNoPermissionOwner(),  styles,
                        CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle()));

                    case HOUSEBLOCK_PHYSICAL_APPEARANCE -> houseblock.getPhysicalAppearances().stream()
                        .filter(pa -> pa.getPropertyValueId().equals(columnHeader.getSubheaderUuid())).findFirst()
                        .ifPresent(pa -> createCellWithValue(row, columnHeader.getColumnIndex(), pa.getAmount(),  styles,
                            CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));

                    case HOUSEBLOCK_TARGET_GROUP -> houseblock.getTargetGroups().stream()
                        .filter(tg -> tg.getPropertyValueId().equals(columnHeader.getSubheaderUuid())).findFirst()
                        .ifPresent(tg -> createCellWithValue(row, columnHeader.getColumnIndex(), tg.getAmount(),  styles,
                            CellStyleType.getCellStyleType(CellContentType.INTEGER, columnHeader.getBorderStyle())));

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


