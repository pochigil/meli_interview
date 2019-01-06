package com.meli.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private double price;
    @JsonProperty("thumbnail")
    private String thumbnail;
    @JsonProperty("shipping")
    private Shipping shipping;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("price")
    public double getPrice() {
        return price;
    }

    @JsonProperty("thumbnail")
    public String getThumbnail() {
        return thumbnail.replace("-I", "-P");
    }

    @JsonProperty("shipping")
    public Shipping getShipping() {
        return shipping;
    }

    public String getPhoto() {
        return getThumbnail().replace("-P", "-B");
    }

}