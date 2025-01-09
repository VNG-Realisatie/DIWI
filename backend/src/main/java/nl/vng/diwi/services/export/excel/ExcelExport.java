package nl.vng.diwi.services.export.excel;

import jakarta.ws.rs.core.StreamingOutput;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.ExcelStrings;
import nl.vng.diwi.services.ExcelTableHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ExcelExport {

    private static final Logger logger = LogManager.getLogger();

    public static String EXCEL_TEMPLATE_NAME  = "Excel_export_template.xlsx";
    public static String EXCEL_TEMPLATE_PATH = "templates/" + EXCEL_TEMPLATE_NAME;

    public static String PROJECT_SHEET_NAME = "Data";

        static public StreamingOutput buildExportObject(
            List<ProjectExportSqlModelPlus> projects,
            List<PropertyModel> customProps,
            Confidentiality minConfidentiality,
            List<DataExchangeExportError> errors) {


        try {
            File excelFile = new File(ExcelExport.class.getClassLoader().getResource(EXCEL_TEMPLATE_PATH).getPath());

            FileInputStream inputStream = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheet(PROJECT_SHEET_NAME);

            CreationHelper createHelper = workbook.getCreationHelper();
            short dateFormat = createHelper.createDataFormat().getFormat("mm/dd/yyyy");
            short doubleFormat = createHelper.createDataFormat().getFormat("0.00");
            short integerFormat = createHelper.createDataFormat().getFormat("0");
            short stringFormat = createHelper.createDataFormat().getFormat("General");

            ExcelExportTemplateColumns header = new ExcelExportTemplateColumns();

            Set<UUID> projectCustomPropIds = new HashSet<>();
            projects.forEach(p -> {
                p.getBooleanProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getTextProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getCategoryProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
                p.getNumericProperties().forEach(prop -> projectCustomPropIds.add(prop.getPropertyId()));
            });
            List<PropertyModel> projectCustomProps = customProps.stream().filter(cp -> cp.getType() == PropertyKind.CUSTOM && projectCustomPropIds.contains(cp.getId()))
                .sorted(Comparator.comparing(PropertyModel::getName)).toList();

            for (int i = ExcelExportTemplateColumns.PROJECT_CUSTOM_PROPS_COLUMNS_DEFAULT; i < projectCustomProps.size(); i++) {
                int newColumnIndex = header.insertColumn(ExcelTableHeader.Column.PROJECT_CUSTOM_PROPERTY);
                insertNewColumnBefore(sheet, newColumnIndex);
            }

            Row suhheaderRow = sheet.getRow(4);
            int projectCpCount = 0;
            for (var h : header.templateTableHeaders) {
                if (h.getColumn() == ExcelTableHeader.Column.PROJECT_CUSTOM_PROPERTY) {
                    h.setPropertyModel(projectCustomProps.get(projectCpCount));
                    h.setSubheader(h.getPropertyModel().getName());
                    createCellWithValue(suhheaderRow, h.getColumnIndex(), h.getSubheader(), stringFormat);
                    projectCpCount++;
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
                }
            }

            int rowCount = 5;
            for (var project : projects) {
                if (project.getHouseblocks() == null || project.getHouseblocks().isEmpty()) {
                    Row row = sheet.getRow(rowCount);
                    if (row == null) {
                        row = sheet.createRow(rowCount);
                    }
                    createProjectCells(header, row, project, stringFormat, dateFormat, integerFormat, doubleFormat);
                    rowCount++;
                } else {
                    for (var houseblock : project.getHouseblocks()) {
                        Row row = sheet.getRow(rowCount);
                        if (row == null) {
                            row = sheet.createRow(rowCount);
                        }
                        createProjectCells(header, row, project, stringFormat, dateFormat, integerFormat, doubleFormat);
                        createHouseblockCells(header, row, houseblock, stringFormat, dateFormat, integerFormat, doubleFormat);
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

    private static void createProjectCells(ExcelExportTemplateColumns header, Row row, ProjectExportSqlModelPlus project,
                                           short stringFormat, short dateFormat, short integerFormat, short doubleFormat) {

            for (var columnHeader : header.templateTableHeaders) {
                switch (columnHeader.getColumn()) {
                    case PROJECT_ID -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getProjectId().toString(), stringFormat);
                    case PROJECT_NAME -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getName(), stringFormat);
//                    case PROJECT_OWNER -> //TODO
                    case PROJECT_CONFIDENTIALITY -> createCellWithValue(row, columnHeader.getColumnIndex(), ExcelStrings.getExcelStringFromEnumValue(project.getConfidentiality().name()), stringFormat);
                    case PROJECT_PLAN_TYPE -> {
                        if (project.getPlanType() != null && !project.getPlanType().isEmpty()) {
                            List<String> planTypes = project.getPlanType().stream().map(pt -> ExcelStrings.getExcelStringFromEnumValue(pt.name())).toList();
                            createCellWithValue(row, columnHeader.getColumnIndex(), String.join(", ", planTypes), stringFormat);
                        }
                    }
//                    case PROJECT_PRIORITY -> TODO
                    case PROJECT_STATUS -> createCellWithValue(row, columnHeader.getColumnIndex(), ExcelStrings.getExcelStringFromEnumValue(project.getStatus().name()), (short) 0);
                    case PROJECT_START_DATE -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getStartDate(), dateFormat);
                    case PROJECT_END_DATE -> createCellWithValue(row, columnHeader.getColumnIndex(), project.getEndDate(), dateFormat);

                    case PROJECT_MUNICIPALITY_ROLE, PROJECT_MUNICIPALITY, PROJECT_DISTRICT, PROJECT_NEIGHBOURHOOD, PROJECT_CUSTOM_PROPERTY -> {
                        switch (columnHeader.getPropertyModel().getPropertyType()) {
                            case TEXT -> project.getTextProperties().stream()
                                .filter(tp -> tp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                .ifPresent(tpm -> createCellWithValue(row, columnHeader.getColumnIndex(), tpm.getTextValue(), stringFormat));
                            case CATEGORY -> project.getCategoryProperties().stream()
                                .filter(cp -> cp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                .ifPresent(cpm -> {
                                    List<String> cats = columnHeader.getPropertyModel().getCategories().stream()
                                        .filter(c -> c.getDisabled() == Boolean.FALSE && cpm.getOptionValues().contains(c.getId()))
                                        .map(SelectModel::getName).sorted().toList();
                                    createCellWithValue(row, columnHeader.getColumnIndex(), String.join(", ", cats), stringFormat);
                                });
                            case NUMERIC -> project.getNumericProperties().stream()
                                .filter(np -> np.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                .ifPresent(npm -> {
                                    if (npm.getValue() != null) {
                                        if (isIntegerValue(npm.getValue())) {
                                            createCellWithValue(row, columnHeader.getColumnIndex(), npm.getValue().longValue(), integerFormat);
                                        } else {
                                            createCellWithValue(row, columnHeader.getColumnIndex(), npm.getValue().doubleValue(), doubleFormat);
                                        }
                                    }
                                });
                            case BOOLEAN -> project.getBooleanProperties().stream()//TODO
                                .filter(bp -> bp.getPropertyId().equals(columnHeader.getPropertyModel().getId())).findFirst()
                                .ifPresent(bpm -> createCellWithValue(row, columnHeader.getColumnIndex(), bpm.getBooleanValue().toString(), stringFormat));
                        }
                    }
                }
            }
    }

    private static void createHouseblockCells(ExcelExportTemplateColumns header, Row row, ProjectExportSqlModelPlus.HouseblockExportSqlModel houseblock,
                                              short stringFormat, short dateFormat, short integerFormat, short doubleFormat) {
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
//                    case PROJECT_PROGRAMMING ->
                    case HOUSEBLOCK_MUTATION_BUILD -> {
                        if (houseblock.getMutationKind() == MutationType.CONSTRUCTION) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(), integerFormat);
                        }
                    }
                    case HOUSEBLOCK_MUTATION_DEMOLISH -> {
                        if (houseblock.getMutationKind() == MutationType.DEMOLITION) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMutationAmount(), integerFormat);
                        }
                    }
                    case HOUSEBLOCK_TYPE_SINGLE_FAMILY -> createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getEengezinswoning(), integerFormat);
                    case HOUSEBLOCK_TYPE_MULTI_FAMILY ->  createCellWithValue(row, columnHeader.getColumnIndex(), houseblock.getMeergezinswoning(), integerFormat);
                    case HOUSEBLOCK_TYPE_UNKNOWN -> {
                        Integer unknownVal = houseblock.getMutationAmount();
                        if (houseblock.getEengezinswoning() != null) {
                            unknownVal -= houseblock.getEengezinswoning();
                        }
                        if (houseblock.getMeergezinswoning() != null) {
                            unknownVal -= houseblock.getMeergezinswoning();
                        }
                        if (unknownVal != 0) {
                            createCellWithValue(row, columnHeader.getColumnIndex(), unknownVal, stringFormat);
                        }
                    }
                }
            }
        }
    }


    private static boolean isIntegerValue(BigDecimal bd) {
        return bd.stripTrailingZeros().scale() <= 0;
    }

    private static void createCellWithValue(Row row, int columnIndex, Object value, short format) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.getCellStyle().setDataFormat(format);
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
}


