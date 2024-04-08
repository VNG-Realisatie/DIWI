package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

import java.util.List;

@Entity
@Table(name = "project_maatwerk_categorie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class ProjectCategoryCustomPropertyChangelog extends MilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eigenschap_id")
    private Property property;

    @OneToMany(mappedBy="categoryChangelog", fetch = FetchType.LAZY)
    private List<ProjectCategoryCustomPropertyChangelogValue> changelogCategoryValues;

}
