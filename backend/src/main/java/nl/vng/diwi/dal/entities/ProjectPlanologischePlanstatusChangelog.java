package nl.vng.diwi.dal.entities;

import java.util.List;

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
public class ProjectPlanologischePlanstatusChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("planologischePlanstatus")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonIgnoreProperties("planStatusChangelog")
    @OneToMany(mappedBy="planStatusChangelog", fetch = FetchType.LAZY)
    private List<ProjectPlanologischePlanstatusChangelogValue> value;
}
