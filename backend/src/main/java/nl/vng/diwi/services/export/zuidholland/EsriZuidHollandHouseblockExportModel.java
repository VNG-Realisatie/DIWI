package nl.vng.diwi.services.export.zuidholland;

import static nl.vng.diwi.services.export.ExportUtil.*;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel;
import nl.vng.diwi.dal.entities.ProjectExportSqlModel.OwnershipValueSqlModel;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.ExportUtil;
import nl.vng.diwi.services.export.OwnershipCategory;

import java.util.ArrayList;
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

    private List<ExportUtil.OwnershipValueModel> ownershipValueList = new ArrayList<>();

    public EsriZuidHollandHouseblockExportModel(UUID projectUuid,
            ProjectExportSqlModel.HouseblockExportSqlModel sqlModel, PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp, List<DataExchangeExportError> errors) {
        this.houseblockId = sqlModel.getHouseblockId();
        this.deliveryYear = sqlModel.getDeliveryYear();
        this.mutationAmount = sqlModel.getMutationAmount();
        this.mutationKind = sqlModel.getMutationKind();
        this.meergezinswoning = sqlModel.getMeergezinswoning();
        this.eengezinswoning = sqlModel.getEengezinswoning();
        var priceCategoryMap = ZuidHollandConstants.priceRangeMap;

        sqlModel.getOwnershipValueList()
                .forEach(o -> {
                    var oModel = createOwnershipValueModel(projectUuid, sqlModel, priceRangeBuyFixedProp, priceRangeRentFixedProp, errors, priceCategoryMap, o);
                    this.ownershipValueList.add(oModel);
                });
    }

    private ExportUtil.OwnershipValueModel createOwnershipValueModel(
            UUID projectUuid,
            ProjectExportSqlModel.HouseblockExportSqlModel sqlModel,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            List<DataExchangeExportError> errors,
            Map<OwnershipCategory, Long> priceCategoryMap,
            OwnershipValueSqlModel o) {
        ExportUtil.OwnershipValueModel oModel = new ExportUtil.OwnershipValueModel();
        oModel.setOwnershipType(o.getOwnershipType());
        oModel.setAmount(o.getOwnershipAmount());
        if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
            if (o.getOwnershipValue() != null) {
                oModel.setOwnershipCategory(
                        getOwnershipCategory(o.getOwnershipType(), o.getOwnershipValue(), priceCategoryMap));
            } else if (o.getOwnershipValueRangeMin() != null) {
                oModel.setOwnershipCategory(ExportUtil.getOwnershipCategory(
                        projectUuid,
                        sqlModel.getHouseblockId(),
                        o.getOwnershipType(),
                        o.getOwnershipValueRangeMin(),
                        o.getOwnershipValueRangeMax(),
                        priceCategoryMap,
                        errors));
            } else if (o.getOwnershipRangeCategoryId() != null) {
                RangeSelectDisabledModel rangeOption = priceRangeBuyFixedProp.getRanges().stream()
                        .filter(r -> r.getDisabled() == Boolean.FALSE
                                && r.getId().equals(o.getOwnershipRangeCategoryId()))
                        .findFirst().orElse(null);
                if (rangeOption == null) {
                    oModel.setOwnershipCategory(OwnershipCategory.koop_onb);
                } else {
                    oModel.setOwnershipCategory(ExportUtil.getOwnershipCategory(
                            projectUuid,
                            sqlModel.getHouseblockId(),
                            o.getOwnershipType(),
                            rangeOption.getMin().longValue(),
                            rangeOption.getMax().longValue(),
                            priceCategoryMap,
                            errors));
                }
            } else {
                oModel.setOwnershipCategory(OwnershipCategory.koop_onb);
            }
        } else {
            if (o.getOwnershipRentalValue() != null) {
                oModel.setOwnershipCategory(getOwnershipCategory(o.getOwnershipType(), o.getOwnershipRentalValue(), priceCategoryMap));
            } else if (o.getOwnershipRentalValueRangeMin() != null) {
                oModel.setOwnershipCategory(
                        ExportUtil.getOwnershipCategory(
                                projectUuid,
                                sqlModel.getHouseblockId(),
                                o.getOwnershipType(),
                                o.getOwnershipRentalValueRangeMin(),
                                o.getOwnershipRentalValueRangeMax(),
                                priceCategoryMap,
                                errors));
            } else if (o.getOwnershipRentalRangeCategoryId() != null) {
                RangeSelectDisabledModel rangeOption = priceRangeRentFixedProp.getRanges().stream()
                        .filter(r -> r.getDisabled() == Boolean.FALSE
                                && r.getId().equals(o.getOwnershipRentalRangeCategoryId()))
                        .findFirst().orElse(null);
                if (rangeOption == null) {
                    oModel.setOwnershipCategory(OwnershipCategory.huur_onb);
                } else {
                    oModel.setOwnershipCategory(
                            ExportUtil.getOwnershipCategory(projectUuid,
                                    sqlModel.getHouseblockId(),
                                    o.getOwnershipType(),
                                    rangeOption.getMin().longValue(),
                                    rangeOption.getMax().longValue(),
                                    priceCategoryMap,
                                    errors));
                }
            } else {
                oModel.setOwnershipCategory(OwnershipCategory.huur_onb);
            }
        }
        return oModel;
    }
}
