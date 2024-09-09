package poeai.currency;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import poeai.currency.model.Currencies;
import poeai.currency.model.CurrencyDetail;
import poeai.currency.model.CurrencyRate;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRepositoryTest {

    private static final Integer EXALT_INDEX = 12;

    private static final String EXALT_TRADE_ID = "exalted";

    private static final String CHAOS_TRADE_ID = "chaos";

    private CurrencyRepository currencyRepository;
    @Mock
    private Resource firstResource;
    @Mock
    private Resource secondResource;
    @Mock
    private ObjectMapper objectMapper;

    @Nested
    class GivenNoCurrencies {

        @BeforeEach
        void before() {
            currencyRepository = new CurrencyRepository(List.of(), objectMapper);
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
    class GivenCurrencyDetail_WithoutTradeId {

        private Currencies currencies;

        @BeforeEach
        void before() throws IOException {
            currencies = mock(Currencies.class);
            var currencyDetailDto = mock(CurrencyDetail.class);
            when(currencies.detailsStream())
                    .thenReturn(Stream.of(currencyDetailDto));
            when(objectMapper.readValue(nullable(File.class), eq(Currencies.class)))
                    .thenReturn(currencies);
            currencyRepository = new CurrencyRepository(List.of(firstResource), objectMapper);
        }

        @Nested
        class WhenFindBy {

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
        }
    }

    @Nested
    class GivenCurrency_exalted_WithTradeId {

        private Currencies currencies;

        private CurrencyDetail exaltedCurrencyDetail;

        @BeforeEach
        void before() {
            currencies = mock(Currencies.class);
            exaltedCurrencyDetail = mock(CurrencyDetail.class);
            when(exaltedCurrencyDetail.hasTradeId())
                    .thenReturn(true);
            when(exaltedCurrencyDetail.index())
                    .thenReturn(EXALT_INDEX);
            when(currencies.detailsStream())
                    .thenReturn(Stream.of(exaltedCurrencyDetail));
        }

        @Nested
        class GivenNoRateFor_exalted {

            @Nested
            class WhenFindBy_exalt {

                private AbstractThrowableAssert<?, ? extends Throwable> call;

                @BeforeEach
                void before() throws IOException {
                    when(objectMapper.readValue(nullable(File.class), eq(Currencies.class)))
                            .thenReturn(currencies);
                    currencyRepository = new CurrencyRepository(List.of(firstResource), objectMapper);
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
            void before() throws IOException {
                when(exaltedCurrencyDetail.tradeId())
                        .thenReturn(EXALT_TRADE_ID);

                var currencyRateDto = mock(CurrencyRate.class);
                when(currencyRateDto.chaosEquivalent())
                        .thenReturn(325.5);
                when(currencyRateDto.index())
                        .thenReturn(EXALT_INDEX);
                when(currencies.lines())
                        .thenReturn(List.of(currencyRateDto));
                when(objectMapper.readValue(nullable(File.class), eq(Currencies.class)))
                        .thenReturn(currencies);
                currencyRepository = new CurrencyRepository(List.of(firstResource), objectMapper);
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
            void before() throws IOException {
                when(exaltedCurrencyDetail.tradeId())
                        .thenReturn(EXALT_TRADE_ID);

                var currencyRateDto = mock(CurrencyRate.class);
                when(currencyRateDto.chaosEquivalent())
                        .thenReturn(325.5);
                when(currencyRateDto.index())
                        .thenReturn(EXALT_INDEX);

                var otherCurrenciesDto = mock(Currencies.class);
                when(otherCurrenciesDto.lines())
                        .thenReturn(List.of(currencyRateDto));

                when(objectMapper.readValue(nullable(File.class), eq(Currencies.class)))
                        .thenReturn(currencies, otherCurrenciesDto);

                currencyRepository = new CurrencyRepository(List.of(firstResource, secondResource), objectMapper);
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

        private static final List<String> MOST_USED_CURRENCY_TRADE_IDS = List.of(
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

        private CurrencyRepository repository;

        @BeforeEach
        void before() throws IOException {
            var objectMapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            var resources = Stream.of(ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader())
                            .getResources("classpath:/poe.ninja/*.json"))
                    .toList();
            repository = new CurrencyRepository(resources, objectMapper);
        }

        @ParameterizedTest
        @FieldSource(value = "poeai.currency.CurrencyRepositoryTest$IntegrationTest#MOST_USED_CURRENCY_TRADE_IDS")
        void thenMostUsedCurrenciesAreFound(String currencyTradeId) {
            assertThat(repository.findById(currencyTradeId))
                    .isNotNull();
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
