package poeai;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import poeai.publicstash.model.DumpedItem;

import java.io.IOException;
import java.nio.file.Path;

public class FileFilter {

    public static void main(String[] args) throws IOException {
        // Note : should we integrate any Container manager at some point ?

        /*
        var computedStats = item.item().mods().stream()
                                .map(statValuator::valuateDisplayedMod)
                                .flatMap(List::stream)
                                .toList();
                        var collected = Stream.concat(defaultStats.stream(), computedStats.stream())
                                .collect(
                                        Collectors.collectingAndThen(Collectors.groupingBy(
                                                        ValuatedStat::id,
                                                        Collectors.reducing(0., ValuatedStat::value, Double::sum)
                                                ), map -> map.entrySet().stream().map(entry -> new ValuatedStat(entry.getKey(), entry.getValue())).sorted(Comparator.comparing(ValuatedStat::id)).toList()
                                        )
                                );
                        return new DumpedItem(item, collected);
         */
    }




}
