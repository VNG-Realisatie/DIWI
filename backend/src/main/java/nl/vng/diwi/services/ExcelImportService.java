package nl.vng.diwi.services;

import nl.vng.diwi.dal.AutoCloseTransaction;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.ProjectDurationChangelog;
import nl.vng.diwi.dal.entities.ProjectFaseChangelog;
import nl.vng.diwi.dal.entities.ProjectNameChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanTypeChangelogValue;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelog;
import nl.vng.diwi.dal.entities.ProjectPlanologischePlanstatusChangelogValue;
import nl.vng.diwi.dal.entities.ProjectState;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.enums.Confidentiality;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.ExcelError;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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

        //get all preliminary data
//        List<CountryState> countryStates = repo.findAllByReportingYear(CountryState.class, activeReportingYear.getId());

        Map<Integer, ExcelColumn> columnsMap = new HashMap<>();
        Map<Integer, String> columnSubheadersMap = new HashMap<>();
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
                        String subheaderName = getStringValue(nextCell, dateFormatter, formulaEvaluator, excelErrors);
                        ExcelColumn excelColumn = columnsMap.get(columnIndex);
                        if (excelColumn != null && excelColumn.hasSubheader) {
                            columnSubheadersMap.put(columnIndex, subheaderName);
                            //TODO: validate subheaders
                        }
                    }
                    if (!excelErrors.isEmpty()) { //there are problems with the headers, cannot attempt to read projects
                        transaction.rollback();
                        return Map.of(errors, excelErrors);
                    }
                }

                if (rowCount > 5) {
                    SelectModel excelProject = processExcelRow(repo, nextRow, columnsMap, columnSubheadersMap, dateFormatter, formulaEvaluator, excelErrors,
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


    private SelectModel processExcelRow(VngRepository repo, Row row, Map<Integer, ExcelColumn> columnsMap, Map<Integer, String> suhheadersMap,
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

                    case PROJECT_ROLE -> { //TODO
                        String subheader = suhheadersMap.get(columnIndex);
                        if (subheader != null) {
                            rowModel.getProjectRoles().put(subheader, getStringValue(nextCell, formatter, evaluator, rowErrors));
                        }
                    }

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

                    case PROJECT_PHASE_1_CONCEPT -> {
                        LocalDate phaseStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (phaseStartDate != null) {
                            rowModel.getProjectPhases().put(ProjectPhase._1_CONCEPT, phaseStartDate);
                        }
                    }
                    case PROJECT_PHASE_2_INITIATIVE -> {
                        LocalDate phaseStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (phaseStartDate != null) {
                            rowModel.getProjectPhases().put(ProjectPhase._2_INITIATIVE, phaseStartDate);
                        }
                    }
                    case PROJECT_PHASE_3_DEFINITION -> {
                        LocalDate phaseStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (phaseStartDate != null) {
                            rowModel.getProjectPhases().put(ProjectPhase._3_DEFINITION, phaseStartDate);
                        }
                    }
                    case PROJECT_PHASE_4_DESIGN -> {
                        LocalDate phaseStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (phaseStartDate != null) {
                            rowModel.getProjectPhases().put(ProjectPhase._4_DESIGN, phaseStartDate);
                        }
                    }
                    case PROJECT_PHASE_5_PREPARATION -> {
                        LocalDate phaseStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (phaseStartDate != null) {
                            rowModel.getProjectPhases().put(ProjectPhase._5_PREPARATION, phaseStartDate);
                        }
                    }
                    case PROJECT_PHASE_6_REALIZATION -> {
                        LocalDate phaseStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (phaseStartDate != null) {
                            rowModel.getProjectPhases().put(ProjectPhase._6_REALIZATION, phaseStartDate);
                        }
                    }
                    case PROJECT_PHASE_7_AFTERCARE -> {
                        LocalDate phaseStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (phaseStartDate != null) {
                            rowModel.getProjectPhases().put(ProjectPhase._7_AFTERCARE, phaseStartDate);
                        }
                    }

                    case PROJECT_PLAN_STATUS_4A_OPGENOMEN_IN_VISIE -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._4A_OPGENOMEN_IN_VISIE, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_4B_NIET_OPGENOMEN_IN_VISIE -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._4B_NIET_OPGENOMEN_IN_VISIE, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_3_IN_VOORBEREIDING -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._3_IN_VOORBEREIDING, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_2A_VASTGESTELD -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._2A_VASTGESTELD, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_2B_VASTGESTELD_MET_UITWERKING_NODIG -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._2B_VASTGESTELD_MET_UITWERKING_NODIG, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_2C_VASTGESTELD_MET_BW_NODIG -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._2C_VASTGESTELD_MET_BW_NODIG, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_1A_ONHERROEPELIJK -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._1A_ONHERROEPELIJK, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._1B_ONHERROEPELIJK_MET_UITWERKING_NODIG, planStatusStartDate);
                        }
                    }
                    case PROJECT_PLAN_STATUS_1C_ONHERROEPELIJK_MET_BW_NODIG -> {
                        LocalDate planStatusStartDate = getLocalDateValue(nextCell, formatter, rowErrors);
                        if (planStatusStartDate != null) {
                            rowModel.getProjectPlanStatuses().put(PlanStatus._1C_ONHERROEPELIJK_MET_BW_NODIG, planStatusStartDate);
                        }
                    }

                    case PROJECT_MUNICIPALITY -> rowModel.setProjectMunicipality(getStringValue(nextCell, formatter, evaluator, rowErrors)); //TODO
                    case PROJECT_DISTRICT -> rowModel.setProjectDistrict(getStringValue(nextCell, formatter, evaluator, rowErrors)); //TODO
                    case PROJECT_NEIGHBOURHOOD -> rowModel.setProjectNeighbourhood(getStringValue(nextCell, formatter, evaluator, rowErrors)); //TODO

                    case PROJECT_CUSTOM_PROPERTY -> { //TODO
                        String subheader = suhheadersMap.get(columnIndex);
                        if (subheader != null) {
                            rowModel.getProjectCustomProperties().put(subheader, getStringValue(nextCell, formatter, evaluator, rowErrors));
                        }
                    }
                }
            }
        }

        if (rowModel.getId() == null) {
            return null;
        }

        if (rowErrors.isEmpty()) { //no errors validating individual fields
//            rowModel.validate(columnsMap, suhheadersMap, rowErrors); //business logic validation
        }

        if (rowErrors.isEmpty()) { //still no errors
            return persistRowModel(repo, rowModel, user, importTime);
        } else {
            excelErrors.addAll(rowErrors);
            return null;
        }

    }

    private MilestoneStatus getMilestoneStatus(LocalDate milestoneTime, ZonedDateTime importTime) {
        if (milestoneTime.isAfter(importTime.toLocalDate())) {
            return MilestoneStatus.GEREALISEERD;
        } else {
            return MilestoneStatus.GEPLAND;
        }
    }

    private Milestone getOrCreateProjectMilestone(VngRepository repo, List<MilestoneState> milestoneStates, Project project, LocalDate milestoneDate,
                                                  MilestoneStatus milestoneStatus, User user, ZonedDateTime importTime) {

        MilestoneState existingMilestone = milestoneStates.stream().filter(ms -> ms.getDate().equals(milestoneDate)).findFirst().orElse(null);
        if (existingMilestone == null) {
            var milestone = new Milestone();
            milestone.setProject(project);
            repo.persist(milestone);

            var milestoneState = new MilestoneState();
            milestoneState.setDate(milestoneDate);
            milestoneState.setMilestone(milestone);
            milestoneState.setCreateUser(user);
            milestoneState.setChangeStartDate(importTime);
            milestoneState.setState(milestoneStatus == null ? getMilestoneStatus(milestoneDate, importTime) : milestoneStatus);
            repo.persist(milestoneState);

            milestoneStates.add(milestoneState);
            return milestone;
        } else {
            return existingMilestone.getMilestone();
        }
    }

    private SelectModel persistRowModel(VngRepository repo, ExcelRowModel rowModel, User user, ZonedDateTime importTime) {

        var project = new Project();
        repo.persist(project);

        List<MilestoneState> projectMilestones = new ArrayList<>();

        var startMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, rowModel.getProjectStartDate(), null, user, importTime);

        var endMilestoneStatus = rowModel.getProjectStatus() == ProjectStatus.TERMINATED ? MilestoneStatus.AFGEBROKEN : getMilestoneStatus(rowModel.getProjectEndDate(), importTime);
        var endMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, rowModel.getProjectEndDate(), endMilestoneStatus, user, importTime);

        Consumer<MilestoneChangeDataSuperclass> setChangelogValues = (MilestoneChangeDataSuperclass entity) -> {
            entity.setStartMilestone(startMilestone);
            entity.setEndMilestone(endMilestone);
            entity.setCreateUser(user);
            entity.setChangeStartDate(importTime);
        };

        var duration = new ProjectDurationChangelog();
        setChangelogValues.accept(duration);
        duration.setProject(project);
        repo.persist(duration);

        var name = new ProjectNameChangelog();
        name.setProject(project);
        name.setName(rowModel.getProjectName());
        setChangelogValues.accept(name);
        repo.persist(name);

        var state = new ProjectState();
        state.setProject(project);
        state.setCreateUser(user);
        state.setChangeStartDate(importTime);
        state.setConfidentiality(Confidentiality.PRIVE);
        state.setColor("#000000");
        repo.persist(state);

        if (rowModel.getPlanType() != null) {
            var planTypeChangelog = new ProjectPlanTypeChangelog();
            planTypeChangelog.setProject(project);
            setChangelogValues.accept(planTypeChangelog);
            repo.persist(planTypeChangelog);
            var planTypeValue = new ProjectPlanTypeChangelogValue();
            planTypeValue.setPlanTypeChangelog(planTypeChangelog);
            planTypeValue.setPlanType(rowModel.getPlanType());
            repo.persist(planTypeValue);
        }

        if (!rowModel.getProjectPhases().isEmpty()) {
            Milestone phaseEndMilestone = endMilestone;
            List<ProjectPhase> projectPhases = Arrays.stream(ProjectPhase.values()).sorted(Collections.reverseOrder()).toList();
            for (ProjectPhase phase : projectPhases) {
                if (rowModel.getProjectPhases().containsKey(phase)) {
                    LocalDate phaseStartDate = rowModel.getProjectPhases().get(phase);
                    Milestone phaseStartMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, phaseStartDate, null, user, importTime);

                    var phaseChangelog = new ProjectFaseChangelog();
                    phaseChangelog.setProject(project);
                    phaseChangelog.setStartMilestone(phaseStartMilestone);
                    phaseChangelog.setEndMilestone(phaseEndMilestone);
                    phaseChangelog.setChangeStartDate(importTime);
                    phaseChangelog.setCreateUser(user);
                    phaseChangelog.setProjectPhase(phase);

                    repo.persist(phaseChangelog);
                    phaseEndMilestone = phaseStartMilestone;
                }
            }
        }
        if (!rowModel.getProjectPlanStatuses().isEmpty()) {
            Milestone planStatusEndMilestone = endMilestone;
            List<PlanStatus> planStatuses = Arrays.stream(PlanStatus.values()).sorted(Collections.reverseOrder()).toList();
            for (PlanStatus planStatus : planStatuses) {
                if (rowModel.getProjectPlanStatuses().containsKey(planStatus)) {
                    LocalDate planStatusStartDate = rowModel.getProjectPlanStatuses().get(planStatus);
                    Milestone planStatusStartMilestone = getOrCreateProjectMilestone(repo, projectMilestones, project, planStatusStartDate, null, user, importTime);

                    var planStatusChangelog = new ProjectPlanologischePlanstatusChangelog();
                    planStatusChangelog.setProject(project);
                    planStatusChangelog.setStartMilestone(planStatusStartMilestone);
                    planStatusChangelog.setEndMilestone(planStatusEndMilestone);
                    planStatusChangelog.setChangeStartDate(importTime);
                    planStatusChangelog.setCreateUser(user);
                    repo.persist(planStatusChangelog);

                    var planStatusChangelogValue = new ProjectPlanologischePlanstatusChangelogValue();
                    planStatusChangelogValue.setPlanStatusChangelog(planStatusChangelog);
                    planStatusChangelogValue.setPlanStatus(planStatus);
                    repo.persist(planStatusChangelogValue);

                    planStatusEndMilestone = planStatusStartMilestone;
                }
            }
        }
//            var faseChangelog = new ProjectFaseChangelog();
//            faseChangelog.setProject(project);
//            setChangelogValues.accept(faseChangelog);
//            faseChangelog.setProjectPhase(projectData.getProjectPhase());
//            repo.persist(faseChangelog);


//        var planStatus = new ProjectPlanologischePlanstatusChangelog();
//        planStatus.setProject(project);
//        setChangelogValues.accept(planStatus);
//        repo.persist(planStatus);

            if (rowModel.getProgramming() != null) {
//            var programming = new ProjectPro
            }
            return new SelectModel(project.getId(), rowModel.getProjectName());

        }

        private String getStringValue (Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List < ExcelError > excelErrors){
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

        private Double getDoubleValue (Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List < ExcelError > excelErrors){
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

        private Integer getIntegerValue (Cell cell, DataFormatter formatter, FormulaEvaluator evaluator, List < ExcelError > excelErrors){
            Double doubleValue = getDoubleValue(cell, formatter, evaluator, excelErrors);
            if (doubleValue != null) {
                return doubleValue.intValue();
            }
            return null;
        }

        private LocalDate getLocalDateValue (Cell cell, DataFormatter formatter, List < ExcelError > excelErrors){
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

        private ExcelError getExcelError (Cell cell, String cellValue, ExcelError.ERROR error){
            String columnString = CellReference.convertNumToColString(cell.getColumnIndex());
            return new ExcelError(cell.getRowIndex() + 1, columnString, cellValue, error);
        }
    }
