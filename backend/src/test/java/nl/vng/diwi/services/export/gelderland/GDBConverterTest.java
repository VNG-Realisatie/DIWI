package nl.vng.diwi.services.export.gelderland;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class GDBConverterTest {

    @Test
    @SneakyThrows
    public void testGDBConverter() {

            GdbConversionService.processGeoJsonToGdb();

    }
}
