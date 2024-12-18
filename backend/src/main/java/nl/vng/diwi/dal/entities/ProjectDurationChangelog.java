package nl.vng.diwi.dal.entities;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import org.apache.commons.lang3.NotImplementedException;
import org.hibernate.Session;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_duration_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class ProjectDurationChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("duration")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var copy = new ProjectDurationChangelog();
        copy.setProject(project);
        return copy;
    }
}
