package poe.publicstash;

import jakarta.annotation.Nonnull;
import poe.publicstash.model.League;

import java.util.Objects;

public record ItemFilters(@Nonnull League league,
                          long itemLimit) {
    public ItemFilters {
        Objects.requireNonNull(league);
    }
}
