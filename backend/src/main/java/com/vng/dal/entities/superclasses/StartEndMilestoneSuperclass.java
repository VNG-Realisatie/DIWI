package com.vng.dal.entities.superclasses;

import com.vng.dal.entities.Milestone;
import com.vng.dal.entities.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
public class StartEndMilestoneSuperclass extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_milestone_id")
    private Milestone startMilestone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_milestone_id")
    private Milestone endMilestone;
}
