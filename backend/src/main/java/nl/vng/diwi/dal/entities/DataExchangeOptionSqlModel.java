package nl.vng.diwi.dal.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DataExchangeOptionSqlModel {

    private UUID id;

    private String name;

    private UUID propertyCategoryValueId;

    private UUID propertyOrdinalValueId;
}
