package poe.publicstash.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import poe.model.Price;

import static org.assertj.core.api.Assertions.assertThat;

class PriceTest {

    @Nested
    class GivenNullPrice {

        @Test
        void thenResultIsEmpty() {
            assertThat(Price.of(null))
                    .isEmpty();
        }
    }

    @Nested
    class GivenNonValidPrice {

        @Test
        void thenResultIsEmpty() {
            assertThat(Price.of("sell for chaos 12"))
                    .isEmpty();
        }
    }

    @Nested
    class GivenValidPrice {

        @Test
        void thenResultIsEmpty() {
            assertThat(Price.of("~price 10 chaos"))
                    .contains(new Price("10", "chaos"));
        }
    }
}
