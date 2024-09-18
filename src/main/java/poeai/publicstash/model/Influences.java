package poeai.publicstash.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record Influences(boolean warlord,
                         boolean elder,
                         boolean shaper,
                         boolean crusader,
                         boolean redeemer,
                         boolean hunter) {

    public boolean hasAny() {
        return warlord || elder || shaper || crusader || redeemer || hunter;
    }
}
