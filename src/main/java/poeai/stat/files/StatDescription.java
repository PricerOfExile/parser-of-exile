package poeai.stat.files;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.lang.String.format;

public record StatDescription(Pattern descriptionPattern,
                              Description description,
                              List<String> statIds) {

    private static final List<String> VALUE_PATTERNS = List.of("{}", "{0}", "{0}", "{1}", "{0:+d}", "{0:d}");

    public StatDescription(Description description,
                           List<String> statIds) {
        this(computeDescriptionPattern(description), description, statIds);
    }

    private static Pattern computeDescriptionPattern(Description description) {
        var withEscapedPlus = description.displayPattern().replace("+{", "\\+{");
        var rawPattern = VALUE_PATTERNS
                .stream()
                .reduce(
                        withEscapedPlus,
                        (acc, toReplace) -> acc.replace(toReplace, "([+-]?[.0-9]+)"),
                        (a, b) -> a
                );
        return java.util.regex.Pattern.compile(format("^%s$", rawPattern));
    }

    public List<Stat> transform(String statSentence) {
        var matcher = descriptionPattern.matcher(statSentence);
        if (matcher.find()) {
            var factor = description.pattern().equals("#|-1") || description.pattern().equals("-1") ? -1. : 1.;
            // There's no numeric value to find
            if (matcher.groupCount() == 0) {
                return statIds.stream()
                        .map(id -> new Stat(id, factor))
                        .toList();
            }
            var values = IntStream.range(1, matcher.groupCount() + 1)
                    .mapToObj(matcher::group)
                    .toList();
            return IntStream.range(0, Math.min(values.size(), statIds.size()))
                    .mapToObj(index -> new Stat(statIds.get(index), factor * Double.parseDouble(values.get(index))))
                    .toList();
        } else {
            return List.of();
        }
    }
}
