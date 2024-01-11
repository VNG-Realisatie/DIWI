package com.vng.dal.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "organization", schema = "diwi_testset_simplified")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_ID_seq")
    @SequenceGenerator(name = "organization_ID_seq", allocationSize = 1)
    @Column(name = "\"ID\"")
    private Long id;

    public Organization() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
