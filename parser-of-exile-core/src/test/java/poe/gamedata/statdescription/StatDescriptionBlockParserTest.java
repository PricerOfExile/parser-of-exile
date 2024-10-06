package poe.gamedata.statdescription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static poe.gamedata.statdescription.StatDescriptionBlockParser.StatDescriptionGathererState.*;

class StatDescriptionBlockParserTest {

    private StatDescriptionBlockParser parser;

    @Nested
    class GivenInitialState {

        @BeforeEach
        void before() {
            parser = new StatDescriptionBlockParser();
        }

        @Nested
        class WhenReceivingDescriptionLine {

            @BeforeEach
            void before() {
                parser.parse("description");
            }

            @Test
            void thenStateIsWaitStatIdList() {
                assertThat(parser.getState())
                        .isEqualTo(WAIT_STAT_ID_LIST);
            }
        }

        @Nested
        class WhenReceivingNotDescriptionLine {

            @BeforeEach
            void before() {
                parser.parse("not description");
            }

            @Test
            void thenStateIsWaitStartingBlock() {
                assertThat(parser.getState())
                        .isEqualTo(WAIT_STARTING_BLOCK);
            }
        }
    }

    @Nested
    class GivenWaitStatIdListState {

        @BeforeEach
        void before() {
            parser = new StatDescriptionBlockParser()
                    .parse("description");
        }

        @Nested
        class WhenReceivingValidLine {

            @BeforeEach
            void before() {
                parser.parse("1 this_is_my_id");
            }

            @Test
            void thenStateIsWaitDescriptionNumbers() {
                assertThat(parser.getState())
                        .isEqualTo(WAIT_DESCRIPTIONS_NUMBER);
            }
        }
    }

    @Nested
    class GivenWaitDescriptionsNumberState {

        @BeforeEach
        void before() {
            parser = new StatDescriptionBlockParser()
                    .parse("description")
                    .parse("1 this_is_my_id");
        }

        @Nested
        class WhenReceivingValidLine {

            @BeforeEach
            void before() {
                parser.parse("2");
            }

            @Test
            void thenStateIsWaitDescriptionNumbers() {
                assertThat(parser.getState())
                        .isEqualTo(WAIT_DESCRIPTION);
            }
        }
    }

    @Nested
    class GivenWait2DescriptionToEndBlock {

        @BeforeEach
        void before() {
            parser = new StatDescriptionBlockParser()
                    .parse("description")
                    .parse("1 this_is_my_id")
                    .parse("2");
        }

        @Nested
        class WhenReceivingOneValidLine {

            @BeforeEach
            void before() {
                parser.parse("This is the first description");
            }

            @Test
            void thenStateIsWaitDescriptionNumbers() {
                assertThat(parser.getState())
                        .isEqualTo(WAIT_DESCRIPTION);
            }
        }

        @Nested
        class WhenReceivingTwoValidLine {

            @BeforeEach
            void before() {
                parser.parse("This is the first description")
                        .parse("This is the second description");
            }

            @Test
            void thenStateIsWaitDescriptionNumbers() {
                assertThat(parser.getState())
                        .isEqualTo(WAIT_STARTING_BLOCK);
            }

            @Nested
            class WhenClosing {

                private List<StatDescriptionBlock> results;

                @BeforeEach
                void before() {
                    results = parser.close();
                }

                @Test
                void thenResultsContainsOneElement() {
                    assertThat(results)
                            .hasSize(1);
                }

                @Test
                void thenCallingParseThrowsException() {
                    assertThatThrownBy(() -> parser.parse("Anything"))
                            .isInstanceOf(IllegalStateException.class);
                }
            }
        }
    }
}
