package poeai.stat.files;

import poeai.stat.tables.English.StatDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatDescriptionBlock {

    private final List<String> statIds;

    private final List<Description> descriptions;

    private StatDescriptionBlock(List<String> statIds,
                                 List<Description> descriptions) {
        this.statIds = statIds;
        this.descriptions = descriptions;
    }

    public boolean isLinkedToAny(List<StatDto> statDtos) {
        return !Collections.disjoint(statIds, statDtos.stream().map(StatDto::id).toList());
    }

    public List<StatDescription> split() {
        return descriptions.stream()
                .map(line -> new StatDescription(line, statIds))
                .toList();
    }

    public static StatDescriptionBlock.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private List<String> statIds;
        private Integer nbOfText;
        private List<Description> descriptions = new ArrayList<>();

        public Builder idsLine(List<String> statIds) {
            this.statIds = statIds;
            return this;
        }

        public Builder nbOfText(int nbOfText) {
            this.nbOfText = nbOfText;
            return this;
        }

        public Builder addDescription(Description substring) {
            descriptions.add(substring);
            return this;
        }

        public boolean hasEnoughDescription() {
            return nbOfText.equals(descriptions.size());
        }

        public StatDescriptionBlock build() {
            if(descriptions.size() != nbOfText) {
                throw new IllegalStateException("Build cannot be called not enough descriptions.");
            }
            return new StatDescriptionBlock(statIds, descriptions);
        }
    }
}
