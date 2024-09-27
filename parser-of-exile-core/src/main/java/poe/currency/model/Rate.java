package poe.currency.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Rate(@JsonProperty("get_currency_id") int counterpartIndex) {

}
