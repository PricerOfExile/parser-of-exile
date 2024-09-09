package poeai.publicstash.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.List;

public record PublicStash(String id,
                          StashType stashType,
                          League league,
                          List<PricedItem> items,
                          String stash) {

    @JsonCreator
    public PublicStash(@JsonProperty("id") String id,
                       @JsonProperty("stashType") String stashType,
                       @JsonProperty("league") String league,
                       @JsonProperty("items") List<Item> items,
                       @JsonProperty("stash") String note) {
        this(
                id,
                StashType.fromLabel(stashType),
                League.fromLabel(league),
                extractPricedItems(items, Price.of(note).orElse(null)),
                note
        );
    }

    @Nonnull
    private static List<PricedItem> extractPricedItems(@Nonnull List<Item> items, @Nullable Price defaultPrice) {
        return items.stream()
                .map(item -> new PricedItem(item, Price.of(item.note()).orElse(defaultPrice)))
                .toList();
    }

    public boolean canContainEquipment() {
        return stashType.canContainEquipment();
    }

    public boolean isOnLeague(@Nonnull League league) {
        return this.league == league;
    }
}
