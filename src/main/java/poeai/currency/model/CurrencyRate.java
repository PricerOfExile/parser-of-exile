package poeai.currency.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CurrencyRate(int index,
                           double chaosEquivalent) {

    @JsonCreator
    public CurrencyRate(@JsonProperty("receive") Rate receive,
                        @JsonProperty("chaosEquivalent") double chaosEquivalent) {
        this(receive.counterpartIndex(), chaosEquivalent);
    }
}
