package com.example.winningreen.api.model;

import com.google.gson.annotations.SerializedName;

public class PropertyValue<T> {

    @SerializedName("value")
    private T value;

    public PropertyValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
