package nl.vng.diwi.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ConfigModel {

    private MapBounds defaultMapBounds;

    @Data
    @AllArgsConstructor
    public static class MapBounds {
        private LocationModel corner1;
        private LocationModel corner2;
    }

}
