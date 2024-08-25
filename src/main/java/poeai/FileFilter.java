package poeai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import poeai.item.DumpedItem;
import poeai.item.EnrichedItem;
import poeai.item.dto.PublicStash;
import poeai.item.dto.PublicStashes;
import poeai.stat.tables.English.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileFilter {

    public static void main(String[] args) throws IOException {

        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var modsFile = ModDtoRepository.class.getResourceAsStream("Mods.json");
        TypeReference<List<ModDto>> listModDtoType = new TypeReference<>() {
        };
        var modDtoRepository = new ModDtoRepository(objectMapper.readValue(modsFile, listModDtoType));

        TypeReference<List<StatDto>> listStatDtoType = new TypeReference<>() {
        };
        var statsFile = StatDtoRepository.class.getResourceAsStream("Stats.json");
        var statDtoRepository = new StatDtoRepository(objectMapper.readValue(statsFile, listStatDtoType));

        TypeReference<List<TagDto>> listTagDtoType = new TypeReference<>() {
        };
        var tagsFile = TagDtoRepository.class.getResourceAsStream("Tags.json");
        var tagDtoRepository = new TagDtoRepository(objectMapper.readValue(tagsFile, listTagDtoType));


        var accessoryTags = tagDtoRepository.findAllAccessoryTags();

        var genericAndAccessoryTags = Stream.of(
                tagDtoRepository.findAllGenericTags(),
                accessoryTags
        ).flatMap(List::stream).toList();

        var modDtos = modDtoRepository.findAllNonUniqueAndEquipmentRelated().stream()
                .filter(modDto -> modDto.canBeCraftedOn(accessoryTags) || modDto.canSpawnOn(genericAndAccessoryTags))
                .toList();
        System.out.printf("%d mods%n", modDtos.size());
        modDtos.stream()
                .forEach(System.out::println);

        var statIndexes = modDtos.stream()
                .map(ModDto::statIndexes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        System.out.printf("%d stat indexes%n", statIndexes.size());

        var statDtos = statIndexes.stream()
                .map(statDtoRepository::getByIndex)
                .toList();
        System.out.printf("%d stat dtos%n", statDtos.size());

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
                    .filter(EnrichedItem::isNotUnique)
                    .filter(EnrichedItem::identified)
                    .filter(EnrichedItem::hasPrice)
                    .map(item -> new DumpedItem(item, statDtos))
                    .limit(1000);
            itemStream.forEach(item -> writeToNewFile(item, folder));
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
