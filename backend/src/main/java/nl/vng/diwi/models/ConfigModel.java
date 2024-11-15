package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.vng.diwi.dal.entities.enums.Confidentiality;

@Data
public class ConfigModel {

    private MapBounds defaultMapBounds;
    private String municipalityName;
    private String regionName;
    private String provinceName;
    private Confidentiality minimumExportConfidentiality = Confidentiality.EXTERNAL_REGIONAL;

    @Data
    @AllArgsConstructor
    public static class MapBounds {
        private LocationModel corner1;
        private LocationModel corner2;
    }

}
