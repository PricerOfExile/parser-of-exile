package poeai.stat.files;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StatDescriptionsParser {

    private final List<StatDescription> descriptions;

    public StatDescriptionsParser() {
        var descriptionsInputStream = this.getClass().getResourceAsStream("Metadata@StatDescriptions@stat_descriptions.txt");
        var gatherer = new BufferedReader(new InputStreamReader(descriptionsInputStream, StandardCharsets.UTF_16LE)).lines()
                .reduce(
                        new StatDescriptionGatherer(),
                        StatDescriptionGatherer::gather,
                        (l, r) -> l.closeCurrentDescription()
                );

        this.descriptions = gatherer.descriptions;
    }

    public List<StatDescription> findAllByStatId(String statId) {
        return descriptions.stream()
                .filter(description -> description.idsLine().ids().contains(statId))
                .toList();
    }

    public static class StatDescriptionGatherer {

        private final List<StatDescription> descriptions;

        private StatDescriptionGathererState state;

        private StatDescription.Builder current;

        public StatDescriptionGatherer() {
            this.state = StatDescriptionGathererState.WAIT_DESCRIPTION;
            this.descriptions = new ArrayList<>();
        }

        public StatDescriptionGatherer gather(String s) {
            switch (state) {
                case WAIT_DESCRIPTION -> waitForDescription(s);
                case WAIT_IDS -> readIds(s);
                case WAIT_NUMBER -> readNbOfText(s);
                case WAIT_TEXT -> readNextTest(s);
            }
            return this;
        }

        private void readNextTest(String line) {
            var split = line.trim().split("\"");
            this.current.addText(new StatDescriptionLine(
                    split[0].trim(),
                    split.length > 1 ? split[1].trim() : "",
                    split.length > 2 ? split[2].trim() : ""
            ));
            if(this.current.hasEnoughText()) {
                closeCurrentDescription();
            }
        }

        private void readNbOfText(String s) {
            this.current.nbOfText(Integer.parseInt(s.trim()));
            this.state = StatDescriptionGathererState.WAIT_TEXT;
        }

        private void readIds(String line) {
            this.current.idsLine(IdsLine.of(line));
            this.state = StatDescriptionGathererState.WAIT_NUMBER;
        }

        public StatDescriptionGatherer closeCurrentDescription() {
            this.state = StatDescriptionGathererState.WAIT_DESCRIPTION;
            this.descriptions.add(this.current.build());
            return this;
        }

        private void waitForDescription(String line) {
            if (line.startsWith("description")) {
                state = StatDescriptionGathererState.WAIT_IDS;
                current = StatDescription.builder();
            }
        }
    }

    public enum StatDescriptionGathererState {
        WAIT_DESCRIPTION,
        WAIT_IDS,
        WAIT_NUMBER,
        WAIT_TEXT
    }
}
