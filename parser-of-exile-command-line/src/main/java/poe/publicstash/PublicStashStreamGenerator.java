package poe.publicstash;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import poe.publicstash.model.PublicStash;
import poe.publicstash.model.PublicStashes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class PublicStashStreamGenerator {

    @Nonnull
    private final PublicStashesTransformerConfig config;
    @Nonnull
    private final ObjectMapper objectMapper;

    public PublicStashStreamGenerator(@Nonnull PublicStashesTransformerConfig config,
                                      @Nonnull ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
    }

    public void execute(ItemFilters filters,
                        Consumer<Stream<PublicStash>> publicStashConsumer) {
        try (Stream<Path> paths = Files.walk(config.getSourceFolder())) {
            var publicStashStream = paths
                    .filter(Files::isRegularFile)
                    .parallel()
                    .map(this::parseFile)
                    .flatMap(PublicStashes::streamStashes)
                    .filter(PublicStash::canContainEquipment)
                    .filter(stash -> stash.isOnLeague(filters.league()));
            publicStashConsumer.accept(publicStashStream);
        } catch (IOException e) {
            throw new PublicStashFileLoadingException(e);
        }
    }

    private PublicStashes parseFile(Path path) {
        try {
            return objectMapper.readValue(path.toFile(), PublicStashes.class);
        } catch (IOException e) {
            throw new PublicStashFileLoadingException(path, e);
        }
    }
}
