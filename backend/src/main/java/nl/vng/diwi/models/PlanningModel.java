package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlanningModel {

    private UUID projectId;

    private String name;

    private Integer amount;

    private Integer year;

}
