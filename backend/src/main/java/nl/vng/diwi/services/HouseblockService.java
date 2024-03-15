package nl.vng.diwi.services;

import io.hypersistence.utils.hibernate.type.range.Range;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.HouseblockAppearanceAndTypeChangelog;
import nl.vng.diwi.dal.entities.HouseblockDurationChangelog;
import nl.vng.diwi.dal.entities.HouseblockGroundPositionChangelog;
import nl.vng.diwi.dal.entities.HouseblockGroundPositionChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockHouseTypeChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockMutatieChangelog;
import nl.vng.diwi.dal.entities.HouseblockMutatieChangelogTypeValue;
import nl.vng.diwi.dal.entities.HouseblockNameChangelog;
import nl.vng.diwi.dal.entities.HouseblockOwnershipValueChangelog;
import nl.vng.diwi.dal.entities.HouseblockPhysicalAppearanceChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockPurposeChangelog;
import nl.vng.diwi.dal.entities.HouseblockPurposeChangelogValue;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.Project;
import nl.vng.diwi.dal.entities.User;
import nl.vng.diwi.dal.entities.HouseblockProgrammingChangelog;
import nl.vng.diwi.dal.entities.HouseblockSizeChangelog;
import nl.vng.diwi.dal.entities.enums.GroundPosition;
import nl.vng.diwi.dal.entities.enums.HouseType;
import nl.vng.diwi.dal.entities.enums.PhysicalAppearance;
import nl.vng.diwi.dal.entities.enums.Purpose;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.rest.VngNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class HouseblockService {
    private static final Logger logger = LogManager.getLogger();

    public HouseblockService() {
    }

    public HouseblockSnapshotModel getHouseblockSnapshot(VngRepository repo, UUID houseblockUuid) throws VngNotFoundException {

        HouseblockSnapshotSqlModel houseblockSnapshotModel = repo.getHouseblockDAO().getHouseblockByUuid(houseblockUuid);

        if (houseblockSnapshotModel == null) {
            logger.error("Houseblock with uuid {} was not found.", houseblockUuid);
            throw new VngNotFoundException();
        }
        return new HouseblockSnapshotModel(houseblockSnapshotModel);
    }

    public List<HouseblockSnapshotModel> getProjectHouseblocks(VngRepository repo, UUID projectUuid) {

        List<HouseblockSnapshotSqlModel> sqlHouseblocks = repo.getHouseblockDAO().getHouseblocksByProjectUuid(projectUuid);

        return sqlHouseblocks.stream().map(HouseblockSnapshotModel::new).toList();
    }

    public Houseblock createHouseblock(VngRepository repo, HouseblockSnapshotModel houseblockSnapshotModel, Milestone startMilestone, Milestone endMilestone,
                                       UUID loggedUserUuid, ZonedDateTime now) {

        Houseblock houseblock = new Houseblock();
        houseblock.setProject(repo.getReferenceById(Project.class, houseblockSnapshotModel.getProjectId()));
        repo.persist(houseblock);

        User user = repo.getReferenceById(User.class, loggedUserUuid);

        Consumer<MilestoneChangeDataSuperclass> setChangelogValues = (MilestoneChangeDataSuperclass entity) -> {
            entity.setStartMilestone(startMilestone);
            entity.setEndMilestone(endMilestone);
            entity.setCreateUser(user);
            entity.setChangeStartDate(now);
        };

        var durationChangelog = new HouseblockDurationChangelog();
        setChangelogValues.accept(durationChangelog);
        durationChangelog.setHouseblock(houseblock);
        repo.persist(durationChangelog);

        var nameChangelog = new HouseblockNameChangelog();
        setChangelogValues.accept(nameChangelog);
        nameChangelog.setName(houseblockSnapshotModel.getHouseblockName());
        nameChangelog.setHouseblock(houseblock);
        repo.persist(nameChangelog);

        SingleValueOrRangeModel<BigDecimal> size = houseblockSnapshotModel.getSize();
        if (size != null) {
            var sizeChangelog = new HouseblockSizeChangelog();
            setChangelogValues.accept(sizeChangelog);
            sizeChangelog.setHouseblock(houseblock);
            if (size.getValue() != null) {
                sizeChangelog.setSize(houseblockSnapshotModel.getSize().getValue());
                sizeChangelog.setValueType(ValueType.SINGLE_VALUE);
            } else {
                sizeChangelog.setSizeRange(Range.closed(size.getMin(), size.getMax()));
                sizeChangelog.setValueType(ValueType.RANGE);
            }
            repo.persist(sizeChangelog);
        }

        if (houseblockSnapshotModel.getProgramming() != null) {
            var programmingChangelog = new HouseblockProgrammingChangelog();
            setChangelogValues.accept(programmingChangelog);
            programmingChangelog.setHouseblock(houseblock);
            programmingChangelog.setProgramming(houseblockSnapshotModel.getProgramming());
            repo.persist(programmingChangelog);
        }

        var mutation = houseblockSnapshotModel.getMutation();
        if (mutation != null) {
            var mutatieChangelog = new HouseblockMutatieChangelog();
            setChangelogValues.accept(mutatieChangelog);
            mutatieChangelog.setHouseblock(houseblock);
            mutatieChangelog.setGrossPlanCapacity(mutation.getGrossPlanCapacity());
            mutatieChangelog.setNetPlanCapacity(mutation.getNetPlanCapacity());
            mutatieChangelog.setDemolition(mutation.getDemolition());
            repo.persist(mutatieChangelog);

            if (mutation.getMutationKind() != null && !mutation.getMutationKind().isEmpty()) {
                mutation.getMutationKind().forEach(mk -> {
                    HouseblockMutatieChangelogTypeValue mKind = new HouseblockMutatieChangelogTypeValue();
                    mKind.setMutatieChangelog(mutatieChangelog);
                    mKind.setMutationType(mk);
                    repo.persist(mKind);
                });
            }
        }

        var groundPosition = houseblockSnapshotModel.getGroundPosition();
        if (groundPosition != null) {
            var groundPositionChangelog = new HouseblockGroundPositionChangelog();
            setChangelogValues.accept(groundPositionChangelog);
            groundPositionChangelog.setHouseblock(houseblock);
            repo.persist(groundPositionChangelog);

            if (groundPosition.getFormalPermissionOwner() != null) {
                var groundPositionChangelogValue = new HouseblockGroundPositionChangelogValue();
                groundPositionChangelogValue.setGroundPositionChangelog(groundPositionChangelog);
                groundPositionChangelogValue.setGroundPosition(GroundPosition.FORMELE_TOESTEMMING_GRONDEIGENAAR);
                groundPositionChangelogValue.setAmount(groundPosition.getFormalPermissionOwner());
                repo.persist(groundPositionChangelogValue);
            }

            if (groundPosition.getIntentionPermissionOwner() != null) {
                var groundPositionChangelogValue = new HouseblockGroundPositionChangelogValue();
                groundPositionChangelogValue.setGroundPositionChangelog(groundPositionChangelog);
                groundPositionChangelogValue.setGroundPosition(GroundPosition.INTENTIE_MEDEWERKING_GRONDEIGENAAR);
                groundPositionChangelogValue.setAmount(groundPosition.getIntentionPermissionOwner());
                repo.persist(groundPositionChangelogValue);
            }

            if (groundPosition.getNoPermissionOwner() != null) {
                var groundPositionChangelogValue = new HouseblockGroundPositionChangelogValue();
                groundPositionChangelogValue.setGroundPositionChangelog(groundPositionChangelog);
                groundPositionChangelogValue.setGroundPosition(GroundPosition.GEEN_TOESTEMMING_GRONDEIGENAAR);
                groundPositionChangelogValue.setAmount(groundPosition.getNoPermissionOwner());
                repo.persist(groundPositionChangelogValue);
            }

            var ownershipValues = houseblockSnapshotModel.getOwnershipValue();
            if (ownershipValues != null) {
                ownershipValues.forEach(ov -> {
                    var ownershipValue = new HouseblockOwnershipValueChangelog();
                    setChangelogValues.accept(ownershipValue);
                    ownershipValue.setHouseblock(houseblock);
                    if (ov.getValue().getValue() != null) {
                        ownershipValue.setValue(ov.getValue().getValue());
                        ownershipValue.setValueType(ValueType.SINGLE_VALUE);
                    } else if (ov.getValue().getMin() != null && ov.getValue().getMax() != null) {
                        ownershipValue.setValueRange(Range.closed(ov.getValue().getMin(), ov.getValue().getMax()));
                    }
                    if (ov.getRentalValue().getValue() != null) {
                        ownershipValue.setRentalValue(ov.getRentalValue().getValue());
                        ownershipValue.setRentalValueType(ValueType.SINGLE_VALUE);
                    } else if (ov.getRentalValue().getMin() != null && ov.getRentalValue().getMax() != null) {
                        ownershipValue.setRentalValueRange(Range.closed(ov.getRentalValue().getMin(), ov.getRentalValue().getMax()));
                    }
                    ownershipValue.setAmount(ov.getAmount());
                    ownershipValue.setOwnershipType(ov.getType());
                    repo.persist(ownershipValue);
                });
            }

            var physicalAppearance = houseblockSnapshotModel.getPhysicalAppearance();
            var houseType = houseblockSnapshotModel.getHouseType();

            if (physicalAppearance != null || houseType != null) {
                var appearanceAndTypeChangelog = new HouseblockAppearanceAndTypeChangelog();
                setChangelogValues.accept(appearanceAndTypeChangelog);
                appearanceAndTypeChangelog.setHouseblock(houseblock);
                repo.persist(appearanceAndTypeChangelog);

                if (physicalAppearance != null) {
                    Map<PhysicalAppearance, Integer> physicalAppearanceAmountMap = new HashMap<>();
                    physicalAppearanceAmountMap.put(PhysicalAppearance.GALLERIJFLAT, physicalAppearance.getGallerijflat());
                    physicalAppearanceAmountMap.put(PhysicalAppearance.HOEKWONING, physicalAppearance.getHoekwoning());
                    physicalAppearanceAmountMap.put(PhysicalAppearance.TUSSENWONING, physicalAppearance.getTussenwoning());
                    physicalAppearanceAmountMap.put(PhysicalAppearance.PORTIEKFLAT, physicalAppearance.getPortiekflat());
                    physicalAppearanceAmountMap.put(PhysicalAppearance.VRIJSTAAND, physicalAppearance.getVrijstaand());
                    physicalAppearanceAmountMap.put(PhysicalAppearance.TWEE_ONDER_EEN_KAP, physicalAppearance.getTweeondereenkap());

                    physicalAppearanceAmountMap.forEach((k, v) -> {
                        if (v != null) {
                            var physicalAppearanceValue = new HouseblockPhysicalAppearanceChangelogValue();
                            physicalAppearanceValue.setAmount(v);
                            physicalAppearanceValue.setPhysicalAppearance(k);
                            physicalAppearanceValue.setAppearanceAndTypeChangelog(appearanceAndTypeChangelog);
                            repo.persist(physicalAppearanceValue);
                        }
                    });
                }

                if (houseType != null) {
                    Map<HouseType, Integer> houseTypeAmountMap = new HashMap<>();
                    houseTypeAmountMap.put(HouseType.EENGEZINSWONING, houseType.getEengezinswoning());
                    houseTypeAmountMap.put(HouseType.MEERGEZINSWONING, houseType.getMeergezinswoning());

                    houseTypeAmountMap.forEach((k, v) -> {
                        if (v != null) {
                            var houseTypeValue = new HouseblockHouseTypeChangelogValue();
                            houseTypeValue.setAmount(v);
                            houseTypeValue.setHouseType(k);
                            houseTypeValue.setAppearanceAndTypeChangelog(appearanceAndTypeChangelog);
                            repo.persist(houseTypeValue);
                        }
                    });
                }
            }

            var purpose = houseblockSnapshotModel.getPurpose();
            if (purpose != null) {
                var purposeChangelog = new HouseblockPurposeChangelog();
                setChangelogValues.accept(purposeChangelog);
                purposeChangelog.setHouseblock(houseblock);
                repo.persist(purposeChangelog);

                Map<Purpose, Integer> purposeAmountMap = new HashMap<>();
                purposeAmountMap.put(Purpose.REGULIER, purpose.getRegular());
                purposeAmountMap.put(Purpose.JONGEREN, purpose.getYouth());
                purposeAmountMap.put(Purpose.STUDENTEN, purpose.getStudent());
                purposeAmountMap.put(Purpose.OUDEREN, purpose.getElderly());
                purposeAmountMap.put(Purpose.GROTE_GEZINNEN, purpose.getLargeFamilies());
                purposeAmountMap.put(Purpose.GEHANDICAPTEN_EN_ZORG, purpose.getGHZ());

                purposeAmountMap.forEach((k, v) -> {
                    if (v != null) {
                        var purposeValue = new HouseblockPurposeChangelogValue();
                        purposeValue.setAmount(v);
                        purposeValue.setPurpose(k);
                        purposeValue.setPurposeChangelog(purposeChangelog);
                        repo.persist(purposeValue);
                    }
                });
            }
        }
        return houseblock;
    }
}
