package nl.vng.diwi.dal.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "woningblok_mutatie_changelog_soort_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockMutatieChangelogTypeValue extends IdSuperclass {

    @JsonIgnoreProperties("type")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "woningblok_mutatie_changelog_id")
    HouseblockMutatieChangelog mutatieChangelog;

    @Column(name = "mutatie_soort")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private MutationType mutationType;

}
