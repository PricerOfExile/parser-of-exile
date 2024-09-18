package poeai.publicstash.model;

import lombok.Builder;
import poeai.gamedata.statdescription.ValuatedStat;

import java.util.List;
import java.util.Optional;

@Builder(toBuilder = true)
public record DumpedItem(String id,
                         String rarity,
                         int ilvl,
                         boolean identified,
                         double priceInChaos,
                         int levelRequirement,
                         String socket,
                         Influences influences,
                         boolean fractured,
                         boolean synthesised,
                         boolean duplicated,
                         boolean split,
                         boolean corrupted,
                         Qualities qualities,
                         List<ValuatedStat> valuatedStats) {

    public DumpedItem(PricedItem item,
                      double priceInChaos,
                      List<ValuatedStat> valuatedStats) {
        this(
                item.id(),
                item.item().rarity(),
                item.item().ilvl(),
                item.identified(),
                priceInChaos,
                item.item().levelRequirement(),
                Optional.ofNullable(item.item().sockets()).stream()
                        .flatMap(List::stream)
                        .findFirst()
                        .map(ItemSocket::sColour)
                        .orElse("N"),
                Optional.ofNullable(item.item().influences())
                        .orElseGet(() -> new Influences(false, false, false, false, false, false)),
                item.item().fractured(),
                item.item().synthesised(),
                item.item().duplicated(),
                item.item().split(),
                item.item().corrupted(),
                new Qualities(
                        0, 20, 0, 0, 0, 0, 0, 0, 0, 0
                ),
                valuatedStats
        );
    }
}
