package nl.vng.diwi.dal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;

@Entity
@Table(name = "project_maatwerk_boolean_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class ProjectBooleanCustomPropertyChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("name")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonIgnoreProperties("name")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eigenschap_id")
    private CustomProperty customProperty;

    @Column(name = "value")
    private Boolean value;

}
