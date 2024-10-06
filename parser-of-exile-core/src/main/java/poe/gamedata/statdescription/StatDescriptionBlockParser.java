package poe.gamedata.statdescription;

import java.util.ArrayList;
import java.util.List;

// TODO : Rewrite this to be immutable instead
class StatDescriptionBlockParser {

    private final List<StatDescriptionBlock> descriptionBlocks;

    private StatDescriptionGathererState state;

    private StatDescriptionBlock.Builder current;

    public StatDescriptionBlockParser() {
        this.state = StatDescriptionGathererState.WAIT_STARTING_BLOCK;
        this.descriptionBlocks = new ArrayList<>();
    }

    public StatDescriptionGathererState getState() {
        return state;
    }

    public StatDescriptionBlockParser parse(String line) {
        switch (state) {
            case WAIT_STARTING_BLOCK -> waitingForStartingBlock(line);
            case WAIT_STAT_ID_LIST -> waitingForStatIdList(line);
            case WAIT_DESCRIPTIONS_NUMBER -> waitingForDescriptionsNumber(line);
            case WAIT_DESCRIPTION -> waitDescription(line);
            case CLOSED -> throw new IllegalStateException("Cannot parse line as parser is closed");
        }
        return this;
    }

    public List<StatDescriptionBlock> close() {
        this.state = StatDescriptionGathererState.CLOSED;
        return descriptionBlocks;
    }

    private void waitDescription(String line) {
        this.current.addDescription(LineParserHelper.parseDescription(line));
        if (this.current.hasEnoughDescription()) {
            endDescriptionBlock();
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

    private void endDescriptionBlock() {
        this.state = StatDescriptionGathererState.WAIT_STARTING_BLOCK;
        this.descriptionBlocks.add(this.current.build());
        this.current = null;
    }

    private void waitingForStartingBlock(String line) {
        if (line.startsWith("description")) {
            state = StatDescriptionGathererState.WAIT_STAT_ID_LIST;
            current = StatDescriptionBlock.builder();
        }
    }

    public enum StatDescriptionGathererState {
        WAIT_STARTING_BLOCK,
        WAIT_STAT_ID_LIST,
        WAIT_DESCRIPTIONS_NUMBER,
        WAIT_DESCRIPTION,
        CLOSED
    }
}
