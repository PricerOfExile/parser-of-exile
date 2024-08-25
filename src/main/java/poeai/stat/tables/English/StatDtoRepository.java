package poeai.stat.tables.English;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class StatDtoRepository {

    private final Map<Integer, StatDto> statById;

    public StatDtoRepository(List<StatDto> statDtos) {
        statById = statDtos.stream()
                .collect(Collectors.toMap(StatDto::index, Function.identity()));
    }

    public StatDto getByIndex(int index) {
        return Optional.ofNullable(statById.get(index))
                .orElseThrow(() -> new NoSuchElementException(format("Stat with index [%d] does not exists.", index)));
    }
}
