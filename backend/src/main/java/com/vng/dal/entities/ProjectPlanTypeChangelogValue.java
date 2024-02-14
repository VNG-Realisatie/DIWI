package com.vng.dal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vng.dal.entities.enums.PlanType;
import com.vng.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_plan_type_changelog_value", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPlanTypeChangelogValue extends IdSuperclass {

    @JsonIgnoreProperties("value")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "changelog_id")
    private ProjectPlanTypeChangelog planTypeChangelog;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    private PlanType planType;
    
}
