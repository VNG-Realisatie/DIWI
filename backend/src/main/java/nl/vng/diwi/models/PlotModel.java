package nl.vng.diwi.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.vng.diwi.dal.entities.ProjectRegistryLinkChangelogValue;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class PlotModel {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public PlotModel(ProjectRegistryLinkChangelogValue value) {
        this.brkGemeenteCode = value.getBrkGemeenteCode();
        this.brkPerceelNummer = value.getBrkPerceelNummer();
        this.brkSectie = value.getBrkSectie();
        this.subselectionGeometry = value.getSubselectionGeometry();
        this.plotFeature = value.getPlotFeature();
    }

    private String brkGemeenteCode;
    private String brkSectie;
    private Long brkPerceelNummer;
    private ObjectNode subselectionGeometry;
    private ObjectNode plotFeature;

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
        else if (brkPerceelNummer == null) {
            return "brkPerceelNummer can not be null";
        }
        else if (plotFeature == null) {
            return "plotFeature can not be null";
        } else {
            try {
                GeoJsonObject plotFeatureObj = MAPPER.treeToValue(plotFeature, GeoJsonObject.class);
                if (!(plotFeatureObj instanceof Feature)) {
                    return "plotFeature does not have expected format";
                }
            } catch (JsonProcessingException e) {
                return "plotFeature does not have expected format";
            }
            if (subselectionGeometry != null) {
                try {
                    GeoJsonObject subselectionGeometryObj = MAPPER.treeToValue(subselectionGeometry, GeoJsonObject.class);
                    if (!(subselectionGeometryObj instanceof Polygon) && !(subselectionGeometryObj instanceof MultiPolygon)) {
                        return "subselectionGeometry does not have expected format";
                    }
                } catch (JsonProcessingException e) {
                    return "subselectionGeometry does not have expected format";
                }
            }
        }
        return null;
    }
}
