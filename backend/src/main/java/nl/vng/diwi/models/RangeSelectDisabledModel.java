package nl.vng.diwi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RangeSelectDisabledModel extends SelectModel {

    @JsonProperty(required = true)
    private BigDecimal min;

    @JsonProperty
    private BigDecimal max;

    @JsonProperty(required = true)
    private Boolean disabled = false;

}
