package nl.vng.diwi.dal.entities;

import java.util.List;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project", schema = GenericRepository.VNG_SCHEMA_NAME)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Project extends IdSuperclass {

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    private List<Milestone> milestones;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectState> state;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectNameChangelog> name;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectDurationChangelog> duration;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectFaseChangelog> phase;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectPlanTypeChangelog> planType;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectPlanologischePlanstatusChangelog> planologischePlanstatus;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectPrioriseringChangelog> priority;

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public List<ProjectState> getState() {
        return state;
    }

    public void setState(List<ProjectState> state) {
        this.state = state;
    }

    public List<ProjectNameChangelog> getName() {
        return name;
    }

    public void setName(List<ProjectNameChangelog> name) {
        this.name = name;
    }

    public List<ProjectDurationChangelog> getDuration() {
        return duration;
    }

    public void setDuration(List<ProjectDurationChangelog> duration) {
        this.duration = duration;
    }

    public List<ProjectFaseChangelog> getPhase() {
        return phase;
    }

    public void setPhase(List<ProjectFaseChangelog> phase) {
        this.phase = phase;
    }

    public List<ProjectPlanTypeChangelog> getPlanType() {
        return planType;
    }

    public void setPlanType(List<ProjectPlanTypeChangelog> planType) {
        this.planType = planType;
    }

    public List<ProjectPlanologischePlanstatusChangelog> getPlanologischePlanstatus() {
        return planologischePlanstatus;
    }

    public void setPlanologischePlanstatus(List<ProjectPlanologischePlanstatusChangelog> planologischePlanstatus) {
        this.planologischePlanstatus = planologischePlanstatus;
    }

    public List<ProjectPrioriseringChangelog> getPriority() {
        return priority;
    }

    public void setPriority(List<ProjectPrioriseringChangelog> priority) {
        this.priority = priority;
    }
}
