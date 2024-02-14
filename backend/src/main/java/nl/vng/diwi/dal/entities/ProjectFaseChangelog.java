package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.enums.ProjectPhase;
import nl.vng.diwi.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "project_fase_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectFaseChangelog extends MilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "project_fase")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private ProjectPhase projectPhase;  //TODO - mapping doesn't work
}
