package nl.vng.diwi.services.export;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import nl.vng.diwi.dal.entities.enums.OwnershipCategory;
import nl.vng.diwi.models.DataExchangeModel;
import nl.vng.diwi.models.DataExchangeModel.PriceCategories;
import nl.vng.diwi.models.DataExchangePropertyModel;

/**
 * Wraps the DataExchangeModel and has some helper functions to efficiently get the contents when exporting.
 */
@Getter
public class DataExchangeConfigForExport {
    private DataExchangeModel dataExchangeModel;
    private Map<String, DataExchangePropertyModel> dxPropertiesMap;
    private Map<UUID, OwnershipCategory> categoryOwnershipMap;

    public DataExchangeConfigForExport(DataExchangeModel dataExchangeModel) {
        this.dataExchangeModel = dataExchangeModel;

        dxPropertiesMap = dataExchangeModel.getProperties().stream()
                .collect(Collectors.toMap(DataExchangePropertyModel::getName, Function.identity()));

        PriceCategories priceCategories = dataExchangeModel.getPriceCategories();
        categoryOwnershipMap = new HashMap<>();
        Stream.concat(priceCategories.getBuy().stream(), priceCategories.getRent().stream())
                .forEach(pc -> pc.getCategoryValueIds()
                        .forEach(id -> categoryOwnershipMap.put(id, pc.getName())));
    }

    public DataExchangePropertyModel getDxProp(String name) {
        return dxPropertiesMap.get(name);
    }

    public OwnershipCategory getMappedCategory(UUID categoryId) {
        return categoryOwnershipMap.get(categoryId);
    }
}
