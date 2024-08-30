package poeai.stat.files;

import poeai.stat.tables.English.StatDto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StatDescriptionBlocksLoader {

    private final List<StatDescriptionBlock> statDescriptionBlocks;

    public StatDescriptionBlocksLoader() {
        var descriptionsInputStream = this.getClass().getResourceAsStream("Metadata@StatDescriptions@stat_descriptions.txt");
        var gatherer = new BufferedReader(new InputStreamReader(descriptionsInputStream, StandardCharsets.UTF_16LE)).lines()
                // This should NEVER be run in // as it depends on lines order
                .reduce(
                        new StatDescriptionParser(),
                        StatDescriptionParser::parse,
                        (l, r) -> l.closeDescriptionBlock()
                );
        this.statDescriptionBlocks = gatherer.descriptionBlocks;
    }

    public List<StatDescriptionBlock> findAllLinkedTo(List<StatDto> statDtos) {
        return statDescriptionBlocks.stream()
                .filter(statDescriptionBlock -> statDescriptionBlock.isLinkedToAny(statDtos))
                .toList();
    }

    public enum StatDescriptionGathererState {
        WAIT_STARTING_BLOCK,
        WAIT_STAT_ID_LIST,
        WAIT_DESCRIPTIONS_NUMBER,
        WAIT_DESCRIPTION
    }

    private static class StatDescriptionParser {

        private final List<StatDescriptionBlock> descriptionBlocks;

        private StatDescriptionGathererState state;

        private StatDescriptionBlock.Builder current;

        public StatDescriptionParser() {
            this.state = StatDescriptionGathererState.WAIT_STARTING_BLOCK;
            this.descriptionBlocks = new ArrayList<>();
        }

        public StatDescriptionParser parse(String line) {
            switch (state) {
                case WAIT_STARTING_BLOCK -> waitingForStartingBlock(line);
                case WAIT_STAT_ID_LIST -> waitingForStatIdList(line);
                case WAIT_DESCRIPTIONS_NUMBER -> waitingForDescriptionsNumber(line);
                case WAIT_DESCRIPTION -> waitDescription(line);
            }
            return this;
        }

        public StatDescriptionParser closeDescriptionBlock() {
            this.state = StatDescriptionGathererState.WAIT_STARTING_BLOCK;
            this.descriptionBlocks.add(this.current.build());
            return this;
        }

        private void waitDescription(String line) {
            this.current.addDescription(LineParserHelper.parseDescription(line));
            if (this.current.hasEnoughDescription()) {
                closeDescriptionBlock();
            }
        }

        private void waitingForDescriptionsNumber(String line) {
            this.current.nbOfText(Integer.parseInt(line.trim()));
            this.state = StatDescriptionGathererState.WAIT_DESCRIPTION;
        }

        private void waitingForStatIdList(String line) {
            this.current.idsLine(LineParserHelper.parseStatIds(line));
            this.state = StatDescriptionGathererState.WAIT_DESCRIPTIONS_NUMBER;
        }

        private void waitingForStartingBlock(String line) {
            if (line.startsWith("description")) {
                state = StatDescriptionGathererState.WAIT_STAT_ID_LIST;
                current = StatDescriptionBlock.builder();
            }
        }
    }
}
