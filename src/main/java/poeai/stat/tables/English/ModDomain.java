package poeai.stat.tables.English;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Set;

public enum ModDomain {

    ITEM(1),
    FLASK(2),
    MONSTER(3),
    CHEST(4),
    AREA(5),
    // Ignored Index _
    SANCTUM_RELIC(7),
    // Ignored Index _
    CRAFTED(9),
    BASE_JEWEL(10),
    ATLAS(11),
    LEAGUESTONE(12),
    ABYSS_JEWEL(13),
    MAP_DEVICE(14),
    DUMMY(15),
    DELVE(16),
    DELVE_AREA(17),
    SYNTHESIS_A(18),
    SYNTHESIS_GLOBALS(19),
    SYNTHESIS_BONUS(20),
    AFFLICTION_JEWEL(21),
    HEIST_AREA(22),
    HEIST_NPC(23),
    HEIST_TRINKET(24),
    WATCHSTONE(25),
    VEILED(26),
    EXPEDITION_RELIC(27),
    UNVEILED(28),
    ELDRITCH_ALTAR(29),
    SENTINEL(30),
    MEMORY_LINE(31),
    SANCTUM_SPECIAL(32),
    CRUCIBLE_MAP(33),
    TINCTURE(34),
    ANIMAL_CHARM(35),
    NECROPOLIS_MONSTER(36),
    UBER_MAP(37);

    private static final Set<ModDomain> EQUIPMENT_RELATED_DOMAINS = Set.of(
            ITEM, CRAFTED, DELVE, VEILED, UNVEILED
    );

    private final int index;

    ModDomain(int index) {
        this.index = index;
    }

    public static ModDomain byIndex(int index){
        return Arrays.stream(ModDomain.values())
                .filter(domain -> domain.index == index)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(""));
    }

    public boolean isEquipmentRelated() {
        return EQUIPMENT_RELATED_DOMAINS.contains(this);
    }

    public boolean isCrafted() {
        return this == CRAFTED;
    }
}
