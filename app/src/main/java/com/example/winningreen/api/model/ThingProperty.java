package com.example.winningreen.api.model;

import com.google.gson.annotations.SerializedName;

public class ThingProperty {
    private String id;
    private String name;

    @SerializedName("variable_name")
    private String variableName;

    @SerializedName("last_value")
    private String lastValue;

    private String type;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastValue() {
        return lastValue;
    }

    public String getType() {
        return type;
    }
}
