package nl.vng.diwi.services.export.zuidholland;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.DataExchangeExportError.EXPORT_ERROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class EsriZuidHollandHouseblockExportModel {

    private UUID houseblockId;
    private Integer deliveryYear;
    private Integer mutationAmount;
    private MutationType mutationKind;

    private Integer meergezinswoning;
    private Integer eengezinswoning;

    private List<OwnershipValueModel> ownershipValueList = new ArrayList<>();

    public EsriZuidHollandHouseblockExportModel(UUID projectUuid, ProjectExportSqlModel.HouseblockExportSqlModel sqlModel, PropertyModel priceRangeBuyFixedProp,
                                                PropertyModel priceRangeRentFixedProp, List<DataExchangeExportError> errors) {
        this.houseblockId = sqlModel.getHouseblockId();
        this.deliveryYear = sqlModel.getDeliveryYear();
        this.mutationAmount = sqlModel.getMutationAmount();
        this.mutationKind = sqlModel.getMutationKind();
        this.meergezinswoning = sqlModel.getMeergezinswoning();
        this.eengezinswoning = sqlModel.getEengezinswoning();

        sqlModel.getOwnershipValueList().forEach(o -> {
           OwnershipValueModel oModel = new OwnershipValueModel();
           oModel.setOwnershipType(o.getOwnershipType());
           oModel.setAmount(o.getOwnershipAmount());
            if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
                if (o.getOwnershipValue() != null) {
                    oModel.setOwnershipCategory(getOwnershipCategory(o.getOwnershipType(), o.getOwnershipValue()));
                } else if (o.getOwnershipValueRangeMin() != null) {
                    oModel.setOwnershipCategory(getOwnershipCategory(projectUuid, sqlModel.getHouseblockId(), o.getOwnershipType(), o.getOwnershipValueRangeMin(), o.getOwnershipValueRangeMax(), errors));
                } else if (o.getOwnershipRangeCategoryId() != null) {
                    RangeSelectDisabledModel rangeOption = priceRangeBuyFixedProp.getRanges().stream()
                        .filter(r -> r.getDisabled() == Boolean.FALSE && r.getId().equals(o.getOwnershipRangeCategoryId()))
                        .findFirst().orElse(null);
                    if (rangeOption == null) {
                        oModel.setOwnershipCategory(OwnershipCategory.koop_onb);
                    } else {
                        oModel.setOwnershipCategory(getOwnershipCategory(projectUuid, sqlModel.getHouseblockId(), o.getOwnershipType(), rangeOption.getMin().longValue(), rangeOption.getMax().longValue(), errors));
                    }
                } else {
                    oModel.setOwnershipCategory(OwnershipCategory.koop_onb);
                }
            } else {
                if (o.getOwnershipRentalValue() != null) {
                    oModel.setOwnershipCategory(getOwnershipCategory(o.getOwnershipType(), o.getOwnershipRentalValue()));
                } else if (o.getOwnershipRentalValueRangeMin() != null) {
                    oModel.setOwnershipCategory(getOwnershipCategory(projectUuid, sqlModel.getHouseblockId(), o.getOwnershipType(), o.getOwnershipRentalValueRangeMin(), o.getOwnershipRentalValueRangeMax(), errors));
                } else if (o.getOwnershipRentalRangeCategoryId() != null) {
                    RangeSelectDisabledModel rangeOption = priceRangeRentFixedProp.getRanges().stream()
                        .filter(r -> r.getDisabled() == Boolean.FALSE && r.getId().equals(o.getOwnershipRentalRangeCategoryId()))
                        .findFirst().orElse(null);
                    if (rangeOption == null) {
                        oModel.setOwnershipCategory(OwnershipCategory.huur_onb);
                    } else {
                        oModel.setOwnershipCategory(getOwnershipCategory(projectUuid, sqlModel.getHouseblockId(), o.getOwnershipType(), rangeOption.getMin().longValue(), rangeOption.getMax().longValue(), errors));
                    }
                } else {
                    oModel.setOwnershipCategory(OwnershipCategory.huur_onb);
                }
            }
            this.ownershipValueList.add(oModel);
        });
    }

    private OwnershipCategory getOwnershipCategory(OwnershipType ownershipType, Long priceValue) {
        if (ownershipType == OwnershipType.KOOPWONING) {
            if (priceValue == null) {
                return OwnershipCategory.koop_onb;
            } else if (priceValue < zuidHollandPriceRangeCategories.get(OwnershipCategory.koop2)) {
                return OwnershipCategory.koop1;
            } else if (priceValue < zuidHollandPriceRangeCategories.get(OwnershipCategory.koop3)) {
                return OwnershipCategory.koop2;
            } else if (priceValue < zuidHollandPriceRangeCategories.get(OwnershipCategory.koop4)) {
                return OwnershipCategory.koop3;
            } else {
                return OwnershipCategory.koop4;
            }
        } else {
            if (priceValue == null) {
                return OwnershipCategory.huur_onb;
            } else if (priceValue < zuidHollandPriceRangeCategories.get(OwnershipCategory.huur2)) {
                return OwnershipCategory.huur1;
            } else if (priceValue < zuidHollandPriceRangeCategories.get(OwnershipCategory.huur3)) {
                return OwnershipCategory.huur2;
            } else if (priceValue < zuidHollandPriceRangeCategories.get(OwnershipCategory.huur4)) {
                return OwnershipCategory.huur3;
            } else {
                return OwnershipCategory.huur4;
            }
        }
    }

    private OwnershipCategory getOwnershipCategory(UUID projectUuid, UUID houseblockUuid, OwnershipType ownershipType, Long priceValueMin, Long priceValueMax,
                                                   List<DataExchangeExportError> errors) {
        OwnershipCategory cat1 = getOwnershipCategory(ownershipType, priceValueMin);
        OwnershipCategory cat2 = getOwnershipCategory(ownershipType, priceValueMax);

        if (cat1 == cat2) {
            return cat1;
        } else {
            errors.add(new DataExchangeExportError(projectUuid, houseblockUuid, EXPORT_ERROR.OWNERSHIP_RANGE_MAPPING_ERROR, cat1, cat2));
            return ownershipType == OwnershipType.KOOPWONING ? OwnershipCategory.koop_onb : OwnershipCategory.huur_onb;
        }
    }


    @Data
    public static class OwnershipValueModel {
        private OwnershipType ownershipType;
        private Integer amount;
        private OwnershipCategory ownershipCategory;
    }

    public enum OwnershipCategory {
        koop1,
        koop2,
        koop3,
        koop4,
        koop_onb,
        huur1,
        huur2,
        huur3,
        huur4,
        huur_onb;
    }

    private static Map<OwnershipCategory, Long> zuidHollandPriceRangeCategories = new HashMap<>();
    static {
        zuidHollandPriceRangeCategories.put(OwnershipCategory.koop1, 0L); //lower range limit in cents
        zuidHollandPriceRangeCategories.put(OwnershipCategory.koop2, 28000000L);
        zuidHollandPriceRangeCategories.put(OwnershipCategory.koop3, 39000000L);
        zuidHollandPriceRangeCategories.put(OwnershipCategory.koop4, 60000000L);

        zuidHollandPriceRangeCategories.put(OwnershipCategory.huur1, 0L);
        zuidHollandPriceRangeCategories.put(OwnershipCategory.huur2, 69400L);
        zuidHollandPriceRangeCategories.put(OwnershipCategory.huur3, 88000L);
        zuidHollandPriceRangeCategories.put(OwnershipCategory.huur4, 112300L);
    }
}
