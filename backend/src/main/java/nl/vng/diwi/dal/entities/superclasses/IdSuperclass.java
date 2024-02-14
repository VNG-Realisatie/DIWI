package nl.vng.diwi.dal.entities.superclasses;

import nl.vng.diwi.dal.CustomUuidGeneration;
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
