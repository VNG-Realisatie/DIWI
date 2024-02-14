package com.vng.dal.entities;

import com.vng.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "project_plan_type_changelog", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectPlanTypeChangelog extends MilestoneChangeDataSuperclass {

    @JsonIgnoreProperties("planType")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    @JsonIgnoreProperties("planTypeChangelog")
    @OneToMany(mappedBy="planTypeChangelog", fetch = FetchType.EAGER)
    private List<ProjectPlanTypeChangelogValue> value;
}
