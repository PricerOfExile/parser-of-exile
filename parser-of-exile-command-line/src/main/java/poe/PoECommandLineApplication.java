package poe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import poe.model.ModelItem;
import poe.publicstash.IdentifiedNonUniqueAccessoryPricedItemStreamGenerator;
import poe.publicstash.ItemFilters;
import poe.publicstash.ModelItemFactory;
import poe.publicstash.PublicStashesTransformerConfig;
import poe.publicstash.model.League;

import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
public class PoECommandLineApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PoECommandLineApplication.class);

    private final PublicStashesTransformerConfig config;

    private final IdentifiedNonUniqueAccessoryPricedItemStreamGenerator itemStreamGenerator;

    private final ObjectMapper objectMapper;

    private final ModelItemFactory modelItemFactory;

    public PoECommandLineApplication(IdentifiedNonUniqueAccessoryPricedItemStreamGenerator itemStreamGenerator,
                                     ObjectMapper objectMapper,
                                     PublicStashesTransformerConfig config,
                                     ModelItemFactory modelItemFactory) {
        this.itemStreamGenerator = itemStreamGenerator;
        this.objectMapper = objectMapper;
        this.config = config;
        this.modelItemFactory = modelItemFactory;
        try {
            Files.createDirectories(this.config.getTargetFolder());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(PoECommandLineApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Running application");
        itemStreamGenerator.execute(new ItemFilters(League.NECROPOLIS, 100), items -> {
            items.map(modelItemFactory::create)
                    .forEach(this::writeToNewFile);
        });
        logger.info("Done");
    }

    private void writeToNewFile(ModelItem item) {
        var resolve = config.getTargetFolder().resolve(item.id() + ".json");
        try {
            objectMapper.writeValue(resolve.toFile(), item);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
