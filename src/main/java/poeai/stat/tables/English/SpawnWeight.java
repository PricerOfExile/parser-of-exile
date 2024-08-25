package poeai.stat.tables.English;

import java.util.List;

public record SpawnWeight(int key, int value) {

    public boolean isHigher(int i) {
        return value > i;
    }

    public boolean isIn(List<TagDto> tagDtos) {
        return tagDtos.stream().anyMatch(tagDto -> tagDto.index() == key);
    }
}
