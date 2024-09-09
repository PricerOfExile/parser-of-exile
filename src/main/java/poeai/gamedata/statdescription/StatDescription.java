package poeai.gamedata.statdescription;

import poeai.gamedata.stat.Stat;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.lang.String.format;

public record StatDescription(Pattern descriptionPattern,
                              Description description,
                              List<String> statIds) {

    /**
     * All the patterns used by GGG to represent a value.
     */
    private static final List<String> VALUE_PATTERNS = List.of(
            "{}", "{0}", "{0}", "{1}", "{0:+d}", "{0:d}", "{2:+d}", "{:d}", "{1:d}", "{:+d}", "{0:+}", "{1:+d}", "{3:+d}", "{6:+d}", "{5:+d}"
    );

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

    public boolean isLinkedToAny(List<Stat> stats) {
        return !Collections.disjoint(statIds, stats.stream().map(Stat::id).toList());
    }

    public List<ValuatedStat> valuateDisplayedMod(String statSentence) {
        var matcher = descriptionPattern.matcher(statSentence);
        if (matcher.find()) {
            var factor = description.valueHint().equals("#|-1") || description.valueHint().equals("-1") ? -1. : 1.;
            // There's no numeric value to find
            if (matcher.groupCount() == 0) {
                return statIds.stream()
                        .map(id -> new ValuatedStat(id, factor))
                        .toList();
            }
            var values = IntStream.range(1, matcher.groupCount() + 1)
                    .mapToObj(matcher::group)
                    .toList();
            return IntStream.range(0, Math.min(values.size(), statIds.size()))
                    .mapToObj(index -> new ValuatedStat(statIds.get(index), factor * Double.parseDouble(values.get(index))))
                    .toList();
        } else {
            return List.of();
        }
    }
}
