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

    private PhysicalAppearance physicalAppearance = new PhysicalAppearance();

    private HouseType houseType = new HouseType();

    private Purpose purpose = new Purpose();

    public HouseblockSnapshotModel(HouseblockSnapshotSqlModel sqlModel) {
        this.projectId = sqlModel.getProjectId();
        this.houseblockId = sqlModel.getHouseblockId();
        this.houseblockName = sqlModel.getHouseblockName();

        this.setStartDate(sqlModel.getStartDate());
        this.setEndDate(sqlModel.getEndDate());

        this.size = new SingleValueOrRangeModel<>(sqlModel.getSizeValue(), sqlModel.getSizeValueRange());

        this.programming = sqlModel.getProgramming();

        this.mutation.setDemolition(sqlModel.getDemolition());
        this.mutation.setGrossPlanCapacity(sqlModel.getGrossPlanCapacity());
        this.mutation.setNetPlanCapacity(sqlModel.getNetPlanCapacity());
        this.mutation.setMutationKind(sqlModel.getMutationKind());

        if (sqlModel.getOwnershipValueList() != null) {
            sqlModel.getOwnershipValueList().forEach(oSql -> this.ownershipValue.add(new OwnershipValue(oSql.getOwnershipId(), oSql.getOwnershipType(), oSql.getOwnershipAmount(),
                new SingleValueOrRangeModel<>(oSql.getOwnershipValue(), oSql.getOwnershipValueRangeMin(), oSql.getOwnershipValueRangeMax()),
                new SingleValueOrRangeModel<>(oSql.getOwnershipRentalValue(), oSql.getOwnershipRentalValueRangeMin(), oSql.getOwnershipRentalValueRangeMax()))));
        }

        this.groundPosition.setFormalPermissionOwner(sqlModel.getFormalPermissionOwner());
        this.groundPosition.setIntentionPermissionOwner(sqlModel.getIntentionPermissionOwner());
        this.groundPosition.setNoPermissionOwner(sqlModel.getNoPermissionOwner());

        this.physicalAppearance.setGallerijflat(sqlModel.getGallerijflat());
        this.physicalAppearance.setHoekwoning(sqlModel.getHoekwoning());
        this.physicalAppearance.setVrijstaand(sqlModel.getVrijstaand());
        this.physicalAppearance.setTweeondereenkap(sqlModel.getTweeondereenkap());
        this.physicalAppearance.setPortiekflat(sqlModel.getPortiekflat());
        this.physicalAppearance.setTussenwoning(sqlModel.getTussenwoning());

        this.houseType.setEengezinswoning(sqlModel.getEengezinswoning());
        this.houseType.setMeergezinswoning(sqlModel.getMeergezinswoning());

        this.purpose.setRegular(sqlModel.getRegular());
        this.purpose.setYouth(sqlModel.getYouth());
        this.purpose.setStudent(sqlModel.getStudent());
        this.purpose.setElderly(sqlModel.getElderly());
        this.purpose.setGHZ(sqlModel.getGHZ());
        this.purpose.setLargeFamilies(sqlModel.getLargeFamilies());
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

        if (mutation != null && mutation.getNetPlanCapacity() == null && mutation.getDemolition() == null &&
            mutation.getGrossPlanCapacity() == null && (mutation.getMutationKind() == null || mutation.getMutationKind().isEmpty())) {
            //mutation info is not present
            this.mutation = null;
        }

        if (groundPosition != null && groundPosition.getNoPermissionOwner() == null &&
            groundPosition.getIntentionPermissionOwner() == null && groundPosition.getFormalPermissionOwner() == null) {
            //ground position info is not present
            this.groundPosition = null;
        }

        if (physicalAppearance != null && physicalAppearance.getTussenwoning() == null && physicalAppearance.getTweeondereenkap() == null &&
            physicalAppearance.getPortiekflat() == null && physicalAppearance.getHoekwoning() == null && physicalAppearance.getVrijstaand() == null &&
            physicalAppearance.getGallerijflat() == null) {
            //physical appearance info is not present
            this.physicalAppearance = null;
        }

        if (houseType != null && houseType.getEengezinswoning() == null && houseType.getMeergezinswoning() == null) {
            //house type info is not present
            this.houseType = null;
        }

        if (purpose != null && purpose.getRegular() == null && purpose.getYouth() == null && purpose.getStudent() == null &&
            purpose.getElderly() == null && purpose.getGHZ() == null && purpose.getLargeFamilies() == null) {
            //purpose info is not present
            this.purpose = null;
        }

        return null;
    }

    @Getter
    @Setter
    public static class Mutation {
        private List<MutationType> mutationKind;
        private Integer grossPlanCapacity;
        private Integer netPlanCapacity;
        private Integer demolition;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
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
    public static class PhysicalAppearance {
        private Integer tussenwoning;
        private Integer tweeondereenkap;
        private Integer portiekflat;
        private Integer hoekwoning;
        private Integer vrijstaand;
        private Integer gallerijflat;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class HouseType {
        private Integer meergezinswoning;
        private Integer eengezinswoning;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Purpose {
        private Integer regular;
        private Integer youth;
        private Integer student;
        private Integer elderly;
        private Integer GHZ;
        private Integer largeFamilies;
    }

}
