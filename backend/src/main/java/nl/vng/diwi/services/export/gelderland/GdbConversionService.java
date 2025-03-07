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
     * Converts the given GeoJSON and CSV files to a GDB (Geodatabase) format and creates a ZIP file containing the GDB.
     * The method performs the following steps:
     * 1. Validates the existence of the input files.
     * 2. Converts the GeoJSON file to a GDB format.
     * 3. Adds the CSV file as a layer in the same GDB.
     * 4. Creates a ZIP file from the GDB folder.
     * 5. Cleans up the input files and temporary files.
     *
     * @param geojsonFile the GeoJSON file to be converted
     * @param csvFile the CSV file to be added as a layer in the GDB
     * @return the created ZIP file containing the GDB
     * @throws VngServerErrorException if any error occurs during the conversion process
     */
    public static File convertToGdb(File geojsonFile, File csvFile) {
        validateFile(geojsonFile, "GeoJSON");
        validateFile(csvFile, "CSV");

        File gdbFile = new File(geojsonFile.getParent(), GDB_NAME);
        File zipFile = new File(geojsonFile.getParent(), ZIP_NAME);

        String geojsonCommand = String.format("ogr2ogr -f OpenFileGDB %s %s", gdbFile.getAbsolutePath(), geojsonFile.getAbsolutePath());
        executeCommand(geojsonCommand, geojsonFile.getParentFile());

        String csvCommand = String.format("ogr2ogr -nln DetailPlanning -f OpenFileGDB %s %s -update", gdbFile.getAbsolutePath(), csvFile.getAbsolutePath());
        executeCommand(csvCommand, csvFile.getParentFile());

        deleteFile(WORKING_DIR, ".zip");
        createZip();

        // Cleanup
        deleteFile(geojsonFile);
        deleteFile(csvFile);

        log.info("Conversion to GDB and ZIP completed successfully. ZIP file at: {}", zipFile.getAbsolutePath());
        return zipFile;
    }

    private static void validateFile(File file, String fileType) {
        if (file == null || !file.exists()) {
            throw new VngServerErrorException(fileType + " file does not exist: " + file);
        }
    }

    private static void createZip() {
        File gdbDirectory = new File(WORKING_DIR + "/" + GDB_NAME);
        if (!gdbDirectory.exists() || !gdbDirectory.isDirectory()) {
            log.error("Could not create zip file: " + gdbDirectory.getAbsolutePath());
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

    private static void deleteFile(File file) {
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
            log.error("Error deleting folder: {}", folder.getName(), e);
            throw new VngServerErrorException("Error walking through folder: " + folder.getName(), e);
        }
    }

    private static void deleteFile(String folderPath, String extension) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new VngServerErrorException("The specified path does not exist or is not a directory.");
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            log.warn("The folder is empty : {}", folder.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(extension)) {
                if (file.delete()) {
                    log.info("Deleted file: {}", file.getName());
                } else {
                    log.error("Failed to delete file: {}", file.getName());
                    throw new VngServerErrorException("Failed to delete file: " + file.getName());
                }
            }
        }
    }

}
