package com.vng.dal.entities;

import jakarta.persistence.*;

import java.util.UUID;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "organization", schema = VNG_SCHEMA_NAME)
public class Organization {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    public Organization() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
