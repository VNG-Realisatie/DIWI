package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "project_duration_changelog_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPlanTypeChangelogValue extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changelog_id")
    private ProjectPlanTypeChangelog planTypeChangelog;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PlanType planType;
}
