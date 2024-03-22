package nl.vng.diwi.dal.entities;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.vng.diwi.dal.GenericRepository;
import nl.vng.diwi.dal.entities.superclasses.IdSuperclass;

@Entity
@Table(name = "project_registry_link_changelog_value", schema = GenericRepository.VNG_SCHEMA_NAME)
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProjectRegistryLinkChangelogValue extends IdSuperclass {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_registry_link_changelog_id")
    private ProjectRegistryLinkChangelog projectRegistryLinkChangelog;


    @Column(name = "brk_gemeente_code")
    private String brkGemeenteCode;

    @Column(name = "brk_sectie")
    private String brkSectie;

    @Column(name = "brk_perceelnummer")
    private Long brkPerceelNummer;

    @Column(name = "brk_selectie")
    private String brkSelectie;

    @Type(JsonType.class)
    @Column(name = "geojson", columnDefinition = "json")
    private ObjectNode geoJson;
}
