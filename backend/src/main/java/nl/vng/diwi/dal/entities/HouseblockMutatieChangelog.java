package nl.vng.diwi.dal.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.enums.MutationType;
import nl.vng.diwi.dal.entities.superclasses.HouseblockMilestoneChangeDataSuperclass;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "woningblok_mutatie_changelog", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseblockMutatieChangelog extends HouseblockMilestoneChangeDataSuperclass {

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "mutation_kind")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private MutationType mutationType;


    @Override
    public Object getShallowCopy() {
        var newChangelog = HouseblockMutatieChangelog.builder()
            .amount(amount).mutationType(mutationType)
            .build();
        newChangelog.setHouseblock(getHouseblock());
        newChangelog.setStartMilestone(getStartMilestone());
        newChangelog.setEndMilestone(getEndMilestone());
        return newChangelog;
    }
}
