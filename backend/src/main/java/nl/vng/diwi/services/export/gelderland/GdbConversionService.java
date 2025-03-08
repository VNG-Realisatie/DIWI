package nl.vng.diwi.services.export.gelderland;

import lombok.extern.log4j.Log4j2;
import nl.vng.diwi.rest.VngServerErrorException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Log4j2
public class GdbConversionService {

    private static final Map<String, String> environment = System.getenv();
    private static final String WORKING_DIR = environment.getOrDefault("GDB_DOWNLOAD_WORKING_DIR", "gdb_download_working_dir");
    private static final String GDB_NAME = environment.getOrDefault("GDB_FOLDER_NAME", "outputTest.gdb");
    private static final String ZIP_NAME = environment.getOrDefault("ZIP_FOLDER_NAME", "outputTest.zip");
    private static final String ZIP_EXTENSION = ".zip";
    private static final int THREAD_COUNT = 4;
    private static final int MAX_RETRIES = 3;

    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    /**
     * Converts GeoJSON and CSV files to a GDB (Geodatabase) format and creates a ZIP archive. This method validates the input files, runs the conversion
     * commands in parallel, deletes old ZIP files, creates a new ZIP archive, and cleans up the input files.
     *
     * @param geojsonFile the GeoJSON file to be converted
     * @param csvFile     the CSV file to be converted
     * @return the created ZIP file
     * @throws VngServerErrorException if any error occurs during the conversion or ZIP creation
     */
    public static File convertToGdb(File geojsonFile, File csvFile) {
        validateFile(geojsonFile, "GeoJSON");
        validateFile(csvFile, "CSV");

        String workingDir = geojsonFile.getParent();
        File gdbDir = new File(workingDir, GDB_NAME);

        executeCommand(String.format("ogr2ogr -nln Planregistratie -f OpenFileGDB %s %s", gdbDir.getAbsolutePath(), geojsonFile.getAbsolutePath()), geojsonFile.getParentFile());
        executeCommand(String.format("ogr2ogr -nln DetailPlanning -f OpenFileGDB %s %s -update", gdbDir.getAbsolutePath(), csvFile.getAbsolutePath()),
                csvFile.getParentFile());
        // Ensure GDB exists before proceeding
        if (!gdbDir.exists() || !gdbDir.isDirectory()) {
            throw new VngServerErrorException("Failed to create the Geodatabase (GDB) file.");
        }

        var zipFile = createZip(gdbDir);

        deleteFile(geojsonFile);
        deleteFile(csvFile);

        return zipFile;
    }

    private static void validateFile(File file, String fileType) {
        if (file == null || !file.exists()) {
            throw new VngServerErrorException(fileType + " file does not exist: " + file);
        }
    }

    private static File createZip(File gdbDir) {
        var gdbDirPath = gdbDir.toPath();
        var zipFile = new File(gdbDir.getAbsolutePath() + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
                ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fos);
                Stream<Path> fileStream = Files.walk(gdbDirPath).parallel()) {

            List<Path> fileList = fileStream
                    .filter(Files::isRegularFile)
                    .toList();

            for (Path filePath : fileList) {
                String zipEntryName = gdbDirPath.relativize(filePath).toString();
                ZipArchiveEntry entry = new ZipArchiveEntry(filePath.toFile(), zipEntryName);
                zos.putArchiveEntry(entry);
                Files.copy(filePath, zos);
                zos.closeArchiveEntry();
            }

            log.info("Successfully created ZIP file: {}", zipFile);
            deleteFolder(gdbDir);

        } catch (IOException e) {
            throw new VngServerErrorException("Error creating ZIP file: " + zipFile, e);
        }

        return zipFile;
    }

    private static void deleteFile(File file) {
        int retryCount = 0;
        boolean deleted = false;

        while (retryCount < MAX_RETRIES && !deleted) {
            if (file.exists() && file.delete()) {
                log.info("Deleted file: {}", file.getName());
                deleted = true;
            } else {
                retryCount++;
                log.warn("Failed to delete file: {}. Attempt {}/{}", file.getName(), retryCount, MAX_RETRIES);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Thread interrupted while waiting to retry file deletion", e);
                    break;
                }
            }
        }

        if (!deleted) {
            log.error("Failed to delete file after {} attempts: {}", MAX_RETRIES, file.getName());
            throw new VngServerErrorException("Failed to delete file: " + file.getName());
        }
    }

    private static void executeCommand(String command, File workingDir) {
        try {
            log.info("Executing command: {}", command);
            Process process = new ProcessBuilder("bash", "-c", command)
                    .directory(workingDir)
                    .inheritIO()
                    .start();
            process.waitFor();
            log.info("Command executed successfully.");
        } catch (IOException | InterruptedException e) {
            throw new VngServerErrorException("Error executing command: " + command, e);
        }
    }

    private static void deleteFolder(File folder) {
        try (Stream<Path> walk = Files.walk(folder.toPath())) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(GdbConversionService::deleteFile);
        } catch (IOException e) {
            throw new VngServerErrorException("Error deleting folder: " + folder.getName(), e);
        }
    }

    private static void deleteFile(String extension) {
        File folder = new File(WORKING_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new VngServerErrorException("The specified path does not exist or is not a directory.");
        }

        try (Stream<Path> fileStream = Files.list(folder.toPath())) {
            fileStream.parallel()
                    .filter(path -> path.toString().endsWith(extension))
                    .map(Path::toFile)
                    .forEach(GdbConversionService::deleteFile);
        } catch (IOException e) {
            throw new VngServerErrorException("Error deleting files in folder: " + WORKING_DIR, e);
        }
    }

    private static void shutdownExecutor() {
        log.info("Shutting down executor service...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate in time, forcing shutdown...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Executor shutdown interrupted", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
