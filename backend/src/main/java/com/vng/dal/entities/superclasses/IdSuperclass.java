package com.vng.dal.entities.superclasses;

import com.vng.dal.CustomUuidGeneration;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@MappedSuperclass
@Data
@NoArgsConstructor
public class IdSuperclass {

    @Id
    @GeneratedValue
    @CustomUuidGeneration
    @Column(name = "id")
    private UUID id;
}
