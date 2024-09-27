package poe.gamedata.stat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import poe.GameDataFileLoadingException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class StatRepository {

    private final Map<Integer, Stat> statById;

    public StatRepository(@Value("classpath:/poe.gamedata/tables/English/Stats.json") @Nonnull Resource statResource,
                          @Nonnull ObjectMapper objectMapper) {
        Objects.requireNonNull(statResource, "Resource is mandatory");
        Objects.requireNonNull(objectMapper, "ObjectMapper is mandatory");
        var listStatType = new TypeReference<List<Stat>>() {
            // Note : helps Jackson to parse the file in the correct type
        };
        try(var inputStream = statResource.getInputStream()) {
            statById = objectMapper.readValue(inputStream, listStatType).stream()
                    .collect(Collectors.toMap(Stat::index, Function.identity()));
        } catch (Exception e) {
            throw new GameDataFileLoadingException(statResource, e);
        }
    }

    public Stat getByIndex(int index) {
        return Optional.ofNullable(statById.get(index))
                .orElseThrow(() -> new NoSuchElementException(format("Stat with index [%d] does not exists.", index)));
    }
}
