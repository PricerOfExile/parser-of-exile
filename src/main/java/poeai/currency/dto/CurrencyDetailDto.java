package poeai.currency.dto;

public record CurrencyDetailDto(Integer id,
                                String tradeId) {

    public boolean hasTradeId() {
        return tradeId != null;
    }
}
