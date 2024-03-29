package nl.vng.diwi.services;

import io.hypersistence.utils.hibernate.type.range.Range;
import nl.vng.diwi.dal.HouseblockDAO;
import nl.vng.diwi.dal.VngRepository;
import nl.vng.diwi.dal.entities.CustomCategoryValue;
import nl.vng.diwi.dal.entities.CustomOrdinalValue;
import nl.vng.diwi.dal.entities.CustomProperty;
import nl.vng.diwi.dal.entities.Houseblock;
import nl.vng.diwi.dal.entities.HouseblockAppearanceAndTypeChangelog;
import nl.vng.diwi.dal.entities.HouseblockBooleanCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockCategoryCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockCategoryCustomPropertyChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockDeliveryDateChangelog;
import nl.vng.diwi.dal.entities.HouseblockDurationChangelog;
import nl.vng.diwi.dal.entities.HouseblockGroundPositionChangelog;
import nl.vng.diwi.dal.entities.HouseblockGroundPositionChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockHouseTypeChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockMutatieChangelog;
import nl.vng.diwi.dal.entities.HouseblockMutatieChangelogTypeValue;
import nl.vng.diwi.dal.entities.HouseblockNameChangelog;
import nl.vng.diwi.dal.entities.HouseblockNumericCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockOrdinalCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.HouseblockOwnershipValueChangelog;
import nl.vng.diwi.dal.entities.HouseblockPhysicalAppearanceChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockPurposeChangelog;
import nl.vng.diwi.dal.entities.HouseblockPurposeChangelogValue;
import nl.vng.diwi.dal.entities.HouseblockTextCustomPropertyChangelog;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
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
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import nl.vng.diwi.models.HouseblockSnapshotModel;
import nl.vng.diwi.dal.entities.HouseblockSnapshotSqlModel;
import nl.vng.diwi.models.HouseblockUpdateModel;
import nl.vng.diwi.models.MilestoneModel;
import nl.vng.diwi.models.ProjectHouseblockCustomPropertyModel;
import nl.vng.diwi.models.SingleValueOrRangeModel;
import nl.vng.diwi.rest.VngBadRequestException;
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
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HouseblockService {
    private static final Logger logger = LogManager.getLogger();

    private ProjectService projectService;

    public HouseblockService() {
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public HouseblockSnapshotModel getHouseblockSnapshot(VngRepository repo, UUID houseblockUuid) throws VngNotFoundException {

        HouseblockSnapshotSqlModel snapshotSqlModel = repo.getHouseblockDAO().getHouseblockByUuid(houseblockUuid);

        if (snapshotSqlModel == null) {
            logger.error("Houseblock with uuid {} was not found.", houseblockUuid);
            throw new VngNotFoundException();
        }

        HouseblockSnapshotModel snapshotModel = new HouseblockSnapshotModel(snapshotSqlModel);
        snapshotModel.setCustomProperties(getHouseblockCustomProperties(repo, houseblockUuid));

        return snapshotModel;
    }

    public List<ProjectHouseblockCustomPropertyModel> getHouseblockCustomProperties(VngRepository repo, UUID houseblockUuid) {

        return repo.getHouseblockDAO().getHouseblockCustomProperties(houseblockUuid).stream()
            .map(ProjectHouseblockCustomPropertyModel::new).toList();
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

        var deliveryDateChangelog = new HouseblockDeliveryDateChangelog();
        setChangelogValues.accept(deliveryDateChangelog);
        deliveryDateChangelog.setHouseblock(houseblock);
        deliveryDateChangelog.setExpectedDeliveryDate(new MilestoneModel(endMilestone).getDate());
        repo.persist(deliveryDateChangelog);

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

        HouseblockNameChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, houseblock.getNames(), newChangelog,
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

    public void updateHouseblockStartDate(VngRepository repo, Project project, Houseblock houseblock, LocalDate newStartDate, UUID loggedInUserUuid) throws VngBadRequestException {

        Set<MilestoneState> activeMilestoneStates = repo.getHouseblockDAO().getHouseblockActiveMilestones(houseblock.getId());

        Milestone houseblockStartMilestone = houseblock.getDuration().get(0).getStartMilestone();
        MilestoneState nextMilestoneState = activeMilestoneStates.stream()
            .filter(ms -> !ms.getMilestone().getId().equals(houseblockStartMilestone.getId()))
            .min(Comparator.comparing(MilestoneState::getDate)).orElseThrow(() -> new VngServerErrorException("Houseblock doesn't have enough active milestones"));

        if (!newStartDate.isBefore(nextMilestoneState.getDate())) {
            throw new VngBadRequestException("Update is not possible because new start date overlaps other existing milestones in this houseblock");
        }

        User userReference = repo.getReferenceById(User.class, loggedInUserUuid);
        ZonedDateTime now = ZonedDateTime.now();

        Milestone newStartMilestone = projectService.getOrCreateMilestoneForProject(repo, project, newStartDate, loggedInUserUuid);

        for (Class<? extends HouseblockMilestoneChangeDataSuperclass> changelogClass : HouseblockDAO.houseblockChangelogs.keySet()) {
            List<? extends HouseblockMilestoneChangeDataSuperclass> activeChangelogs = repo.getHouseblockDAO()
                .findActiveHouseblockChangelogByStartMilestone(changelogClass, houseblock.getId(), houseblockStartMilestone.getId());
            activeChangelogs.forEach(activeChangelog -> {
                updateChangelogDuration(repo, activeChangelog, newStartMilestone, activeChangelog.getEndMilestone(), userReference, now);
            });
        }
    }

    public void updateHouseblockEndDate(VngRepository repo, Project project, Houseblock houseblock, LocalDate newEndDate, UUID loggedInUserUuid, LocalDate updateDate)
        throws VngBadRequestException {
        Set<MilestoneState> activeMilestoneStates = repo.getHouseblockDAO().getHouseblockActiveMilestones(houseblock.getId());

        Milestone houseblockEndMilestone = houseblock.getDuration().get(0).getEndMilestone();
        MilestoneState previousMilestoneState = activeMilestoneStates.stream()
            .filter(ms -> !ms.getMilestone().getId().equals(houseblockEndMilestone.getId()))
            .max(Comparator.comparing(MilestoneState::getDate)).orElseThrow(() -> new VngServerErrorException("Houseblock doesn't have enough active milestones"));

        if (!newEndDate.isAfter(previousMilestoneState.getDate())) {
            throw new VngBadRequestException("Update is not possible because new end date overlaps other existing milestones in this houseblock");
        }

        User userReference = repo.getReferenceById(User.class, loggedInUserUuid);
        ZonedDateTime now = ZonedDateTime.now();

        Milestone newEndMilestone = projectService.getOrCreateMilestoneForProject(repo, project, newEndDate, loggedInUserUuid);

        for (Class<? extends HouseblockMilestoneChangeDataSuperclass> changelogClass : HouseblockDAO.houseblockChangelogs.keySet()) {
            List<? extends HouseblockMilestoneChangeDataSuperclass> activeChangelogs = repo.getHouseblockDAO()
                .findActiveHouseblockChangelogByEndMilestone(changelogClass, houseblock.getId(), houseblockEndMilestone.getId());
            activeChangelogs.forEach(activeChangelog -> {
                if (changelogClass == HouseblockDeliveryDateChangelog.class) {
                    updateHouseblockExpectedDeliveryDate(repo, project, (HouseblockDeliveryDateChangelog) activeChangelog,
                        newEndMilestone, loggedInUserUuid, updateDate);
                } else {
                    updateChangelogDuration(repo, activeChangelog, activeChangelog.getStartMilestone(), newEndMilestone, userReference, now);
                }
            });
        }
    }

    private void updateChangelogDuration(VngRepository repo, HouseblockMilestoneChangeDataSuperclass activeChangelog, Milestone newStartMilestone, Milestone newEndMilestone,
                                                User userReference, ZonedDateTime now) {
        var newChangelog = (HouseblockMilestoneChangeDataSuperclass) activeChangelog.getShallowCopy();
        newChangelog.setStartMilestone(newStartMilestone);
        newChangelog.setEndMilestone(newEndMilestone);
        newChangelog.setCreateUser(userReference);
        newChangelog.setChangeStartDate(now);
        repo.persist(newChangelog);

        if (activeChangelog instanceof HouseblockPurposeChangelog oldPurposeChangelog) {
            oldPurposeChangelog.getPurposeValues().forEach(pv -> {
                var newPurposeValue = HouseblockPurposeChangelogValue.builder()
                    .purpose(pv.getPurpose()).amount(pv.getAmount()).purposeChangelog((HouseblockPurposeChangelog) newChangelog).build();
                repo.persist(newPurposeValue);
            });
        }
        if (activeChangelog instanceof HouseblockGroundPositionChangelog oldGroundPosChangelog) {
            oldGroundPosChangelog.getValues().forEach(gpv -> {
                var newGroundPosValue = HouseblockGroundPositionChangelogValue.builder()
                    .groundPosition(gpv.getGroundPosition()).amount(gpv.getAmount()).groundPositionChangelog((HouseblockGroundPositionChangelog) newChangelog).build();
                repo.persist(newGroundPosValue);
            });
        }
        if (activeChangelog instanceof HouseblockCategoryCustomPropertyChangelog oldCategCPChangelog) {
            oldCategCPChangelog.getChangelogCategoryValues().forEach(cv -> {
                var newCategCPValue = HouseblockCategoryCustomPropertyChangelogValue.builder()
                    .categoryValue(cv.getCategoryValue()).categoryChangelog((HouseblockCategoryCustomPropertyChangelog) newChangelog).build();
                repo.persist(newCategCPValue);
            });
        }
        if (activeChangelog instanceof HouseblockMutatieChangelog oldMutatieChangelog) {
            oldMutatieChangelog.getType().forEach(mv -> {
                var newMutatieValue = HouseblockMutatieChangelogTypeValue.builder()
                    .mutationType(mv.getMutationType()).mutatieChangelog((HouseblockMutatieChangelog) newChangelog).build();
                repo.persist(newMutatieValue);
            });
        }
        if (activeChangelog instanceof HouseblockAppearanceAndTypeChangelog oldAppTypeChangelog) {
            oldAppTypeChangelog.getPhysicalAppearanceValues().forEach(av -> {
                var newAppValue = HouseblockPhysicalAppearanceChangelogValue.builder()
                    .physicalAppearance(av.getPhysicalAppearance()).amount(av.getAmount())
                    .appearanceAndTypeChangelog((HouseblockAppearanceAndTypeChangelog) newChangelog).build();
                repo.persist(newAppValue);
            });
            oldAppTypeChangelog.getHouseblockHouseTypeValues().forEach(tv -> {
                var newTypeValue = HouseblockHouseTypeChangelogValue.builder()
                    .houseType(tv.getHouseType()).amount(tv.getAmount())
                    .appearanceAndTypeChangelog((HouseblockAppearanceAndTypeChangelog) newChangelog).build();
                repo.persist(newTypeValue);
            });
        }

        activeChangelog.setChangeUser(userReference);
        activeChangelog.setChangeEndDate(now);
        repo.persist(activeChangelog);
    }

    public void updateHouseblockExpectedDeliveryDate(VngRepository repo, Project project, HouseblockDeliveryDateChangelog changelog,
                                                     Milestone newEndMilestone, UUID loggedInUserUuid, LocalDate updateDate) {

        LocalDate changelogStartDate = new MilestoneModel(changelog.getStartMilestone()).getDate();

        LocalDate newDeliveryDate = new MilestoneModel(newEndMilestone).getDate();

        var newChangelog = (HouseblockDeliveryDateChangelog) changelog.getShallowCopy();
        newChangelog.setExpectedDeliveryDate(newDeliveryDate);
        newChangelog.setChangeStartDate(ZonedDateTime.now());
        newChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
        newChangelog.setEndMilestone(newEndMilestone);

        if (!updateDate.isBefore(changelogStartDate) && !newDeliveryDate.isBefore(updateDate)) {
            Milestone newMilestone = projectService.getOrCreateMilestoneForProject(repo, project, updateDate, loggedInUserUuid);
            newChangelog.setStartMilestone(newMilestone);

            var oldChangelogV2 = (HouseblockDeliveryDateChangelog) changelog.getShallowCopy();
            oldChangelogV2.setChangeStartDate(ZonedDateTime.now());
            oldChangelogV2.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));
            oldChangelogV2.setEndMilestone(newMilestone);
            repo.persist(oldChangelogV2);
        }

        repo.persist(newChangelog);

        changelog.setChangeEndDate(ZonedDateTime.now());
        changelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
        repo.persist(changelog);
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

        HouseblockSizeChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, houseblock.getSizes(), newChangelog,
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

        HouseblockPurposeChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, houseblock.getPurposes(), newChangelog,
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

        HouseblockGroundPositionChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, houseblock.getGroundPositions(), newChangelog,
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

        HouseblockAppearanceAndTypeChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, houseblock.getAppearanceAndTypes(), newChangelog,
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

        HouseblockProgrammingChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, houseblock.getProgrammings(), newChangelog,
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

        HouseblockMutatieChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, houseblock.getMutaties(), newChangelog,
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
                newChangelog.setStartMilestone(houseblockStartMilestone);
            }
            newChangelog.setEndMilestone(houseblockEndMilestone);
            repo.persist(newChangelog);
        } else if (actionType == HouseblockUpdateModel.ActionType.remove) {
            HouseblockOwnershipValueChangelog oldChangelog = houseblock.getOwnershipValues().stream().filter(ov -> ov.getId().equals(ownershipValue.getId()))
                .findFirst().orElseThrow(() -> new VngServerErrorException(String.format("Ownership value %s not found", ownershipValue.getId())));
            oldChangelog.setChangeEndDate(ZonedDateTime.now());
            oldChangelog.setChangeUser(repo.getReferenceById(User.class, loggedInUserUuid));
            repo.persist(oldChangelog);
        } else {
            HouseblockOwnershipValueChangelog oldChangelogAfterUpdate = new HouseblockOwnershipValueChangelog();
            HouseblockOwnershipValueChangelog newChangelog = createOwnershipChangelog(ownershipValue);
            newChangelog.setHouseblock(houseblock);
            newChangelog.setCreateUser(repo.getReferenceById(User.class, loggedInUserUuid));

            List<HouseblockOwnershipValueChangelog> changelos = houseblock.getOwnershipValues().stream().filter(c -> c.getId().equals(ownershipValue.getId())).toList();

            HouseblockOwnershipValueChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, changelos, newChangelog,
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

    private <T extends MilestoneChangeDataSuperclass> T prepareHouseblockChangelogValuesToUpdate(VngRepository repo, Project project, Houseblock houseblock, List<T> changelogs,
                                                                                       T newChangelog, T oldChangelogAfterUpdate, UUID loggedInUserUuid, LocalDate updateDate) {

        Milestone houseblockStartMilestone = houseblock.getDuration().get(0).getStartMilestone();
        Milestone houseblockEndMilestone = houseblock.getDuration().get(0).getEndMilestone();

        return projectService.prepareChangelogValuesToUpdate(repo, project, changelogs, newChangelog, oldChangelogAfterUpdate, loggedInUserUuid,
            houseblockStartMilestone, houseblockEndMilestone, updateDate);
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

    public void updateHouseblockBooleanCustomProperty(VngRepository repo, Project project, Houseblock houseblock, UUID customPropertyId, Boolean newBooleanValue,
                                                      UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockBooleanCustomPropertyChangelog oldChangelogAfterUpdate = new HouseblockBooleanCustomPropertyChangelog();
        HouseblockBooleanCustomPropertyChangelog newChangelog = null;
        if (newBooleanValue != null) {
            newChangelog = new HouseblockBooleanCustomPropertyChangelog();
            newChangelog.setHouseblock(houseblock);
            newChangelog.setValue(newBooleanValue);
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }


        List<HouseblockBooleanCustomPropertyChangelog> changelogs = houseblock.getBooleanCustomProperties().stream()
            .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        HouseblockBooleanCustomPropertyChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, changelogs, newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current houseblock && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateHouseblockTextCustomProperty(VngRepository repo, Project project, Houseblock houseblock, UUID customPropertyId, String newTextValue,
                                                   UUID loggedInUserUuid, LocalDate updateDate) {
        HouseblockTextCustomPropertyChangelog oldChangelogAfterUpdate = new HouseblockTextCustomPropertyChangelog();
        HouseblockTextCustomPropertyChangelog newChangelog = null;
        if (newTextValue != null) {
            newChangelog = new HouseblockTextCustomPropertyChangelog();
            newChangelog.setHouseblock(houseblock);
            newChangelog.setValue(newTextValue);
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<HouseblockTextCustomPropertyChangelog> changelogs = houseblock.getTextCustomProperties().stream()
            .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        HouseblockTextCustomPropertyChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, changelogs, newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current houseblock && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }


    public void updateHouseblockNumericCustomProperty(VngRepository repo, Project project, Houseblock houseblock, UUID customPropertyId, SingleValueOrRangeModel<BigDecimal> newNumericValue,
                                                      UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockNumericCustomPropertyChangelog oldChangelogAfterUpdate = new HouseblockNumericCustomPropertyChangelog();
        HouseblockNumericCustomPropertyChangelog newChangelog = null;
        if (newNumericValue.getValue() != null || newNumericValue.getMin() != null || newNumericValue.getMax() != null) {
            newChangelog = new HouseblockNumericCustomPropertyChangelog();
            newChangelog.setHouseblock(houseblock);
            if (newNumericValue.getValue() != null) {
                newChangelog.setValue(newNumericValue.getValue().doubleValue());
                newChangelog.setValueType(ValueType.SINGLE_VALUE);
            } else {
                newChangelog.setValueRange(Range.closed(newNumericValue.getMin(), newNumericValue.getMax()));
                newChangelog.setValueType(ValueType.RANGE);
            }
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<HouseblockNumericCustomPropertyChangelog> changelogs = houseblock.getNumericCustomProperties().stream()
            .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        HouseblockNumericCustomPropertyChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, changelogs, newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current houseblock && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setValueRange(oldChangelog.getValueRange());
                oldChangelogAfterUpdate.setValueType(oldChangelog.getValueType());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateHouseblockOrdinalCustomProperty(VngRepository repo, Project project, Houseblock houseblock, UUID customPropertyId, SingleValueOrRangeModel<UUID> newOrdinalValue,
                                                      UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockOrdinalCustomPropertyChangelog oldChangelogAfterUpdate = new HouseblockOrdinalCustomPropertyChangelog();
        HouseblockOrdinalCustomPropertyChangelog newChangelog = null;
        if (newOrdinalValue.getValue() != null || newOrdinalValue.getMin() != null || newOrdinalValue.getMax() != null) {
            newChangelog = new HouseblockOrdinalCustomPropertyChangelog();
            newChangelog.setHouseblock(houseblock);
            if (newOrdinalValue.getValue() != null) {
                newChangelog.setValue(repo.getReferenceById(CustomOrdinalValue.class, newOrdinalValue.getValue()));
                newChangelog.setValueType(ValueType.SINGLE_VALUE);
            } else {
                newChangelog.setMinValue(repo.getReferenceById(CustomOrdinalValue.class, newOrdinalValue.getMin()));
                newChangelog.setMaxValue(repo.getReferenceById(CustomOrdinalValue.class, newOrdinalValue.getMax()));
                newChangelog.setValueType(ValueType.RANGE);
            }
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<HouseblockOrdinalCustomPropertyChangelog> changelogs = houseblock.getOrdinalCustomProperties().stream()
            .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        HouseblockOrdinalCustomPropertyChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, changelogs, newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
        }

        if (oldChangelog != null) {
            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current houseblock && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setValue(oldChangelog.getValue());
                oldChangelogAfterUpdate.setMinValue(oldChangelog.getMinValue());
                oldChangelogAfterUpdate.setMaxValue(oldChangelog.getMaxValue());
                oldChangelogAfterUpdate.setValueType(oldChangelog.getValueType());
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
            }
        }
    }

    public void updateHouseblockCategoryCustomProperty(VngRepository repo, Project project, Houseblock houseblock, UUID customPropertyId, Set<UUID> newCategoryValues,
                                                       UUID loggedInUserUuid, LocalDate updateDate) {

        HouseblockCategoryCustomPropertyChangelog oldChangelogAfterUpdate = new HouseblockCategoryCustomPropertyChangelog();
        HouseblockCategoryCustomPropertyChangelog newChangelog = null;
        if (newCategoryValues != null && !newCategoryValues.isEmpty()) {
            newChangelog = new HouseblockCategoryCustomPropertyChangelog();
            newChangelog.setHouseblock(houseblock);
            newChangelog.setCustomProperty(repo.getReferenceById(CustomProperty.class, customPropertyId));
        }

        List<HouseblockCategoryCustomPropertyChangelog> changelogs = houseblock.getCategoryCustomProperties().stream()
            .filter(cp -> cp.getCustomProperty().getId().equals(customPropertyId)).toList();

        HouseblockCategoryCustomPropertyChangelog oldChangelog = prepareHouseblockChangelogValuesToUpdate(repo, project, houseblock, changelogs, newChangelog,
            oldChangelogAfterUpdate, loggedInUserUuid, updateDate);

        if (newChangelog != null) {
            repo.persist(newChangelog);
            for (UUID newCategoryValue : newCategoryValues) {
                HouseblockCategoryCustomPropertyChangelogValue newChangelogValue = new HouseblockCategoryCustomPropertyChangelogValue();
                newChangelogValue.setCategoryChangelog(newChangelog);
                newChangelogValue.setCategoryValue(repo.getReferenceById(CustomCategoryValue.class, newCategoryValue));
                repo.persist(newChangelogValue);
            }
        }
        if (oldChangelog != null) {
            Set<UUID> oldCategoryValues = oldChangelog.getChangelogCategoryValues().stream()
                .map(cv -> cv.getCategoryValue().getId()).collect(Collectors.toSet());

            repo.persist(oldChangelog);
            if (oldChangelogAfterUpdate.getStartMilestone() != null) {
                // it is a current houseblock && it had a non-null changelog before the update
                oldChangelogAfterUpdate.setHouseblock(houseblock);
                oldChangelogAfterUpdate.setCustomProperty(oldChangelog.getCustomProperty());
                repo.persist(oldChangelogAfterUpdate);
                for (UUID oldCategoryValue : oldCategoryValues) {
                    HouseblockCategoryCustomPropertyChangelogValue oldChangelogValue = new HouseblockCategoryCustomPropertyChangelogValue();
                    oldChangelogValue.setCategoryChangelog(oldChangelogAfterUpdate);
                    oldChangelogValue.setCategoryValue(repo.getReferenceById(CustomCategoryValue.class, oldCategoryValue));
                    repo.persist(oldChangelogValue);
                }
            }
        }
    }
}
