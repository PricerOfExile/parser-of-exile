package poeai.gamedata.statdescription;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineParserHelperTest {

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
