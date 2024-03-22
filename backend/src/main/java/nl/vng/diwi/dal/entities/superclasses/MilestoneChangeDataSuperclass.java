package nl.vng.diwi.dal.entities.superclasses;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.entities.Milestone;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class MilestoneChangeDataSuperclass extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_milestone_id")
    private Milestone startMilestone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "end_milestone_id")
    private Milestone endMilestone;
}
