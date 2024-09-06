package poeai.currency.dto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record CurrenciesDto(List<CurrencyRateDto> lines,
                            List<CurrencyDetailDto> currencyDetails) {

    public Stream<CurrencyDetailDto> detailsStream() {
        return currencyDetails.stream();
    }

    public Optional<CurrencyRateDto> findRateByTradeId(Integer currencyId) {
        return lines.stream()
                .filter(rateDto -> currencyId.equals(rateDto.index()))
                .findFirst();
    }
}
