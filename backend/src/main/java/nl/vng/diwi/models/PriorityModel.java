package nl.vng.diwi.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class PriorityModel {

    private SelectModel value;
    private SelectModel min;
    private SelectModel max;

    public PriorityModel(List<SelectModel> priorities) {
        if (priorities != null) {
            if (priorities.size() == 1) {
                value = priorities.get(0);
            } else if (priorities.size() == 2) {
                min = priorities.get(0);
                max = priorities.get(1);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriorityModel that = (PriorityModel) o;

        if (!Objects.equals(this.value, that.value)) return false;
        if (!Objects.equals(this.min, that.min)) return false;
        return Objects.equals(this.max, that.max);
    }

}
