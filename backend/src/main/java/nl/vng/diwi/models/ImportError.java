package nl.vng.diwi.models;

import lombok.Data;

@Data
public class ImportError {

    private String errorCode;
    private Integer row;
    private String column;
    private String cellValue;
    private String errorMessage;


    public ImportError(ERROR error) {
        this.errorMessage = error.errorMsg;
        this.errorCode = error.errorCode;
    }

    public ImportError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ImportError(Integer row, ERROR error) {
        this.row = row;
        this.errorMessage = error.errorMsg;
        this.errorCode = error.errorCode;
    }

    public ImportError(Integer row, String cellValue, ERROR error) {
        this.row = row;
        this.cellValue = cellValue;
        this.errorMessage = error.errorMsg;
        this.errorCode = error.errorCode;
    }

    public ImportError(Integer row, String column, String cellValue, ERROR error) {
        this.row = row;
        this.column = column;
        this.cellValue = cellValue;
        this.errorMessage = error.errorMsg;
        this.errorCode = error.errorCode;
    }

    public enum ERROR {

        IO_ERROR("io_error", "The excel file could not be read. There was a problem with the upload or with the file type."),
        MISSING_DATA_SHEET("no_data_sheet", "The excel file is missing the data sheet."),
        MISSING_TABLE_HEADERS("missing_headers", "The excel file does not contain all expected headers."),

        WRONG_TYPE_NOT_STRING("not_string", "A string value was expected, but a different type was found."),
        WRONG_TYPE_NOT_NUMERIC("not_numeric", "A numeric value was expected, but a different type was found."),
        WRONG_TYPE_NOT_DATE("not_date", "A date value expected, but a different type was found."),
        WRONG_TYPE_NOT_BOOLEAN("not_boolean", "A true/false or 0/1 value was expected, but a different type was found."),
        WRONG_TYPE_UNKNOWN("unknown_type", "An unknown value type was found. A different type was expected."),

        UNKNOWN_PROJECT_CATEGORY_PROPERTY("unknown_project_category_property", "A project category property with this name was not found."),
        UNKNOWN_PROJECT_PROPERTY("unknown_project_property", "A project property with this name was not found."),
        UNKNOWN_HOUSEBLOCK_PROPERTY("unknown_houseblock_property", "A houseblock property with this name was not found."),
        UNKNOWN_HOUSEBLOCK_NUMERIC_PROPERTY("unknown_houseblock_numeric_property", "A houseblock numeric property with this name was not found."),
        UNKNOWN_PROPERTY_VALUE("unknown_property_value", "The value is not defined as an active option for the property on this column."),
        MISSING_FIXED_PROPERTY("missing_fixed_property", "The fixed property was not found"),

        PROJECT_LEVEL_DATA_INVALID("project_level_data_invalid", "Project level data does not match data of previous project with the same id."),
        PROJECT_START_DATE_AFTER_END_DATE("project_dates_error", "The project start date is not before the project end date."),
        PROJECT_DATES_WRONG_FOR_PROJECT_STATUS("project_dates_status_mismatch", "The project start date and end date are not consistent with the project status."),
        PROJECT_WRONG_PHASE_DATES("wrong_project_phase_dates", "The project phase start dates are not within the project duration or are not in the right order."),
        PROJECT_WRONG_PLAN_STATUS_DATES("wrong_project_plan_status_dates", "The project planning plan status start dates are not within the project duration or are not in the right order."),
        PROJECT_LOCATION_INCOMPLETE("incomplete_project_location", "Project neighbourhood/district are present without district/municipality being present"),

        HOUSEBLOCK_DELIVERY_DATE_AFTER_PROJECT_END_DATE("houseblock_delivery_date", "At least one housing block delivery date is after the project end date."),
        HOUSEBLOCK_HOUSING_NUMBER_NOT_POSITIVE("houseblock_housing_number", "The housing block mutation number is not greater than 0."),
        HOUSEBLOCK_DELIVERY_TOTAL_INCORRECT("houseblock_delivery_total", "The total of houses in Delivery section does not match the mutation amount."),
        HOUSEBLOCK_DELIVERY_DATE_BEFORE_PROJECT_DEVLIVERY_PHASE("houseblock_delivery_phase", "The housing block delivery dates are before the project delivery phase"),
        HOUSEBLOCK_OWNERSHIP_TYPE_TOTAL_INCORRECT("houseblock_ownership_total", "The total of houses in Property type section does not match the mutation amount."),
        HOUSEBLOCK_OWNERSHIP_OWNER_TOTAL_INCORRECT("houseblock_ownership_owner_total", "The total of houses in Home-value section does not match the owner-occupied amount."),
        HOUSEBLOCK_OWNERSHIP_LANDLORD_TOTAL_INCORRECT("houseblock_ownership_landlord_total", "The total of houses in Rental-price-private-landlord section does not match the private-landlord amount."),
        HOUSEBLOCK_OWNERSHIP_HOUSING_ASSOCIATION_TOTAL_INCORRECT("houseblock_ownership_housing_assoc_total", "The total of houses in Housing-association-rental-price section does not match the rent-housing-association amount."),
        HOUSEBLOCK_HOUSE_TYPE_TOTAL_INCORRECT("houseblock_house_type_total", "The total of houses in House-type section does not match the mutation amount."),
        HOUSEBLOCK_PHYSICAL_APPEARANCE_TOTAL_INCORRECT("houseblock_physical_appearance_total", "The total of houses in Physical-appearance section does not match the mutation amount."),
        HOUSEBLOCK_TARGET_GROUP_TOTAL_INCORRECT("houseblock_target_group_total", "The total of houses in Target-group section does not match the mutation amount."),
        HOUSEBLOCK_GROUND_POSITION_TOTAL_INCORRECT("houseblock_ground_position_total", "The total of houses in Ground-position section does not match the mutation amount."),

        MISSING_PROJECT_NAME("no_project_name", "Project name is missing."),
        MISSING_PROJECT_ID("no_project_identificationNo", "Project identification number is missing."),
        MISSING_PROJECT_STATUS("no_project_status", "Project status is missing or invalid"),
        MISSING_PROJECT_START_DATE("no_project_start_date", "Project start date is missing or invalid."),
        MISSING_PROJECT_END_DATE("no_project_end_date", "Project end date is missing or invalid."),
        MISSING_HOUSEBLOCK_MUTATION("no_houseblock_mutation", "Houseblock mutation type or amount is missing."),
        MISSING_HOUSEBLOCK_NAME("no_houseblock_name", "Houseblock name is missing."),
        INVALID_PLAN_TYPE("invalid_plan_type", "Project plan type has an unknow value"),
        INVALID_RANGE("invalid_numeric_range", "The value does not match a numeric range.");

        public final String errorMsg;

        public final String errorCode;

        ERROR(String errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }
    }

}
