package poe.model;

import lombok.Builder;

// TODO - MTI - Could we get rid of this Lombok annotation ?
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
