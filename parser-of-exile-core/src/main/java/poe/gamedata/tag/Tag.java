package poe.gamedata.tag;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Tag(@JsonProperty("_index") int index,
                  @JsonProperty("Id") String id) {

    // NOTE: These Tags could be linked to everything
    public static final List<String> GENERIC_IDS = List.of("default", "focus");

    // NOTE: Keywords we'll find in Tag's Id related to Accessories
    public static final List<String> ACCESSORY_KEYWORDS = List.of("amulet", "ring", "belt");

    public boolean isGenericTag() {
        return GENERIC_IDS.contains(id);
    }

    public boolean isAccessoryTag() {
        return ACCESSORY_KEYWORDS.stream().anyMatch(id::contains);
    }
}
