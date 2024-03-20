package nl.vng.diwi.models;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelogValue;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PlotModel {
    public PlotModel(ProjectRegistryLinkChangelogValue value) {
        this.brkGemeenteCode = value.getBrkGemeenteCode();
        this.brkPerceelNummer = value.getBrkPerceelNummer();
        this.brkSectie = value.getBrkSectie();
        this.brkSelectie = value.getBrkSelectie();
        this.geoJson = value.getGeoJson();
    }

    private String brkGemeenteCode;
    private String brkSectie;
    private Long brkPerceelNummer;
    private String brkSelectie;
    private ObjectNode geoJson;
}
