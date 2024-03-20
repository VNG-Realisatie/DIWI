package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.enums.Purpose;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
public class HouseblockUpdateModel {

    public enum HouseblockProperty {
        name,
        purpose,
        // Do not change the order - startDate and endDate must be the last ones!
        // It will cause problems and extra milestones to be created.
        startDate, // Do not change the order - startDate and endDate must be the last ones
        endDate; // Do not change the order - startDate and endDate must be the last ones
    }

    private HouseblockProperty property;
    private String value;
    private Map<Purpose, Integer> purposeMap;

    public HouseblockUpdateModel(HouseblockProperty property, String value) {
        this.property = property;
        this.value = value;
    }

    public HouseblockUpdateModel(HouseblockProperty property, Map<Purpose, Integer> valuesMap) {
        this.property = property;
        this.purposeMap = valuesMap;
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
            case purpose -> {
                purposeMap.entrySet().removeIf(entry -> entry.getValue() == null);
                yield null;
            }
        };

    }

}
