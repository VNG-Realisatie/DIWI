package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationModel {

    @JsonProperty(required = true)
    private Double lat;

    @JsonProperty(required = true)
    private Double lng;

}
