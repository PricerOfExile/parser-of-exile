package poeai.stat;

import poeai.stat.files.Stat;
import poeai.stat.files.StatDescription;
import poeai.stat.files.StatDescriptionBlocksLoader;

import java.util.List;

public class StatDescriptionLoader {

    private final List<StatDescription> statDescriptions;

    public StatDescriptionLoader(StatDescriptionBlocksLoader statDescriptionBlocksLoader,
                                 StatDtoFilteringService statDtoFilteringService) {
        var statDtos = statDtoFilteringService.findAllNonUniqueAndAccessoryRelated();
        statDescriptions = statDescriptionBlocksLoader.findAllLinkedTo(statDtos)
                .stream()
                .flatMap(block -> block.split().stream())
                .toList();
    }

    public List<Stat> findStatsFor(String descriptionLine) {
        var result = statDescriptions.parallelStream()
                .map(statDescription -> statDescription.transform(descriptionLine))
                .flatMap(List::stream)
                .toList();
        if (result.isEmpty()) {
            System.out.printf("Not computed stats for %s%n", descriptionLine);
        }
        return result;
    }
}
