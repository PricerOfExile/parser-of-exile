package poeai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import poeai.currency.CurrencyRepository;
import poeai.gamedata.statdescription.StatCatalog;
import poeai.gamedata.statdescription.StatValuator;
import poeai.gamedata.statdescription.ValuatedStat;
import poeai.publicstash.IdentifiedNonUniqueAccessoryPricedItemStreamGenerator;
import poeai.publicstash.ItemFilters;
import poeai.publicstash.PublicStashesTransformerConfig;
import poeai.publicstash.model.DumpedItem;
import poeai.publicstash.model.League;
import poeai.publicstash.model.PricedItem;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class PoEAIApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PoEAIApplication.class);

    private final IdentifiedNonUniqueAccessoryPricedItemStreamGenerator itemStreamGenerator;

    private final StatValuator statValuator;

    private final List<ValuatedStat> defaultValuatedStats;

    private final ObjectMapper objectMapper;

    private final PublicStashesTransformerConfig config;

    private final CurrencyRepository currencyRepository;

    public PoEAIApplication(IdentifiedNonUniqueAccessoryPricedItemStreamGenerator itemStreamGenerator,
                            StatValuator statValuator,
                            StatCatalog statCatalog,
                            ObjectMapper objectMapper,
                            PublicStashesTransformerConfig config,
                            CurrencyRepository currencyRepository) {
        this.itemStreamGenerator = itemStreamGenerator;
        this.statValuator = statValuator;
        this.objectMapper = objectMapper;
        this.config = config;
        this.currencyRepository = currencyRepository;
        try {
            Files.createDirectories(this.config.getTargetFolder());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var statDtos = statCatalog.findAllNonUniqueAndAccessoryRelated();
        defaultValuatedStats = statDtos.stream()
                .map(statDto -> new ValuatedStat(statDto.id(), 0.))
                .toList();
    }

    public static void main(String[] args) {
        SpringApplication.run(PoEAIApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Running application");
        itemStreamGenerator.execute(new ItemFilters(League.NECROPOLIS, 100), items -> {
            items.map(this::buildDumpedItem)
                    .forEach(this::writeToNewFile);
        });
    }

    private DumpedItem buildDumpedItem(PricedItem pricedItem) {
        var computedStats = pricedItem.item().mods().stream()
                .map(statValuator::valuateDisplayedMod)
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
        var currencyRate = currencyRepository.findById(pricedItem.price().currency()).chaosEquivalent();
        return new DumpedItem(
                pricedItem,
                currencyRate * Double.parseDouble(pricedItem.price().quantity()),
                collected
        );
    }

    private void writeToNewFile(DumpedItem item) {
        var resolve = config.getTargetFolder().resolve(item.id() + ".json");
        try {
            objectMapper.writeValue(resolve.toFile(), item);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
