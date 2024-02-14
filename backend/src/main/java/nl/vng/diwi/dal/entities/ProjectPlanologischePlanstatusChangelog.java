package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "project_planologische_planstatus_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
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
