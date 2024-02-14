package com.vng.dal.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vng.dal.entities.enums.PlanStatus;
import com.vng.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_planologische_planstatus_changelog_value", schema = VNG_SCHEMA_NAME)
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
    private PlanStatus planStatus;
}
