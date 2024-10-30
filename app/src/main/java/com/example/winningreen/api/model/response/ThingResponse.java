package com.example.winningreen.api.model.response;

import com.example.winningreen.api.model.ThingProperty;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThingResponse {
    private String id;
    private String name;

    @SerializedName("properties")
    private List<ThingProperty> properties;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ThingProperty> getProperties() {
        return properties;
    }
}
