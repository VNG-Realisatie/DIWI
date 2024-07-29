package nl.vng.diwi.dal.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

@Entity
@Table(name = "plan_conditie_registry_link_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
public class PlanConditionRegistryLinkValue extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_conditie_registry_link_id")
    private PlanConditionRegistryLink registryLink;

    @Column(name = "brk_gemeente_code")
    private String brkGemeenteCode;

    @Column(name = "brk_sectie")
    private String brkSectie;

    @Column(name = "brk_perceelnummer")
    private Long brkPerceelNummer;

    @Column(name = "brk_selectie")
    private String brkSelectie;

}
