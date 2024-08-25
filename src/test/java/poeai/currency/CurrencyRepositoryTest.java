package poeai.currency;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import poeai.currency.dto.CurrenciesDto;
import poeai.currency.dto.CurrencyDetailDto;
import poeai.currency.dto.CurrencyRateDto;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CurrencyRepositoryTest {

    private static final Integer EXALT_ID = 12;

    private static final String EXALT_TRADE_ID = "exalted";

    private static final String CHAOS_TRADE_ID = "chaos";

    private CurrencyRepository currencyRepository;

    @Nested
    class GivenNoCurrenciesDto {

        @BeforeEach
        void before() {
            currencyRepository = new CurrencyRepository(List.of());
        }

        @Test
        void thenFindById_chaos_returnsCurrency() {
            assertThat(currencyRepository.findById("chaos"))
                    .isEqualTo(new Currency("chaos", 1));
        }

        @Test
        void thenFindById_other_raiseException() {
            assertThatThrownBy(() -> currencyRepository.findById("any_other_currency"))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Currency [any_other_currency] Not Found");
        }
    }

    @Nested
    class GivenCurrency_exalt_WithoutTradeId {

        private CurrenciesDto currenciesDto;

        @BeforeEach
        void before() {
            currenciesDto = mock(CurrenciesDto.class);
            var currencyDetailDto = mock(CurrencyDetailDto.class);
            when(currenciesDto.currencyDetails())
                    .thenReturn(List.of(currencyDetailDto));
            currencyRepository = new CurrencyRepository(List.of(currenciesDto));
        }

        @Nested
        class WhenFindBy_exalt {

            private AbstractThrowableAssert<?, ? extends Throwable> call;

            @BeforeEach
            void before() {
                call = assertThatThrownBy(() -> currencyRepository.findById(EXALT_TRADE_ID));
            }

            @Test
            void thenItThrowsException() {
                call.isInstanceOf(NoSuchElementException.class)
                        .hasMessage("Currency [exalted] Not Found");
            }

            @Test
            void thenFindRateByTradeIdIsNeverCalled() {
                verify(currenciesDto, never()).findRateByTradeId(anyInt());
            }
        }
    }

    @Nested
    class GivenCurrency_exalted_WithTradeId {

        private CurrenciesDto currenciesDto;

        @BeforeEach
        void before() {
            currenciesDto = mock(CurrenciesDto.class);
            var currencyDetailDto = mock(CurrencyDetailDto.class);
            when(currencyDetailDto.tradeId())
                    .thenReturn(EXALT_TRADE_ID);
            when(currencyDetailDto.hasTradeId())
                    .thenReturn(true);
            when(currencyDetailDto.id())
                    .thenReturn(EXALT_ID);
            when(currenciesDto.detailsStream())
                    .thenReturn(Stream.of(currencyDetailDto));
        }

        @Nested
        class GivenNoRateFor_exalted {

            @Nested
            class WhenFindBy_exalt {

                private AbstractThrowableAssert<?, ? extends Throwable> call;

                @BeforeEach
                void before() {
                    currencyRepository = new CurrencyRepository(List.of(currenciesDto));
                    call = assertThatThrownBy(() -> currencyRepository.findById(EXALT_TRADE_ID));
                }

                @Test
                void thenItThrowsException() {
                    call.isInstanceOf(NoSuchElementException.class)
                            .hasMessage("Currency [exalted] Not Found");
                }
            }
        }

        @Nested
        class GivenRateFor_exalted_InSameDto {

            @BeforeEach
            void before() {
                var currencyRateDto = mock(CurrencyRateDto.class);
                when(currencyRateDto.chaosEquivalent())
                        .thenReturn(325.5);
                when(currenciesDto.findRateByTradeId(EXALT_ID))
                        .thenReturn(Optional.of(currencyRateDto));

                currencyRepository = new CurrencyRepository(List.of(currenciesDto));
            }

            @Test
            void thenExaltCurrencyIsFound() {
                assertThat(currencyRepository.findById(EXALT_TRADE_ID))
                        .isEqualTo(new Currency(EXALT_TRADE_ID, 325.5));
            }

            @Test
            void then_chaos_CurrencyIsStillFound() {
                assertThat(currencyRepository.findById(CHAOS_TRADE_ID))
                        .isEqualTo(new Currency(CHAOS_TRADE_ID, 1));
            }
        }

        @Nested
        class GivenRateFor_exalted_InOtherDto {

            @BeforeEach
            void before() {
                var currencyRateDto = mock(CurrencyRateDto.class);
                when(currencyRateDto.chaosEquivalent())
                        .thenReturn(325.5);

                var otherDto = mock(CurrenciesDto.class);
                when(otherDto.currencyDetails())
                        .thenReturn(List.of());
                when(otherDto.findRateByTradeId(EXALT_ID))
                        .thenReturn(Optional.of(currencyRateDto));

                currencyRepository = new CurrencyRepository(List.of(currenciesDto, otherDto));
            }

            @Test
            void thenExaltCurrencyIsFound() {
                assertThat(currencyRepository.findById(EXALT_TRADE_ID))
                        .isEqualTo(new Currency(EXALT_TRADE_ID, 325.5));
            }

            @Test
            void then_chaos_CurrencyIsStillFound() {
                assertThat(currencyRepository.findById(CHAOS_TRADE_ID))
                        .isEqualTo(new Currency(CHAOS_TRADE_ID, 1));
            }
        }
    }

    @Nested
    class IntegrationTest {

        private static final List<String> MOST_USED_CURRENCIES = List.of(
                "chaos",
                "exalted",
                "divine",
                "alch",
                "fusing",
                "alt",
                "regal",
                "vaal",
                "regret",
                "chisel",
                "jewellers"
        );

        private CurrenciesDto currenciesDto;

        private CurrenciesDto fragmentsDto;

        private CurrencyRepository repository;

        @BeforeEach
        void before() throws IOException {
            var objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            currenciesDto = objectMapper.readValue(getClass().getResourceAsStream("currencies.json"), CurrenciesDto.class);
            fragmentsDto = objectMapper.readValue(getClass().getResourceAsStream("fragments.json"), CurrenciesDto.class);
            repository = new CurrencyRepository(List.of(
                    currenciesDto,
                    fragmentsDto
            ));
        }

        @Test
        void thenMostUsedCurrenciesAreFound() {
            assertThat(currenciesDto.currencyDetails())
                    .filteredOn(CurrencyDetailDto::hasTradeId)
                    .filteredOn(currencyDetailDto -> MOST_USED_CURRENCIES.contains(currencyDetailDto.tradeId()))
                    .allSatisfy(dto ->
                            assertThat(repository.findById(dto.tradeId()))
                                    .isNotNull()
                    );
        }

        @Test
        void then_chaos_RateIsOne() {
            assertThat(repository.findById(CHAOS_TRADE_ID))
                    .isEqualTo(new Currency(CHAOS_TRADE_ID, 1));
        }

        @Test
        void then_exalted_RateIsFound() {
            assertThat(repository.findById(EXALT_TRADE_ID))
                    .isEqualTo(new Currency(EXALT_TRADE_ID, 5.81));
        }

        @Test
        void then_offer_RateIsFound() {
            assertThat(repository.findById("offer"))
                    .isEqualTo(new Currency("offer", 4.00));
        }
    }
}
