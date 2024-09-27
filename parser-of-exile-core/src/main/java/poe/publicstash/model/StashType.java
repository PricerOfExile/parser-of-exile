package poe.publicstash.model;

import java.util.Arrays;

public enum StashType {

    PREMIUM_STASH("PremiumStash", true),
    QUAD_STASH("QuadStash", true),
    UNKNOWN("Unknown", false);

    private final String label;

    private final boolean canContainEquipment;

    StashType(String label,
              boolean canContainEquipment) {
        this.label = label;
        this.canContainEquipment = canContainEquipment;
    }

    public boolean canContainEquipment() {
        return canContainEquipment;
    }

    public static StashType fromLabel(String label) {
        return Arrays.stream(StashType.values())
                .filter(stashType -> stashType.label.equals(label))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
