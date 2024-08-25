package poeai.stat.files;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public record IdsLine(int nbOfIds,
                      List<String> ids) {

    public static IdsLine of(String line) {
        var splitLine = line.trim().split(" ");
        var nbOfIds = Integer.parseInt(splitLine[0]);
        if (nbOfIds != splitLine.length - 1) {
            throw new IllegalArgumentException(format("These line (%s) has not the correct number of ids", line));
        }
        return new IdsLine(nbOfIds, Arrays.stream(splitLine).skip(1).toList());
    }
}
