package nl.vng.diwi.models;

import nl.vng.diwi.models.interfaces.WeightedValueModelInterface;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class PriorityModel implements WeightedValueModelInterface<String> {
    private Integer level;
    private String data;
    @JsonValue
    private String name;

    public PriorityModel(Integer level, String data) {
        this.level = level;
        this.data = data;
        updateName();
    }
    private void updateName() {
        if (level != null && data != null) {
            name = new String();
            name = name.concat(this.level.toString());
            name = name.concat(" ");
            name = name.concat(this.data);
            name = name.trim();
        }
    }

    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
        updateName();
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
        updateName();
    }
    public String getName() {
        return name;
    }    
}
