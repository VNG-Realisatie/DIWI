package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.PlanType;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_plan_type_changelog_value", schema = GenericRepository.VNG_SCHEMA_NAME)
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
