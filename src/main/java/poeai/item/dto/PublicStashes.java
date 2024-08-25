package poeai.item.dto;

import java.util.List;
import java.util.stream.Stream;

public record PublicStashes(List<PublicStash> stashes) {

    public Stream<PublicStash> streamStashes() {
        return stashes.stream();
    }
}
