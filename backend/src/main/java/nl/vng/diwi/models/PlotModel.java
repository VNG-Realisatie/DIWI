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

    public String validate() {
        if (brkGemeenteCode == null) {
            return "brkGemeenteCode can not be null";
        }
        else if (brkGemeenteCode.isBlank()) {
            return "brkGemeenteCode can not be blank";
        }
        else if (brkSectie == null) {
            return "brkSectie can not be null";
        }
        else if (brkSectie.isBlank()) {
            return "brkSectie can not be blank";
        }
        else if (brkSelectie == null) {
            return "brkPerceelNummer can not be null";
        }
        else if (brkSelectie.isBlank()) {
            return "brkSelectie can not be blank";
        }
        else if (brkPerceelNummer == null) {
            return "brkPerceelNummer can not be null";
        }
        else if (geoJson == null) {
            return "geoJson can not be null";
        }
        return null;
    }
}
