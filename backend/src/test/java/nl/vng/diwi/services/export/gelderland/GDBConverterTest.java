package nl.vng.diwi.services.export.gelderland;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class GDBConverterTest {

    @Test
    @SneakyThrows
    public void testGDBConverter() {
        //IMPORTANT : for this test to work you need to go to : Modify Run Configuration -> Environment -> Add -> GDB_DOWNLOAD_WORKING_DIR=src/test/resources/GdbExportFiles/temp
        Path baseDirPath = Paths.get("src/test/resources/GdbExportFiles").toAbsolutePath();
        Path tempDirPath = baseDirPath.resolve("temp");
        Files.createDirectories(tempDirPath);

        Path geojsonPath = baseDirPath.resolve("test.geojson");
        Path csvPath = baseDirPath.resolve("testLayer.csv");

        log.info("GeoJSON exists: {}", Files.exists(geojsonPath));
        log.info("CSV exists: {}", Files.exists(csvPath));

        Path geojsonDestPath = tempDirPath.resolve("test.geojson");
        Path csvDestPath = tempDirPath.resolve("testLayer.csv");

        Files.copy(geojsonPath, geojsonDestPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(csvPath, csvDestPath, StandardCopyOption.REPLACE_EXISTING);

        File geojson = geojsonDestPath.toFile();
        File csv = csvDestPath.toFile();

        File zipFile = GdbConversionService.convertToGdb(geojson, csv);

        assertTrue(zipFile.exists(), "The ZIP file should exist");
        assertTrue(zipFile.getName().endsWith(".zip"), "The output should be a ZIP file");

        // Delete the /temp folder
        Files.walk(tempDirPath)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
}
