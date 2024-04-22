package nl.vng.diwi.services;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.ObjectType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.enums.PropertyType;
import nl.vng.diwi.generic.Constants;
import nl.vng.diwi.models.ExcelError;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.SelectModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static nl.vng.diwi.services.ExcelTableHeader.Column.*;

public class ExcelImportService {
    private static final Logger logger = LogManager.getLogger();

    public static String errors = "errors";
    public static String result = "result";

    public ExcelImportService() {
    }

    public Map<String, Object> importExcel(String excelFilePath, VngRepository repo, UUID loggedInUserUuid) {

        List<ExcelError> excelErrors = new ArrayList<>();
        List<SelectModel> excelProjects = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(excelFilePath); Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet dataSheet = workbook.getSheetAt(0);
            if (dataSheet == null) {
                return Map.of(errors, List.of(new ExcelError(ExcelError.ERROR.MISSING_DATA_SHEET)));
            }

            DataFormatter dateFormatter = new DataFormatter();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            List<PropertyModel> activeProperties = repo.getPropertyDAO().getPropertiesList(null, false, null);

            Map<Integer, ExcelTableHeader> tableHeaderMap = new HashMap<>();

            ZonedDateTime importTime = ZonedDateTime.now();

            Iterator<Row> iterator = dataSheet.iterator();
            int rowCount = 0;

            try (AutoCloseTransaction transaction = repo.beginTransaction()) {

                User user = repo.getReferenceById(User.class, loggedInUserUuid);
                while (iterator.hasNext()) {
                    Row nextRow = iterator.next();
                    rowCount++;

                    if (rowCount == 2) { //sections
                        ExcelTableHeader.Section lastSection = null; //sections
                        Iterator<Cell> cellIterator = nextRow.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell nextCell = cellIterator.next();
                            int columnIndex = nextCell.getColumnIndex();
                            String sectionName = getStringValue(nextCell, dateFormatter, formulaEvaluator, excelErrors);
                            ExcelTableHeader.Section excelSection = ExcelTableHeader.Section.findByName(sectionName);
                            if (excelSection != null) {
                                lastSection = excelSection;
                            }
                            if (lastSection == ExcelTableHeader.Section.CONTROL_CONSTRUCTION) {
                                break;
                            } else {
                                ExcelTableHeader tableHeader = new ExcelTableHeader();
                                tableHeader.setSection(lastSection);
                                tableHeaderMap.put(columnIndex, tableHeader);
                            }
                        }
                    }

                    if (rowCount == 4) { //headers - set columns and propertymodel for fixedd properties
                        Iterator<Cell> cellIterator = nextRow.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell nextCell = cellIterator.next();
                            int columnIndex = nextCell.getColumnIndex();
                            if (tableHeaderMap.containsKey(columnIndex)) {
                                ExcelTableHeader tableHeader = tableHeaderMap.get(columnIndex);
                                String headerName = getStringValue(nextCell, dateFormatter, formulaEvaluator, excelErrors);
                                ExcelTableHeader.Column excelColumn = ExcelTableHeader.Column.findByName(headerName);
                                if (excelColumn != null) {
                                    tableHeader.setColumn(excelColumn);
                                    switch (excelColumn) {
                                        case PROJECT_MUNICIPALITY_ROLE ->
                                            addTableHeaderFixedPropertyModel(tableHeader, Constants.FIXED_PROPERTY_MUNICIPALITY_ROLE,
                                                ObjectType.PROJECT, PropertyType.CATEGORY, nextCell, activeProperties, excelErrors);
                                        case PROJECT_PRIORITY -> addTableHeaderFixedPropertyModel(tableHeader, Constants.FIXED_PROPERTY_PRIORITY,
                                            ObjectType.PROJECT, PropertyType.ORDINAL, nextCell, activeProperties, excelErrors);
                                        case PROJECT_MUNICIPALITY -> addTableHeaderFixedPropertyModel(tableHeader, Constants.FIXED_PROPERTY_MUNICIPALITY,
                                            ObjectType.PROJECT, PropertyType.CATEGORY, nextCell, activeProperties, excelErrors);
                                        case PROJECT_DISTRICT -> addTableHeaderFixedPropertyModel(tableHeader, Constants.FIXED_PROPERTY_DISTRICT,
                                            ObjectType.PROJECT, PropertyType.CATEGORY, nextCell, activeProperties, excelErrors);
                                        case PROJECT_NEIGHBOURHOOD -> addTableHeaderFixedPropertyModel(tableHeader, Constants.FIXED_PROPERTY_NEIGHBOURHOOD,
                                            ObjectType.PROJECT, PropertyType.CATEGORY, nextCell, activeProperties, excelErrors);
                                        case HOUSEBLOCK_PHYSICAL_APPEARANCE ->
                                            addTableHeaderFixedPropertyModel(tableHeader, Constants.FIXED_PROPERTY_PHYSICAL_APPEARANCE,
                                                ObjectType.WONINGBLOK, PropertyType.CATEGORY, nextCell, activeProperties, excelErrors);
                                        case HOUSEBLOCK_TARGET_GROUP -> addTableHeaderFixedPropertyModel(tableHeader, Constants.FIXED_PROPERTY_TARGET_GROUP,
                                            ObjectType.WONINGBLOK, PropertyType.CATEGORY, nextCell, activeProperties, excelErrors);
                                    }
                                }
                            }
                        }
                        tableHeaderMap = tableHeaderMap.entrySet().stream()
                            .filter(h -> h.getValue().getColumn() != null)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    }

                    if (rowCount == 5) { //subheaders - set subheader values and property model for columns with subheaders
                        Iterator<Cell> cellIterator = nextRow.cellIterator();
                        while (cellIterator.hasNext()) {
                            Cell nextCell = cellIterator.next();
                            int columnIndex = nextCell.getColumnIndex();
                            if (tableHeaderMap.containsKey(columnIndex) && tableHeaderMap.get(columnIndex).getColumn().hasSubheader) {
                                ExcelTableHeader tableHeader = tableHeaderMap.get(columnIndex);
                                String subheader = getStringValue(nextCell, dateFormatter, formulaEvaluator, excelErrors);
                                if (subheader == null || subheader.isEmpty()) {
                                    tableHeaderMap.remove(columnIndex); //ignore columns with subheaders if the subheader is not defined
                                } else {
                                    tableHeader.setSubheader(subheader);
                                    switch (tableHeader.getColumn()) {
                                        case PROJECT_ROLE -> {
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.PROJECT &&
                                                p.getPropertyType() == PropertyType.CATEGORY && p.getType() == PropertyKind.CUSTOM && p.getName().equals(subheader)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_PROJECT_CATEGORY_PROPERTY));
                                            } else {
                                                tableHeader.setPropertyModel(propertyModel);
                                            }
                                        }
                                        case PROJECT_CUSTOM_PROPERTY -> {
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.PROJECT &&
                                                p.getType() == PropertyKind.CUSTOM && p.getName().equals(subheader)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_PROJECT_PROPERTY));
                                            } else {
                                                tableHeader.setPropertyModel(propertyModel);
                                            }
                                        }
                                        case HOUSEBLOCK_DELIVERY_DATE -> {
                                            String propertyName = ExcelStrings.DELIVERY_DATE + " " + subheader;
                                            tableHeader.setSubheaderDateValue(getLocalDateValue(nextCell, dateFormatter, excelErrors));
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.WONINGBLOK &&
                                                p.getPropertyType() == PropertyType.NUMERIC && p.getType() == PropertyKind.CUSTOM && p.getName().equals(propertyName)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_HOUSEBLOCK_NUMERIC_PROPERTY));
                                            } else {
                                                tableHeader.setPropertyModel(propertyModel);
                                            }
                                        }
                                        case HOUSEBLOCK_CUSTOM_PROPERTY -> {
                                            PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == ObjectType.WONINGBLOK &&
                                                p.getType() == PropertyKind.CUSTOM && p.getName().equals(subheader)).findFirst().orElse(null);
                                            if (propertyModel == null) {
                                                excelErrors.add(getExcelError(nextCell, subheader, ExcelError.ERROR.UNKNOWN_HOUSEBLOCK_PROPERTY));
                                            } else {
                                                tableHeader.setPropertyModel(propertyModel);
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
                        SelectModel excelProject = processExcelRow(repo, nextRow, tableHeaderMap, dateFormatter, formulaEvaluator, excelErrors,
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
        } catch (IOException | NotOfficeXmlFileException e) { //excelFile could not be read. Return error.
            logger.error("Error creating workbook", e);
            return Map.of(errors, List.of(new ExcelError(ExcelError.ERROR.IO_ERROR)));
        }
    }

    private void addTableHeaderFixedPropertyModel(ExcelTableHeader tableHeader, String propertyName, ObjectType objectType, PropertyType propertyType,
                                                  Cell cell, List<PropertyModel> activeProperties, List<ExcelError> excelErrors) {
        PropertyModel propertyModel = activeProperties.stream().filter(p -> p.getObjectType() == objectType &&
            p.getPropertyType() == propertyType && p.getType() == PropertyKind.FIXED && p.getName().equals(propertyName)).findFirst().orElse(null);
        if (propertyModel == null) {
            excelErrors.add(getExcelError(cell, propertyName, ExcelError.ERROR.MISSING_FIXED_PROPERTY));
        } else {
            tableHeader.setPropertyModel(propertyModel);
        }
    }

    private SelectModel processExcelRow(VngRepository repo, Row row, Map<Integer, ExcelTableHeader> tableHeaderMap, DataFormatter formatter,
                                        FormulaEvaluator evaluator, List<ExcelError> excelErrors, User user, ZonedDateTime importTime) {
        ExcelProjectRowModel rowModel = new ExcelProjectRowModel();

        List<ExcelError> rowErrors = new ArrayList<>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell nextCell = cellIterator.next();
            int columnIndex = nextCell.getColumnIndex();

            ExcelTableHeader tableHeader = tableHeaderMap.get(columnIndex);
            if (tableHeader != null) {
                if (tableHeader.getSection() == ExcelTableHeader.Section.PROJECT_DATA) {
                    switch (tableHeader.getColumn()) {
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
                            Boolean programming = getBooleanValue(nextCell, formatter, evaluator, rowErrors);
                            if (programming != null) {
                                rowModel.setProgramming(programming);
                            }
                        }

                        case PROJECT_ROLE -> addProjectCategoryProperty(rowModel, tableHeader.getPropertyModel(), nextCell, formatter, evaluator, rowErrors);
                        case PROJECT_MUNICIPALITY ->
                            rowModel.setHasMunicipality(addProjectCategoryProperty(rowModel, tableHeader.getPropertyModel(), nextCell, formatter, evaluator, rowErrors));
                        case PROJECT_DISTRICT ->
                            rowModel.setHasDistrict(addProjectCategoryProperty(rowModel, tableHeader.getPropertyModel(), nextCell, formatter, evaluator, rowErrors));
                        case PROJECT_NEIGHBOURHOOD ->
                            rowModel.setHasNeighbourhood(addProjectCategoryProperty(rowModel, tableHeader.getPropertyModel(), nextCell, formatter, evaluator, rowErrors));

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
                        case PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK ->
                            addProjectPlanStatus(rowModel, PlanStatus._1A_ONHERROEPELIJK, nextCell, formatter, rowErrors);
                        case PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG ->
                            addProjectPlanStatus(rowModel, PlanStatus._1B_ONHERROEPELIJK_MET_UITWERKING_NODIG, nextCell, formatter, rowErrors);
                        case PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG ->
                            addProjectPlanStatus(rowModel, PlanStatus._1C_ONHERROEPELIJK_MET_BW_NODIG, nextCell, formatter, rowErrors);

                        case PROJECT_CUSTOM_PROPERTY, PROJECT_PRIORITY, PROJECT_MUNICIPALITY_ROLE -> {
                            PropertyModel propertyModel = tableHeader.getPropertyModel();
                            switch (propertyModel.getPropertyType()) {
                                case CATEGORY -> addProjectCategoryProperty(rowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                case ORDINAL -> addProjectOrdinalProperty(rowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                case NUMERIC -> addProjectNumericProperty(rowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                case BOOLEAN -> addProjectBooleanProperty(rowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                case TEXT -> addProjectTextProperty(rowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                            }
                        }
                    }
                }
                if (tableHeader.getSection() == ExcelTableHeader.Section.HOUSE_NUMBERS) {
                    Integer mutationAmount = getIntegerValue(nextCell, formatter, evaluator, rowErrors);
                    if (mutationAmount != null && mutationAmount > 0) {
                        if (tableHeader.getColumn() == HOUSEBLOCK_MUTATION_BUILD) {
                            rowModel.setConstructionHouseblock(new ExcelProjectRowModel.HouseblockRowModel(MutationType.CONSTRUCTION, mutationAmount));
                        } else {
                            rowModel.setDemolitionHouseblock(new ExcelProjectRowModel.HouseblockRowModel(MutationType.DEMOLITION, mutationAmount));
                        }
                    }
                }

                if (tableHeader.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA || tableHeader.getSection() == ExcelTableHeader.Section.DEMOLITION_DATA) {
                    ExcelProjectRowModel.HouseblockRowModel houseblockRowModel = (tableHeader.getSection() == ExcelTableHeader.Section.CONSTRUCTION_DATA) ? rowModel.getConstructionHouseblock() : rowModel.getDemolitionHouseblock();
                    if (houseblockRowModel != null) {
                        switch (tableHeader.getColumn()) {
                            case HOUSEBLOCK_DELIVERY_DATE -> {
                                PropertyModel propertyModel = tableHeader.getPropertyModel();
                                boolean newDeliveryDate = addHouseblockIntegerNumericProperty(houseblockRowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                if (newDeliveryDate) {
                                    houseblockRowModel.addDeliveryDate(tableHeader.getSubheaderDateValue());
                                }
                            }

                            case HOUSEBLOCK_PROPERTY_TYPE_OWNER, HOUSEBLOCK_PROPERTY_TYPE_LANDLORD, HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION,
                                 HOUSEBLOCK_PROPERTY_TYPE_UNKNOWN ->
                                addHouseblockPropertyType(houseblockRowModel, tableHeader.getColumn(), nextCell, formatter, evaluator, rowErrors);

                            case HOUSEBLOCK_PROPERTY_PURCHASE_PRICE, HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE,
                                 HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE ->
                                addHouseblockOwnershipValue(houseblockRowModel, tableHeader.getColumn(), tableHeader.getSubheader(), nextCell, formatter, evaluator, rowErrors);

                            case HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION, HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION,
                                 HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION ->
                                addHouseblockGroundPosition(houseblockRowModel, tableHeader.getColumn(), nextCell, formatter, evaluator, rowErrors);

                            case HOUSEBLOCK_TARGET_GROUP -> {
                                Integer targetGroupAmount = getIntegerValue(nextCell, formatter, evaluator, rowErrors);
                                if (targetGroupAmount != null && targetGroupAmount > 0) {
                                    SelectModel categoryValue = tableHeader.getPropertyModel().getActiveCategoryValue(tableHeader.getSubheader());
                                    if (categoryValue == null) {
                                        excelErrors.add(getExcelError(nextCell, tableHeader.getSubheader(), ExcelError.ERROR.UNKNOWN_PROPERTY_VALUE));
                                    } else {
                                        houseblockRowModel.getTargetGroupMap().put(categoryValue.getId(), targetGroupAmount);
                                    }
                                }
                            }
                            case HOUSEBLOCK_PHYSICAL_APPEARANCE -> {
                                Integer physicalAppearanceAmount = getIntegerValue(nextCell, formatter, evaluator, rowErrors);
                                if (physicalAppearanceAmount != null && physicalAppearanceAmount > 0) {
                                    SelectModel categoryValue = tableHeader.getPropertyModel().getActiveCategoryValue(tableHeader.getSubheader());
                                    if (categoryValue == null) {
                                        excelErrors.add(getExcelError(nextCell, tableHeader.getSubheader(), ExcelError.ERROR.UNKNOWN_PROPERTY_VALUE));
                                    } else {
                                        houseblockRowModel.getPhysicalAppearanceMap().put(categoryValue.getId(), physicalAppearanceAmount);
                                    }
                                }
                            }

                            case HOUSEBLOCK_TYPE_MULTI_FAMILY, HOUSEBLOCK_TYPE_SINGLE_FAMILY, HOUSEBLOCK_TYPE_UNKNOWN ->
                                addHouseblockHouseType(houseblockRowModel, tableHeader.getColumn(), nextCell, formatter, evaluator, rowErrors);

                            case HOUSEBLOCK_CUSTOM_PROPERTY -> {
                                PropertyModel propertyModel = tableHeader.getPropertyModel();
                                switch (propertyModel.getPropertyType()) {
                                    case CATEGORY ->
                                        addHouseblockCategoryProperty(houseblockRowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                    case ORDINAL -> addHouseblockOrdinalProperty(houseblockRowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                    case NUMERIC -> addHouseblockNumericProperty(houseblockRowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                    case BOOLEAN -> addHouseblockBooleanProperty(houseblockRowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                    case TEXT -> addHouseblockTextProperty(houseblockRowModel, propertyModel, nextCell, formatter, evaluator, rowErrors);
                                }
                            }
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

    public void addProjectPhase(ExcelProjectRowModel rowModel, ProjectPhase phase, Cell cell, DataFormatter formatter, List<ExcelError> excelErrors) {
        LocalDate phaseStartDate = getLocalDateValue(cell, formatter, excelErrors);
        if (phaseStartDate != null) {
            rowModel.getProjectPhasesMap().put(phase, phaseStartDate);
        }
    }

    public void addProjectPlanStatus(ExcelProjectRowModel rowModel, PlanStatus planStatus, Cell cell, DataFormatter formatter, List<ExcelError> excelErrors) {
        LocalDate planStatusStartDate = getLocalDateValue(cell, formatter, excelErrors);
        if (planStatusStartDate != null) {
            rowModel.getProjectPlanStatusesMap().put(planStatus, planStatusStartDate);
        }
    }

    private boolean addProjectCategoryProperty(ExcelProjectRowModel rowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String categoryValueStr = getStringValue(cell, formatter, evaluator, excelErrors);
        if (categoryValueStr != null && !categoryValueStr.isBlank()) {
            SelectModel categoryValue = propertyModel.getActiveCategoryValue(categoryValueStr);
            if (categoryValue == null) {
                excelErrors.add(getExcelError(cell, categoryValueStr, ExcelError.ERROR.UNKNOWN_PROPERTY_VALUE));
                return false;
            } else {
                rowModel.getProjectCategoryPropsMap().put(propertyModel.getId(), categoryValue.getId());
                return true;
            }
        }
        return false;
    }

    private boolean addProjectOrdinalProperty(ExcelProjectRowModel rowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String ordinalValueStr = getStringValue(cell, formatter, evaluator, excelErrors);
        if (ordinalValueStr != null && !ordinalValueStr.isBlank()) {
            SelectModel ordinalValue = propertyModel.getActiveOrdinalValue(ordinalValueStr);
            if (ordinalValue == null) {
                excelErrors.add(getExcelError(cell, ordinalValueStr, ExcelError.ERROR.UNKNOWN_PROPERTY_VALUE));
                return false;
            } else {
                rowModel.getProjectOrdinalPropsMap().put(propertyModel.getId(), ordinalValue.getId());
                return true;
            }
        }
        return false;
    }

    private void addProjectTextProperty(ExcelProjectRowModel rowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String textValue = getStringValue(cell, formatter, evaluator, excelErrors);
        if (textValue != null && !textValue.isBlank()) {
            rowModel.getProjectStringPropsMap().put(propertyModel.getId(), textValue);
        }
    }


    private void addProjectNumericProperty(ExcelProjectRowModel rowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Double doubleValue = getDoubleValue(cell, formatter, evaluator, excelErrors);
        if (doubleValue != null) {
            rowModel.getProjectNumericPropsMap().put(propertyModel.getId(), doubleValue);
        }
    }

    private void addProjectBooleanProperty(ExcelProjectRowModel rowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Boolean booleanValue = getBooleanValue(cell, formatter, evaluator, excelErrors);
        if (booleanValue != null) {
            rowModel.getProjectBooleanPropsMap().put(propertyModel.getId(), booleanValue);
        }
    }

    private void addHouseblockPropertyType(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, ExcelTableHeader.Column column, Cell nextCell,
                                           DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> rowErrors) {
        Integer ownershipTypeAmount = getIntegerValue(nextCell, formatter, evaluator, rowErrors);
        if (ownershipTypeAmount != null && ownershipTypeAmount > 0) {
            OwnershipType ownershipType = switch (column) {
                case HOUSEBLOCK_PROPERTY_TYPE_OWNER -> OwnershipType.KOOPWONING;
                case HOUSEBLOCK_PROPERTY_TYPE_LANDLORD -> OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER;
                case HOUSEBLOCK_PROPERTY_TYPE_HOUSING_ASSOCIATION -> OwnershipType.HUURWONING_WONINGCORPORATIE;
                default -> null;
            };
            houseblockRowModel.getOwnershipTypeMap().put(ownershipType, ownershipTypeAmount);
        }
    }

    private void addHouseblockOwnershipValue(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, ExcelTableHeader.Column column, String subheader, Cell cell,
                                             DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Integer ownershipValueAmount = getIntegerValue(cell, formatter, evaluator, excelErrors);
        if (ownershipValueAmount != null && ownershipValueAmount > 0) {
            SingleValueOrRangeModel<Integer> subheaderRange;
            if (subheader.equalsIgnoreCase("Onbekend")) {
                subheaderRange = null;
            } else {
                subheaderRange = new SingleValueOrRangeModel<>();
                List<String> substringRangeStrList = Arrays.asList(subheader.replaceAll("\\s", "").replaceAll(",", ".").split("-"));
                if (substringRangeStrList.size() != 2) {
                    excelErrors.add(getExcelError(cell, subheader, ExcelError.ERROR.INVALID_RANGE));
                }
                try {
                    Double minValue = Double.parseDouble(substringRangeStrList.get(0)) * 100;
                    subheaderRange.setMin(minValue.intValue());
                    String maxValueStr = substringRangeStrList.get(1);
                    if (!maxValueStr.equalsIgnoreCase("Inf")) {
                        Double maxValue = Double.parseDouble(substringRangeStrList.get(1)) * 100;
                        subheaderRange.setMax(maxValue.intValue());
                    }
                } catch (NumberFormatException e) {
                    excelErrors.add(getExcelError(cell, subheader, ExcelError.ERROR.INVALID_RANGE));
                }
            }
            HouseblockSnapshotModel.OwnershipValue ownershipValue = new HouseblockSnapshotModel.OwnershipValue();
            ownershipValue.setAmount(ownershipValueAmount);
            switch (column) {
                case HOUSEBLOCK_PROPERTY_PURCHASE_PRICE -> {
                    ownershipValue.setType(OwnershipType.KOOPWONING);
                    ownershipValue.setValue(subheaderRange);
                    ownershipValue.setRentalValue(null);
                }
                case HOUSEBLOCK_PROPERTY_LANDLORD_RENTAL_PRICE -> {
                    ownershipValue.setType(OwnershipType.HUURWONING_PARTICULIERE_VERHUURDER);
                    ownershipValue.setRentalValue(subheaderRange);
                    ownershipValue.setValue(null);
                }
                case HOUSEBLOCK_PROPERTY_HOUSING_ASSOCIATION_RENTAL_PRICE -> {
                    ownershipValue.setType(OwnershipType.HUURWONING_WONINGCORPORATIE);
                    ownershipValue.setRentalValue(subheaderRange);
                    ownershipValue.setValue(null);
                }
            }
            houseblockRowModel.getOwnershipValues().add(ownershipValue);
        }
    }

    private void addHouseblockHouseType(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, ExcelTableHeader.Column column, Cell nextCell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> rowErrors) {
        Integer houseTypeAmount = getIntegerValue(nextCell, formatter, evaluator, rowErrors);
        if (houseTypeAmount != null && houseTypeAmount > 0) {
            HouseType houseType = switch (column) {
                case HOUSEBLOCK_TYPE_MULTI_FAMILY -> HouseType.MEERGEZINSWONING;
                case HOUSEBLOCK_TYPE_SINGLE_FAMILY -> HouseType.EENGEZINSWONING;
                default -> null;
            };
            houseblockRowModel.getHouseTypeMap().put(houseType, houseTypeAmount);
        }
    }

    private void addHouseblockGroundPosition(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, ExcelTableHeader.Column column, Cell nextCell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> rowErrors) {
        Integer groundPosAmount = getIntegerValue(nextCell, formatter, evaluator, rowErrors);
        if (groundPosAmount != null && groundPosAmount > 0) {
            GroundPosition groundPosition = switch (column) {
                case HOUSEBLOCK_GROUND_POSITION_NO_PERMISSION -> GroundPosition.GEEN_TOESTEMMING_GRONDEIGENAAR;
                case HOUSEBLOCK_GROUND_POSITION_COOPERATE_INTENTION -> GroundPosition.INTENTIE_MEDEWERKING_GRONDEIGENAAR;
                case HOUSEBLOCK_GROUND_POSITION_FORMAL_PERMISSION -> GroundPosition.FORMELE_TOESTEMMING_GRONDEIGENAAR;
                default -> null;
            };
            houseblockRowModel.getGroundPositionMap().put(groundPosition, groundPosAmount);
        }
    }

    private void addHouseblockTextProperty(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, PropertyModel propertyModel, Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String textValue = getStringValue(cell, formatter, evaluator, excelErrors);
        if (textValue != null && !textValue.isBlank()) {
            houseblockRowModel.getHouseblockStringPropsMap().put(propertyModel.getId(), textValue);
        }
    }

    private void addHouseblockCategoryProperty(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, PropertyModel propertyModel, Cell cell,
                                               DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String categoryValueStr = getStringValue(cell, formatter, evaluator, excelErrors);
        if (categoryValueStr != null && !categoryValueStr.isBlank()) {
            SelectModel categoryValue = propertyModel.getActiveCategoryValue(categoryValueStr);
            if (categoryValue == null) {
                excelErrors.add(getExcelError(cell, categoryValueStr, ExcelError.ERROR.UNKNOWN_PROPERTY_VALUE));
            } else {
                houseblockRowModel.getHouseblockCategoryPropsMap().put(propertyModel.getId(), categoryValue.getId());
            }
        }
    }

    private void addHouseblockOrdinalProperty(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, PropertyModel propertyModel, Cell cell,
                                              DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String ordinalValueStr = getStringValue(cell, formatter, evaluator, excelErrors);
        if (ordinalValueStr != null && !ordinalValueStr.isBlank()) {
            SelectModel ordinalValue = propertyModel.getActiveOrdinalValue(ordinalValueStr);
            if (ordinalValue == null) {
                excelErrors.add(getExcelError(cell, ordinalValueStr, ExcelError.ERROR.UNKNOWN_PROPERTY_VALUE));
            } else {
                houseblockRowModel.getHouseblockOrdinalPropsMap().put(propertyModel.getId(), ordinalValue.getId());
            }
        }
    }

    private boolean addHouseblockIntegerNumericProperty(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, PropertyModel propertyModel, Cell cell,
                                                        DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Integer integerValue = getIntegerValue(cell, formatter, evaluator, excelErrors);
        if (integerValue != null && integerValue > 0) {
            houseblockRowModel.getHouseblockNumericPropsMap().put(propertyModel.getId(), integerValue.doubleValue());
            return true;
        }
        return false;
    }

    private void addHouseblockNumericProperty(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, PropertyModel propertyModel, Cell cell,
                                              DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Double doubleValue = getDoubleValue(cell, formatter, evaluator, excelErrors);
        if (doubleValue != null && doubleValue > 0) {
            houseblockRowModel.getHouseblockNumericPropsMap().put(propertyModel.getId(), doubleValue);
        }
    }

    private void addHouseblockBooleanProperty(ExcelProjectRowModel.HouseblockRowModel houseblockRowModel, PropertyModel propertyModel, Cell cell,
                                              DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        Boolean booleanValue = getBooleanValue(cell, formatter, evaluator, excelErrors);
        if (booleanValue != null) {
            houseblockRowModel.getHouseblockBooleanPropsMap().put(propertyModel.getId(), booleanValue);
        }
    }

    private Boolean getBooleanValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List<ExcelError> excelErrors) {
        String stringValue = getStringValue(cell, formatter, evaluator, excelErrors);
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        } else if (stringValue.equalsIgnoreCase("true") || stringValue.equals("1")) {
            return Boolean.TRUE;
        } else if (stringValue.equalsIgnoreCase("false") || stringValue.equals("0")) {
            return Boolean.FALSE;
        } else {
            excelErrors.add(getExcelError(cell, null, ExcelError.ERROR.WRONG_TYPE_NOT_BOOLEAN));
            return null;
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
