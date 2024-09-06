package poeai;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.core.io.ClassPathResource;
import poeai.gamedata.mod.ModRepository;
import poeai.gamedata.stat.StatRepository;
import poeai.gamedata.tag.TagRepository;
import poeai.item.DumpedItem;
import poeai.item.EnrichedItem;
import poeai.item.dto.PublicStash;
import poeai.item.dto.PublicStashes;
import poeai.stat.StatDescriptionLoader;
import poeai.stat.StatDtoFilteringService;
import poeai.stat.files.Stat;
import poeai.stat.files.StatDescriptionBlocksLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileFilter {

    public static void main(String[] args) throws IOException {
        // Note : should we integrate any Container manager at some point ?

        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var modDtoRepository = new ModRepository(
                new ClassPathResource("/poe.gamedata/tables/English/Mods.json"),
                objectMapper
        );
        var statDtoRepository = new StatRepository(
                new ClassPathResource("/poe.gamedata/tables/English/Stats.json"),
                objectMapper
        );

        var tagDtoRepository = new TagRepository(
                new ClassPathResource("/poe.gamedata/tables/English/Tags.json"),
                objectMapper
        );

        var statDtoFilteringService = new StatDtoFilteringService(modDtoRepository, tagDtoRepository, statDtoRepository);


        var statDescriptionLoader = new StatDescriptionLoader(new StatDescriptionBlocksLoader(), statDtoFilteringService);

        statDescriptionLoader.findStatsFor("+45% to Fire Resistance")
                .stream().forEach(System.out::println);

        var statDtos = statDtoFilteringService.findAllNonUniqueAndAccessoryRelated();

        var defaultStats = statDtos.stream()
                .map(statDto -> new Stat(statDto.id(), 0.))
                .toList();

        var folder = Paths.get("/Users/mtintinger/dev/ahah/samples-dump-1000");
        Files.createDirectories(folder);
        long begin = System.currentTimeMillis();
        try (Stream<Path> paths = Files.walk(Paths.get("/Users/mtintinger/dev/ahah/api"))) {
            var itemStream = paths.filter(Files::isRegularFile)
                    .parallel()
                    .map(FileFilter::handleFile)
                    .flatMap(PublicStashes::streamStashes)
                    .filter(PublicStash::canContainEquipment)
                    .filter(PublicStash::isNecropolisLeague)
                    .flatMap(PublicStash::itemStream)
                    .filter(EnrichedItem::isAccessories)
                    .filter(EnrichedItem::isNotTrinket)
                    .filter(EnrichedItem::isNotUnique)
                    .filter(EnrichedItem::identified)
                    .filter(EnrichedItem::hasPrice)
                    .map(item -> {
                        var computedStats = item.item().mods().stream()
                                .map(statDescriptionLoader::findStatsFor)
                                .flatMap(List::stream)
                                .toList();
                        var collected = Stream.concat(defaultStats.stream(), computedStats.stream())
                                .collect(
                                        Collectors.collectingAndThen(Collectors.groupingBy(
                                                        Stat::id,
                                                        Collectors.reducing(0., Stat::value, Double::sum)
                                                ), map -> map.entrySet().stream().map(entry -> new Stat(entry.getKey(), entry.getValue())).sorted(Comparator.comparing(Stat::id)).toList()
                                        )
                                );
                        return new DumpedItem(item, collected);
                    })
                    .limit(50000)
                    ;

            itemStream.forEach(item -> writeToNewFile(item, folder));
            // System.out.println(itemStream.count());
        }
        System.out.println(System.currentTimeMillis() - begin);

    }

    private static void writeToNewFile(DumpedItem item, Path folder) {
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true);
        var resolve = folder.resolve(item.id() + ".json");
        try {
            objectMapper.writeValue(resolve.toFile(), item);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PublicStashes handleFile(Path path) {
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(path.toFile(), PublicStashes.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
