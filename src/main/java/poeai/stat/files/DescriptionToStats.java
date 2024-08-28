package poeai.stat.files;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public record DescriptionToStats(Pattern descriptionPattern,
                                 StatDescriptionLine description,
                                 IdsLine idsLine) {

    private static final List<String> VALUE_PATTERNS = List.of("{0}", "{1}", "{0:+d}");

    @JsonCreator
    public DescriptionToStats(StatDescriptionLine description,
                              IdsLine idsLine) {
        this(
                computeDescriptionPattern(description),
                description,
                idsLine
        );
    }

    private static Pattern computeDescriptionPattern(StatDescriptionLine description) {
        var rawPattern = VALUE_PATTERNS
                .stream()
                .reduce(
                        description.second(),
                        (acc, toReplace) -> acc.replace(toReplace, "([+-]?[0-9]+)"),
                        (a, b) -> a
                );
        return Pattern.compile(rawPattern);
    }

    public List<Stat> transform(String statSentence) {
        var matcher = descriptionPattern.matcher(statSentence);
        if (matcher.find()) {
            var factor = description.first().equals("#|-1") || description.first().equals("-1") ? -1 : 1;
            // There's no numeric value to find
            if(matcher.groupCount() == 0) {
                return idsLine.ids().stream()
                        .map(id -> new Stat(id, factor))
                        .toList();
            }
            var values = IntStream.range(1, matcher.groupCount() + 1)
                    .mapToObj(matcher::group)
                    .toList();
            return IntStream.range(0, Math.min(values.size(), idsLine.ids().size()))
                    .mapToObj(index -> new Stat(idsLine.ids().get(index), factor * Integer.parseInt(values.get(index))))
                    .toList();
        } else {
            return List.of();
        }
    }
}
