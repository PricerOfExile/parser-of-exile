package poe.currency.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

public record CurrencyDetail(@JsonProperty("id") int index,
                             @Nullable String tradeId) {

    public boolean hasTradeId() {
        return tradeId != null;
    }
}
