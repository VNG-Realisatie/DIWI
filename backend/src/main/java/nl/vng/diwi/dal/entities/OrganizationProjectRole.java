package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.ProjectRole;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "organization_to_project", schema = GenericRepository.VNG_SCHEMA_NAME)
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrganizationProjectRole extends ChangeDataSuperclass {
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "project_rol")
    @Enumerated(EnumType.STRING)
    private ProjectRole projectRole;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public ProjectRole getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(ProjectRole projectRole) {
        this.projectRole = projectRole;
    }
}
