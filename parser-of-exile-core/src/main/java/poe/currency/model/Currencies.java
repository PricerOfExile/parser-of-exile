package poe.currency.model;

import java.util.List;
import java.util.stream.Stream;

public record Currencies(List<CurrencyRate> lines,
                         List<CurrencyDetail> currencyDetails) {

    public Stream<CurrencyDetail> detailsStream() {
        return currencyDetails.stream();
    }
}
