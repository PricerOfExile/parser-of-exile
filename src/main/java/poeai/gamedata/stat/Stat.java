package poeai.gamedata.stat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Stat(@JsonProperty("_index") int index,
                   @JsonProperty("Id") String id) {
}
