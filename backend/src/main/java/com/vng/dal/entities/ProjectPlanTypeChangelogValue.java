package com.vng.dal.entities;

import com.vng.dal.entities.enums.PlanType;
import com.vng.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_duration_changelog_value", schema = VNG_SCHEMA_NAME)
@Data
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
