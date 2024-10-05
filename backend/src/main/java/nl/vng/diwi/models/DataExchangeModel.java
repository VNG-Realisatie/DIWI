package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.DataExchangeState;
import nl.vng.diwi.dal.entities.DataExchangeType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DataExchangeModel {

    @JsonProperty(required = true)
    private UUID id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private DataExchangeType type;

    private String apiKey;

    private String projectUrl;

    private String projectDetailUrl;

    private List<DataExchangePropertyModel> properties = new ArrayList<>();

    public DataExchangeModel(DataExchangeState dataExchangeState) {
        this.setId(dataExchangeState.getDataExchange().getId());
        this.setName(dataExchangeState.getName());
        this.setType(dataExchangeState.getType());
        this.setApiKey(dataExchangeState.getApiKey());
        this.setProjectUrl(dataExchangeState.getProjectUrl());
        this.setProjectDetailUrl(dataExchangeState.getProjectDetailUrl());
    }

    public String validate() {
        if (this.name == null || this.name.isBlank()) {
            return "Property name can not be null.";
        }
        if (this.type == null) {
            return "Property type can not be null.";
        }
        return null;
    }
}
