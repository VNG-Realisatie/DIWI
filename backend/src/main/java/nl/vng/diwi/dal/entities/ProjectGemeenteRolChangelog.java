package nl.vng.diwi.dal.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import nl.vng.diwi.dal.GenericRepository;

@Entity
@Table(name = "project_gemeenterol_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class ProjectGemeenteRolChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("municipalityRole")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_gemeenterol_value_id")
    private ProjectGemeenteRolValue value;

}
