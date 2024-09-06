package poeai.stat;

import poeai.gamedata.mod.Mod;
import poeai.gamedata.mod.ModRepository;
import poeai.gamedata.stat.Stat;
import poeai.gamedata.stat.StatRepository;
import poeai.gamedata.tag.TagRepository;

import java.util.List;
import java.util.Set;

public class StatDtoFilteringService {

    private final ModRepository modRepository;

    private final TagRepository tagRepository;

    private final StatRepository statRepository;

    public StatDtoFilteringService(ModRepository modRepository,
                                   TagRepository tagRepository,
                                   StatRepository statRepository) {
        this.modRepository = modRepository;
        this.tagRepository = tagRepository;
        this.statRepository = statRepository;
    }

    public List<Stat> findAllNonUniqueAndAccessoryRelated() {
        var accessoryTags = tagRepository.findAllAccessoryTags();
        var genericAndAccessoryTags = tagRepository.findAllGenericAndAccessoryTags();
        return modRepository.findAllNonUniqueAndEquipmentRelated().stream()
                .filter(mod -> mod.canBeCraftedOn(accessoryTags) || mod.canSpawnOn(genericAndAccessoryTags))
                .map(Mod::statIndexes)
                .flatMap(Set::stream)
                .distinct()
                .map(statRepository::getByIndex)
                .toList();
    }
}
