package nl.vng.diwi.services.export.geojson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import nl.vng.diwi.dal.entities.ProjectExportSqlModelPlus;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.enums.OwnershipType;
import nl.vng.diwi.models.PropertyModel;
import nl.vng.diwi.services.DataExchangeExportError;

@Data
public class GeoJsonHouseblockExportModel {

    private UUID houseblockId;
    private String name;
    private Integer deliveryYear;
    private Integer mutationAmount;
    private MutationType mutationKind;

    private Integer meergezinswoning;
    private Integer eengezinswoning;

    private List<OwnershipValueModel> ownershipValueList = new ArrayList<>();

    public GeoJsonHouseblockExportModel(UUID projectUuid,
            ProjectExportSqlModelPlus.HouseblockExportSqlModel sqlModel, PropertyModel priceRangeBuyFixedProp,
            PropertyModel priceRangeRentFixedProp, List<DataExchangeExportError> errors) {
        this.houseblockId = sqlModel.getHouseblockId();
        this.name = sqlModel.getName();
        this.deliveryYear = sqlModel.getDeliveryYear();
        this.mutationAmount = sqlModel.getMutationAmount();
        this.mutationKind = sqlModel.getMutationKind();
        this.meergezinswoning = sqlModel.getMeergezinswoning();
        this.eengezinswoning = sqlModel.getEengezinswoning();
    }

    @Data
    public static class OwnershipValueModel {
        private OwnershipType ownershipType;
        private Integer amount;
    }
}
