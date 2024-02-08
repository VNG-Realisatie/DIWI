package com.vng.models;

import java.util.UUID;

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

    public MilestoneModel(MilestoneState mileStoneState) {
        this.setId(mileStoneState.getMilestone().getId());
        this.setDescription(mileStoneState.getDescription());
        this.setDate(new LocalDateModel(mileStoneState.getDate()));
        this.setState(mileStoneState.getState());
    }
}
