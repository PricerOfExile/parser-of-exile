package poeai.item;

import poeai.item.dto.Influences;
import poeai.item.dto.ItemSocket;
import poeai.stat.files.Stat;

import java.util.List;
import java.util.Optional;

public record DumpedItem(String id,
                         String rarity,
                         int ilvl,
                         boolean identified,
                         String priceInChaos,
                         int levelRequirement,
                         String socket,
                         Influences influences,
                         boolean fractured,
                         boolean synthesised,
                         boolean duplicated,
                         boolean split,
                         boolean corrupted,
                         Qualities qualities,
                         List<String> mods,
                         List<Stat> stats) {

    public DumpedItem(EnrichedItem item,
                      List<Stat> stats) {
        this(
                item.id(),
                item.item().rarity(),
                item.item().ilvl(),
                item.identified(),
                item.price().quantity(),
                68,
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
                        20, 0, 0,0,0,0,0,0,0,0
                ),
                item.item().explicitMods(),
                stats
        );
    }
}
