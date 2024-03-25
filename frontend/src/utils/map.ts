import { Extent } from "ol/extent";
import { MapBounds } from "../api/configServices";

export function mapBoundsToExtent(mapBounds: MapBounds) {
    // make sure to swap min and max if they are provided in the wrong order!
    const minx = mapBounds.corner1.lng < mapBounds.corner2.lng ? mapBounds.corner1.lng : mapBounds.corner2.lng;
    const miny = mapBounds.corner1.lat < mapBounds.corner2.lat ? mapBounds.corner1.lat : mapBounds.corner2.lat;
    const maxx = mapBounds.corner1.lng > mapBounds.corner2.lng ? mapBounds.corner1.lng : mapBounds.corner2.lng;
    const maxy = mapBounds.corner1.lat > mapBounds.corner2.lat ? mapBounds.corner1.lat : mapBounds.corner2.lat;
    const extent = [minx, miny, maxx, maxy];
    return extent;
}

export function extentToCenter(extent: Extent): [number, number] {
    return [extent[0] + (extent[2] - extent[0]) / 2, extent[1] + (extent[3] - extent[1]) / 2];
}
