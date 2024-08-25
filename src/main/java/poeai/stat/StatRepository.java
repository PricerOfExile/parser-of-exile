package poeai.stat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import poeai.stat.files.StatDescription;
import poeai.stat.files.StatDescriptionsParser;
import poeai.stat.tables.English.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatRepository {


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

        var statDescriptionParser = new StatDescriptionsParser();

        // PROCESS //

        var accessoryTags = tagDtoRepository.findAllAccessoryTags();

        var genericAndAccessoryTags = Stream.of(
                tagDtoRepository.findAllGenericTags(),
                accessoryTags
        ).flatMap(List::stream).toList();

        var modDtos = modDtoRepository.findAllNonUniqueAndEquipmentRelated().stream()
                .filter(modDto -> modDto.canBeCraftedOn(accessoryTags) || modDto.canSpawnOn(genericAndAccessoryTags))
                .toList();
        System.out.printf("%d mods%n", modDtos.size());
        modDtos.forEach(System.out::println);

        var statIndexes = modDtos.stream()
                .map(ModDto::statIndexes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        System.out.printf("%d stat indexes%n", statIndexes.size());

        var statDtos = statIndexes.stream()
                .map(statDtoRepository::getByIndex)
                .toList();
        System.out.printf("%d stat dtos%n", statDtos.size());

        var statDescriptions = statDtos.stream()
                .map(statDto -> statDescriptionParser.findAllByStatId(statDto.id()))
                .flatMap(List::stream)
                .map(StatDescription::reverse)
                .flatMap(List::stream)
                .toList();

        var folder = Paths.get("/Users/mtintinger/dev/ahah/api-2");
        var resolve = folder.resolve("stats_equipment.json");
        try {
            objectMapper.writeValue(resolve.toFile(), statDescriptions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
