package nl.vng.diwi.dal.entities;

import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usergroup_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class UserGroupState extends ChangeDataSuperclass {

    @JsonIgnoreProperties("state")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usergroup_id")
    private UserGroup userGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_usergroup_id")
    private UserGroup parentUserGroup;

    @Column(name = "naam")
    private String name;

}
