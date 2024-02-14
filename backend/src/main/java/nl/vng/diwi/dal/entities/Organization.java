package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;

@Entity
@Table(name = "organization", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Organization extends IdSuperclass {

}
