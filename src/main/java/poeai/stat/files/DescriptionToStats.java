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
            var values = IntStream.range(1, matcher.groupCount() + 1)
                    .mapToObj(matcher::group)
                    .toList();
            return IntStream.range(0, Math.min(values.size(), idsLine.ids().size()))
                    .mapToObj(index -> new Stat(idsLine.ids().get(index), Integer.parseInt(values.get(index))))
                    .toList();
        } else {
            return List.of();
        }
    }

    public static void main(String[] args) {
        testOne();
        testTwo();


    }

    private static void testOne() {
        var specific = VALUE_PATTERNS
                .stream()
                .reduce("{0} to {1} Fire Damage per Endurance Charge", (acc, toReplace) -> acc.replace(toReplace, "([0-9]+)"), (a, b) -> a);

        System.out.println(specific);
        var meme = Pattern.compile(specific).matcher("1 to 3 Fire Damage per Endurance Charge");
        System.out.println(meme.groupCount());
        meme.find();
        var values = IntStream.range(1, meme.groupCount() + 1)
                .mapToObj(index -> meme.group(index))
                .toList();

        var stats = List.of(
                "minimum_added_fire_damage_per_endurance_charge",
                "maximum_added_fire_damage_per_endurance_charge"
        );

        var list = IntStream.range(0, Math.min(values.size(), stats.size()))
                .mapToObj(index -> new Stat(stats.get(index), Integer.parseInt(values.get(index))))
                .toList();

        System.out.println(list);
    }

    private static void testTwo() {
        var specific = VALUE_PATTERNS
                .stream()
                .reduce("{0:+d} to maximum number of Raised Zombies", (acc, toReplace) -> acc.replace(toReplace, "([0-9]+)"), (a, b) -> a);

        System.out.println(specific);
        var meme = Pattern.compile(specific).matcher("12 to maximum number of Raised Zombies");

        meme.find();
        var values = IntStream.range(1, meme.groupCount() + 1)
                .mapToObj(index -> meme.group(index))
                .toList();

        var stats = List.of(
                "base_number_of_zombies_allowed",
                "quality_display_raise_zombie_is_gem"
        );

        var list = IntStream.range(0, Math.min(values.size(), stats.size()))
                .mapToObj(index -> new Stat(stats.get(index), Integer.parseInt(values.get(index))))
                .toList();

        System.out.println(list);
    }

//    public List<Stat> transform(String sentence) {
//        if (description.first().equals("# #")) {
//
//        } else {
//            throw new UnsupportedOperationException(format("This description transformer is not supported", description.first()));
//        }
//    }

    /*
    {
        "description": {
            "first": "# #",
            "second": "{0} to {1} Fire Damage per Endurance Charge",
            "third": ""
        },
        "idsLine": {
            "nbOfIds": 2,
            "ids": [
                "minimum_added_fire_damage_per_endurance_charge",
                "maximum_added_fire_damage_per_endurance_charge"
            ]
        }
    },
     */
}
