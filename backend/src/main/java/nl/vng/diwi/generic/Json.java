package nl.vng.diwi.generic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {
    public static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Creates a deep copy by converting to JSON and back. Can be used to convert to similar types.
	 */
	public static <T> T jsonCopy(Object original, Class<T> targetType) throws JsonProcessingException, JsonMappingException {
	    return mapper
	            .readValue(mapper.writeValueAsString(original), targetType);
	}

}
