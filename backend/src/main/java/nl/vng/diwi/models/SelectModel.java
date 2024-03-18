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
@EqualsAndHashCode
public class SelectModel implements Comparable<SelectModel> {
    @JsonProperty(required = true)
    private UUID id;

    @JsonProperty(required = true)
    private String name;


    @Override
    public int compareTo(SelectModel o) {
        return this.name.compareToIgnoreCase(o.getName());
    }

}
