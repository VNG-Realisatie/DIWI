package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
public class HouseblockUpdateModel {

    public enum HouseblockProperty {
        name,
        groundPosition,
        purpose,
        physicalAppearanceAndHouseType,
        programming,
        size,
        // Do not change the order - startDate and endDate must be the last ones!
        // It will cause problems and extra milestones to be created.
        startDate, // Do not change the order - startDate and endDate must be the last ones
        endDate; // Do not change the order - startDate and endDate must be the last ones
    }

    private HouseblockProperty property;
    private String value;
    private Boolean booleanValue;
    private SingleValueOrRangeModel<BigDecimal> sizeValue;
    private Map<Object, Integer> valuesMap;

    public HouseblockUpdateModel(HouseblockProperty property, String value) {
        this.property = property;
        this.value = value;
    }

    public HouseblockUpdateModel(HouseblockProperty property, Boolean value) {
        this.property = property;
        this.booleanValue = value;
    }

    public HouseblockUpdateModel(HouseblockProperty property, Map<Object, Integer> valuesMap) {
        this.property = property;
        this.valuesMap = valuesMap;
    }

    public HouseblockUpdateModel(HouseblockProperty property, SingleValueOrRangeModel<BigDecimal> singleValueOrRangeModel) {
        this.property = property;
        this.sizeValue = singleValueOrRangeModel;
    }

    public String validate() {
        if (property == null) {
            return "Property is missing";
        }

        return switch (property) {
            case startDate, endDate -> {
                try {
                    LocalDate.parse(value);
                } catch (Exception ex) {
                    yield "Date provided is not valid.";
                }
                yield null;
            }
            case name -> (value == null || value.isBlank()) ? "New houseblock name value is not valid." : null;
            case size -> sizeValue.isValid() ? null : "Size value is not valid.";
            case purpose, groundPosition, physicalAppearanceAndHouseType -> {
                valuesMap.entrySet().removeIf(entry -> entry.getValue() == null);
                yield null;
            }
            case programming -> null;
        };

    }

}
