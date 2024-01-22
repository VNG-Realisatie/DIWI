package com.vng.dal.entities.superclasses;

import com.vng.dal.entities.Milestone;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StartEndMilestoneSuperclass extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_milestone_id")
    private Milestone startMilestone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_milestone_id")
    private Milestone endMilestone;
}
