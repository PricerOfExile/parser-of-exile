package poe.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import poe.currency.model.Currencies;
import poe.currency.model.CurrencyDetail;
import poe.currency.model.CurrencyRate;
import poe.GameDataFileLoadingException;

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
                .collect(Collectors.toMap(CurrencyRate::index, CurrencyRate::chaosEquivalent));
        currencies = currenciesDtos.stream()
                .flatMap(Currencies::detailsStream)
                .filter(CurrencyDetail::hasTradeId)
                .distinct()
                .filter(currencyDetail -> chaosEquivalentPerCurrencyIndex.containsKey(currencyDetail.index()))
                .map(currencyDetail -> {
                    var chaosOrbEquivalent = chaosEquivalentPerCurrencyIndex.get(currencyDetail.index());
                    return new Currency(currencyDetail.tradeId(), chaosOrbEquivalent);
                })
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

    private List<Currencies> parseResources(List<Resource> resources, ObjectMapper objectMapper) {
        return resources.stream()
                .map(resource -> parseResource(resource, objectMapper))
                .toList();
    }

    private Currencies parseResource(Resource resource, ObjectMapper objectMapper) {
        try(var inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, Currencies.class);
        } catch (IOException e) {
            throw new GameDataFileLoadingException(resource, e);
        }
    }
}
