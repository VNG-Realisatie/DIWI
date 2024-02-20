package nl.vng.diwi.dal.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import nl.vng.diwi.dal.entities.enums.MilestoneStatus;
import nl.vng.diwi.dal.entities.superclasses.ChangeDataSuperclass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.GenericRepository;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "milestone_state", schema = GenericRepository.VNG_SCHEMA_NAME)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MilestoneState extends ChangeDataSuperclass {

    @JsonIgnoreProperties("state")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "milestone_id")
    private Milestone milestone;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private MilestoneStatus state;

    @Column(name = "omschrijving")
    private String description;
}
