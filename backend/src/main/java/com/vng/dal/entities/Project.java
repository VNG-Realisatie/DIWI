package com.vng.dal.entities;

import jakarta.persistence.*;

import java.util.UUID;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "project", schema = VNG_SCHEMA_NAME)
public class Project {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    public Project() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
