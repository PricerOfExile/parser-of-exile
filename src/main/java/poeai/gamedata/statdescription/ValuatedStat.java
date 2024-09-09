package poeai.gamedata.statdescription;

import jakarta.annotation.Nonnull;

import java.util.Objects;

public record ValuatedStat(@Nonnull String id,
                           double value) {
    public ValuatedStat {
        Objects.requireNonNull(id, "Valuated Stat id is mandatory");
    }
}
