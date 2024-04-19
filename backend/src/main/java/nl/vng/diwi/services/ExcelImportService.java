package nl.vng.diwi.services;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ExcelError;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectDisabledModel;
import nl.vng.diwi.models.SelectModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExcelImportService {
    private static final Logger logger = LogManager.getLogger();

    public static String errors = "errors";
    public static String result = "result";

    public ExcelImportService() {
    }

    public Map<String, Object> importExcel(String excelFilePath, VngRepository repo, UUID loggedInUserUuid) {

        List<ExcelError> excelErrors = new ArrayList<>();
        List<SelectModel> excelProjects = new ArrayList<>();

        Workbook workbook;
        try {
            InputStream inputStream = new FileInputStream(excelFilePath);
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException | NotOfficeXmlFileException e) { //excelFile could not be read. Return error.
            logger.error("Error creating workbook", e);
            return Map.of(errors, List.of(new ExcelError(ExcelError.ERROR.IO_ERROR)));
        }

        Sheet dataSheet = workbook.getSheetAt(0);
        if (dataSheet == null) {
            return Map.of(errors, List.of(new ExcelError(ExcelError.ERROR.MISSING_DATA_SHEET)));
        }

        DataFormatter dateFormatter = new DataFormatter();
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        List<PropertyModel> activeProperties = repo.getPropertyDAO().getPropertiesList(null, false, null);

        Map<Integer, ExcelColumn> columnsMap = new HashMap<>();
        Map<Integer, PropertyModel> propertyModelMap = new HashMap<>();

        ZonedDateTime importTime = ZonedDateTime.now();

        Iterator<Row> iterator = dataSheet.iterator();
        int rowCount = 0;

        try (AutoCloseTransaction transaction = repo.beginTransaction()) {

            User user = repo.getReferenceById(User.class, loggedInUserUuid);
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                rowCount++;

                if (rowCount == 4) {
                    Iterator<Cell> cellIterator = nextRow.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell nextCell = cellIterator.next();
                        int columnIndex = nextCell.getColumnIndex();
                        String headerName = getStringValue(nextCell, dateFormatter, formulaEvaluator, excelErrors);
                        ExcelColumn excelColumn = ExcelColumn.findByName(headerName);
                        if (excelColumn != null) {
                            columnsMap.put(columnIndex, excelColumn);
                        }
                    }
                }
                if (rowCount == 5) {
                    Iterator<Cell> cellIterator = nextRow.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell nextCell = cellIterator.next();
                        int columnIndex = nextCell.getColumnIndex();
                        ExcelColumn excelColumn = columnsMap.get(columnIndex);
                        if (excelColumn != null) {
                            if (excelColumn == ExcelColumn.PROJECT_MUNICIPALITY) {
                                PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.PROJECT &&
                                    p.getPropertyType() == PropertyType.CATEGORY && p.getType() == PropertyKind.FIXED && p.getName().equals(Constants.FIXED_PROPERTY_MUNICIPALITY)).findFirst().orElse(null);
                                if (propertyModel == null) {
                                    excelErrors.add(getExcelError(nextCell, Constants.FIXED_PROPERTY_MUNICIPALITY, ExcelError.ERROR.MISSING_FIXED_PROPERTY));
                                } else {
                                    propertyModelMap.put(columnIndex, propertyModel);
                                }
                            }
                            if (excelColumn == ExcelColumn.PROJECT_DISTRICT) {
                                PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.PROJECT &&
                                    p.getPropertyType() == PropertyType.CATEGORY && p.getType() == PropertyKind.FIXED && p.getName().equals(Constants.FIXED_PROPERTY_DISTRICT)).findFirst().orElse(null);
                                if (propertyModel == null) {
                                    excelErrors.add(getExcelError(nextCell, Constants.FIXED_PROPERTY_MUNICIPALITY, ExcelError.ERROR.MISSING_FIXED_PROPERTY));
                                } else {
                                    propertyModelMap.put(columnIndex, propertyModel);
                                }
                            }
                            if (excelColumn == ExcelColumn.PROJECT_NEIGHBOURHOOD) {
                                PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.PROJECT &&
                                    p.getPropertyType() == PropertyType.CATEGORY && p.getType() == PropertyKind.FIXED && p.getName().equals(Constants.FIXED_PROPERTY_NEIGHBOURHOOD)).findFirst().orElse(null);
                                if (propertyModel == null) {
                                    excelErrors.add(getExcelError(nextCell, Constants.FIXED_PROPERTY_MUNICIPALITY, ExcelError.ERROR.MISSING_FIXED_PROPERTY));
                                } else {
                                    propertyModelMap.put(columnIndex, propertyModel);
                                }
                            } else if (excelColumn.hasSubheader) {
                                String subheader = getStringValue(nextCell, dateFormatter, formulaEvaluator, excelErrors);
                                if (subheader == null || subheader.isEmpty()) {
                                    columnsMap.remove(columnIndex); //ignore columns with subheaders if the subheader is not defined
                                } else {
                                    switch (excelColumn) {
                                        case PROJECT_ROLE -> {
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.PROJECT &&
                                                p.getPropertyType() == PropertyType.CATEGORY && p.getType() == PropertyKind.CUSTOM && p.getName().equals(subheader)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_PROJECT_CATEGORY_PROPERTY));
                                            } else {
                                                propertyModelMap.put(columnIndex, propertyModel);
                                            }
                                        }
                                        case PROJECT_CUSTOM_PROPERTY -> {
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.PROJECT &&
                                                p.getType() == PropertyKind.CUSTOM && p.getName().equals(subheader)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_PROJECT_PROPERTY));
                                            } else {
                                                propertyModelMap.put(columnIndex, propertyModel);
                                            }
                                        }
                                        case HOUSEBLOCK_DELIVERY_DATE -> {
                                            String propertyName = ExcelStrings.DELIVERY_DATE + " " + subheader;
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.WONINGBLOK &&
                                                p.getPropertyType() == PropertyType.NUMERIC && p.getType() == PropertyKind.CUSTOM && p.getName().equals(propertyName)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_HOUSEBLOCK_NUMERIC_PROPERTY));
                                            } else {
                                                propertyModelMap.put(columnIndex, propertyModel);
                                            }
                                        }
                                        case HOUSEBLOCK_CUSTOM_PROPERTY -> {
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.WONINGBLOK &&
                                                p.getType() == PropertyKind.CUSTOM && p.getName().equals(subheader)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_HOUSEBLOCK_PROPERTY));
                                            } else {
                                                propertyModelMap.put(columnIndex, propertyModel);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!excelErrors.isEmpty()) { //there are problems with the headers, cannot attempt to read projects
                        transaction.rollback();
                        return Map.of(errors, excelErrors);
                    }
                }

                if (rowCount > 5) {
                    SelectModel excelProject = processExcelRow(repo, nextRow, columnsMap, propertyModelMap, dateFormatter, formulaEvaluator, excelErrors,
                        user, importTime);
                    if (excelProject != null) {
                        excelProjects.add(excelProject);
                    }
                }
            }
            if (excelErrors.isEmpty()) {
                transaction.commit();
                return Map.of(result, excelProjects);
            } else {
                transaction.rollback();
                return Map.of(errors, excelErrors);
            }
        }
    }


    private SelectModel processExcelRow(VngRepository repo, Row row, Map<Integer, ExcelColumn> columnsMap, Map<Integer, PropertyModel> columnPropertiesMap,
                                        DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors, User user, ZonedDateTime importTime) {
        ExcelRowModel rowModel = new ExcelRowModel();

        List<ExcelError> rowErrors = new ArrayList<>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell nextCell = cellIterator.next();
            int columnIndex = nextCell.getColumnIndex();

            ExcelColumn excelColumn = columnsMap.get(columnIndex);
            if (excelColumn != null) {
                switch (excelColumn) {
                    case PROJECT_ID -> {
                        rowModel.setId(getIntegerValue(nextCell, formatter, evaluator, rowErrors));
                        if (rowModel.getId() == null) {
                            return null; //missing id number means no information on that row
                        }
                    }
                    case PROJECT_NAME -> {
                        String projectName = getStringValue(nextCell, formatter, evaluator, rowErrors);
                        if (projectName == null || projectName.isBlank()) {
                            rowErrors.add(getExcelError(nextCell, projectName, ExcelError.ERROR.MISSING_PROJECT_NAME));
                        } else {
                            rowModel.setProjectName(projectName);
                        }
                    }
                    case PROJECT_PLAN_TYPE -> {
                        String excelPlanType = getStringValue(nextCell, formatter, evaluator, rowErrors);
                        if (excelPlanType != null && !excelPlanType.isBlank()) {
                            String planTypeStr = ExcelStrings.map.get(excelPlanType);
                            if (planTypeStr == null) {
                                rowErrors.add(getExcelError(nextCell, excelPlanType, ExcelError.ERROR.INVALID_PLAN_TYPE));
                            } else {
                                rowModel.setPlanType(PlanType.valueOf(planTypeStr));
                            }
                        }
                    }
                    case PROJECT_PROGRAMMING -> {
                        String programming = getStringValue(nextCell, formatter, evaluator, rowErrors);
                        if (programming != null && !programming.isBlank()) {
                            rowModel.setProgramming(Boolean.valueOf(programming));
                        }
                    }
                    case PROJECT_PRIORITY -> rowModel.setPriority(getStringValue(nextCell, formatter, evaluator, rowErrors)); //TODO
                    case PROJECT_MUNICIPALITY_ROLE -> rowModel.setMunicipalityRole(getStringValue(nextCell, formatter, evaluator, rowErrors)); //TODO

                    case PROJECT_ROLE, PROJECT_MUNICIPALITY, PROJECT_DISTRICT, PROJECT_NEIGHBOURHOOD ->
                        addProjectCategoryValue(rowModel, columnPropertiesMap.get(columnIndex), nextCell, formatter, evaluator, rowErrors);

                    case PROJECT_STATUS -> {
                        String excelProjectStatus = getStringValue(nextCell, formatter, evaluator, rowErrors);
                        if (excelProjectStatus == null || excelProjectStatus.isBlank() || ExcelStrings.map.get(excelProjectStatus) == null) {
                            rowErrors.add(getExcelError(nextCell, excelProjectStatus, ExcelError.ERROR.MISSING_PROJECT_STATUS));
                        } else {
                            rowModel.setProjectStatus(ProjectStatus.valueOf(ExcelStrings.map.get(excelProjectStatus)));
                        }
                    }
                    case PROJECT_START_DATE -> {
                        LocalDate projectStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (projectStartDate == null) {
                            rowErrors.add(getExcelError(nextCell, null, ExcelError.ERROR.MISSING_PROJECT_START_DATE));
                        }
                        rowModel.setProjectStartDate(getLocalDateValue(nextCell, formatter, rowErrors));
                    }
                    case PROJECT_END_DATE -> {
                        LocalDate projectEndDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (projectEndDate == null) {
                            rowErrors.add(getExcelError(nextCell, null, ExcelError.ERROR.MISSING_PROJECT_END_DATE));
                        }
                        rowModel.setProjectEndDate(getLocalDateValue(nextCell, formatter, rowErrors));
                    }

                    case PROJECT_PHASE_1_CONCEPT -> addProjectPhase(rowModel, ProjectPhase._1_CONCEPT, nextCell, formatter, rowErrors);
                    case PROJECT_PHASE_2_INITIATIVE -> addProjectPhase(rowModel, ProjectPhase._2_INITIATIVE, nextCell, formatter, rowErrors);
                    case PROJECT_PHASE_3_DEFINITION -> addProjectPhase(rowModel, ProjectPhase._3_DEFINITION, nextCell, formatter, rowErrors);
                    case PROJECT_PHASE_4_DESIGN -> addProjectPhase(rowModel, ProjectPhase._4_DESIGN, nextCell, formatter, rowErrors);
                    case PROJECT_PHASE_5_PREPARATION -> addProjectPhase(rowModel, ProjectPhase._5_PREPARATION, nextCell, formatter, rowErrors);
                    case PROJECT_PHASE_6_REALIZATION -> addProjectPhase(rowModel, ProjectPhase._6_REALIZATION, nextCell, formatter, rowErrors);
                    case PROJECT_PHASE_7_AFTERCARE -> addProjectPhase(rowModel, ProjectPhase._7_AFTERCARE, nextCell, formatter, rowErrors);

                    case PROJECT_PLAN_STATUS_4A_OPGENOMEN_IN_VISIE ->
                        addProjectPlanStatus(rowModel, PlanStatus._4A_OPGENOMEN_IN_VISIE, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_4B_NIET_OPGENOMEN_IN_VISIE ->
                        addProjectPlanStatus(rowModel, PlanStatus._4B_NIET_OPGENOMEN_IN_VISIE, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_3_IN_VOORBEREIDING ->
                        addProjectPlanStatus(rowModel, PlanStatus._3_IN_VOORBEREIDING, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_2A_VASTGESTELD -> addProjectPlanStatus(rowModel, PlanStatus._2A_VASTGESTELD, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_2B_VASTGESTELD_MET_UITWERKING_NODIG ->
                        addProjectPlanStatus(rowModel, PlanStatus._2B_VASTGESTELD_MET_UITWERKING_NODIG, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_2C_VASTGESTELD_MET_BW_NODIG ->
                        addProjectPlanStatus(rowModel, PlanStatus._2C_VASTGESTELD_MET_BW_NODIG, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK -> addProjectPlanStatus(rowModel, PlanStatus._1A_ONHERROEPELIJK, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG ->
                        addProjectPlanStatus(rowModel, PlanStatus._1B_ONHERROEPELIJK_MET_UITWERKING_NODIG, nextCell, formatter, rowErrors);
                    case PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG ->
                        addProjectPlanStatus(rowModel, PlanStatus._1C_ONHERROEPELIJK_MET_BW_NODIG, nextCell, formatter, rowErrors);

                    case PROJECT_CUSTOM_PROPERTY -> {
                        PropertyModel propertyModel = columnPropertiesMap.get(columnIndex);
                        switch (propertyModel.getPropertyType()) {
                            case CATEGORY -> addProjectCategoryValue(rowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                            case ORDINAL -> {
                            } //TODO
                            case NUMERIC -> {
                                //TODO
                            }
                            case BOOLEAN -> {
                            } //TODO
                            case TEXT -> addProjectTextValue(rowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                        }
                    }

                }
            }
        }

        if (rowModel.getId() == null) { //sometimes, excel cells are missing on empty rows - check again that the id is null
            return null;
        }

        if (rowErrors.isEmpty()) { //no errors validating individual fields
            rowModel.validate(row.getRowNum() + 1, rowErrors, importTime.toLocalDate()); //business logic validation
        }

        if (rowErrors.isEmpty()) { //still no errors
            return rowModel.persistProjectAndHouseblocks(repo, user, importTime);
        } else {
            excelErrors.addAll(rowErrors);
            return null;
        }

    }

    public void addProjectPhase(ExcelRowModel rowModel, ProjectPhase phase, Cell cell, DataFormatter formatter, List<ExcelError> excelErrors) {
        LocalDate phaseStartDate = getLocalDateValue(cell, formatter, excelErrors);
        if (phaseStartDate != null) {
            rowModel.getProjectPhases().put(phase, phaseStartDate);
        }
    }

    public void addProjectPlanStatus(ExcelRowModel rowModel, PlanStatus planStatus, Cell cell, DataFormatter formatter, List<ExcelError> excelErrors) {
        LocalDate planStatusStartDate = getLocalDateValue(cell, formatter, excelErrors);
        if (planStatusStartDate != null) {
            rowModel.getProjectPlanStatuses().put(planStatus, planStatusStartDate);
        }
    }

    private void addProjectCategoryValue(ExcelRowModel rowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String categoryValueStr = getStringValue(cell, formatter, evaluator, excelErrors);
        if (categoryValueStr != null && !categoryValueStr.isBlank()) {
            SelectDisabledModel categoryValue = propertyModel.getActiveCategoryValue(categoryValueStr);
            if (categoryValue == null) {
                excelErrors.add(getExcelError(cell, categoryValueStr, ExcelError.ERROR.UNKNOWN_PROPERTY_VALUE));
            } else {
                rowModel.getProjectCategoryProperties().put(propertyModel.getId(), categoryValue.getId());
            }
        }
    }

    private void addProjectTextValue(ExcelRowModel rowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String textValue = getStringValue(cell, formatter, evaluator, excelErrors);
        if (textValue != null && !textValue.isBlank()) {
            rowModel.getProjectStringProperties().put(propertyModel.getId(), textValue);
        }
    }

    private String getStringValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        return switch (cell.getCellType()) {
            case BLANK -> null;
            case BOOLEAN -> cell.getBooleanCellValue() ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> formatter.formatCellValue(cell).trim(); //stringify value
            case FORMULA -> {   //try to get calculated value
                CellValue formulaCellValue = evaluator.evaluate(cell);
                yield switch (formulaCellValue.getCellType()) {
                    case STRING -> formulaCellValue.getStringValue().trim();
                    case NUMERIC -> String.valueOf(formulaCellValue.getNumberValue()).trim();
                    case BOOLEAN -> formulaCellValue.getBooleanValue() ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
                    default -> {
                        excelErrors.add(getExcelError(cell, null, ExcelError.ERROR.WRONG_TYPE_NOT_STRING));
                        yield null;
                    }
                };
            }
            default -> {
                excelErrors.add(getExcelError(cell, null, ExcelError.ERROR.WRONG_TYPE_NOT_STRING));
                yield null;
            }
        };
    }

    private Double getDoubleValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Object dblCellValue = switch (cell.getCellType()) {
            case BLANK -> null;
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {   //check it's not date formatted
                if (DateUtil.isCellDateFormatted(cell)) {
                    excelErrors.add(getExcelError(cell, formatter.formatCellValue(cell), ExcelError.ERROR.WRONG_TYPE_NOT_NUMERIC));
                    yield null;
                } else {
                    yield cell.getNumericCellValue();
                }
            }
            case FORMULA -> {   //try to get calculated value
                CellValue formulaCellValue = evaluator.evaluate(cell);
                yield switch (formulaCellValue.getCellType()) {
                    case STRING -> formulaCellValue.getStringValue().trim();
                    case NUMERIC -> formulaCellValue.getNumberValue();
                    default -> {
                        excelErrors.add(getExcelError(cell, formatter.formatCellValue(cell), ExcelError.ERROR.WRONG_TYPE_NOT_NUMERIC));
                        yield null;
                    }
                };
            }
            default -> {
                excelErrors.add(getExcelError(cell, formatter.formatCellValue(cell), ExcelError.ERROR.WRONG_TYPE_NOT_NUMERIC));
                yield null;
            }
        };

        //a string value was assigned, try to parse it as a number
        if (dblCellValue instanceof String) {
            if (((String) dblCellValue).isBlank()) {
                return null;
            }
            try {
                return Double.parseDouble((String) dblCellValue);
            } catch (NumberFormatException ex) {
                excelErrors.add(getExcelError(cell, formatter.formatCellValue(cell), ExcelError.ERROR.WRONG_TYPE_NOT_NUMERIC));
                return null;
            }
        }

        //a double value or null were assigned
        return (Double) dblCellValue;
    }

    private Integer getIntegerValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Double doubleValue = getDoubleValue(cell, formatter, evaluator, excelErrors);
        if (doubleValue != null) {
            return doubleValue.intValue();
        }
        return null;
    }

    private LocalDate getLocalDateValue(Cell cell, DataFormatter formatter, List<ExcelError> excelErrors) {
        return switch (cell.getCellType()) {
            case BLANK -> null;
            case NUMERIC -> {   //we can only extract date from cell formatted as date
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate();
                }
                yield null;
            }
            default -> {
                excelErrors.add(getExcelError(cell, formatter.formatCellValue(cell), ExcelError.ERROR.WRONG_TYPE_NOT_DATE));
                yield null;
            }
        };
    }

    private ExcelError getExcelError(Cell cell, String cellValue, ExcelError.ERROR error) {
        String columnString = CellReference.convertNumToColString(cell.getColumnIndex());
        return new ExcelError(cell.getRowIndex() + 1, columnString, cellValue, error);
    }
}
