package poe.gamedata.statdescription;

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
        List<ValuatedStat> valuatedStats = statDescriptions.parallelStream()
                .map(statDescription -> statDescription.valuateDisplayedMod(descriptionLine))
                .flatMap(List::stream)
                .toList();
        if (valuatedStats.isEmpty()
                // Note: Expected Lines we cannot parse
                && !descriptionLine.startsWith("Allocates")
                && !descriptionLine.contains("Tower")
                // Note: Expected Lines we don't have to parse : data quality ?
                && !descriptionLine.startsWith("Suffix")
                && !descriptionLine.startsWith("Prefix")
        ) {
            log.error(descriptionLine);
        }
        return valuatedStats;
    }
}
