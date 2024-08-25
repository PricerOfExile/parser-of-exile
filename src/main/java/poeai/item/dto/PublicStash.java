package poeai.item.dto;

import poeai.item.EnrichedItem;
import poeai.item.Price;

import java.util.List;
import java.util.stream.Stream;

public record PublicStash(String id,
                          String stashType,
                          String league,
                          List<Item> items,
                          String stash) {

    private static final List<String> CONTAINS_EQUIPMENT_STASH_TYPES = List.of(
            "PremiumStash",
            "QuadStash"
    );

    public boolean canContainEquipment() {
        return CONTAINS_EQUIPMENT_STASH_TYPES.contains(stashType);
    }

    public boolean isNecropolisLeague() {
        return "Necropolis".equals(league);
    }

    public Stream<EnrichedItem> itemStream() {
        return items.stream()
                .map(item -> {
                    var price = Price.of(item.note())
                            .or(() -> Price.of(stash))
                            .orElse(null);
                    return new EnrichedItem(
                            id,
                            item,
                            price
                    );
                });
    }
}
