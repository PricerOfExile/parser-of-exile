package poe.publicstash;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import poe.currency.CurrencyRepository;
import poe.gamedata.statdescription.AccessoryStatCatalog;
import poe.gamedata.statdescription.AccessoryStatValuator;
import poe.gamedata.statdescription.ValuatedStat;
import poe.model.ModelItem;
import poe.model.Influences;
import poe.publicstash.model.ItemProperty;
import poe.publicstash.model.ItemSocket;
import poe.publicstash.model.PricedItem;
import poe.model.Qualities;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ModelItemFactory {

    private static final Logger logger = LoggerFactory.getLogger(ModelItemFactory.class);

    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("+##%");

    @Nonnull
    private final AccessoryStatValuator accessoryStatValuator;
    @Nonnull
    private final CurrencyRepository currencyRepository;
    @Nonnull
    private final List<ValuatedStat> defaultValuatedStats;

    public ModelItemFactory(@Nonnull AccessoryStatValuator accessoryStatValuator,
                            @Nonnull CurrencyRepository currencyRepository,
                            @Nonnull AccessoryStatCatalog accessoryStatCatalog) {
        this.accessoryStatValuator = accessoryStatValuator;
        this.currencyRepository = currencyRepository;
        this.defaultValuatedStats = accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated().stream()
                .map(statDto -> new ValuatedStat(statDto.id(), 0.))
                .toList();
    }

    public ModelItem create(PricedItem item) {
        try {
            var computedStats = item.item().mods().stream()
                    .map(accessoryStatValuator::valuateDisplayedMod)
                    .flatMap(List::stream)
                    .toList();
            var collected = Stream.concat(defaultValuatedStats.stream(), computedStats.stream())
                    .collect(
                            Collectors.collectingAndThen(
                                    Collectors.groupingBy(ValuatedStat::id, Collectors.reducing(0., ValuatedStat::value, Double::sum)),
                                    map -> map.entrySet().stream()
                                            .map(entry -> new ValuatedStat(entry.getKey(), entry.getValue()))
                                            .sorted(Comparator.comparing(ValuatedStat::id))
                                            .toList()
                            )
                    );
            var currencyRate = currencyRepository.findById(item.price().currency()).chaosEquivalent();
            return new ModelItem(
                    item.id(),
                    item.item().rarity(),
                    item.item().ilvl(),
                    item.identified(),
                    currencyRate * Double.parseDouble(item.price().quantity()),
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
                    ModelItemFactory.from(item.item().properties()),
                    collected
            );
        } catch (Exception e) {
            logger.error("Cannot create ModelItem with id({})", item.id(), e);
            return null;
        }
    }

    private static Qualities from(List<ItemProperty> properties) {
        return Optional.ofNullable(properties)
                .orElse(List.of())
                .stream()
                .map(property -> Qualities.QualityType.fromLabel(property.name())
                        .map(type -> {
                            try {
                                var parse = PERCENT_FORMAT.parse((String) property.values().get(0).get(0));
                                return type.buildQualities((int) (100 * parse.doubleValue()));
                            } catch (Exception e) {
                                logger.warn("Cannot properly parse quantity {}", property, e);
                                return null;
                            }
                        })
                )
                .filter(Optional::isPresent)
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(new Qualities(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }
}
