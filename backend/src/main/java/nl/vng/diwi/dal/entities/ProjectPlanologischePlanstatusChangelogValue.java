package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "project_planologische_planstatus_changelog_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPlanologischePlanstatusChangelogValue extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planologische_planstatus_changelog_id")
    private ProjectPlanTypeChangelog planStatusChangelog;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "planologische_planstatus")
    private PlanStatus planStatus;  //TODO: mapping doesn't work
}
