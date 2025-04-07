package nl.vng.diwi.services.export.gelderland;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelExtended.*;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dataexchange.DataExchangeTemplate;
import nl.vng.diwi.models.RangeSelectDisabledModel;
import nl.vng.diwi.services.DataExchangeExportError;
import nl.vng.diwi.services.export.DataExchangeConfigForExport;
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
            List<RangeSelectDisabledModel> ranges,
            DataExchangeConfigForExport dxConfig,
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
                    var oModel = ExportUtil.createOwnershipValueModel(
                            projectUuid,
                            block,
                            ranges,
                            errors,
                            priceCategoriesForPeriod,
                            dxConfig,
                            o);

                    this.ownershipValueList.add(oModel);
                });
    }
}
