import { register } from "ol/proj/proj4.js";
import proj4 from "proj4";

export const registerTransformations = () => {
    // proj4.defs(
    //     // Source https://epsg.io/28992-4833, has comment on the page about it being incorrect
    //     "EPSG:28992", // RD
    //     "+proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=565.4171,50.3319,465.5524,1.9342,-1.6677,9.1019,4.0725 +units=m +no_defs +type=crs",
    // );
    // proj4.defs(
    //     // Source https//epsg.io/28992-15934
    //     "EPSG:28992", // RD
    //     "+proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=565.2369,50.0087,465.658,1.9725,-1.7004,9.0677,4.0812 +units=m +no_defs +type=crs",
    // );
    proj4.defs(
        // Source https://gis.stackexchange.com/questions/337848/display-esri-mapservice-wmts-with-epsg28992-coordinate-system-in-openlayers-w
        "EPSG:28992",
        "+proj=sterea +lat_0=52.15616055555555 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=565.417,50.3319,465.552,-0.398957,0.343988,-1.8774,4.0725 +units=m +no_defs",
    );

    // proj4.defs(
    //     // Source QGIS, doesn't seem to work
    //     "EPSG:28992",
    //     "+proj=pipeline +step +inv +proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +step +proj=push +v_3 +step +proj=cart +ellps=bessel +step +proj=helmert +x=565.4171 +y=50.3319 +z=465.5524 +rx=0.398957388243134 +ry=-0.343987817378283 +rz=1.87740163998045 +s=4.0725 +convention=coordinate_frame +step +inv +proj=cart +ellps=WGS84 +step +proj=pop +v_3 +step +proj=webmerc +lat_0=0 +lon_0=0 +x_0=0 +y_0=0 +ellps=WGS84",
    // );

    // proj4.defs(
    //     // Source https://spatialreference.org/ref/epsg/28992/proj4.txt
    //     "EPSG:28992", // RD
    //     "+proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +units=m +no_defs +type=crs",
    // );

    // proj4.defs(
    //     //Source https://epsg.io/28992.js
    //     "EPSG:28992",
    //     "+proj=sterea +lat_0=52.1561605555556 +lon_0=5.38763888888889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +towgs84=565.4171,50.3319,465.5524,1.9342,-1.6677,9.1019,4.0725 +units=m +no_defs +type=crs",
    // );

    // proj4.defs(
    //     // Source https://geoforum.nl/t/betrouwbare-bron-voor-proj4-definitie-van-rd-new-epsg-28992/5144/5
    //     "EPSG:28992",
    //     "+proj=sterea +lat_0=52.156160556 +lon_0=5.387638889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +wktext +nadgrids=rdtrans2018.gsb +geoidgrids=naptrans2018.gtx +no_defs",
    // );

    // proj4.defs(
    //     // Source https://geoforum.nl/t/betrouwbare-bron-voor-proj4-definitie-van-rd-new-epsg-28992/5144/5
    //     // Removed the reference to the files
    //     "EPSG:28992",
    //     "+proj=sterea +lat_0=52.156160556 +lon_0=5.387638889 +k=0.9999079 +x_0=155000 +y_0=463000 +ellps=bessel +wktext +no_defs",
    // );

    // proj4.defs(
    //     // Source https://epsg.io/4937
    //     "EPSG:4937", // ETRS89
    //     "+proj=longlat +ellps=GRS80 +no_defs +type=crs",
    // );

    proj4.defs(
        // Source https://epsg.io/4258.js
        "EPSG:4258", // ETRS89
        "+proj=longlat +ellps=GRS80 +no_defs +type=crs",
    );

    register(proj4);
};
