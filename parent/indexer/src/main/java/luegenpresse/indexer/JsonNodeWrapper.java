package luegenpresse.indexer;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Wraps a JSON node such that it will return Optionals instead of nulls.
 */
public class JsonNodeWrapper {
	private JsonNode node;

	public JsonNodeWrapper(JsonNode node) {
		this.node = node;
	}
	
	public Optional<JsonNode> get(String fieldName) {
		return Optional.ofNullable(node.get(fieldName));
	}
}
