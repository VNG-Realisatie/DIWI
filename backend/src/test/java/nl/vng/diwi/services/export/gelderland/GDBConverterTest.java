package nl.vng.diwi.services.export.gelderland;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GDBConverterTest {

    @Test
    @SneakyThrows
    public void testGDBConverter() {
        //for this test to work you need to go to : Modify Run Configuration -> Environment -> Add -> GDB_DOWNLOAD_WORKING_DIR -> src/test/resources/GdbExportFiles/temp

        // Create temporary directory inside GdbExportFilesTest
        Path baseDirPath = Paths.get("src/test/resources/GdbExportFiles").toAbsolutePath();
        Path tempDirPath = baseDirPath.resolve("temp");
        Files.createDirectories(tempDirPath);

        // Define the source paths
        Path geojsonPath = baseDirPath.resolve("test.geojson");
        Path csvPath = baseDirPath.resolve("testLayer.csv");

        System.out.println("GeoJSON exists: " + Files.exists(geojsonPath));
        System.out.println("CSV exists: " + Files.exists(csvPath));

        // Define the destination paths
        Path geojsonDestPath = tempDirPath.resolve("test.geojson");
        Path csvDestPath = tempDirPath.resolve("testLayer.csv");

        // Copy the files to the temporary directory
        Files.copy(geojsonPath, geojsonDestPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(csvPath, csvDestPath, StandardCopyOption.REPLACE_EXISTING);

        // Use the copied files for testing
        File geojson = geojsonDestPath.toFile();
        File csv = csvDestPath.toFile();

        File zipFile = GdbConversionService.convertToGdb(geojson, csv);

        // Verify the output
        assertTrue(zipFile.exists(), "The ZIP file should exist");
        assertTrue(zipFile.getName().endsWith(".zip"), "The output should be a ZIP file");

        // Delete the /temp folder
        Files.walk(tempDirPath)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
}
