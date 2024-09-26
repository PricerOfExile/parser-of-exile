package poeai.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PythonAiResponse(
        @JsonProperty("prediction") Prediction prediction
) {
}
