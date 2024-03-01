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
@Table(name = "woningblok", schema = GenericRepository.VNG_SCHEMA_NAME)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Woningblok extends IdSuperclass {

    @JsonIgnoreProperties("woningblokken")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @JsonIgnoreProperties("woningblok")
    @OneToMany(mappedBy="woningblok", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<WoningblokMutatieChangelog> mutaties;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<WoningblokMutatieChangelog> getMutaties() {
        return mutaties;
    }

    public void setMutatie(List<WoningblokMutatieChangelog> mutaties) {
        this.mutaties = mutaties;
    }

}
