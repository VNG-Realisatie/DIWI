package nl.vng.diwi.dal.entities.superclasses;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.CustomUuidGeneration;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class IdSuperclass {

    @Id
    @GeneratedValue
    @CustomUuidGeneration
    @Column(name = "id")
    private UUID id;

}
