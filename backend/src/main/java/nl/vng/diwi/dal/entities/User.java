package nl.vng.diwi.dal.entities;

import java.util.List;

import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import org.hibernate.annotations.Filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends IdSuperclass {
    
    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<UserState> state;

}
