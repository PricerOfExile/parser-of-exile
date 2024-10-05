package poe.publicstash;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import poe.publicstash.model.PricedItem;

import java.util.function.Consumer;
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

    public void execute(ItemFilters filters,
                        Consumer<Stream<PricedItem>> itemStreamConsumer) {
        publicStashStreamGenerator.execute(filters, publicStashStream -> {
            var pricedItemStream = publicStashStream.flatMap(publicStash -> publicStash.items().stream())
                    .filter(PricedItem::isAccessories)
                    .filter(PricedItem::isNotTrinket)
                    .filter(PricedItem::isNotUnique)
                    .filter(PricedItem::identified)
                    .filter(PricedItem::hasPrice)
                    .limit(filters.itemLimit());
            itemStreamConsumer.accept(pricedItemStream);
        });
    }
}
