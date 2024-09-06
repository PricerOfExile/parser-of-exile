package poeai.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import poeai.currency.dto.CurrenciesDto;
import poeai.currency.dto.CurrencyDetailDto;
import poeai.currency.dto.CurrencyRateDto;
import poeai.gamedata.GameDataFileLoadingException;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
public class CurrencyRepository {

    private final Set<Currency> currencies;

    public CurrencyRepository(@Value("classpath*:/poe.ninja/*.json") @Nonnull List<Resource> resources,
                              @Nonnull ObjectMapper objectMapper) {
        Objects.requireNonNull(resources, "Resources are mandatory");
        Objects.requireNonNull(objectMapper, "ObjectMapper is mandatory");

        var currenciesDtos = parseResources(resources, objectMapper);
        var chaosEquivalentPerCurrencyIndex = currenciesDtos.stream()
                .flatMap(currenciesDto -> currenciesDto.lines().stream())
                .collect(Collectors.toMap(CurrencyRateDto::index, CurrencyRateDto::chaosEquivalent));
        currencies = currenciesDtos.stream()
                .flatMap(CurrenciesDto::detailsStream)
                .filter(CurrencyDetailDto::hasTradeId)
                .distinct()
                .filter(currencyDetailDto -> chaosEquivalentPerCurrencyIndex.containsKey(currencyDetailDto.id()))
                .map(currencyDetailDto -> {
                    var chaosOrbEquivalent = chaosEquivalentPerCurrencyIndex.get(currencyDetailDto.id());
                    return new Currency(currencyDetailDto.tradeId(), chaosOrbEquivalent);
                })
                .collect(Collectors.toSet());
    }

    private List<CurrenciesDto> parseResources(List<Resource> resources, ObjectMapper objectMapper) {
        return resources.stream()
                .map(resource -> parseResource(resource, objectMapper))
                .toList();
    }

    private CurrenciesDto parseResource(Resource resource, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(resource.getFile(), CurrenciesDto.class);
        } catch (IOException e) {
            throw new GameDataFileLoadingException(resource, e);
        }
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
