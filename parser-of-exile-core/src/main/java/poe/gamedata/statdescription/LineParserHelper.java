package poe.gamedata.statdescription;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

class LineParserHelper {

    private LineParserHelper() {
        // Contains utility methods to parse stat_descriptions.txt file
    }

    static List<String> parseStatIds(String line) {
        var splitLine = line.trim().split(" ");
        var nbOfIds = Integer.parseInt(splitLine[0]);
        if (nbOfIds != splitLine.length - 1) {
            throw new IllegalArgumentException(format("These line (%s) has not the correct number of ids", line));
        }
        return Arrays.stream(splitLine).skip(1).toList();
    }

    static Description parseDescription(String line) {
        var split = line.trim().split("\"");
        return new Description(
                split[0].trim(),
                split.length > 1 ? split[1].trim() : "",
                split.length > 2 ? split[2].trim() : ""
        );
    }
}
