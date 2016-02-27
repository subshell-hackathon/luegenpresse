package luegenpresse.indexer;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Wraps a JSON node such that it will return Optionals instead of nulls.
 */
public class JsonNodeWrapper {
	private JsonNode node;

	public JsonNodeWrapper(JsonNode node) {
		if (node == null) {
			throw new IllegalArgumentException("node must not be null.");
		}
		this.node = node;
	}
	
	public Optional<JsonNodeWrapper> get(String fieldName) {
		JsonNode jsonNode = node.get(fieldName);
		if (jsonNode != null) {
			return Optional.of(new JsonNodeWrapper(jsonNode));
		} else {
			return Optional.empty();
		}
	}
	
	public Optional<JsonNodeWrapper> get(int childIndex) {
		JsonNode jsonNode = node.get(childIndex);
		if (jsonNode != null) {
			return Optional.of(new JsonNodeWrapper(jsonNode));
		} else {
			return Optional.empty();
		}
	}
	
	public JsonNode getJsonNode() {
		return node;
	}
}
