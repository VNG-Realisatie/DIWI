package nl.vng.diwi.generic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Json {
    public static final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    /**
     * Creates a deep copy by converting to JSON and back. Can be used to convert to similar types.
     */
    public static <T> T jsonCopy(Object original, Class<T> targetType) throws JsonProcessingException, JsonMappingException {
        return mapper
                .readValue(mapper.writeValueAsString(original), targetType);
    }

}
