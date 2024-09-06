package poeai.gamedata.tag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import poeai.gamedata.GameDataFileLoadingException;

import java.util.List;
import java.util.Objects;

@Service
public class TagRepository {

    private final List<Tag> tags;

    public TagRepository(@Value("classpath:/poe.gamedata/tables/English/Tags.json") @Nonnull Resource resource,
                         @Nonnull ObjectMapper objectMapper) {
        Objects.requireNonNull(resource, "Resource is mandatory");
        Objects.requireNonNull(objectMapper, "ObjectMapper is mandatory");
        TypeReference<List<Tag>> listTagType = new TypeReference<>() {
            // Note : hint for Jackson to parse file
        };
        try {
            tags = objectMapper.readValue(resource.getFile(), listTagType);
        } catch (Exception e) {
            throw new GameDataFileLoadingException(resource, e);
        }
    }

    public List<Tag> findAllAccessoryTags() {
        return tags.stream()
                .filter(Tag::isAccessoryTag)
                .toList();
    }

    public List<Tag> findAllGenericAndAccessoryTags() {
        return tags.stream()
                .filter(tagDto -> tagDto.isGenericTag() || tagDto.isAccessoryTag())
                .toList();
    }
}
