package nl.vng.diwi.models;

import lombok.Data;
import nl.vng.diwi.models.superclasses.DatedDataModelSuperClass;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class DatedPriorityModel extends DatedDataModelSuperClass {

    private PriorityModel priorityModel = new PriorityModel();

    public static String getLabel(Integer level, String label) {
        return level.toString() + " " + label;
    }

}
