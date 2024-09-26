package poeai.evaluation.poemodel;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import poeai.publicstash.model.DumpedItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class PoEModelAPIClient {

    @Nonnull
    private final RestClient modelApiRestClient;

    public PoEModelAPIClient(@Nonnull RestClient modelApiRestClient) {
        this.modelApiRestClient = modelApiRestClient;
    }

    public String getAiResult(DumpedItem dumpedItem) {
        var bodilessEntity = modelApiRestClient.post().uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dumpedItem)
                .retrieve()
                .toEntity(PredictResponse.class);
        return transformResult(bodilessEntity.getBody());
    }

    private String transformResult(PredictResponse predictResponse) {

        Objects.requireNonNull(predictResponse, "Response from model cannot be null");

        ArrayList<Map<String, Double>> results = new ArrayList<>();
        results.add(Map.of("A", predictResponse.prediction().a()));
        results.add(Map.of("B", predictResponse.prediction().b()));
        results.add(Map.of("C", predictResponse.prediction().c()));
        results.add(Map.of("D", predictResponse.prediction().d()));
        results.add(Map.of("E", predictResponse.prediction().e()));
        results.add(Map.of("S", predictResponse.prediction().s()));

        return results.stream()
                .flatMap(map -> map.entrySet().stream())
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

}
