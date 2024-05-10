import { registerTransformations } from "../utils/gis";
import proj4 from "proj4";
import fs from "fs";

// register(proj4);
describe("GIS Utils", () => {
    beforeAll(() => {
        // Register the transformations before running the tests
        registerTransformations();
    });

    it("should correctly transform coordinates using Z001_ETRS89andRDNAP.txt", () => {
        // This file van be found here: https://www.nsgi.nl/geodetische-infrastructuur/producten/programma-rdnaptrans/zelfvalidatie
        const inputFile = fs.readFileSync("test_data/Z001_ETRS89andRDNAP.txt", "utf-8");
        const lines = inputFile.split("\n");

        lines.forEach((line) => {
            if (line.trim() === "") {
                return;
            }
            const cells = line.split("\t");
            const etrsCoords = [cells[2], cells[1]].map(parseFloat);
            const rdCoords = [cells[4], cells[5]].map(parseFloat);

            // Compare to the reference file
            const rdToEtrs89Coords = proj4("EPSG:28992", "EPSG:4258", rdCoords);
            const etrs89ToRdCoords = proj4("EPSG:4258", "EPSG:28992", etrsCoords);

            expect(etrs89ToRdCoords[0]).toBeCloseTo(rdCoords[0], 0);
            expect(etrs89ToRdCoords[1]).toBeCloseTo(rdCoords[1], 0);

            expect(rdToEtrs89Coords[0]).toBeCloseTo(etrsCoords[0], 5);
            expect(rdToEtrs89Coords[1]).toBeCloseTo(etrsCoords[1], 5);

            // Also compare to the EPSG:3857 projection we use in the map
            const rdTo3857 = proj4("EPSG:28992", "EPSG:3857", rdCoords);
            const etrs89To3857 = proj4("EPSG:4258", "EPSG:3857", etrsCoords);

            expect(rdTo3857[0]).toBeCloseTo(etrs89To3857[0], 0);
            expect(rdTo3857[1]).toBeCloseTo(etrs89To3857[1], 0);
        });
    });
});
