package poeai.stat.tables.English;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TagDtoRepositoryTest {

    private TagDtoRepository tagDtoRepository;

    @Nested
    class GivenEmptyListTagDtos {

        @BeforeEach
        void before() {
            tagDtoRepository = new TagDtoRepository(List.of());
        }

        @Test
        void thenFindAllLinkedToAccessoryIsEmpty() {
            assertThat(tagDtoRepository.findAllAccessoryTags())
                    .isEmpty();
        }
    }

    @Nested
    class GivenOneTagDtoNOTLinkedToAccessory {

        @BeforeEach
        void before() {
            var tagDto = mock(TagDto.class);
            when(tagDto.isAccessoryTag())
                    .thenReturn(false);
            tagDtoRepository = new TagDtoRepository(List.of(tagDto));
        }

        @Test
        void thenFindAllLinkedToAccessoryIsEmpty() {
            assertThat(tagDtoRepository.findAllAccessoryTags())
                    .isEmpty();
        }
    }

    @Nested
    class GivenOneTagDtoLinkedToAccessory {

        private TagDto tagDto;

        @BeforeEach
        void before() {
            tagDto = mock(TagDto.class);
            when(tagDto.isAccessoryTag())
                    .thenReturn(true);
            tagDtoRepository = new TagDtoRepository(List.of(tagDto));
        }

        @Test
        void thenFindAllLinkedToAccessoryContainsExpected() {
            assertThat(tagDtoRepository.findAllAccessoryTags())
                    .contains(tagDto);
        }
    }

    @Nested
    class GivenOneTagDtoLinkedToAccessoryAndOneNotLinked {

        private TagDto expectedTagDto;

        @BeforeEach
        void before() {
            expectedTagDto = mock(TagDto.class);
            when(expectedTagDto.isAccessoryTag())
                    .thenReturn(true);
            var notLinkedTagDto = mock(TagDto.class);
            when(notLinkedTagDto.isAccessoryTag())
                    .thenReturn(false);
            tagDtoRepository = new TagDtoRepository(List.of(expectedTagDto, notLinkedTagDto));
        }

        @Test
        void thenFindAllLinkedToAccessoryContainsOnlyExpected() {
            assertThat(tagDtoRepository.findAllAccessoryTags())
                    .containsOnly(expectedTagDto);
        }
    }

    @Nested
    class IntegrationTest {

        @BeforeEach
        void before() throws IOException {
            TypeReference<List<TagDto>> listTagDtoType = new TypeReference<>() {
            };
            var tagsFile = this.getClass().getResourceAsStream("Tags.json");
            tagDtoRepository = new TagDtoRepository(new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(tagsFile, listTagDtoType));
        }

        @Test
        void thenFindAllLinkedToAccessoryReturnsMoreThanOneElement() {
            assertThat(tagDtoRepository.findAllAccessoryTags())
                    .hasSizeGreaterThan(0);
        }
    }
}
