package poeai.publicstash;

import jakarta.annotation.Nonnull;
import poeai.publicstash.model.League;

import java.util.Objects;

public record ItemFilters(@Nonnull League league,
                          long itemLimit) {
    public ItemFilters {
        Objects.requireNonNull(league);
    }
}
