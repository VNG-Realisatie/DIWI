package nl.vng.diwi.dal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

@Entity
@Table(name = "blueprint_to_usergroup", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class BlueprintToUserGroup extends IdSuperclass {

    @ManyToOne
    @JoinColumn(name = "blueprint_state_id")
    private BlueprintState blueprintState;

    @ManyToOne
    @JoinColumn(name = "usergroup_id")
    private UserGroup userGroup;
}
