package nl.vng.diwi.models;

import java.time.LocalDate;
import java.util.UUID;

import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.dal.entities.MilestoneState;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MilestoneModel {
    private UUID id;
    private MilestoneStatus state;
    private LocalDate date;
    private String description;

    public MilestoneModel(Milestone milestone) {
        this.setId(milestone.getId());
        MilestoneState milestoneState = milestone.getState().get(0);
        this.setDescription(milestoneState.getDescription());
        this.setDate(milestoneState.getDate());
        this.setState(milestoneState.getState());
    }
}
