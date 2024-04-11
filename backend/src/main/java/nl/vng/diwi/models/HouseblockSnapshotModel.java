package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class HouseblockSnapshotModel extends DatedDataModelSuperClass {

    private UUID projectId;
    private UUID houseblockId;
    private String houseblockName;
    private SingleValueOrRangeModel<BigDecimal> size;
    private Boolean programming;

    private Mutation mutation = new Mutation();

    private List<OwnershipValue> ownershipValue = new ArrayList<>();

    private GroundPosition groundPosition = new GroundPosition();

    private List<AmountModel> physicalAppearance = new ArrayList<>();

    private HouseType houseType = new HouseType();

    private List<AmountModel> targetGroup = new ArrayList<>();

    private List<ProjectHouseblockCustomPropertyModel> customProperties = new ArrayList<>();

    public HouseblockSnapshotModel(HouseblockSnapshotSqlModel sqlModel) {
        this.projectId = sqlModel.getProjectId();
        this.houseblockId = sqlModel.getHouseblockId();
        this.houseblockName = sqlModel.getHouseblockName();

        this.setStartDate(sqlModel.getStartDate());
        this.setEndDate(sqlModel.getEndDate());

        this.size = new SingleValueOrRangeModel<>(sqlModel.getSizeValue(), sqlModel.getSizeValueRange());

        this.programming = sqlModel.getProgramming();

        this.mutation.setAmount(sqlModel.getMutationAmount());
        this.mutation.setKind(sqlModel.getMutationKind());

        if (sqlModel.getOwnershipValueList() != null) {
            sqlModel.getOwnershipValueList().forEach(oSql -> this.ownershipValue.add(new OwnershipValue(oSql.getOwnershipId(), oSql.getOwnershipType(), oSql.getOwnershipAmount(),
                new SingleValueOrRangeModel<>(oSql.getOwnershipValue(), oSql.getOwnershipValueRangeMin(), oSql.getOwnershipValueRangeMax()),
                new SingleValueOrRangeModel<>(oSql.getOwnershipRentalValue(), oSql.getOwnershipRentalValueRangeMin(), oSql.getOwnershipRentalValueRangeMax()))));
        }

        this.groundPosition.setFormalPermissionOwner(sqlModel.getFormalPermissionOwner());
        this.groundPosition.setIntentionPermissionOwner(sqlModel.getIntentionPermissionOwner());
        this.groundPosition.setNoPermissionOwner(sqlModel.getNoPermissionOwner());

        this.physicalAppearance.addAll(sqlModel.getPhysicalAppearanceList());

        this.houseType.setEengezinswoning(sqlModel.getEengezinswoning());
        this.houseType.setMeergezinswoning(sqlModel.getMeergezinswoning());

        this.targetGroup.addAll(sqlModel.getTargetGroupList());
    }

    public boolean isSizeEqualTo(SingleValueOrRangeModel<BigDecimal> otherSize) {
        if ((this.size.getValue() == null && otherSize.getValue() != null) ||
            (this.size.getValue() != null && otherSize.getValue() == null) ||
            (this.size.getMin() == null && otherSize.getMin() != null) ||
            (this.size.getMin() != null && otherSize.getMin() == null) ||
            (this.size.getMax() == null && otherSize.getMax() != null) ||
            (this.size.getMax() != null && otherSize.getMax() == null)) {
            return false;
        }
        if ((this.size.getValue() != null && this.size.getValue().doubleValue() != otherSize.getValue().doubleValue()) ||
            (this.size.getMin() != null && this.size.getMin().doubleValue() != otherSize.getMin().doubleValue()) ||
            (this.size.getMax() != null && this.size.getMax().doubleValue() != otherSize.getMax().doubleValue())) {
            return false;
        }

        return true;
    }

    public String validate(LocalDate projectStartDate, LocalDate projectEndDate) {

        if (this.houseblockName == null || this.houseblockName.isBlank()) {
            return "Houseblock name can not be null.";
        } else if (this.getStartDate() == null) {
            return "Houseblock start date can not be null";
        } else if (this.getEndDate() == null) {
            return "Houseblock end date can not be null";
        } else if (projectStartDate.isAfter(this.getStartDate())) {
            return "Houseblock start date must be within the duration of the project.";
        } else if (projectEndDate.isBefore(this.getEndDate())) {
            return "Houseblock end date must be within the duration of the project";
        }

        if (size != null) {
            if (size.getValue() != null) {
                size.setMin(null);
                size.setMax(null);
            } else if (size.getMax() == null || size.getMin() == null) {
                //size info is not present
                this.size = null;
            }
        }

        if (mutation != null && (mutation.getAmount() == null || mutation.getKind() == null)) {
            //mutation info is not present
            this.mutation = null;
        }

        if (groundPosition != null && groundPosition.getNoPermissionOwner() == null &&
            groundPosition.getIntentionPermissionOwner() == null && groundPosition.getFormalPermissionOwner() == null) {
            //ground position info is not present
            this.groundPosition = null;
        }

        if (physicalAppearance != null && physicalAppearance.isEmpty()) {
            //physical appearance info is not present
            this.physicalAppearance = null;
        }

        if (houseType != null && houseType.getEengezinswoning() == null && houseType.getMeergezinswoning() == null) {
            //house type info is not present
            this.houseType = null;
        }

        if (targetGroup != null && targetGroup.isEmpty()) {
            //target group info is not present
            this.targetGroup = null;
        }

        return null;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Mutation {
        private MutationType kind;
        private Integer amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class OwnershipValue {
        private UUID id;
        private OwnershipType type;
        private Integer amount;
        private SingleValueOrRangeModel<Integer> value = new SingleValueOrRangeModel<>();
        private SingleValueOrRangeModel<Integer> rentalValue = new SingleValueOrRangeModel<>();
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class GroundPosition {
        private Integer noPermissionOwner;
        private Integer intentionPermissionOwner;
        private Integer formalPermissionOwner;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class HouseType {
        private Integer meergezinswoning;
        private Integer eengezinswoning;
    }

}
