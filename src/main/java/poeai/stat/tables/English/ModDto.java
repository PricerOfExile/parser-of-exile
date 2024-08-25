package poeai.stat.tables.English;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public record ModDto(int index,
                     String id,
                     ModDomain domain,
                     List<SpawnWeight> tagSpawnWeights,
                     ModGenerationType generationType,
                     Set<Integer> statIndexes) {

    @JsonCreator
    public ModDto(@JsonProperty("_index") int index,
                  @JsonProperty("Id") String id,
                  @JsonProperty("Domain") int domainIndex,
                  @JsonProperty("SpawnWeight_TagsKeys") List<Integer> spawnWeightTagsIndexes,
                  @JsonProperty("SpawnWeight_Values") List<Integer> spawnWeightValues,
                  @JsonProperty("GenerationType") int generationTypeIndex,
                  @JsonProperty("StatsKey1") Integer statsIndex1,
                  @JsonProperty("StatsKey2") Integer statsIndex2,
                  @JsonProperty("StatsKey3") Integer statsIndex3,
                  @JsonProperty("StatsKey4") Integer statsIndex4,
                  @JsonProperty("StatsKey5") Integer statsIndex5,
                  @JsonProperty("StatsKey6") Integer statsIndex6) {
        this(
                index,
                id,
                ModDomain.byIndex(domainIndex),
                computeTagSpawnWeights(spawnWeightTagsIndexes, spawnWeightValues),
                ModGenerationType.byIndex(generationTypeIndex),
                collectStatIndexes(statsIndex1, statsIndex2, statsIndex3, statsIndex4, statsIndex5, statsIndex6)
        );
    }

    private static Set<Integer> collectStatIndexes(Integer statsKey1,
                                                   Integer statsKey2,
                                                   Integer statsKey3,
                                                   Integer statsKey4,
                                                   Integer statsKey5,
                                                   Integer statsKey6) {
        return Stream.of(statsKey1, statsKey2, statsKey3, statsKey4, statsKey5, statsKey6)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private static List<SpawnWeight> computeTagSpawnWeights(List<Integer> spawnWeightTagsKeys,
                                                            List<Integer> spawnWeightValues) {
        return IntStream.range(0, spawnWeightTagsKeys.size())
                .mapToObj(spawnIndex -> new SpawnWeight(spawnWeightTagsKeys.get(spawnIndex), spawnWeightValues.get(spawnIndex)))
                .toList();
    }

    public boolean canSpawnOn(List<TagDto> tags) {
        return tagSpawnWeights.stream()
                .anyMatch(spawn -> spawn.isIn(tags) && spawn.isHigher(0));
    }

    public boolean canBeCraftedOn(List<TagDto> tagDtos) {
        return domain.isCrafted()
                && tagSpawnWeights.stream().anyMatch(spawn -> spawn.isIn(tagDtos));
    }

    public boolean isEquipmentRelated() {
        return domain.isEquipmentRelated();
    }

    public boolean isNotUniqueNorWeaponTree() {
        return generationType != ModGenerationType.UNIQUE
                && generationType != ModGenerationType.WEAPON_TREE;
    }
}
