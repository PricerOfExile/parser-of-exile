package poeai.publicstash.model;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record Qualities(int attack,
                        int attribute,
                        int caster,
                        int critical,
                        int defense,
                        int elemental,
                        int lifeAndMana,
                        int physicalAndChaos,
                        int resistance,
                        int speed
) {
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("+##%");

    public static Qualities from(List<ItemProperty> properties) {
        return Optional.ofNullable(properties)
                .orElse(List.of())
                .stream()
                .map(property -> QualityType.fromLabel(property.name())
                        .map(type -> {
                            try {
                                var parse = PERCENT_FORMAT.parse((String) property.values().get(0).get(0));
                                return type.qualityFactory.apply((int) (100 * parse.doubleValue()));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        })
                )
                .filter(Optional::isPresent)
                .flatMap(Optional::stream)
                .findFirst()
                .orElse(new Qualities(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }

    public static Qualities ofAttack(int value) {
        return new Qualities(value, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static Qualities ofAttribute(int value) {
        return new Qualities(0, value, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static Qualities ofCaster(int value) {
        return new Qualities(0, 0, value, 0, 0, 0, 0, 0, 0, 0);
    }

    public static Qualities ofCritical(int value) {
        return new Qualities(0, 0, 0, value, 0, 0, 0, 0, 0, 0);
    }

    public static Qualities ofDefense(int value) {
        return new Qualities(0, 0, 0, 0, value, 0, 0, 0, 0, 0);
    }

    public static Qualities ofElementalDamage(int value) {
        return new Qualities(0, 0, 0, 0, 0, value, 0, 0, 0, 0);
    }

    public static Qualities ofLifeAndMana(int value) {
        return new Qualities(0, 0, 0, 0, 0, 0, value, 0, 0, 0);
    }

    public static Qualities ofPhysicalAndChaos(int value) {
        return new Qualities(0, 0, 0, 0, 0, 0, 0, value, 0, 0);
    }

    public static Qualities ofResistance(int value) {
        return new Qualities(0, 0, 0, 0, 0, 0, 0, 0, value, 0);
    }

    public static Qualities ofSpeed(int value) {
        return new Qualities(0, 0, 0, 0, 0, 0, 0, 0, 0, value);
    }

    private enum QualityType {

        ATTACK("Quality (Attack Modifiers)", Qualities::ofAttack),
        ATTRIBUTE("Quality (Attribute Modifiers)", Qualities::ofAttribute),
        CASTER("Quality (Caster Modifiers)", Qualities::ofCaster),
        CRITICAL("Quality (Critical Modifiers)", Qualities::ofCritical),
        DEFENSE("Quality (Defence Modifiers)", Qualities::ofDefense),
        ELEMENTAL_DAMAGE("Quality (Elemental Damage Modifiers)", Qualities::ofElementalDamage),
        LIFE_AND_MANA("Quality (Life and Mana Modifiers)", Qualities::ofLifeAndMana),
        PHYSICAL_AND_CHAOS("Quality (Physical and Chaos Damage Modifiers)", Qualities::ofPhysicalAndChaos),
        RESISTANCE("Quality (Resistance Modifiers)", Qualities::ofResistance),
        SPEED("Quality (Speed Modifiers)", Qualities::ofSpeed);

        private final String label;

        private final Function<Integer, Qualities> qualityFactory;

        QualityType(String label,
                    Function<Integer, Qualities> qualityFactory) {
            this.label = label;
            this.qualityFactory = qualityFactory;
        }

        private static Optional<QualityType> fromLabel(String label) {
            return Arrays.stream(QualityType.values())
                    .filter(type -> type.label.equals(label))
                    .findFirst();
        }
    }
}
