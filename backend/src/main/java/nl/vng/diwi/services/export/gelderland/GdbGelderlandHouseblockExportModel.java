package nl.vng.diwi.services.export.gelderland;

import static nl.vng.diwi.services.export.ExportUtil.*;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.*;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.dataexchange.DataExchangeTemplate.PriceCategoryPeriod;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.ExportUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class GdbGelderlandHouseblockExportModel {

    private UUID houseblockId;
    private Integer deliveryYear;
    private Integer mutationAmount;
    private MutationType mutationKind;

    private Integer meergezinswoning;
    private Integer eengezinswoning;

    private List<ExportUtil.OwnershipValueModel> ownershipValueList = new ArrayList<>();

    public GdbGelderlandHouseblockExportModel(UUID projectUuid,
            HouseblockExportSqlModel block,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            DataExchangeTemplate template,
            List<DataExchangeExportError> errors) {
        this.houseblockId = block.getHouseblockId();
        this.deliveryYear = block.getDeliveryYear();
        this.mutationAmount = block.getMutationAmount();
        this.mutationKind = block.getMutationKind();
        this.meergezinswoning = block.getMeergezinswoning();
        this.eengezinswoning = block.getEengezinswoning();

        // This assumes the periods are ordered from old to new
        var priceCategoriesForPeriod = template.getPriceCategoryPeriods()
                .stream()
                .filter(pcp -> pcp.getValidUntil() == null || pcp.getValidUntil().isAfter(block.getEndDate()))
                .findFirst()
                .orElseThrow();
        block.getOwnershipValueList()
                .forEach(o -> {
                    var oModel = createOwnershipValueModel(
                            projectUuid,
                            block,
                            priceRangeBuyFixedProp,
                            priceRangeRentFixedProp,
                            errors,
                            priceCategoriesForPeriod,
                            o);

                    this.ownershipValueList.add(oModel);
                });
    }

    private ExportUtil.OwnershipValueModel createOwnershipValueModel(
            UUID projectUuid,
            HouseblockExportSqlModel sqlModel,
            PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp,
            List<DataExchangeExportError> errors,
            PriceCategoryPeriod priceCategoriesForPeriod,
            OwnershipValueSqlModel o) {
        ExportUtil.OwnershipValueModel oModel = new ExportUtil.OwnershipValueModel();
        oModel.setOwnershipType(o.getOwnershipType());
        oModel.setAmount(o.getOwnershipAmount());
        if (o.getOwnershipType() == OwnershipType.KOOPWONING) {
            if (o.getOwnershipValue() != null) {
                oModel.setOwnershipCategory(
                        getOwnershipCategory(o.getOwnershipType(),
                                o.getOwnershipValue(),
                                priceCategoriesForPeriod));
            } else if (o.getOwnershipValueRangeMin() != null) {
                oModel.setOwnershipCategory(ExportUtil.getOwnershipCategory(
                        projectUuid,
                        sqlModel.getHouseblockId(),
                        o.getOwnershipType(),
                        o.getOwnershipValueRangeMin(),
                        o.getOwnershipValueRangeMax(),
                        priceCategoriesForPeriod,
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
                            rangeOption.getMin() == null ? null : rangeOption.getMin().longValue(),
                            rangeOption.getMax() == null ? null : rangeOption.getMax().longValue(),
                            priceCategoriesForPeriod,
                            errors));
                }
            } else {
                oModel.setOwnershipCategory(OwnershipCategory.koop_onb);
            }
        } else {
            if (o.getOwnershipRentalValue() != null) {
                oModel.setOwnershipCategory(getOwnershipCategory(o.getOwnershipType(), o.getOwnershipRentalValue(), priceCategoriesForPeriod));
            } else if (o.getOwnershipRentalValueRangeMin() != null) {
                oModel.setOwnershipCategory(
                        ExportUtil.getOwnershipCategory(
                                projectUuid,
                                sqlModel.getHouseblockId(),
                                o.getOwnershipType(),
                                o.getOwnershipRentalValueRangeMin(),
                                o.getOwnershipRentalValueRangeMax(),
                                priceCategoriesForPeriod,
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
                                    rangeOption.getMin() == null ? null : rangeOption.getMin().longValue(),
                                    rangeOption.getMax() == null ? null : rangeOption.getMax().longValue(),
                                    priceCategoriesForPeriod,
                                    errors));
                }
            } else {
                oModel.setOwnershipCategory(OwnershipCategory.huur_onb);
            }
        }
        return oModel;
    }
}
