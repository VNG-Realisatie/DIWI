package nl.vng.diwi.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
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

}
