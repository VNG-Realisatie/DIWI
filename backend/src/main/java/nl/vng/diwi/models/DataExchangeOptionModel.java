package nl.vng.diwi.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangeOptionSqlModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DataExchangeOptionModel {

    private UUID id;

    private String name;

    private List<UUID> propertyCategoryValueIds = new ArrayList<>();

    private List<UUID> propertyOrdinalValueIds = new ArrayList<>();

    public DataExchangeOptionModel(DataExchangeOptionSqlModel sqlOption) {
        this.id = sqlOption.getId();
        this.name = sqlOption.getName();
    }
}
