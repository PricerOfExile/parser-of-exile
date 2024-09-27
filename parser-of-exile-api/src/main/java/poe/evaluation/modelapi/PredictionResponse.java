package poe.evaluation.modelapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PredictionResponse(@JsonProperty("prediction") Prediction prediction) {
}
