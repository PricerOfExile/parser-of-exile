package poeai.stat.files;

import java.util.ArrayList;
import java.util.List;

public record StatDescription(IdsLine idsLine,
                              int nbOfText,
                              List<StatDescriptionLine> texts) {

    public List<DescriptionToStats> reverse() {
        return texts.stream()
                .map(line -> new DescriptionToStats(line, idsLine))
                .toList();
    }

    public static StatDescription.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private IdsLine idsLine;
        private Integer nbOfText;
        private List<StatDescriptionLine> texts = new ArrayList<>();

        public Builder idsLine(IdsLine idsLine) {
            this.idsLine = idsLine;
            return this;
        }

        public Builder nbOfText(int nbOfText) {
            this.nbOfText = nbOfText;
            return this;
        }

        public Builder addText(StatDescriptionLine substring) {
            texts.add(substring);
            return this;
        }

        public boolean hasEnoughText() {
            return nbOfText.equals(texts.size());
        }

        public StatDescription build() {
            return new StatDescription(idsLine, nbOfText, texts);
        }
    }
}
