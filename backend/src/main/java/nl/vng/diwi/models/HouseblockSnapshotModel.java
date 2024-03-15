package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import java.math.BigDecimal;
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
    public static class GroundPosition {
        private Integer noPermissionOwner;
        private Integer intentionPermissionOwner;
        private Integer formalPermissionOwner;
    }

    @Getter
    @Setter
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
    public static class HouseType {
        private Integer meergezinswoning;
        private Integer eengezinswoning;
    }

    @Getter
    @Setter
    public static class Purpose {
        private Integer regular;
        private Integer youth;
        private Integer student;
        private Integer elderly;
        private Integer GHZ;
        private Integer largeFamilies;
    }

}
