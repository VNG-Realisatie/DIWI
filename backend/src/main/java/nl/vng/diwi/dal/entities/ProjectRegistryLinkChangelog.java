package nl.vng.diwi.dal.entities;

import java.util.List;

import org.hibernate.Session;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

@Entity
@Table(name = "project_registry_link_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProjectRegistryLinkChangelog extends MilestoneChangeDataSuperclass {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "projectRegistryLinkChangelog", fetch = FetchType.LAZY)
    private List<ProjectRegistryLinkChangelogValue> values;

    @Override
    public Object getCopyWithoutMilestones(Session session) {
        var copy = new ProjectRegistryLinkChangelog();
        copy.setProject(project);
        var newValues = values.stream()
                .map(v -> {
                    var newValue = new ProjectRegistryLinkChangelogValue();
                    newValue.setBrkGemeenteCode(v.getBrkGemeenteCode());
                    newValue.setBrkPerceelNummer(v.getBrkPerceelNummer());
                    newValue.setBrkSectie(v.getBrkSectie());
                    newValue.setPlotFeature(v.getPlotFeature());
                    newValue.setSubselectionGeometry(v.getSubselectionGeometry());
                    return newValue;
                })
                .toList();
        copy.setValues(newValues);
        return copy;
    }

    @Override public void persistValues(Session session) {
        for(var singleValue: values) {
            singleValue.setProjectRegistryLinkChangelog(this);
            session.persist(singleValue);
        }
    }
}
