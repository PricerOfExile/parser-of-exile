package poeai.stat;

import poeai.stat.tables.English.*;

import java.util.List;
import java.util.Set;

public class StatDtoFilteringService {

    private final ModDtoRepository modDtoRepository;

    private final TagDtoRepository tagDtoRepository;

    private final StatDtoRepository statDtoRepository;

    public StatDtoFilteringService(ModDtoRepository modDtoRepository,
                                   TagDtoRepository tagDtoRepository,
                                   StatDtoRepository statDtoRepository) {
        this.modDtoRepository = modDtoRepository;
        this.tagDtoRepository = tagDtoRepository;
        this.statDtoRepository = statDtoRepository;
    }

    public List<StatDto> findAllNonUniqueAndAccessoryRelated() {
        var accessoryTags = tagDtoRepository.findAllAccessoryTags();
        var genericAndAccessoryTags = tagDtoRepository.findAllGenericAndAccessoryTags();
        return modDtoRepository.findAllNonUniqueAndEquipmentRelated().stream()
                .filter(modDto -> modDto.canBeCraftedOn(accessoryTags) || modDto.canSpawnOn(genericAndAccessoryTags))
                .map(ModDto::statIndexes)
                .flatMap(Set::stream)
                .distinct()
                .map(statDtoRepository::getByIndex)
                .toList();
    }
}
