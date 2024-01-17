package com.vng.dal.entities;

import com.vng.dal.CustomUuidGeneration;
import jakarta.persistence.*;

import java.util.UUID;

import static com.vng.dal.GenericRepository.VNG_SCHEMA_NAME;

@Entity
@Table(name = "milestone", schema = VNG_SCHEMA_NAME)
public class Milestone {

    @Id
    @GeneratedValue
    @CustomUuidGeneration
    @Column(name = "id")
    private UUID id;

    public Milestone() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
