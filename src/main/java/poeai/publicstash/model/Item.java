package poeai.publicstash.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record Item(String id,
                   String baseType,
                   String rarity,
                   int ilvl,
                   boolean identified,
                   ExtendedData extended,
                   String note,
                   List<String> implicitMods,
                   List<String> explicitMods,
                   List<ItemProperty> requirements,
                   List<ItemProperty> properties,
                   List<String> enchantMods,
                   List<String> craftedMods,
                   List<String> fracturedMods,
                   List<String> veiledMods,
                   List<ItemSocket> sockets,
                   boolean fractured,
                   boolean synthesised,
                   boolean duplicated,
                   boolean split,
                   boolean corrupted,
                   Influences influences,
                   Qualities qualities) {

    @JsonCreator
    public Item(String id,
                String baseType,
                String rarity,
                int ilvl,
                boolean identified,
                ExtendedData extended,
                String note,
                List<String> implicitMods,
                List<String> explicitMods,
                List<ItemProperty> requirements,
                List<ItemProperty> properties,
                List<String> enchantMods,
                List<String> craftedMods,
                List<String> fracturedMods,
                List<String> veiledMods,
                List<ItemSocket> sockets,
                boolean fractured,
                boolean synthesised,
                boolean duplicated,
                boolean split,
                boolean corrupted,
                Influences influences) {
        this(
                id,
                baseType,
                rarity,
                ilvl,
                identified,
                extended,
                note,
                implicitMods,
                explicitMods,
                requirements,
                properties,
                enchantMods,
                craftedMods,
                fracturedMods,
                veiledMods,
                sockets,
                fractured,
                synthesised,
                duplicated,
                split,
                corrupted,
                influences,
                Qualities.from(properties)
        );
    }

    public boolean isAccessories() {
        return extended.category().equals("accessories");
    }

    public boolean isNotTrinket() {
        return !extended.hasSubCategory("trinket");
    }

    public boolean isNotUnique() {
        return !rarity.equals("Unique");
    }

    public List<String> mods() {
        return Stream.of(implicitMods, explicitMods, enchantMods, craftedMods, fracturedMods, veiledMods)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }

    public boolean hasNoteStartingWithTilde() {
        return note != null && note.startsWith("~");
    }

    public boolean hasEnchantMods() {
        return enchantMods != null && !enchantMods.isEmpty();
    }

    public boolean hasInfluences() {
        return influences != null && influences.hasAny();
    }

    public boolean hasSockets() {
        return sockets != null && !sockets.isEmpty();
    }

    public boolean hasFracturedOrVeiledMods() {
        return (fracturedMods != null && !fracturedMods.isEmpty())
                || (veiledMods != null && !veiledMods.isEmpty());
    }
}
