package nl.vng.diwi.dal.entities.superclasses;

import nl.vng.diwi.dal.CustomUuidGeneration;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.UUID;

@MappedSuperclass
@NoArgsConstructor
public class IdSuperclass {

    @Id
    @GeneratedValue
    @CustomUuidGeneration
    @Column(name = "id")
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
