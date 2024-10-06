package poe.gamedata.statdescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import poe.gamedata.stat.Stat;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessoryStatValuatorTest {

    private static final String DESCRIPTION_LINE = "ANY_LINE";

    private AccessoryStatValuator valuator;
    @Mock
    private StatDescriptionRepository statDescriptionRepository;
    @Mock
    private AccessoryStatCatalog accessoryStatCatalog;

    @Nested
    class GivenNoStatLinkedToAccessory {

        @Nested
        class WhenCallingValuate {

            @BeforeEach
            void before() {
                valuator = new AccessoryStatValuator(statDescriptionRepository, accessoryStatCatalog);
            }

            @Test
            void thenResultIsEmpty() {
                assertThat(valuator.valuateDisplayedMod(DESCRIPTION_LINE))
                        .isEmpty();
            }
        }
    }

    @Nested
    class GivenOneStatDescriptionLinkedToAccessory {

        private ValuatedStat expectedValuatedStat;

        @BeforeEach
        void before() {
            when(accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated())
                    .thenReturn(List.of(mock(Stat.class)));
            var statDescription = mock(StatDescription.class);
            expectedValuatedStat = mock(ValuatedStat.class);
            when(statDescription.valuateDisplayedMod(anyString()))
                    .thenReturn(List.of(expectedValuatedStat));
            when(statDescriptionRepository.findAllLinkedTo(anyList()))
                    .thenReturn(List.of(statDescription));

            valuator = new AccessoryStatValuator(statDescriptionRepository, accessoryStatCatalog);
        }

        @Test
        void thenResultContainsExpected() {
            assertThat(valuator.valuateDisplayedMod(DESCRIPTION_LINE))
                    .contains(expectedValuatedStat);
        }
    }

    @Nested
    class GivenOneStatsDescriptionReturningTwoValuatedStats {

        private ValuatedStat firstStat;
        private ValuatedStat secondStat;

        @BeforeEach
        void before() {
            when(accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated())
                    .thenReturn(List.of(mock(Stat.class)));
            var statDescription = mock(StatDescription.class);
            firstStat = mock(ValuatedStat.class);
            secondStat = mock(ValuatedStat.class);
            when(statDescription.valuateDisplayedMod(anyString()))
                    .thenReturn(List.of(firstStat, secondStat));
            when(statDescriptionRepository.findAllLinkedTo(anyList()))
                    .thenReturn(List.of(statDescription));

            valuator = new AccessoryStatValuator(statDescriptionRepository, accessoryStatCatalog);
        }

        @Test
        void thenResultContainsExpected() {
            assertThat(valuator.valuateDisplayedMod(DESCRIPTION_LINE))
                    .contains(firstStat, secondStat);
        }
    }

    @Nested
    class GivenTwoStatsDescriptionReturningOneValuatedStatsEach {

        private ValuatedStat firstStat;
        private ValuatedStat secondStat;

        @BeforeEach
        void before() {
            when(accessoryStatCatalog.findAllNonUniqueAndAccessoryRelated())
                    .thenReturn(List.of(mock(Stat.class)));
            var first = mock(StatDescription.class);
            var second = mock(StatDescription.class);
            firstStat = mock(ValuatedStat.class);
            secondStat = mock(ValuatedStat.class);
            when(first.valuateDisplayedMod(anyString()))
                    .thenReturn(List.of(firstStat));
            when(second.valuateDisplayedMod(anyString()))
                    .thenReturn(List.of(secondStat));
            when(statDescriptionRepository.findAllLinkedTo(anyList()))
                    .thenReturn(List.of(first, second));

            valuator = new AccessoryStatValuator(statDescriptionRepository, accessoryStatCatalog);
        }

        @Test
        void thenResultContainsExpected() {
            assertThat(valuator.valuateDisplayedMod(DESCRIPTION_LINE))
                    .contains(firstStat, secondStat);
        }
    }
}
