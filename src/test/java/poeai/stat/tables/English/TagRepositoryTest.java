package poeai.stat.tables.English;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import poeai.gamedata.tag.Tag;
import poeai.gamedata.tag.TagRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagRepositoryTest {

    private TagRepository tagRepository;
    @Mock
    private Resource resource;
    @Mock
    private ObjectMapper objectMapper;

    @Nested
    class GivenEmptyListTagDtos {

        @BeforeEach
        void before() throws IOException {
            when(objectMapper.readValue(nullable(File.class), any(TypeReference.class)))
                    .thenReturn(List.of());
            tagRepository = new TagRepository(resource, objectMapper);
        }

        @Test
        void thenFindAllLinkedToAccessoryIsEmpty() {
            assertThat(tagRepository.findAllAccessoryTags())
                    .isEmpty();
        }
    }

    @Nested
    class GivenOneTagNOTLinkedToAccessory {

        @BeforeEach
        void before() throws IOException {
            var tagDto = mock(Tag.class);
            when(tagDto.isAccessoryTag())
                    .thenReturn(false);
            when(objectMapper.readValue(nullable(File.class), any(TypeReference.class)))
                    .thenReturn(List.of(tagDto));
            tagRepository = new TagRepository(resource, objectMapper);
        }

        @Test
        void thenFindAllLinkedToAccessoryIsEmpty() {
            assertThat(tagRepository.findAllAccessoryTags())
                    .isEmpty();
        }
    }

    @Nested
    class GivenOneTagLinkedToAccessory {

        private Tag tag;

        @BeforeEach
        void before() throws IOException {
            tag = mock(Tag.class);
            when(tag.isAccessoryTag())
                    .thenReturn(true);
            when(objectMapper.readValue(nullable(File.class), any(TypeReference.class)))
                    .thenReturn(List.of(tag));
            tagRepository = new TagRepository(resource, objectMapper);
        }

        @Test
        void thenFindAllLinkedToAccessoryContainsExpected() {
            assertThat(tagRepository.findAllAccessoryTags())
                    .contains(tag);
        }
    }

    @Nested
    class GivenOneTagLinkedToAccessoryAndOneNotLinked {

        private Tag expectedTag;

        @BeforeEach
        void before() throws IOException {
            expectedTag = mock(Tag.class);
            when(expectedTag.isAccessoryTag())
                    .thenReturn(true);
            var notLinkedTagDto = mock(Tag.class);
            when(notLinkedTagDto.isAccessoryTag())
                    .thenReturn(false);
            when(objectMapper.readValue(nullable(File.class), any(TypeReference.class)))
                    .thenReturn(List.of(expectedTag, notLinkedTagDto));
            tagRepository = new TagRepository(resource, objectMapper);
        }

        @Test
        void thenFindAllLinkedToAccessoryContainsOnlyExpected() {
            assertThat(tagRepository.findAllAccessoryTags())
                    .containsOnly(expectedTag);
        }
    }

    @Nested
    class IntegrationTest {

        @BeforeEach
        void before() {
            var objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            tagRepository = new TagRepository(
                    new ClassPathResource("/poe.gamedata/tables/English/Tags.json"),
                    objectMapper
            );
        }

        @Test
        void thenFindAllLinkedToAccessoryReturnsMoreThanOneElement() {
            assertThat(tagRepository.findAllAccessoryTags())
                    .hasSizeGreaterThan(0);
        }
    }
}
