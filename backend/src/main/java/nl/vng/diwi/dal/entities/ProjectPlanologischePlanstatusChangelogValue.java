package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.PlanStatus;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "project_planologische_planstatus_changelog_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPlanologischePlanstatusChangelogValue extends IdSuperclass {

    @JsonIgnoreProperties("value")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planologische_planstatus_changelog_id")
    private ProjectPlanologischePlanstatusChangelog planStatusChangelog;

    @Enumerated(EnumType.STRING)
    @Column(name = "planologische_planstatus")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PlanStatus planStatus;
}
