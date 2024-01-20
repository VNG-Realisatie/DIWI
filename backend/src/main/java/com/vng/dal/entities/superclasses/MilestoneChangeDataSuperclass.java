package com.vng.dal.entities.superclasses;

import com.vng.dal.entities.Milestone;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MilestoneChangeDataSuperclass extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_milestone_id")
    private Milestone startMilestone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_milestone_id")
    private Milestone endMilestone;
}
