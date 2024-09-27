package poe.publicstash.model;

import java.util.Arrays;

public enum League {

    NECROPOLIS("Necropolis"),
    UNKNOWN("Unknown");

    private final String label;

    League(String label) {
        this.label = label;
    }

    public static League fromLabel(String label) {
        return Arrays.stream(League.values())
                .filter(league -> league.label.equals(label))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
