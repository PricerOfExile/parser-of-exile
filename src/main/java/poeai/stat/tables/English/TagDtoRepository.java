package poeai.stat.tables.English;

import java.util.List;

public class TagDtoRepository {

    private final List<TagDto> tagDtos;

    public TagDtoRepository(List<TagDto> tagDtos) {
        this.tagDtos = tagDtos;
    }

    public List<TagDto> findAllAccessoryTags() {
        return tagDtos.stream()
                .filter(TagDto::isAccessoryTag)
                .toList();
    }

    public List<TagDto> findAllGenericTags() {
        return tagDtos.stream()
                .filter(TagDto::isGenericTag)
                .toList();
    }
}
