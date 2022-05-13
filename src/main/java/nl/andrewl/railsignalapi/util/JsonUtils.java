package nl.andrewl.railsignalapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static String toJson(Object o) throws JsonProcessingException {
		return mapper.writeValueAsString(o);
	}

	public static void writeJsonString(DataOutputStream out, Object o) throws IOException {
		byte[] data = toJson(o).getBytes(StandardCharsets.UTF_8);
		if (data.length > Short.MAX_VALUE) throw new IOException("Data is too large!");
		out.writeShort(data.length);
		out.write(data);
	}

	public static <T> T readMessage(DataInputStream in, Class<T> type) throws IOException {
		short len = in.readShort();
		byte[] data = in.readNBytes(len);
		return mapper.readValue(data, type);
	}

	public static <T> T readMessage(String in, Class<T> type) throws IOException {
		return mapper.readValue(in, type);
	}
}
