package nl.vng.diwi.services;

import lombok.Data;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.enums.ProjectStatus;
import nl.vng.diwi.models.ExcelError;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExcelRowModel {

    private Integer id;
    private String projectName;
    private PlanType planType;
    private Boolean programming;
    private String priority;
    private String municipalityRole;

    private Map<String, String> projectRoles = new HashMap<>();

    private ProjectStatus projectStatus;
    private LocalDate projectStartDate;
    private LocalDate projectEndDate;

    private Map<ProjectPhase, LocalDate> projectPhases = new HashMap<>();
    private Map<PlanStatus, LocalDate> projectPlanStatuses = new HashMap<>();

    private String projectMunicipality;
    private String projectDistrict;
    private String projectNeighbourhood;

    private Map<String, String> projectCustomProperties = new HashMap<>();

    public void validate(Integer excelRow, Map<Integer, ExcelColumn> columnsMap, Map<Integer, String> subheadersMap, List<ExcelError> rowErrors) {

//        if (projectName == null || projectName.isEmpty()) {
//            rowErrors.add(new ImportExcelError(excelRow, columnString, projectName, ImportExcelError.EXCEL_ERRORS.MISSING_PROJECT_NAME));
//        }

    }
}
