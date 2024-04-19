package nl.vng.diwi.models;

import lombok.Data;

@Data
public class ExcelError {

    private String errorCode;
    private Integer row;
    private String column;
    private String cellValue;
    private String errorMessage;


    public ExcelError(ERROR error) {
        this.errorMessage = error.errorMsg;
        this.errorCode = error.errorCode;
    }

    public ExcelError(Integer row, String column, String cellValue, ERROR error) {
        this.row = row;
        this.column = column;
        this.cellValue = cellValue;
        this.errorMessage = error.errorMsg;
        this.errorCode = error.errorCode;
    }

    public enum ERROR {

        IO_ERROR("io_error", "The excel file could not be read. There was a problem with the upload or with the file type."),
        MISSING_DATA_SHEET("no_data_sheet", "The excel file is missing the data sheet."),

        WRONG_TYPE_NOT_STRING("not_string", "A string value was expected, but a different type was found."),
        WRONG_TYPE_NOT_NUMERIC("not_numeric", "A numeric value was expected, but a different type was found."),
        WRONG_TYPE_NOT_DATE("not_date", "A date value expected, but a different type was found."),
        WRONG_TYPE_UNKNOWN("unknown_type", "An unknown value type was found. A different type was expected."),

        MISSING_PROJECT_NAME("no_project_name", "Project name is missing."),
        MISSING_PROJECT_STATUS("no_project_status", "Project status is missing or invalid"),
        MISSING_PROJECT_START_DATE("no_project_start_date", "Project start date is missing or invalid."),
        MISSING_PROJECT_END_DATE("no_project_end_date", "Project end date is missing or invalid."),
        INVALID_PLAN_TYPE("invalid_plan_type", "Project plan type has an unknow value");

        public final String errorMsg;

        public final String errorCode;

        ERROR(String errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }
    }

}
