package poeai.stat.tables.English;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatDtoRepositoryTest {

    private static final int STAT_INDEX = 154;

    private StatDtoRepository statDtoRepository;

    @Nested
    class GivenNoStatDto {

        @BeforeEach
        void before() {
            statDtoRepository = new StatDtoRepository(List.of());
        }

        @Test
        void thenFindByIndexThrowsNoSuchElementException() {
            assertThatThrownBy(() -> statDtoRepository.getByIndex(STAT_INDEX))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Stat with index [154] does not exists.");
        }
    }

    @Nested
    class GivenStatDtoWithIndex154 {

        private StatDto expectedStatDto;

        @BeforeEach
        void before() {
            expectedStatDto = mock(StatDto.class);
            when(expectedStatDto.index())
                    .thenReturn(STAT_INDEX);
            statDtoRepository = new StatDtoRepository(List.of(expectedStatDto));
        }

        @Test
        void thenFindByIndexReturnExpectedStatDto() {
            assertThat(statDtoRepository.getByIndex(STAT_INDEX))
                    .isSameAs(expectedStatDto);
        }
    }

    @Nested
    class GivenStatDtoWithoutIndex154 {

        @BeforeEach
        void before() {
            StatDto expectedStatDto = mock(StatDto.class);
            when(expectedStatDto.index())
                    .thenReturn(STAT_INDEX + 1);
            statDtoRepository = new StatDtoRepository(List.of(expectedStatDto));
        }

        @Test
        void thenFindByIndexThrowsNoSuchElementException() {
            assertThatThrownBy(() -> statDtoRepository.getByIndex(STAT_INDEX))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Stat with index [154] does not exists.");
        }
    }

    @Nested
    class IntegrationTest {

        @BeforeEach
        void before() throws IOException {
            TypeReference<List<StatDto>> listStatDtoType = new TypeReference<>() {
            };
            var statsFile = this.getClass().getResourceAsStream("Stats.json");
            statDtoRepository = new StatDtoRepository(new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(statsFile, listStatDtoType));
        }

        @Test
        void thenFindByIndexReturns() {
            IntStream.range(0, 20821)
                    .forEach(index -> {
                        assertThat(statDtoRepository.getByIndex(index))
                                .isNotNull()
                                .extracting(StatDto::index)
                                .isEqualTo(index);
                    });
        }
    }
}
