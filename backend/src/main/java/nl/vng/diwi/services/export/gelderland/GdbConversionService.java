package nl.vng.diwi.services.export.gelderland;

import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.rest.VngServerErrorException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Log4j2
public class GdbConversionService {

    private static final String WORKING_DIR = System.getenv().getOrDefault("GDB_DOWNLOAD_WORKING_DIR", "gdb_download_working_dir");
    private static final String GDB_NAME = System.getenv().getOrDefault("GDB_FOLDER_NAME", "outputTest.gdb");
    private static final String ZIP_NAME = System.getenv().getOrDefault("ZIP_FOLDER_NAME", "outputTest.zip");

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
        executeCommand(command, geoJsonFile.getParentFile());

        deleteFile(geoJsonFile);
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
        executeCommand(command, csvFile.getParentFile());

        deleteFile(csvFile);
    }

    /**
     * Creates a ZIP file
     *
     * @throws IOException If an I/O error occurs.
     */
    public static void createZip() throws IOException {
        File gdbDirectory = new File(WORKING_DIR + "/" + GDB_NAME);

        if (!gdbDirectory.exists() || !gdbDirectory.isDirectory()) {
            throw new VngServerErrorException("The specified directory does not exist or is not a valid .gdb directory.");
        }

        String zipFilePath = gdbDirectory.getParent() + "/" + ZIP_NAME;

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fos)) {

            Path gdbPath = Paths.get(gdbDirectory.toURI());
            Files.walk(gdbPath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        String zipEntryName = gdbPath.relativize(path).toString();
                        ZipArchiveEntry entry = new ZipArchiveEntry(path.toFile(), zipEntryName);
                        zos.putArchiveEntry(entry);
                        Files.copy(path, zos);

                        zos.closeArchiveEntry();
                    } catch (IOException e) {
                        log.error("Error while adding file to zip: {}", path, e);
                    }
                });
        } catch (IOException e) {
            log.error("Error creating ZIP file: {}", zipFilePath, e);
            throw new VngServerErrorException("Error creating ZIP file: " + zipFilePath, e);
        }
        log.info("Successfully created ZIP file: {}", zipFilePath);

        deleteFolder(gdbDirectory);
    }

    private static void executeCommand(String command, File workingDir) {
        try {
            log.info("Executing command: {}", command);
            Process process = new ProcessBuilder("bash", "-c", "cd " + workingDir.getAbsolutePath() + " && " + command)
                .inheritIO()
                .start();
            process.waitFor();
            log.info("Command executed successfully.");
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command: {}", command, e);
            throw new VngServerErrorException("Error executing command: " + command, e);
        }
    }

    private static void deleteFile(File file) {
        if (file.delete()) {
            log.info("Deleted file: {}", file.getName());
        } else {
            log.error("Failed to delete file: {}", file.getName());
            throw new RuntimeException("Failed to delete file: " + file.getName());
        }
    }

    private static void deleteFolder(File folder) {
        try {
            Files.walk(folder.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(file -> {
                    try {
                        if (file.delete()) {
                            log.info("Deleted file: {}", file.getName());
                        } else {
                            log.error("Failed to delete file: {}", file.getName());
                        }
                    } catch (Exception e) {
                        log.error("Error deleting file: {}", file.getName(), e);
                        throw new VngServerErrorException("Error deleting file: " + file.getName(), e);
                    }
                });
            log.info("Deleted folder: {}", folder.getName());
        } catch (IOException e) {
            log.error("Error walking through folder: {}", folder.getName(), e);
            throw new VngServerErrorException("Error walking through folder: " + folder.getName(), e);
        }
    }

}
