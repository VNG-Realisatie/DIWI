package nl.vng.diwi.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import lombok.Getter;
import lombok.AccessLevel;

import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataExchangeExportError {

    private UUID projectId;
    private UUID houseblockId;
    private String fieldName;
    private EXPORT_ERROR error;
    private OwnershipCategory cat1;
    private OwnershipCategory cat2;
    private Long priceValueMin;
    private Long priceValueMax;

    public DataExchangeExportError(UUID projectId, String fieldName, EXPORT_ERROR exportError) {
        this(projectId, null, fieldName, exportError, null, null, null, null);
    }

    public DataExchangeExportError(UUID projectId, UUID houseblockId, EXPORT_ERROR exportError) {
        this(projectId, houseblockId, null, exportError, null, null, null, null);
    }

    public DataExchangeExportError(UUID projectId, UUID houseblockId, EXPORT_ERROR exportError, OwnershipCategory cat1, OwnershipCategory cat2, Long priceValueMin, Long priceValueMax) {
        this(projectId, houseblockId, null, exportError, cat1, cat2, priceValueMin, priceValueMax);
    }

    public String getCode(){
        return error.getErrorCode();
    }

    public String getMessage(){
        return error.getErrorMessage();
    }

    @Getter
    public enum EXPORT_ERROR {
        MISSING_MANDATORY_VALUE("missing_mandatory_value", "Value is mandatory in the export, but is missing from the project properties."),
        MULTIPLE_SINGLE_SELECT_VALUES("multiple_single_select_values", "Value is single-select in the export, but multiple values were assigned in the project properties."),
        VALUE_LARGER_THAN_CONSTRUCTION_HOUSEBLOCKS("value_larger_than_construction_houseblocks", "Value is greater than the total number of houses in the project's construction houseblocks, which is not allowed."),
        MISSING_DATAEXCHANGE_MAPPING("missing_dataexchange_mapping", "The data exchange mapping is incomplete. A custom property is not configured for this field."),
        NUMERIC_RANGE_VALUE("numeric_range_value", "The numeric property has a range value assigned for this project, but only single values are allowed."),
        OWNERSHIP_RANGE_MAPPING_ERROR("ownership_range_mapping_error", "The ownership range selected cannot be mapped within one interval of the export ranges."),
        CONFIDENTIALITY_ERROR("min_confidentiality_error", "The confidentiality level of the project is less than the minimum allowed export confidentiality."),
        PROJECT_DOES_NOT_HAVE_GEOMETRY("project_does_not_have_geometry", "Project doesn't have geometry");

        private final String errorCode;
        private final String errorMessage;

        EXPORT_ERROR(String errorCode, String errorMessage) {
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }
}
