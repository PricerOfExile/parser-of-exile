package poeai.gamedata.statdescription;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StatValuator {

    private final List<StatDescription> statDescriptions;

    public StatValuator(@Nonnull StatDescriptionRepository statDescriptionRepository,
                        @Nonnull StatCatalog statCatalog) {
        var nonUniqueAccessoriesStats = statCatalog.findAllNonUniqueAndAccessoryRelated();
        statDescriptions = statDescriptionRepository.findAllLinkedTo(nonUniqueAccessoriesStats)
                .stream()
                .toList();
    }

    public List<ValuatedStat> valuateDisplayedMod(String descriptionLine) {
        return statDescriptions.parallelStream()
                .map(statDescription -> statDescription.valuateDisplayedMod(descriptionLine))
                .flatMap(List::stream)
                .toList();
    }
}
