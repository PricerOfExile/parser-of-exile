package poeai.gamedata.statdescription;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import poeai.gamedata.mod.Mod;
import poeai.gamedata.mod.ModRepository;
import poeai.gamedata.stat.Stat;
import poeai.gamedata.stat.StatRepository;
import poeai.gamedata.tag.TagRepository;

import java.util.List;
import java.util.Set;

@Service
public class StatCatalog {

    private final ModRepository modRepository;

    private final TagRepository tagRepository;

    private final StatRepository statRepository;

    public StatCatalog(@Nonnull ModRepository modRepository,
                       @Nonnull TagRepository tagRepository,
                       @Nonnull StatRepository statRepository) {
        this.modRepository = modRepository;
        this.tagRepository = tagRepository;
        this.statRepository = statRepository;
    }

    public List<Stat> findAllNonUniqueAndAccessoryRelated() {
        var accessoryTags = tagRepository.findAllAccessoryTags();
        var genericAndAccessoryTags = tagRepository.findAllGenericAndAccessoryTags();
        return modRepository.findAllNonUniqueAndEquipmentRelated().stream()
                .filter(mod -> mod.canBeCraftedOn(accessoryTags)
                        || mod.canSpawnOn(genericAndAccessoryTags)
                        || mod.isSynthesis()
                        || mod.isImplicitAccessory()
                )
                .map(Mod::statIndexes)
                .flatMap(Set::stream)
                .distinct()
                .map(statRepository::getByIndex)
                .toList();
    }
}
