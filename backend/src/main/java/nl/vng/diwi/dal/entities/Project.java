package nl.vng.diwi.dal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;

import java.util.List;

import org.hibernate.annotations.Filter;

@Entity
@Table(name = "project", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Project extends IdSuperclass {

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectState> state;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectNameChangelog> name;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectDurationChangelog> duration;
    
    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectFaseChangelog> phase;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectPlanTypeChangelog> planType;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectPlanologischePlanstatusChangelog> planologischePlanstatus;
    
    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = "current", condition = "change_end_date IS NULL")
    private List<ProjectPrioriseringChangelog> priority;
}
