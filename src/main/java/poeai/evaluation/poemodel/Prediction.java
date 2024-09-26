package poeai.evaluation.poemodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Prediction(@JsonProperty("E") double e,
                         @JsonProperty("D") double d,
                         @JsonProperty("C") double c,
                         @JsonProperty("B") double b,
                         @JsonProperty("A") double a,
                         @JsonProperty("S") double s) {
}
