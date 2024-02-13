package com.vng.models;

import java.util.UUID;

import com.vng.dal.entities.Milestone;
import com.vng.dal.entities.MilestoneState;
import com.vng.dal.entities.enums.MilestoneStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MilestoneModel {
    private UUID id;
    private MilestoneStatus state;
    private LocalDateModel date;
    private String description;

    public MilestoneModel(Milestone milestone) {
        this.setId(milestone.getId());
        MilestoneState milestoneState = milestone.getState().get(0);
        this.setDescription(milestoneState.getDescription());
        this.setDate(new LocalDateModel(milestoneState.getDate()));
        this.setState(milestoneState.getState());
    }
}
