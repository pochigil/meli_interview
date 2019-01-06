package com.meli.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Description {

    @JsonProperty("plain_text")
    private String plain_text;

    @JsonProperty("plain_text")
    public String getPlainText() {
        return plain_text;
    }

}