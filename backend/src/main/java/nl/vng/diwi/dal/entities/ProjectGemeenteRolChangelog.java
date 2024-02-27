package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import nl.vng.diwi.dal.GenericRepository;

@Entity
@Table(name = "project_gemeenterol_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectGemeenteRolChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("municipalityRole")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_gemeenterol_value_id")
    private ProjectGemeenteRolValue value;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectGemeenteRolValue getValue() {
        return value;
    }

    public void setValue(ProjectGemeenteRolValue value) {
        this.value = value;
    }
}
