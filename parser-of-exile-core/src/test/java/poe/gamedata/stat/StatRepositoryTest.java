package poe.gamedata.stat;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatRepositoryTest {

    private static final int STAT_INDEX = 154;

    private StatRepository statRepository;
    @Mock
    private Resource statResource;
    @Mock
    private ObjectMapper objectMapper;

    @Nested
    class GivenNoValuatedStat {

        @BeforeEach
        void before() throws IOException {
            when(objectMapper.readValue(nullable(InputStream.class), any(TypeReference.class)))
                    .thenReturn(List.of());
            statRepository = new StatRepository(statResource, objectMapper);
        }

        @Test
        void thenFindByIndexThrowsNoSuchElementException() {
            assertThatThrownBy(() -> statRepository.getByIndex(STAT_INDEX))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Stat with index [154] does not exists.");
        }
    }

    @Nested
    class GivenValuatedStatWithIndex154 {

        private Stat expectedStat;

        @BeforeEach
        void before() throws IOException {
            expectedStat = mock(Stat.class);
            when(expectedStat.index())
                    .thenReturn(STAT_INDEX);
            when(objectMapper.readValue(nullable(InputStream.class), any(TypeReference.class)))
                    .thenReturn(List.of(expectedStat));
            statRepository = new StatRepository(statResource, objectMapper);
        }

        @Test
        void thenFindByIndexReturnExpectedStatDto() {
            assertThat(statRepository.getByIndex(STAT_INDEX))
                    .isSameAs(expectedStat);
        }
    }

    @Nested
    class GivenValuatedStatWithoutIndex154 {

        @BeforeEach
        void before() throws IOException {
            Stat expectedStat = mock(Stat.class);
            when(expectedStat.index())
                    .thenReturn(STAT_INDEX + 1);
            when(objectMapper.readValue(nullable(InputStream.class), any(TypeReference.class)))
                    .thenReturn(List.of(expectedStat));
            statRepository = new StatRepository(statResource, objectMapper);
        }

        @Test
        void thenFindByIndexThrowsNoSuchElementException() {
            assertThatThrownBy(() -> statRepository.getByIndex(STAT_INDEX))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Stat with index [154] does not exists.");
        }
    }

    @Nested
    class IntegrationTest {

        @BeforeEach
        void before() {
            statRepository = new StatRepository(
                    new ClassPathResource("/poe.gamedata/tables/English/Stats.json"),
                    new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            );
        }

        @Test
        void thenFindByIndexReturns() {
            IntStream.range(0, 20821)
                    .forEach(index -> {
                        assertThat(statRepository.getByIndex(index))
                                .isNotNull()
                                .extracting(Stat::index)
                                .isEqualTo(index);
                    });
        }
    }
}
