package nl.vng.diwi.dal.entities;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
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

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectGemeenteRolChangelog> municipalityRole;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<OrganizationProjectRole> organizationProjectRoles;

    @JsonIgnoreProperties("project")
    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<Houseblock> houseblocks;

    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<OrganizationToProject> organizationToProject;

    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectBooleanCustomPropertyChangelog> booleanCustomProperties;

    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectNumericCustomPropertyChangelog> numericCustomProperties;

    @OneToMany(mappedBy="project", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<ProjectTextCustomPropertyChangelog> textCustomProperties;
}
