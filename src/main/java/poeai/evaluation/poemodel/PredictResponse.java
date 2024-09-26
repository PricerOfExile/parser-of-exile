package poeai.evaluation.poemodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PredictResponse(@JsonProperty("prediction") Prediction prediction) {
}
