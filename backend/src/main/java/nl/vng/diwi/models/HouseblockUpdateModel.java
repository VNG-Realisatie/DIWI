package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class HouseblockUpdateModel {

    private HouseblockProperty property;
    private String value;
    private Boolean booleanValue;
    private SingleValueOrRangeModel<BigDecimal> sizeValue;
    private Map<Object, Integer> valuesMap;
    private List<AmountModel> amountValuesList;
    private HouseblockSnapshotModel.OwnershipValue ownershipValue;
    private HouseblockSnapshotModel.Mutation mutationValue;
    private ActionType actionType;

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

    public HouseblockUpdateModel(HouseblockProperty property, List<AmountModel> amountValuesList) {
        this.property = property;
        this.amountValuesList = amountValuesList;
    }

    public HouseblockUpdateModel(HouseblockProperty property, List<AmountModel> amountValuesList, Map<Object, Integer> valuesMap) {
        this.property = property;
        this.valuesMap = valuesMap;
        this.amountValuesList = amountValuesList;
    }

    public HouseblockUpdateModel(HouseblockProperty property, SingleValueOrRangeModel<BigDecimal> singleValueOrRangeModel) {
        this.property = property;
        this.sizeValue = singleValueOrRangeModel;
    }

    public HouseblockUpdateModel(HouseblockProperty property, HouseblockSnapshotModel.OwnershipValue ownershipValue, ActionType actionType) {
        this.property = property;
        this.ownershipValue = ownershipValue;
        this.actionType = actionType;
    }

    public HouseblockUpdateModel(HouseblockProperty property, HouseblockSnapshotModel.Mutation mutationValue) {
        this.property = property;
        this.mutationValue = mutationValue;
    }

    public String validate() {
        return validate(new ArrayList<>(), new ArrayList<>());
    }
    public String validate(List<UUID> targetGroupUuids, List<UUID> physicalAppeareanceUuids) {
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
            case groundPosition -> {
                valuesMap.entrySet().removeIf(entry -> entry.getValue() == null);
                yield null;
            }
            case physicalAppearanceAndHouseType -> {
                for (AmountModel amountModel : amountValuesList) {
                    if (!physicalAppeareanceUuids.contains(amountModel.getId())) {
                        yield "Physical appearance category id is not valid.";
                    }
                }
                valuesMap.entrySet().removeIf(entry -> entry.getValue() == null);
                yield null;
            }
            case targetGroup -> {
                for (AmountModel amountModel : amountValuesList) {
                    if (!targetGroupUuids.contains(amountModel.getId())) {
                        yield "Target group category id is not valid.";
                    }
                }
                yield null;
            }
            case programming, mutation  -> null;
            case ownershipValue -> (ownershipValue.getType() == null || ownershipValue.getAmount() == null || !ownershipValue.getRentalValue().isValid() ||
                !ownershipValue.getValue().isValid()) ? "Ownership value is not valid" : null;
        };

    }

    public enum HouseblockProperty {
        name,
        groundPosition,
        mutation,
        ownershipValue,
        targetGroup,
        physicalAppearanceAndHouseType,
        programming,
        size,
        startDate,
        endDate;
    }

    public enum ActionType {
        add,
        remove,
        update;
    }

}
