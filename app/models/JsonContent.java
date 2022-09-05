package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.twirl.api.Content;

import java.io.IOException;

public class JsonContent implements Content {

	private final JsonNode json;

	public JsonContent(final String content) {
		super();
		final ObjectMapper mapper = new ObjectMapper();
		this.json = mapper.createObjectNode();
		((ObjectNode) this.json).put("content", content);
	}

	@Override
	public String body() {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this.json);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String contentType() {
		String contentType = "application/json";
		return contentType;
	}
}
