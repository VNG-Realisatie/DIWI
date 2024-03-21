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
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.PhysicalAppearance;
import nl.vng.diwi.dal.entities.enums.Purpose;
import nl.vng.diwi.dal.entities.enums.ValueType;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.models.HouseblockUpdateModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.rest.VngNotFoundException;
import nl.vng.diwi.rest.VngServerErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class HouseblockService {
    private static final Logger logger = LogManager.getLogger();

    private ProjectService projectService;

    public HouseblockService() {
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
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

        return houseblock;
    }

    public void updateHouseblockName(VngRepository repo, Project project, Houseblock houseblock, String newName, UUID loggedInUserUuid, LocalDate updateDate) {
        // name is mandatory for the entire duration of the houseblock

        HouseblockNameChangelog oldChangelogAfterUpdate = new HouseblockNameChangelog();
        HouseblockNameChangelog newChangelog = new HouseblockNameChangelog();
        newChangelog.setHouseblock(houseblock);
        newChangelog.setName(newName);

        HouseblockNameChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, houseblock.getNames(), newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        repo.persist(newChangelog);
        if (oldChangelog == null) {
            logger.error("Houseblock with uuid {} has missing name changelog value", houseblock.getId());
            throw new VngServerErrorException("Houseblock name changelog is invalid.");
        }

        repo.persist(oldChangelog);
        if (oldChangelogAfterUpdate.getStartMilestone() != null) {
            // it is a current houseblock && it had a non-null changelog before the update
            oldChangelogAfterUpdate.setHouseblock(houseblock);
            oldChangelogAfterUpdate.setName(oldChangelog.getName());
            repo.persist(oldChangelogAfterUpdate);
        }
    }

    public void updateHouseblockSize(VngRepository repo, Project project, Houseblock houseblock, SingleValueOrRangeModel<BigDecimal> sizeValue,
                                     UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockSizeChangelog oldChangelogAfterUpdate = new HouseblockSizeChangelog();
        HouseblockSizeChangelog newChangelog = null;
        if (sizeValue.getValue() != null) {
            newChangelog = new HouseblockSizeChangelog();
            newChangelog.setHouseblock(houseblock);
            newChangelog.setValueType(ValueType.SINGLE_VALUE);
            newChangelog.setSize(sizeValue.getValue());
        } else if (sizeValue.getMin() != null && sizeValue.getMax() != null) {
            newChangelog = new HouseblockSizeChangelog();
            newChangelog.setHouseblock(houseblock);
            newChangelog.setValueType(ValueType.RANGE);
            newChangelog.setSizeRange(Range.closed(sizeValue.getMin(), sizeValue.getMax()));
        }

        HouseblockSizeChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, houseblock.getSizes(), newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setSize(oldChangelog.getSize());
                oldChangelogAfterUpdate.setSizeRange(oldChangelog.getSizeRange());
                oldChangelogAfterUpdate.setValueType(oldChangelog.getValueType());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateHouseblockPurpose(VngRepository repo, Project project, Houseblock houseblock, Map<Purpose, Integer> newPurposeMap, UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockPurposeChangelog oldChangelogAfterUpdate = new HouseblockPurposeChangelog();
        HouseblockPurposeChangelog newChangelog = null;
        if (!newPurposeMap.isEmpty()) {
            newChangelog = new HouseblockPurposeChangelog();
            newChangelog.setHouseblock(houseblock);
        }

        HouseblockPurposeChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, houseblock.getPurposes(), newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
            for (Map.Entry<Purpose, Integer> purposeMapEntry : newPurposeMap.entrySet()) {
                HouseblockPurposeChangelogValue newChangelogValue = new HouseblockPurposeChangelogValue();
                newChangelogValue.setPurposeChangelog(newChangelog);
                newChangelogValue.setPurpose(purposeMapEntry.getKey());
                newChangelogValue.setAmount(purposeMapEntry.getValue());
                repo.persist(newChangelogValue);
            }
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                repo.persist(oldChangelogAfterUpdate);
                for (HouseblockPurposeChangelogValue purposeValue : oldChangelog.getPurposeValues()) {
                    HouseblockPurposeChangelogValue oldChangelogValue = new HouseblockPurposeChangelogValue();
                    oldChangelogValue.setPurposeChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setPurpose(purposeValue.getPurpose());
                    oldChangelogValue.setAmount(purposeValue.getAmount());
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }

    public void updateHouseblockGroundPosition(VngRepository repo, Project project, Houseblock houseblock, Map<GroundPosition, Integer> newGroundPositionMap,
                                               UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockGroundPositionChangelog oldChangelogAfterUpdate = new HouseblockGroundPositionChangelog();
        HouseblockGroundPositionChangelog newChangelog = null;
        if (!newGroundPositionMap.isEmpty()) {
            newChangelog = new HouseblockGroundPositionChangelog();
            newChangelog.setHouseblock(houseblock);
        }

        HouseblockGroundPositionChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, houseblock.getGroundPositions(), newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
            for (Map.Entry<GroundPosition, Integer> gpMapEntry : newGroundPositionMap.entrySet()) {
                HouseblockGroundPositionChangelogValue newChangelogValue = new HouseblockGroundPositionChangelogValue();
                newChangelogValue.setGroundPositionChangelog(newChangelog);
                newChangelogValue.setGroundPosition(gpMapEntry.getKey());
                newChangelogValue.setAmount(gpMapEntry.getValue());
                repo.persist(newChangelogValue);
            }
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                repo.persist(oldChangelogAfterUpdate);
                for (HouseblockGroundPositionChangelogValue gpValue : oldChangelog.getValues()) {
                    HouseblockGroundPositionChangelogValue oldChangelogValue = new HouseblockGroundPositionChangelogValue();
                    oldChangelogValue.setGroundPositionChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setGroundPosition(gpValue.getGroundPosition());
                    oldChangelogValue.setAmount(gpValue.getAmount());
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }

    public void updatePhysicalAppearanceAndHouseType(VngRepository repo, Project project, Houseblock houseblock, Map<PhysicalAppearance, Integer> physicalAppearanceMap,
                                                     Map<HouseType, Integer> houseTypeMap, UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockAppearanceAndTypeChangelog oldChangelogAfterUpdate = new HouseblockAppearanceAndTypeChangelog();
        HouseblockAppearanceAndTypeChangelog newChangelog = null;
        if (!physicalAppearanceMap.isEmpty() || !houseTypeMap.isEmpty()) {
            newChangelog = new HouseblockAppearanceAndTypeChangelog();
            newChangelog.setHouseblock(houseblock);
        }

        HouseblockAppearanceAndTypeChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, houseblock.getAppearanceAndTypes(), newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
            for (Map.Entry<PhysicalAppearance, Integer> paMapEntry : physicalAppearanceMap.entrySet()) {
                HouseblockPhysicalAppearanceChangelogValue newChangelogValue = new HouseblockPhysicalAppearanceChangelogValue();
                newChangelogValue.setAppearanceAndTypeChangelog(newChangelog);
                newChangelogValue.setPhysicalAppearance(paMapEntry.getKey());
                newChangelogValue.setAmount(paMapEntry.getValue());
                repo.persist(newChangelogValue);
            }
            for (Map.Entry<HouseType, Integer> htMapEntry : houseTypeMap.entrySet()) {
                HouseblockHouseTypeChangelogValue newChangelogValue = new HouseblockHouseTypeChangelogValue();
                newChangelogValue.setAppearanceAndTypeChangelog(newChangelog);
                newChangelogValue.setHouseType(htMapEntry.getKey());
                newChangelogValue.setAmount(htMapEntry.getValue());
                repo.persist(newChangelogValue);
            }
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                repo.persist(oldChangelogAfterUpdate);
                for (HouseblockPhysicalAppearanceChangelogValue paValue : oldChangelog.getPhysicalAppearanceValues()) {
                    HouseblockPhysicalAppearanceChangelogValue oldChangelogValue = new HouseblockPhysicalAppearanceChangelogValue();
                    oldChangelogValue.setAppearanceAndTypeChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setPhysicalAppearance(paValue.getPhysicalAppearance());
                    oldChangelogValue.setAmount(paValue.getAmount());
                    repo.persist(oldChangelogValue);
                }
                for (HouseblockHouseTypeChangelogValue htValue : oldChangelog.getHouseblockHouseTypeValues()) {
                    HouseblockHouseTypeChangelogValue oldChangelogValue = new HouseblockHouseTypeChangelogValue();
                    oldChangelogValue.setAppearanceAndTypeChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setHouseType(htValue.getHouseType());
                    oldChangelogValue.setAmount(htValue.getAmount());
                    repo.persist(oldChangelogValue);
                }
            }
        }

    }

    public void updateHouseblockProgramming(VngRepository repo, Project project, Houseblock houseblock, Boolean newProgramming, UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockProgrammingChangelog oldChangelogAfterUpdate = new HouseblockProgrammingChangelog();
        HouseblockProgrammingChangelog newChangelog = null;
        if (newProgramming != null) {
            newChangelog = new HouseblockProgrammingChangelog();
            newChangelog.setHouseblock(houseblock);
            newChangelog.setProgramming(newProgramming);
        }

        HouseblockProgrammingChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, houseblock.getProgrammings(), newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setProgramming(oldChangelog.getProgramming());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateHouseblockMutation(VngRepository repo, Project project, Houseblock houseblock, HouseblockSnapshotModel.Mutation newMutation,
                                         UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockMutatieChangelog oldChangelogAfterUpdate = new HouseblockMutatieChangelog();
        HouseblockMutatieChangelog newChangelog = null;
        if (newMutation != null && (newMutation.getDemolition() != null || newMutation.getNetPlanCapacity() != null || newMutation.getGrossPlanCapacity() != null ||
            (newMutation.getMutationKind() != null && !newMutation.getMutationKind().isEmpty()))) {
            newChangelog = new HouseblockMutatieChangelog();
            newChangelog.setHouseblock(houseblock);
            newChangelog.setDemolition(newMutation.getDemolition());
            newChangelog.setGrossPlanCapacity(newMutation.getGrossPlanCapacity());
            newChangelog.setNetPlanCapacity(newMutation.getNetPlanCapacity());
        }

        HouseblockMutatieChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, houseblock.getMutaties(), newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
            if (newMutation.getMutationKind() != null) {
                for (MutationType mutationType : newMutation.getMutationKind()) {
                    HouseblockMutatieChangelogTypeValue newChangelogValue = new HouseblockMutatieChangelogTypeValue();
                    newChangelogValue.setMutatieChangelog(newChangelog);
                    newChangelogValue.setMutationType(mutationType);
                    repo.persist(newChangelogValue);
                }
            }
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current project && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setDemolition(oldChangelog.getDemolition());
                oldChangelogAfterUpdate.setNetPlanCapacity(oldChangelog.getNetPlanCapacity());
                oldChangelogAfterUpdate.setGrossPlanCapacity(oldChangelog.getGrossPlanCapacity());
                repo.persist(oldChangelogAfterUpdate);
                for (HouseblockMutatieChangelogTypeValue type : oldChangelog.getType()) {
                    HouseblockMutatieChangelogTypeValue oldChangelogValue = new HouseblockMutatieChangelogTypeValue();
                    oldChangelogValue.setMutatieChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setMutationType(type.getMutationType());
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }

    public void updateHouseblockOwnershipValue(VngRepository repo, Project project, Houseblock houseblock, HouseblockSnapshotModel.OwnershipValue ownershipValue,
                                               HouseblockUpdateModel.ActionType actionType, UUID loggedInUserUuid, LocalDate updateDate) {

        Milestone houseblockStartMilestone = houseblock.getDuration().get(0).getStartMilestone();
        Milestone houseblockEndMilestone = houseblock.getDuration().get(0).getEndMilestone();
        LocalDate houseblockStartDate = new MilestoneModel(houseblockStartMilestone).getDate();
        LocalDate houseblockEndDate = new MilestoneModel(houseblockEndMilestone).getDate();

        if (actionType == HouseblockUpdateModel.ActionType.add) {
            HouseblockOwnershipValueChangelog newChangelog = createOwnershipChangelog(ownershipValue);
            newChangelog.setHouseblock(houseblock);
            newChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
            if (houseblockStartDate.isBefore(updateDate) && !houseblockEndDate.isBefore(updateDate)) {
                newChangelog.setStartMilestone(projectService.getOrCreateMilestoneForProject(repo, project, updateDate, loggedInUserUuid));
            } else {
                newChangelog.setStartMilestone(houseblockStartMilestone); //TODO: is this ok for past houseblock?
            }
            newChangelog.setEndMilestone(houseblockEndMilestone); //TODO: is this ok for future houseblock?
            repo.persist(newChangelog);
        } else if (actionType == HouseblockUpdateModel.ActionType.remove) {
            HouseblockOwnershipValueChangelog oldChangelog = houseblock.getOwnershipValues().stream().filter(ov -> ov.getId().equals(ownershipValue.getId()))
                .findFirst().orElseThrow(() -> new VngServerErrorException(String.format("Ownerhip value %s not found", ownershipValue.getId())));
            oldChangelog.setChangeEndDate(ZonedDateTime.now());
            oldChangelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
            repo.persist(oldChangelog);
        } else {
            HouseblockOwnershipValueChangelog oldChangelogAfterUpdate = new HouseblockOwnershipValueChangelog();
            HouseblockOwnershipValueChangelog newChangelog = createOwnershipChangelog(ownershipValue);
            newChangelog.setHouseblock(houseblock);
            newChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));

            List<HouseblockOwnershipValueChangelog> changelos = houseblock.getOwnershipValues().stream().filter(c -> c.getId().equals(ownershipValue.getId())).toList();

            HouseblockOwnershipValueChangelog oldChangelog = prepareChangelogValuesToUpdate(repo, project, houseblock, changelos, newChangelog,
                oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

            repo.persist(newChangelog);
            repo.persist(oldChangelog);

            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current houseblock && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setOwnershipType(oldChangelog.getOwnershipType());
                oldChangelogAfterUpdate.setAmount(oldChangelog.getAmount());
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setValueRange(oldChangelog.getValueRange());
                oldChangelogAfterUpdate.setValueType(oldChangelog.getValueType());
                oldChangelogAfterUpdate.setRentalValue(oldChangelog.getRentalValue());
                oldChangelogAfterUpdate.setRentalValueRange(oldChangelog.getRentalValueRange());
                oldChangelogAfterUpdate.setRentalValueType(oldChangelog.getRentalValueType());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    private HouseblockOwnershipValueChangelog createOwnershipChangelog(HouseblockSnapshotModel.OwnershipValue ownershipValue) {
        HouseblockOwnershipValueChangelog newChangelog = new HouseblockOwnershipValueChangelog();
        newChangelog.setOwnershipType(ownershipValue.getType());
        newChangelog.setAmount(ownershipValue.getAmount());
        newChangelog.setValue(ownershipValue.getValue().getValue());
        if (ownershipValue.getValue().getMin() != null && ownershipValue.getValue().getMax() != null) {
            newChangelog.setValueRange(Range.closed(ownershipValue.getValue().getMin(), ownershipValue.getValue().getMax()));
            newChangelog.setValueType(ValueType.RANGE);
        } else {
            newChangelog.setValueType(ValueType.SINGLE_VALUE);
        }
        newChangelog.setRentalValue(ownershipValue.getRentalValue().getValue());
        if (ownershipValue.getRentalValue().getMin() != null && ownershipValue.getRentalValue().getMax() != null) {
            newChangelog.setRentalValueRange(Range.closed(ownershipValue.getRentalValue().getMin(), ownershipValue.getRentalValue().getMax()));
            newChangelog.setRentalValueType(ValueType.RANGE);
        } else {
            newChangelog.setRentalValueType(ValueType.SINGLE_VALUE);
        }
        newChangelog.setChangeStartDate(ZonedDateTime.now());
        return newChangelog;
    }

    private <T extends MilestoneChangeDataSuperclass> T prepareChangelogValuesToUpdate(VngRepository repo, Project project, Houseblock houseblock, List<T> changelogs,
                                                                                       T newChangelog, T oldChangelogAfterUpdate, UUID loggedInUserUuid, LocalDate updateDate) {

        Milestone houseblockStartMilestone = houseblock.getDuration().get(0).getStartMilestone();
        Milestone houseblockEndMilestone = houseblock.getDuration().get(0).getEndMilestone();

        ZonedDateTime zdtNow = ZonedDateTime.now();
        LocalDate houseblockStartDate = (new MilestoneModel(houseblockStartMilestone)).getDate();
        LocalDate houseblockEndDate = (new MilestoneModel(houseblockEndMilestone)).getDate();
        boolean currentOrFutureHouseblock = true;

        T oldChangelog;
        if (newChangelog != null) {
            newChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
            newChangelog.setChangeStartDate(zdtNow);
        }

        if (houseblockStartDate.isAfter(updateDate)) {
            updateDate = houseblockStartDate;
        }

        if (houseblockEndDate.isBefore(updateDate)) {
            updateDate = houseblockEndDate;
            currentOrFutureHouseblock = false;
        }

        LocalDate finalUpdateDate = updateDate;
        boolean finalIsCurrentOrFutureHouseblock = currentOrFutureHouseblock;

        oldChangelog = changelogs.stream()
            .filter(c -> (finalIsCurrentOrFutureHouseblock && !(new MilestoneModel(c.getStartMilestone())).getDate().isAfter(finalUpdateDate)
                && (new MilestoneModel(c.getEndMilestone())).getDate().isAfter(finalUpdateDate)) ||
                (!finalIsCurrentOrFutureHouseblock && (new MilestoneModel(c.getEndMilestone())).getDate().equals(finalUpdateDate)))
            .findFirst().orElse(null);

        Milestone updateMilestone = projectService.getOrCreateMilestoneForProject(repo, project, updateDate, loggedInUserUuid);

        if (oldChangelog != null && finalIsCurrentOrFutureHouseblock && !Objects.equals(oldChangelog.getStartMilestone().getId(), updateMilestone.getId())) {
            oldChangelogAfterUpdate.setStartMilestone(oldChangelog.getStartMilestone());
            oldChangelogAfterUpdate.setEndMilestone(updateMilestone);
            oldChangelogAfterUpdate.setCreateUser(oldChangelog.getCreateUser());
            oldChangelogAfterUpdate.setChangeStartDate(zdtNow);
        }

        if (newChangelog != null) {
            if (finalIsCurrentOrFutureHouseblock) {
                newChangelog.setStartMilestone(updateMilestone);
            } else {
                if (oldChangelog != null) {
                    newChangelog.setStartMilestone(oldChangelog.getStartMilestone());
                } else {
                    newChangelog.setStartMilestone(houseblockStartMilestone);
                }
            }
        }

        if (oldChangelog != null) {
            oldChangelog.setChangeEndDate(zdtNow);
            oldChangelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
        }

        if (newChangelog != null) {
            if (oldChangelog != null) {
                newChangelog.setEndMilestone(oldChangelog.getEndMilestone());
            } else {
                LocalDate currentStartDate = (new MilestoneModel(newChangelog.getStartMilestone())).getDate();
                UUID newEndMilestoneUuid = changelogs.stream().map(MilestoneChangeDataSuperclass::getStartMilestone)
                    .map(MilestoneModel::new)
                    .filter(mm -> mm.getDate().isAfter(currentStartDate))
                    .min(Comparator.comparing(MilestoneModel::getDate))
                    .map(MilestoneModel::getId)
                    .orElse(houseblockEndMilestone.getId());
                newChangelog.setEndMilestone(repo.getReferenceById(Milestone.class, newEndMilestoneUuid));
            }
        }

        return oldChangelog;
    }

    public Houseblock getCurrentHouseblockAndPerformPreliminaryUpdateChecks(VngRepository repo, UUID houseblockId) throws VngNotFoundException {
        Houseblock houseblock = repo.getHouseblockDAO().getCurrentHouseblock(houseblockId);

        if (houseblock == null) {
            logger.error("Houseblock with uuid {} not found.", houseblockId);
            throw new VngNotFoundException("Houseblock not found");
        }

        if (houseblock.getDuration().size() != 1) {
            logger.error("Houseblock with uuid {} has {} duration changelog values", houseblockId, houseblock.getDuration().size());
            throw new VngServerErrorException("Houseblock duration changelog is invalid.");
        }

        MilestoneModel houseblockStartMilestone = new MilestoneModel(houseblock.getDuration().get(0).getStartMilestone());
        MilestoneModel houseblockEndMilestone = new MilestoneModel(houseblock.getDuration().get(0).getEndMilestone());
        if (houseblockStartMilestone.getStateId() == null || houseblockEndMilestone.getStateId() == null) {
            logger.error("Houseblock with uuid {} has start or end milestone with invalid states.", houseblockId);
            throw new VngServerErrorException("Houseblock milestones are invalid.");
        }

        return houseblock;
    }

}
