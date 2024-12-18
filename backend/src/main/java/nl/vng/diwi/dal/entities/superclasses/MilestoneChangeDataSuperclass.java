package nl.vng.diwi.dal.entities.superclasses;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.Milestone;
import nl.vng.diwi.generic.CopyObject;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class MilestoneChangeDataSuperclass extends ChangeDataSuperclass  implements CopyObject{

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_milestone_id")
    private Milestone startMilestone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "end_milestone_id")
    private Milestone endMilestone;

}
