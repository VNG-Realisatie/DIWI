import { register } from "ol/proj/proj4.js";
import proj4 from "proj4";

export const registerTransformations = () => {
    proj4.defs(
        // Source https://gis.stackexchange.com/questions/337848/display-esri-mapservice-wmts-with-epsg28992-coordinate-system-in-openlayers-w
        "EPSG:28992",
        "+proj=sterea +lat_0=52.15616055555555 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=565.417,50.3319,465.552,-0.398957,0.343988,-1.8774,4.0725 +units=m +no_defs",
    );

    proj4.defs(
        // Source https://epsg.io/4258.js
        "EPSG:4258", // ETRS89
        "+proj=longlat +ellps=GRS80 +no_defs +type=crs",
    );

    register(proj4);
};
