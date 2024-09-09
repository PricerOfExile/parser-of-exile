package poeai.publicstash;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import poeai.publicstash.model.PricedItem;
import poeai.publicstash.model.League;

import java.util.stream.Stream;

@Service
public class IdentifiedNonUniqueAccessoryPricedItemStreamGenerator {

    @Nonnull
    private final PublicStashesTransformerConfig config;
    @Nonnull
    private final PublicStashStreamGenerator publicStashStreamGenerator;

    public IdentifiedNonUniqueAccessoryPricedItemStreamGenerator(@Nonnull PublicStashesTransformerConfig config,
                                                                 @Nonnull PublicStashStreamGenerator publicStashStreamGenerator) {
        this.config = config;
        this.publicStashStreamGenerator = publicStashStreamGenerator;
    }

    public Stream<PricedItem> generateForLeague(League league) {
        return publicStashStreamGenerator.generateForLeague(league)
                .flatMap(publicStash -> publicStash.items().stream())
                .filter(PricedItem::isAccessories)
                .filter(PricedItem::isNotTrinket)
                .filter(PricedItem::isNotUnique)
                .filter(PricedItem::identified)
                .filter(PricedItem::hasPrice)
                .limit(config.getItemReadLimit());
    }
}
