package poe.gamedata.statdescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValuatedStatDescriptionTest {

    private StatDescription statDescription;

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
                statDescription = new StatDescription(
                        new Description("#", "Cannot be Ignited", ""),
                        List.of("base_cannot_be_ignited")
                );
            }

            @Test
            void thenValidSentenceWith10PercentReturnsFilledStat() {
                assertThat(statDescription.valuateDisplayedMod("Cannot be Ignited"))
                        .contains(new ValuatedStat("base_cannot_be_ignited", 1.));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(statDescription.valuateDisplayedMod("Cannot be NOT Ignited"))
                        .isEmpty();
            }
        }

        @Nested
        class GivenThisValueIsPercent {

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

            @BeforeEach
            void before() {
                statDescription = new StatDescription(
                        new Description("#", "{0}% Chance to Block Attack Damage", ""),
                        List.of("monster_base_block_%")
                );
            }

            @Test
            void thenValidSentenceWith10PercentReturnsFilledStat() {
                assertThat(statDescription.valuateDisplayedMod("+10% Chance to Block Attack Damage"))
                        .contains(new ValuatedStat("monster_base_block_%", 10.));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(statDescription.valuateDisplayedMod("10% Another Sentence"))
                        .isEmpty();
            }
        }

        @Nested
        class GivenThisValueIsPlusD {

            /*
            {
                "description": {
                    "first": "#",
                    "second": "{0:+d} to Strength",
                    "third": ""
                },
                "idsLine": {
                    "nbOfIds": 1,
                    "ids": [
                        "additional_strength"
                    ]
                }
            },
             */

            @BeforeEach
            void before() {
                statDescription = new StatDescription(
                        new Description("#", "{0:+d} to Strength", ""),
                        List.of("additional_strength")
                );
            }

            @Test
            void thenValidSentenceWith10PercentReturnsFilledStat() {
                assertThat(statDescription.valuateDisplayedMod("+25 to Strength"))
                        .contains(new ValuatedStat("additional_strength", 25.));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(statDescription.valuateDisplayedMod("+5 to Another Sentence"))
                        .isEmpty();
            }
        }
    }

    @Nested
    class GivenOneOnly {
        /*
        {
            "description": {
                "first": "1",
                "second": "You can apply an additional Curse",
                "third": ""
            },
            "idsLine": {
                "nbOfIds": 1,
                "ids": [
                    "number_of_additional_curses_allowed"
                ]
            }
        },
         */
        @BeforeEach
        void before() {
            statDescription = new StatDescription(
                    new Description("1", "You can apply one additional Curse", ""),
                    List.of("number_of_additional_curses_allowed")
            );
        }

        @Test
        void thenValidSentenceReturnsFilledStat() {
            assertThat(statDescription.valuateDisplayedMod("You can apply one additional Curse"))
                    .contains(new ValuatedStat("number_of_additional_curses_allowed", 1.));
        }
    }

    @Nested
    class GivenMinusOneOnly {

        /*
        {
            "description": {
                "first": "-1",
                "second": "You can apply one fewer Curse",
                "third": ""
            },
            "idsLine": {
                "nbOfIds": 1,
                "ids": [
                    "number_of_additional_curses_allowed"
                ]
            }
        },
         */
        @BeforeEach
        void before() {
            statDescription = new StatDescription(
                    new Description("-1", "You can apply one fewer Curse", ""),
                    List.of("number_of_additional_curses_allowed")
            );
        }

        @Test
        void thenValidSentenceReturnsFilledStat() {
            assertThat(statDescription.valuateDisplayedMod("You can apply one fewer Curse"))
                    .contains(new ValuatedStat("number_of_additional_curses_allowed", -1.));
        }

        @Test
        void thenNonValidSentence() {
            assertThat(statDescription.valuateDisplayedMod("You can apply one additional Curse"))
                    .isEmpty();
        }

    }

    @Nested
    class GivenOneHashAndPlusOrMinusOne {

        @Nested
        class GivenThisValueIsPositive {

            /*
            {
                "description": {
                    "first": "1|#",
                    "second": "{0}% increased Chaos Damage",
                    "third": ""
                },
                "idsLine": {
                    "nbOfIds": 1,
                    "ids": [
                        "chaos_damage_+%"
                    ]
                }
            },
             */

            @BeforeEach
            void before() {
                statDescription = new StatDescription(
                        new Description("1|#", "{0}% increased Chaos Damage", ""),
                        List.of("chaos_damage_+%")
                );
            }

            @Test
            void thenValidSentenceReturnsFilledStat() {
                assertThat(statDescription.valuateDisplayedMod("62% increased Chaos Damage"))
                        .contains(new ValuatedStat("chaos_damage_+%", 62.));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(statDescription.valuateDisplayedMod("62% reduced Chaos Damage"))
                        .isEmpty();
            }
        }

        @Nested
        class GivenThisValueIsNegative {

            /*
            {
                "description": {
                    "first": "#|-1",
                    "second": "{0}% reduced Chaos Damage",
                    "third": "negate 1"
                },
                "idsLine": {
                    "nbOfIds": 1,
                    "ids": [
                        "chaos_damage_+%"
                    ]
                }
            },
             */

            @BeforeEach
            void before() {
                statDescription = new StatDescription(
                        new Description("#|-1", "{0}% reduced Chaos Damage", "negate 1"),
                        List.of("chaos_damage_+%")
                );
            }

            @Test
            void thenValidSentenceReturnsFilledStat() {
                assertThat(statDescription.valuateDisplayedMod("62% reduced Chaos Damage"))
                        .contains(new ValuatedStat("chaos_damage_+%", -62.));
            }

            @Test
            void thenNonValidSentenceWith10Percent() {
                assertThat(statDescription.valuateDisplayedMod("62% increased Chaos Damage"))
                        .isEmpty();
            }
        }
    }

    @Nested
    class GivenTwoAndOneHashTag {

        /*
        {
            "description": {
                "first": "2|#",
                "second": "You can apply {0} additional Curses",
                "third": "canonical_line"
            },
            "idsLine": {
                "nbOfIds": 1,
                "ids": [
                    "number_of_additional_curses_allowed"
                ]
            }
        },
         */

        @BeforeEach
        void before() {
            statDescription = new StatDescription(
                    new Description("2|#", "You can apply {0} additional Curses", "canonical_line"),
                    List.of("number_of_additional_curses_allowed")
            );
        }

        @Test
        void thenValidSentenceReturnsFilledStat() {
            assertThat(statDescription.valuateDisplayedMod("You can apply 7 additional Curses"))
                    .contains(new ValuatedStat("number_of_additional_curses_allowed", 7.));
        }
    }

    @Nested
    class GivenTwoHashTags {

        @Nested
        class GivenTheseAreTwoValues {

            /*
            {
                "description": {
                    "first": "# #",
                    "second": "{0} to {1} Added Cold Damage per Frenzy Charge",
                    "third": ""
                },
                "idsLine": {
                    "nbOfIds": 2,
                    "ids": [
                        "minimum_added_cold_damage_per_frenzy_charge",
                        "maximum_added_cold_damage_per_frenzy_charge"
                    ]
                }
            },
             */

            @BeforeEach
            void before() {
                statDescription = new StatDescription(
                        new Description("# #", "{0} to {1} Added Cold Damage per Frenzy Charge", ""),
                        List.of("minimum_added_cold_damage_per_frenzy_charge", "maximum_added_cold_damage_per_frenzy_charge")
                );
            }

            @Test
            void thenValidSentenceReturnsFilledStat() {
                assertThat(statDescription.valuateDisplayedMod("5 to 18 Added Cold Damage per Frenzy Charge"))
                        .contains(new ValuatedStat("minimum_added_cold_damage_per_frenzy_charge", 5.))
                        .contains(new ValuatedStat("maximum_added_cold_damage_per_frenzy_charge", 18.));
            }
        }
    }
}
