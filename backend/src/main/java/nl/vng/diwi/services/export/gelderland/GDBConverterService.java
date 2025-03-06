package nl.vng.diwi.services.export.gelderland;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service that watches a folder for new GeoJSON and CSV files and converts them to a File Geodatabase.
 *  Key points:
 * 	1.	Listens for new .geojson or .csv files in "gdb_download_working_dir".
 * 	2.	Executes ogr2ogr commands to convert the files into .gdb.
 * 	3.	Deletes processed files immediately.
 * 	4.	Runs continuously.
 */

@Log4j2
public class GDBConverterService {
    private final Path folderPath;
    private final String gdbName;
    private final ExecutorService executor;
    private WatchService watchService;
    private volatile boolean running = false;

    public GDBConverterService(String watchDir, String gdbName) {
        this.folderPath = Paths.get(watchDir);
        this.gdbName = gdbName;
        this.executor = Executors.newFixedThreadPool(2); // Parallel execution
    }

    public void startWatching() throws IOException {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            this.watchService = watchService;
            folderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            running = true;
            log.info("Watching folder: {}", folderPath);

            executor.submit(this::processEvents);
        }
    }

    public void stopWatching() {
        running = false;
        executor.shutdown();
        try {
            if (watchService != null) watchService.close();
        } catch (IOException e) {
            log.error("Error closing WatchService", e);
        }
        log.info("Stopped watching folder.");
    }


    private void processEvents() {
        while (running) {
            try {
                WatchKey key = watchService.take(); // Wait for new files
                List<WatchEvent<?>> events = key.pollEvents();

                // Sort events to process .geojson files before .csv files
                events.sort((e1, e2) -> {
                    Path path1 = folderPath.resolve((Path) e1.context());
                    Path path2 = folderPath.resolve((Path) e2.context());
                    String fileName1 = path1.getFileName().toString();
                    String fileName2 = path2.getFileName().toString();
                    if (fileName1.endsWith(".geojson") && fileName2.endsWith(".csv")) {
                        return -1;
                    } else if (fileName1.endsWith(".csv") && fileName2.endsWith(".geojson")) {
                        return 1;
                    }
                    return 0;
                });

                for (WatchEvent<?> event : events) {
                    WatchEvent.Kind<?> eventType = event.kind();
                    Path filePath = folderPath.resolve((Path) event.context());

                    if (eventType == StandardWatchEventKinds.ENTRY_CREATE) {
                        executor.submit(() -> processFile(filePath)); // Process file async
                    }
                }
                key.reset();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Error processing events", e);
            }
        }
    }

    private void processFile(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String fileType = fileName.endsWith(".geojson") ? "GeoJSON" :
            fileName.endsWith(".csv") ? "CSV" : null;

        if (fileType == null) {
            log.warn("Unsupported file type: {}", fileName);
            return; // Ignore unsupported files
        }

        String ogrCommand = fileType.equals("GeoJSON")
            ? String.format("ogr2ogr -f OpenFileGDB %s %s", gdbName, filePath)
            : String.format("ogr2ogr -nln DetailPlanning -f OpenFileGDB %s %s -update", gdbName, filePath);

        log.info("Executing: {}", ogrCommand);
        executeCommand(ogrCommand);

        // Delete processed file
        try {
            Files.delete(filePath);
            log.info("Deleted: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete: {}", filePath, e);
        }
    }

    private void executeCommand(String command) {
        try {
            new ProcessBuilder("bash", "-c", command).inheritIO().start().waitFor();
        } catch (Exception e) {
            log.error("Failed to execute command: {}", command, e);
        }
    }

}
