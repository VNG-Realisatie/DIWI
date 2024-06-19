package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrdinalSelectDisabledModel extends SelectDisabledModel {

    @JsonProperty(required = true)
    private Integer level;

    public String getOrdinalValue() {
        return this.getLevel() + " " + this.getName();
    }
}
