package com.meli.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    @JsonProperty("results")
    private List<Item> results = null;

    @JsonProperty("results")
    public List<Item> getResults() {
        return results;
    }

}