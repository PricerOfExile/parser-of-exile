package poeai.currency.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CurrencyRateDto(int index,
                              double chaosEquivalent) {

    @JsonCreator
    public CurrencyRateDto(@JsonProperty("receive") RateDto receive,
                           @JsonProperty("chaosEquivalent") double chaosEquivalent) {
        this(receive.get_currency_id(), chaosEquivalent);
    }
}
