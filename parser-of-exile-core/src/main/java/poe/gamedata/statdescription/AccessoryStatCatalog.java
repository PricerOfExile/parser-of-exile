package poe.gamedata.statdescription;

import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;
import poe.gamedata.mod.Mod;
import poe.gamedata.mod.ModRepository;
import poe.gamedata.stat.Stat;
import poe.gamedata.stat.StatRepository;
import poe.gamedata.tag.Tag;
import poe.gamedata.tag.TagRepository;

import java.util.List;
import java.util.Set;

@Service
public class AccessoryStatCatalog {

    @Nonnull
    private final ModRepository modRepository;
    @Nonnull
    private final StatRepository statRepository;
    @Nonnull
    private final List<Tag> accessoryTags;
    @Nonnull
    private final List<Tag> genericAndAccessoryTags;

    public AccessoryStatCatalog(@Nonnull ModRepository modRepository,
                                @Nonnull TagRepository tagRepository,
                                @Nonnull StatRepository statRepository) {
        this.modRepository = modRepository;
        this.statRepository = statRepository;
        this.accessoryTags = tagRepository.findAllAccessoryTags();
        this.genericAndAccessoryTags = tagRepository.findAllGenericAndAccessoryTags();
    }

    public List<Stat> findAllNonUniqueAndAccessoryRelated() {
        return modRepository.findAllNonUniqueAndEquipmentRelated().stream()
                .filter(this::isAccessoryMod)
                .map(Mod::statIndexes)
                .flatMap(Set::stream)
                .distinct()
                .map(statRepository::getByIndex)
                .toList();
    }

    private boolean isAccessoryMod(Mod mod) {
        return mod.canBeCraftedOn(accessoryTags)
                || mod.canSpawnOn(genericAndAccessoryTags)
                || mod.isSynthesis()
                || mod.isImplicitAccessory();
    }
}
