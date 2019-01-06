package com.meli.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Shipping {

    @JsonProperty("free_shipping")
    private boolean free_shipping;

    @JsonProperty("free_shipping")
    public boolean isFreeShipping() {
        return free_shipping;
    }
}