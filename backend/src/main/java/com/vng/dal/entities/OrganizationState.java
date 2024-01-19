package com.vng.dal.entities;

import com.vng.dal.entities.superclasses.ChangeDataSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "organization_state", schema = VNG_SCHEMA_NAME)
@Data
@NoArgsConstructor
public class OrganizationState extends ChangeDataSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_organization_id")
    private Organization parentOrganization;

    @Column(name = "naam")
    private String name;

}
