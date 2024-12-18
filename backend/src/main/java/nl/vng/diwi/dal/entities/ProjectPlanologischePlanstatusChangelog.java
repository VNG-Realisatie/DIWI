package nl.vng.diwi.dal.entities;

import java.util.List;

import org.hibernate.Session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_planologische_planstatus_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectPlanologischePlanstatusChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("planologischePlanstatus")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonIgnoreProperties("planStatusChangelog")
    @OneToMany(mappedBy = "planStatusChangelog", fetch = FetchType.LAZY)
    private List<ProjectPlanologischePlanstatusChangelogValue> value;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var copy = new ProjectPlanologischePlanstatusChangelog();
        copy.setProject(project);
        var newValues = value.stream()
                .map(v -> {
                    var newValue = new ProjectPlanologischePlanstatusChangelogValue();
                    newValue.setPlanStatus(v.getPlanStatus());
                    newValue.setPlanStatusChangelog(copy);
                    session.persist(newValue);
                    return newValue;
                })
                .toList();
        copy.setValue(newValues);
        return copy;
    }
}
