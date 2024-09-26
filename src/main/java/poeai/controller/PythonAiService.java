package poeai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import poeai.publicstash.model.DumpedItem;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PythonAiService {

    private final ObjectMapper objectMapper;


    //just for test now
    private final WebClient client = WebClient.create("http://localhost:8000");

    @Value("classpath*:/model-test.json")
    private final List<Resource> resources;

    public String getAiResult(DumpedItem dumpedItem) {

        //for now we dont use that since we have an issue in the python stuff, instead we use a test file, see below
//        String dumpedItemAsJson;
//        try {
//            dumpedItemAsJson = objectMapper.writeValueAsString(dumpedItem);;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        //the test file in question, replace later when model is working properly
        DumpedItem itemFromTestFile;
        try {
            itemFromTestFile = objectMapper.readValue(resources.get(0).getFile(), DumpedItem.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var pythonAiResponse = client.post().uri("/predict")
                .bodyValue(itemFromTestFile)
                .retrieve()
                .bodyToMono(PythonAiResponse.class)
                .block();

        return transformResult(pythonAiResponse);
    }

    private String transformResult(PythonAiResponse pythonAiResponse) {
        Objects.requireNonNull(pythonAiResponse, "Response from model cannot be null");

        ArrayList<Map<String, Double>> results = new ArrayList<>();
        results.add(Map.of("A", pythonAiResponse.prediction().a()));
        results.add(Map.of("B", pythonAiResponse.prediction().b()));
        results.add(Map.of("C", pythonAiResponse.prediction().c()));
        results.add(Map.of("D", pythonAiResponse.prediction().d()));
        results.add(Map.of("E", pythonAiResponse.prediction().e()));
        results.add(Map.of("S", pythonAiResponse.prediction().s()));

        return results.stream()
                .flatMap(map -> map.entrySet().stream())
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

}
