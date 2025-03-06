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
     * Converts GeoJSON files to GDB format and deletes the original GeoJSON files.
     * <p>
     * This method searches for GeoJSON files in the working directory. If a GeoJSON file is found,
     * it uses the `ogr2ogr` command to convert the GeoJSON data to GDB format. After the conversion,
     * the original GeoJSON file is deleted.
     * </p>
     *
     * @throws VngServerErrorException if an error occurs during the conversion process or file deletion.
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
     * Converts a CSV file to a GDB format and deletes the original CSV file.
     * <p>
     * This method searches for CSV files in the working directory. If a CSV file is found,
     * it uses the `ogr2ogr` command to add the CSV data to the GDB. After the conversion,
     * the original CSV file is deleted.
     * </p>
     *
     * @throws VngServerErrorException if an error occurs during the conversion process or file deletion.
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
     * Creates a ZIP file from the contents of the specified directory.
     * <p>
     * This method first ensures that the working directory does not contain any other `.zip` files.
     * It then checks if the specified GDB directory exists and is valid. If valid, it creates a ZIP
     * file containing all files from the GDB directory. Finally, it deletes the original GDB directory.
     * </p>
     *
     * @throws VngServerErrorException if the specified GDB directory does not exist or is not valid,
     *                                 or if an error occurs during the ZIP file creation process.
     */
    public static void createZip() {
        // First we need to make sure that the folder does not contain any other .zip file(s)
        deleteFile(WORKING_DIR, ".zip");
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
                        throw new VngServerErrorException("Error while adding file to zip: " + path, e);
                    }
                });
        } catch (IOException e) {
            log.error("Error creating ZIP file: {}", zipFilePath, e);
            throw new VngServerErrorException("Error creating ZIP file: " + zipFilePath, e);
        }
        log.info("Successfully created ZIP file: {}", zipFilePath);

        deleteFolder(gdbDirectory);
    }


    /**
     * Deletes the specified file.
     * <p>
     * This method attempts to delete the given file. If the deletion is successful,
     * it logs an informational message. If the deletion fails, it logs an error message
     * and throws a `VngServerErrorException`.
     * </p>
     *
     * @param file the file to be deleted
     * @throws RuntimeException if the file deletion fails
     */
    public static void deleteFile(File file) {
        if (file.delete()) {
            log.info("Deleted file: {}", file.getName());
        } else {
            log.error("Failed to delete file: {}", file.getName());
            throw new VngServerErrorException("Failed to delete file: " + file.getName());
        }
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
                            throw new RuntimeException("Failed to delete file: " + file.getName());
                        }
                    } catch (Exception e) {
                        log.error("Error deleting file: {}", file.getName(), e);
                        throw new VngServerErrorException("Error deleting file: " + file.getName(), e);
                    }
                });
            log.info("Deleted folder: {}", folder.getName());
        } catch (IOException e) {
            throw new VngServerErrorException("Error walking through folder: " + folder.getName(), e);
        }
    }

    private static void deleteFile(String folderPath, String zipExtension) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new VngServerErrorException("The specified path does not exist or is not a directory.");
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            log.warn("The folder is empty.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(zipExtension)) {
                if (file.delete()) {
                    log.info("Deleted file: {}", file.getName());
                } else {
                    throw new VngServerErrorException("Failed to delete file: " + file.getName());
                }
            }
        }
    }

}
