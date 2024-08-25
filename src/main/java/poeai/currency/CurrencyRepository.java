package poeai.currency;

import poeai.currency.dto.CurrenciesDto;
import poeai.currency.dto.CurrencyDetailDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class CurrencyRepository {

    private final Set<Currency> currencies;

    public CurrencyRepository(List<CurrenciesDto> currenciesDtos) {
        currencies = currenciesDtos.stream()
                .flatMap(CurrenciesDto::detailsStream)
                .filter(CurrencyDetailDto::hasTradeId)
                .distinct()
                .map(currencyDetail -> currenciesDtos.stream()
                        .flatMap(currenciesDto -> currenciesDto.findRateByTradeId(currencyDetail.id()).stream())
                        .findFirst()
                        .map(rate -> new Currency(currencyDetail.tradeId(), rate.chaosEquivalent()))
                )
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public Currency findById(String tradeId) {
        if (tradeId.equals("chaos")) {
            return new Currency(tradeId, 1);
        }
        return currencies.stream()
                .filter(currency -> tradeId.equals(currency.tradeId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(format("Currency [%s] Not Found", tradeId)));
    }
}
