package com.example.winningreen.model;

public class HumiditySensor {
    private String id;
    private String name;
    private String displayName;
    private int value;

    public HumiditySensor(String id, String name, String displayName, int value) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
