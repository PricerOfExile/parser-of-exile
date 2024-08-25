package poeai.stat.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DescriptionToStatsTest {

    /*{
        "description": {
            "first": "#",
            "second": "{0}% Chance to Block Attack Damage",
            "third": ""
        },
        "idsLine": {
            "nbOfIds": 1,
            "ids": [
                "monster_base_block_%"
            ]
        }
    }*/

    private DescriptionToStats descriptionToStats;

    @Nested
    class GivenOneHashtagDescription {

        @Nested
        class GivenNoValue {

            /*{
                "description": {
                    "first": "#",
                    "second": "Cannot be Ignited",
                    "third": ""
                },
                "idsLine": {
                    "nbOfIds": 1,
                    "ids": [
                        "base_cannot_be_ignited"
                    ]
                }
            },*/
            @BeforeEach
            void before() {
                descriptionToStats = new DescriptionToStats(
                        new StatDescriptionLine("#", "Cannot be Ignited", ""),
                        new IdsLine(1, List.of("base_cannot_be_ignited"))
                );
            }

            @Test
            void thenValidSentenceWith10PercentReturnsFilledStat() {
                assertThat(descriptionToStats.transform("Cannot be Ignited"))
                        .contains(new Stat("base_cannot_be_ignited", 1));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(descriptionToStats.transform("10% Another Sentence"))
                        .isEmpty();
            }
        }

        @Nested
        class GivenThisValueIsPercent {
            @BeforeEach
            void before() {
                descriptionToStats = new DescriptionToStats(
                        new StatDescriptionLine("#", "{0}% Chance to Block Attack Damage", ""),
                        new IdsLine(1, List.of("monster_base_block_%"))
                );
            }

            @Test
            void thenValidSentenceWith10PercentReturnsFilledStat() {
                assertThat(descriptionToStats.transform("+10% Chance to Block Attack Damage"))
                        .contains(new Stat("monster_base_block_%", 10));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(descriptionToStats.transform("10% Another Sentence"))
                        .isEmpty();
            }
        }

        @Nested
        class GivenThisValueIsPlusD {

            @BeforeEach
            void before() {
                descriptionToStats = new DescriptionToStats(
                        new StatDescriptionLine("#", "{0:+d} to Strength", ""),
                        new IdsLine(1, List.of("additional_strength"))
                );
            }

            @Test
            void thenValidSentenceWith10PercentReturnsFilledStat() {
                assertThat(descriptionToStats.transform("+25 to Strength"))
                        .contains(new Stat("additional_strength", 25));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(descriptionToStats.transform("+5 to Another Sentence"))
                        .isEmpty();
            }
        }
    }
}
