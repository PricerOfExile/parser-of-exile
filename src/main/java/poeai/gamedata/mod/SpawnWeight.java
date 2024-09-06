package poeai.gamedata.mod;

import poeai.gamedata.tag.Tag;

import java.util.List;

public record SpawnWeight(int key, int value) {

    public boolean isHigher(int i) {
        return value > i;
    }

    public boolean isIn(List<Tag> tags) {
        return tags.stream().anyMatch(tagDto -> tagDto.index() == key);
    }
}
