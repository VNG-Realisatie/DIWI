package nl.vng.diwi.dal.entities;

import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;

@Entity
@Table(name = "project_gemeenterol_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ProjectGemeenteRolValue extends IdSuperclass {
}
