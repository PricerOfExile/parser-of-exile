package poeai.publicstash;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import poeai.publicstash.model.League;
import poeai.publicstash.model.PublicStash;
import poeai.publicstash.model.PublicStashes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public Stream<PublicStash> generateForLeague(League league) {
        try {
            // TODO - I cannot return the stream from here as it will be close by the try catch
            Stream<Path> paths = Files.walk(config.getSourceFolder());
            return paths
                    .filter(Files::isRegularFile)
                    .parallel()
                    .map(this::parseFile)
                    .flatMap(PublicStashes::streamStashes)
                    .filter(PublicStash::canContainEquipment)
                    .filter(stash -> stash.isOnLeague(league));
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
