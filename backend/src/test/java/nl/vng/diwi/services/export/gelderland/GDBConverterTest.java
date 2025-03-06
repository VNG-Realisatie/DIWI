package nl.vng.diwi.services.export.gelderland;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class GDBConverterTest {

    @Test
    @SneakyThrows
    public void testGDBConverter() {
       //before running please add 1 valid  .geojson file and one valid .csv file in the gdb_download_working_dir folder
        //this is a temoprary solution, as these test files should be generated here
        GdbConversionService.processGeoJsonToGdb();
        GdbConversionService.processCsvToGdb();
        GdbConversionService.createZip();

    }

}
