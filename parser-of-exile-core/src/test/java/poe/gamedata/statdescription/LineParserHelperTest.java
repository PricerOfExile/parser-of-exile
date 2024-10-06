package poe.gamedata.statdescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineParserHelperTest {

    @Nested
    class GivenStatIds {

        @Nested
        class GivenLine_withOneId {

            @Test
            void thenResultHasOneId() {
                assertThat(LineParserHelper.parseStatIds("\t1 damage_with_lightning_skills_+%"))
                        .isEqualTo(List.of("damage_with_lightning_skills_+%"));

            }
        }

        @Nested
        class GivenLine_withTwoIds {

            @Test
            void thenResultHasOneId() {
                assertThat(LineParserHelper.parseStatIds("\t2 global_minimum_added_chaos_damage global_maximum_added_chaos_damage"))
                        .isEqualTo(List.of(
                                "global_minimum_added_chaos_damage",
                                "global_maximum_added_chaos_damage"
                        ));

            }
        }

        @Nested
        class GivenLine_withThreeIds {

            @Test
            void thenResultHasOneId() {
                assertThat(LineParserHelper.parseStatIds("\t3 spell_minimum_base_fire_damage spell_maximum_base_fire_damage spell_base_fire_damage_%_maximum_life"))
                        .isEqualTo(List.of(
                                "spell_minimum_base_fire_damage",
                                "spell_maximum_base_fire_damage",
                                "spell_base_fire_damage_%_maximum_life"
                        ));
            }
        }

        @Nested
        class GivenLineWithoutCorrectNbOfIds {

            @Test
            void thenResultHasOneId() {
                assertThatThrownBy(() -> LineParserHelper.parseStatIds("\t1 spell_minimum_base_fire_damage spell_maximum_base_fire_damage spell_base_fire_damage_%_maximum_life"))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    class GivenDescription {

        @Nested
        class GivenLineWithoutQuote {

            private static final String LINE_WITHOUT_QUOTE = "Testing";

            private Description description;

            @BeforeEach
            void before() {
                description = LineParserHelper.parseDescription(LINE_WITHOUT_QUOTE);
            }


            @Test
            void thenDescriptionValueHintIsTesting() {
                assertThat(description.valueHint())
                        .isEqualTo(LINE_WITHOUT_QUOTE);
            }

            @Test
            void thenDescriptionDisplayPatternIsEmpty() {
                assertThat(description.displayPattern())
                        .isEmpty();
            }

            @Test
            void thenDescriptionMarkersIsEmpty() {
                assertThat(description.markers())
                        .isEmpty();
            }
        }

        @Nested
        class GivenLineWithOneQuote {

            private static final String LINE_WITH_ONE_QUOTE = "Testing\"Working";

            private Description description;

            @BeforeEach
            void before() {
                description = LineParserHelper.parseDescription(LINE_WITH_ONE_QUOTE);
            }


            @Test
            void thenDescriptionValueHintIsTesting() {
                assertThat(description.valueHint())
                        .isEqualTo("Testing");
            }

            @Test
            void thenDescriptionDisplayPatternIsWorking() {
                assertThat(description.displayPattern())
                        .isEqualTo("Working");
            }

            @Test
            void thenDescriptionMarkersIsEmpty() {
                assertThat(description.markers())
                        .isEmpty();
            }
        }


        @Nested
        class GivenLineWithTwoQuote {

            private static final String LINE_WITH_TWO_QUOTE = "Testing\"Working\"Another";

            private Description description;

            @BeforeEach
            void before() {
                description = LineParserHelper.parseDescription(LINE_WITH_TWO_QUOTE);
            }


            @Test
            void thenDescriptionValueHintIsTesting() {
                assertThat(description.valueHint())
                        .isEqualTo("Testing");
            }

            @Test
            void thenDescriptionDisplayPatternIsWorking() {
                assertThat(description.displayPattern())
                        .isEqualTo("Working");
            }

            @Test
            void thenDescriptionMarkersIsAnother() {
                assertThat(description.markers())
                        .isEqualTo("Another");
            }
        }
    }

}
