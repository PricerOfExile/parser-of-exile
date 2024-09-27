package poe.gamedata.mod;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static java.lang.String.format;

public enum ModGenerationType {

    PREFIX(1),
    SUFFIX(2),
    UNIQUE(3),
    NEMESIS(4),
    CORRUPTED(5),
    BLOODLINES(6),
    TORMENT(7),
    TEMPEST(8),
    TALISMAN(9),
    ENCHANTMENT(10),
    ESSENCE(11),
    // Ignored Index _
    BESTIARY(13),
    DELVE_AREA(14),
    SYNTHESIS_A(15),
    SYNTHESIS_GLOBALS(16),
    SYNTHESIS_BONUS(17),
    BLIGHT(18),
    BLIGHT_TOWER(19),
    MONSTER_AFFLICTION(20),
    FLASK_ENCHANTMENT_ENKINDLING(21),
    FLASK_ENCHANTMENT_INSTILLING(22),
    EXPEDITION_LOGBOOK(23),
    SCOURGE_UPSIDE(24),
    SCOURGE_DOWNSIDE(25),
    SCOURGE_MAP(26),
    // Ignored Index _
    EXARCH_IMPLICIT(28),
    EATER_IMPLICIT(29),
    // Ignored Index _
    WEAPON_TREE(31),
    WEAPON_TREE_RECOMBINED(32),
    UNKNOWN_33(33), // Marked as Ignored Index _ but appears in the Data
    NECROPOLIS_HAUNTED(34),
    NECROPOLIS_DEVOTED(35);

    private final int index;

    ModGenerationType(int index) {
        this.index = index;
    }

    public static ModGenerationType fromIndex(int index){
        return Arrays.stream(ModGenerationType.values())
                .filter(generationType -> generationType.index == index)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(format("ModGenerationType with index[%d] does not exists.", index)));
    }
}
