package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.PropertyKind;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.List;

@Entity
@Table(name = "property", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class Property extends IdSuperclass {

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PropertyKind type;

    @OneToMany(mappedBy= "property", fetch = FetchType.LAZY)
    @Filter(name = GenericRepository.CURRENT_DATA_FILTER, condition = "change_end_date IS NULL")
    private List<PropertyState> states;

    @OneToMany(mappedBy= "property", fetch = FetchType.LAZY)
    private List<PropertyCategoryValue> categoryValues;

    @OneToMany(mappedBy= "property", fetch = FetchType.LAZY)
    private List<PropertyOrdinalValue> ordinalValues;

}
