package poe.evaluation.modelapi;

import jakarta.annotation.Nonnull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import poe.model.ModelItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@Service
public class PoEModelAPIClient {

    @Nonnull
    private final RestClient modelApiRestClient;

    public PoEModelAPIClient(@Nonnull RestClient modelApiRestClient) {
        this.modelApiRestClient = modelApiRestClient;
    }

    public String getAiResult(ModelItem modelItem) {
        var bodilessEntity = modelApiRestClient.post().uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .body(modelItem)
                .retrieve()
                .toEntity(PredictionResponse.class);
        return transformResult(bodilessEntity.getBody());
    }

    private String transformResult(PredictionResponse predictionResponse) {

        Objects.requireNonNull(predictionResponse, "Response from model cannot be null");

        ArrayList<Map<String, Double>> results = new ArrayList<>();
        results.add(Map.of("A", predictionResponse.prediction().a()));
        results.add(Map.of("B", predictionResponse.prediction().b()));
        results.add(Map.of("C", predictionResponse.prediction().c()));
        results.add(Map.of("D", predictionResponse.prediction().d()));
        results.add(Map.of("E", predictionResponse.prediction().e()));
        results.add(Map.of("S", predictionResponse.prediction().s()));

        return results.stream()
                .flatMap(map -> map.entrySet().stream())
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
