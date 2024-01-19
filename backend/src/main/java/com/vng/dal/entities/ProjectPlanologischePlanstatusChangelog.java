package com.vng.dal.entities;

import com.vng.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_planologische_planstatus_changelog", schema = VNG_SCHEMA_NAME)
@Data
@NoArgsConstructor
public class ProjectPlanologischePlanstatusChangelog extends MilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
}
