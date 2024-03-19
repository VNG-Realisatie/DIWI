package nl.vng.diwi.models;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SelectDisabledModel extends SelectModel {
    @JsonProperty(required = false)
    private UUID id;

    @JsonProperty(required = true)
    private Boolean disabled = false;

}
