package poe.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import poe.gamedata.statdescription.ValuatedStat;

import java.util.List;

// TODO - MTI - Could we get rid of this Lombok annotation ?
@Builder(toBuilder = true)
public record ModelItem(@Nullable String id,
                        @Nullable String rarity,
                        int ilvl,
                        boolean identified,
                        double priceInChaos,
                        int levelRequirement,
                        @Nonnull String socket,
                        @Nonnull Influences influences,
                        boolean fractured,
                        boolean synthesised,
                        boolean duplicated,
                        boolean split,
                        boolean corrupted,
                        @Nonnull Qualities qualities,
                        @Nonnull List<ValuatedStat> valuatedStats) {
}
