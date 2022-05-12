package nl.andrewl.railsignalapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String toJson(Object o) throws JsonProcessingException {
		return mapper.writeValueAsString(o);
	}
}
