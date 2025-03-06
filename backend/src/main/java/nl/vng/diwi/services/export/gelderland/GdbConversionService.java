package nl.vng.diwi.services.export.gelderland;

import lombok.extern.log4j.Log4j2;
import java.io.File;
import java.io.IOException;

@Log4j2
public class GdbConversionService {

    private static final String WORKING_DIR = "gdb_download_working_dir";
    private static final String GDB_NAME = "output.gdb";

    /**
     * Convert GeoJSON to GDB and delete the GeoJSON file
     */
    public static void processGeoJsonToGdb() {
        File folder = new File(WORKING_DIR);
        File[] geoJsonFiles = folder.listFiles((dir, name) -> name.endsWith(".geojson"));

        if (geoJsonFiles == null || geoJsonFiles.length == 0) {
            log.warn("No GeoJSON file found in the directory.");
            return;
        }

        File geoJsonFile = geoJsonFiles[0];
        log.info("Processing GeoJSON file: {}", geoJsonFile.getName());

        // Run ogr2ogr to convert GeoJSON to GDB
        String command = String.format("ogr2ogr -f OpenFileGDB %s %s", GDB_NAME, geoJsonFile.getAbsolutePath());
        executeCommand(command);
    }

    /**
     * Convert CSV to GDB and delete the CSV file
     */
    public static void processCsvToGdb() {
        File folder = new File(WORKING_DIR);
        File[] csvFiles = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            log.warn("No CSV file found in the directory.");
            return;
        }

        File csvFile = csvFiles[0];
        log.info("Adding CSV file to GDB: {}", csvFile.getName());

        // Run ogr2ogr to add CSV to GDB
        String command = String.format("ogr2ogr -nln DetailPlanning -f OpenFileGDB %s %s -update", GDB_NAME, csvFile.getAbsolutePath());
        executeCommand(command);

        deleteFile(csvFile);
    }

    private static void executeCommand(String command) {
        try {
            log.info("Executing command: {}", command);
            Process process = new ProcessBuilder("bash", "-c", command).inheritIO().start();
            process.waitFor();
            log.info("Command executed successfully.");
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command: {}", command, e);
        }
    }

    private static void deleteFile(File file) {
        if (file.delete()) {
            log.info("Deleted file: {}", file.getName());
        } else {
            log.error("Failed to delete file: {}", file.getName());
        }
    }
}
