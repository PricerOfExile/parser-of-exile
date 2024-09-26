package poeai.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import poeai.gamedata.statdescription.StatCatalog;
import poeai.gamedata.statdescription.StatValuator;
import poeai.gamedata.statdescription.ValuatedStat;
import poeai.publicstash.model.DumpedItem;
import poeai.publicstash.model.Influences;
import poeai.publicstash.model.Qualities;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FrontItemParser {

    private final StatValuator statValuator;
    private final StatCatalog statCatalog;

    public DumpedItem execute(String itemFromFront){

        var lines = itemFromFront.split("\r\n");
        var statDtos = statCatalog.findAllNonUniqueAndAccessoryRelated();
        var defaultValuatedStats = statDtos.stream()
                .map(statDto -> new ValuatedStat(statDto.id(), 0.))
                .toList();

        DumpedItem dumpedItem = DumpedItem.builder()
                .identified(true)
                .socket("N")
                .influences(Influences.builder().build())
                .qualities(new Qualities(0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
                .valuatedStats(defaultValuatedStats)
                .build();

        for (String line : lines) {
            DumpedItem.DumpedItemBuilder builder = dumpedItem.toBuilder();
            processLine(line, builder, dumpedItem);
            dumpedItem = builder.build();
        }

        return dumpedItem;
    }

    private void processLine(String line, DumpedItem.DumpedItemBuilder builder, DumpedItem dumpedItem) {
        extractRarity(line, builder);
        extractItemLevel(line, builder);
        extractRequiredLevel(line, builder);
        extractSockets(line, builder);
        extractInfluences(line, dumpedItem, builder);
        extractBooleans(line, builder);
        extractQualities(line, builder);
        extractMods(line, dumpedItem, builder);
    }

    private static void extractQualities(String line, DumpedItem.DumpedItemBuilder builder) {
        //Qualities
        if (line.startsWith("Quality (Attack Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Attack Modifiers): {}", quality);
            builder.qualities(Qualities.ofAttack(quality)).build();
        }
        if (line.startsWith("Quality (Attribute Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Attribute Modifiers): {}", quality);
            builder.qualities(Qualities.ofAttribute(quality)).build();
        }
        if (line.startsWith("Quality (Caster Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Caster Modifiers): {}", quality);
            builder.qualities(Qualities.ofCaster(quality)).build();
        }
        if (line.startsWith("Quality (Critical Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Critical Modifiers) {}", quality);
            builder.qualities(Qualities.ofCritical(quality)).build();
        }
        if (line.startsWith("Quality (Defence Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Defence Modifiers) {}", quality);
            builder.qualities(Qualities.ofDefense(quality)).build();
        }
        if (line.startsWith("Quality (Elemental Damage Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Elemental Damage Modifiers) {}", quality);
            builder.qualities(Qualities.ofElementalDamage(quality)).build();
        }
        if (line.startsWith("Quality (Life and Mana Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Life and Mana Modifiers) {}", quality);
            builder.qualities(Qualities.ofLifeAndMana(quality)).build();
        }
        if (line.startsWith("Quality (Physical and Chaos Damage Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Physical and Chaos Damage Modifiers) {}", quality);
            builder.qualities(Qualities.ofPhysicalAndChaos(quality)).build();
        }
        if (line.startsWith("Quality (Resistance Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Resistance Modifiers) {}", quality);
            builder.qualities(Qualities.ofResistance(quality)).build();
        }
        if (line.startsWith("Quality (Speed Modifiers)")) {
            int quality = Integer.parseInt(line.replaceAll("\\D+", ""));
            log.info(" We got Quality (Speed Modifiers) {}", quality);
            builder.qualities(Qualities.ofSpeed(quality)).build();
        }
        //END Qualities
    }

    private static void extractBooleans(String line, DumpedItem.DumpedItemBuilder builder) {
        //Booleans
        if (line.contains("Fractured Item")) {
            log.info(" We got fractured boolean ");
            builder.fractured(true).build();
        }
        if (line.contains("Synthesised Item")) {
            log.info(" We got Synthesised boolean ");
            builder.synthesised(true).build();
        }
        if (line.contains("Duplicated Item")) {
            log.info(" We got Duplicated boolean ");
            builder.duplicated(true).build();
        }
        if (line.contains("Split Item")) {
            log.info(" We got Split boolean ");
            builder.split(true).build();
        }
        if (line.contains("Corrupted Item")) {
            log.info(" We got Corrupted boolean ");
            builder.corrupted(true).build();
        }
        if (line.contains("Unidentified")) {
            log.info(" We got Unidentified boolean ");
            builder.identified(false).build();
        }
        //END Booleans
    }

    private static void extractInfluences(String line, DumpedItem dumpedItem, DumpedItem.DumpedItemBuilder builder) {
        //Influences :
        if (line.contains("Shaper Item")) {
            if (Objects.isNull(dumpedItem.influences())) {
                builder.influences(new Influences(false, false, true, false, false, false)).build();
            } else {
                builder.influences(dumpedItem.influences().toBuilder().shaper(true).build()).build();
            }
            log.info(" We got Shaper Influence: {}", line);
        }
        if (line.contains("Hunter Item")) {
            if (Objects.isNull(dumpedItem.influences())) {
                builder.influences(new Influences(false, false, false, false, false, true)).build();
            } else {
                builder.influences(dumpedItem.influences().toBuilder().hunter(true).build()).build();
            }
            log.info(" We got Hunter Influence: {}", line);
        }
        if (line.contains("Elder Item")) {
            if (Objects.isNull(dumpedItem.influences())) {
                builder.influences(new Influences(false, true, false, false, false, false)).build();
            } else {
                builder.influences(dumpedItem.influences().toBuilder().elder(true).build()).build();
            }
            log.info(" We got Elder Influence: {}", line);
        }
        if (line.contains("Warlord Item")) {
            if (Objects.isNull(dumpedItem.influences())) {
                builder.influences(new Influences(true, false, false, false, false, false)).build();
            } else {
                builder.influences(dumpedItem.influences().toBuilder().warlord(true).build()).build();
            }
            log.info(" We got Warlord Influence: {}", line);
        }
        if (line.contains("Crusader Item")) {
            if (Objects.isNull(dumpedItem.influences())) {
                builder.influences(new Influences(false, false, false, true, false, false)).build();
            } else {
                builder.influences(dumpedItem.influences().toBuilder().crusader(true).build()).build();
            }
            log.info(" We got Crusader Influence: {}", line);
        }
        if (line.contains("Redeemer Item")) {
            if (Objects.isNull(dumpedItem.influences())) {
                builder.influences(new Influences(false, false, false, false, true, false)).build();
            } else {
                builder.influences(dumpedItem.influences().toBuilder().redeemer(true).build()).build();
            }
            log.info(" We got redeemer Influence: {}", line);
        }
        //END Influences
    }

    private static void extractSockets(String line, DumpedItem.DumpedItemBuilder builder) {
        if (line.contains("Sockets:")) {
            log.info(" We got Sockets: {}", line.replace("Sockets: ", ""));
            builder.socket(line.replace("Sockets: ", "")).build();
        }
    }

    private static void extractRequiredLevel(String line, DumpedItem.DumpedItemBuilder builder) {
        if (line.startsWith("Level:")) {
            var level = Integer.parseInt(line.replace("Level: ", ""));
            log.info(" We got REQUIREMENT LEVEL: {}", level);
            builder.levelRequirement(level).build();
        }
    }

    private static void extractItemLevel(String line, DumpedItem.DumpedItemBuilder builder) {
        if (line.startsWith("Item Level:")) {
            log.info(" We got Item level: {}", line.replace("Item Level: ", ""));
            builder.ilvl(Integer.parseInt(line.replace("Item Level: ", ""))).build();
        }
    }

    private static void extractRarity(String line, DumpedItem.DumpedItemBuilder builder) {
        if (line.startsWith("Rarity:")) {
            log.info(" We got Rarity: {}", line.replace("Rarity: ", ""));
            builder.rarity(line.replace("Rarity: ", "")).build();
        }
    }

    private void extractMods(String line, DumpedItem dumpedItem, DumpedItem.DumpedItemBuilder builder) {
        var newStats = statValuator.valuateDisplayedMod(line).stream();
        Stream<ValuatedStat> currentStats = dumpedItem.valuatedStats().stream();
        var bothStats = Stream.concat(newStats, currentStats)
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(ValuatedStat::id, Collectors.reducing(0., ValuatedStat::value, Double::sum)),
                                map -> map.entrySet().stream()
                                        .map(entry -> new ValuatedStat(entry.getKey(), entry.getValue()))
                                        .sorted(Comparator.comparing(ValuatedStat::id))
                                        .toList()
                        )
                );
        builder.valuatedStats(bothStats).build();
    }
}
