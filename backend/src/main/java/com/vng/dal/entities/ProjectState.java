package com.vng.dal.entities;

import com.vng.dal.entities.enums.Confidentiality;
import com.vng.dal.entities.superclasses.MilestoneChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project_state", schema = VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectState extends MilestoneChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_organization_id")
    private Organization ownerOrganization;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "confidentiality_level")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Confidentiality confidentiality;

    @Column(name = "project_color")
    private String color;
}
