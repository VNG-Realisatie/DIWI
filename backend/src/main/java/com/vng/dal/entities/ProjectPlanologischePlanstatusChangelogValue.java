package com.vng.dal.entities;

import com.vng.dal.entities.enums.PlanStatus;
import com.vng.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_planologische_planstatus_changelog_value", schema = VNG_SCHEMA_NAME)
@Data
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
