package poeai.stat.tables.English;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StatDto(@JsonProperty("_index") int index,
                      @JsonProperty("Id") String id) {
}
