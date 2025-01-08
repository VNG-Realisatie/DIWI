package nl.vng.diwi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Creates a deep copy by converting to JSON and back. Can be used to convert to similar types.
     */
    public static <T> T jsonCopy(Object original, Class<T> targetType) throws JsonProcessingException, JsonMappingException {
        return MAPPER
                .readValue(MAPPER.writeValueAsString(original), targetType);
    }


}
