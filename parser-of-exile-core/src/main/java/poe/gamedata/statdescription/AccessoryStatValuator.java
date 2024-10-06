package poe.gamedata.statdescription;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessoryStatValuator {

    private static final Logger logger = LoggerFactory.getLogger(AccessoryStatValuator.class);

    private final List<StatDescription> statDescriptions;

    public AccessoryStatValuator(@Nonnull StatDescriptionRepository statDescriptionRepository,
                                 @Nonnull AccessoryStatCatalog accessoryStatCatalog) {
        var nonUniqueAccessoriesStats = accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated();
        statDescriptions = statDescriptionRepository.findAllLinkedTo(nonUniqueAccessoriesStats)
                .stream()
                .toList();
    }

    public List<ValuatedStat> valuateDisplayedMod(String descriptionLine) {
        List<ValuatedStat> valuatedStats = statDescriptions.parallelStream()
                .map(statDescription -> statDescription.valuateDisplayedMod(descriptionLine))
                .flatMap(List::stream)
                .toList();
        if (valuatedStats.isEmpty() && shouldReturnsValuatedStat(descriptionLine)) {
            logger.debug(descriptionLine);
        }
        return valuatedStats;
    }

    private boolean shouldReturnsValuatedStat(String descriptionLine) {
        return  // Note: Expected Lines we cannot parse
                !descriptionLine.startsWith("Allocates") && !descriptionLine.contains("Tower")
                        // Note: Expected Lines we don't have to parse : GGG data quality ?
                        && !descriptionLine.startsWith("Suffix")
                        && !descriptionLine.startsWith("Prefix");
    }
}
