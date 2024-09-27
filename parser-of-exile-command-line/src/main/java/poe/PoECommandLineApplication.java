package poe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import poe.currency.CurrencyRepository;
import poe.gamedata.statdescription.StatCatalog;
import poe.gamedata.statdescription.StatValuator;
import poe.gamedata.statdescription.ValuatedStat;
import poe.publicstash.IdentifiedNonUniqueAccessoryPricedItemStreamGenerator;
import poe.publicstash.PublicStashesTransformerConfig;
import poe.publicstash.model.DumpedItem;
import poe.publicstash.model.PricedItem;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class PoECommandLineApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PoECommandLineApplication.class);

    private final IdentifiedNonUniqueAccessoryPricedItemStreamGenerator itemStreamGenerator;

    private final StatValuator statValuator;

    private final List<ValuatedStat> defaultValuatedStats;

    private final ObjectMapper objectMapper;

    private final PublicStashesTransformerConfig config;

    private final CurrencyRepository currencyRepository;

    private final StatCatalog statCatalog;

    public PoECommandLineApplication(IdentifiedNonUniqueAccessoryPricedItemStreamGenerator itemStreamGenerator,
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

        this.statCatalog = statCatalog;
        var statDtos = statCatalog.findAllNonUniqueAndAccessoryRelated();
        defaultValuatedStats = statDtos.stream()
                .map(statDto -> new ValuatedStat(statDto.id(), 0.))
                .toList();
    }

    public static void main(String[] args) {
        SpringApplication.run(PoECommandLineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Running application");
        // Note: I had to remove that, we need to split the code in 2 different apps
        logger.info("Done");
    }

    private DumpedItem buildDumpedItem(PricedItem pricedItem) {
        try {
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
        } catch (Exception e) {
            logger.error("Cannot Dump Item {}", pricedItem.id(), e);
            return null;
        }
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
