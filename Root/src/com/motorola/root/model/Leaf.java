package com.motorola.root.model;

public class Leaf
{
    private String leafId;
    private Boolean leafChanged;

    public Leaf(String leafId, Boolean leafChanged)
    {
        this.leafId = leafId;
        this.leafChanged = leafChanged;
    }

    public String getLeafId() {
        return leafId;
    }

    public Boolean isLeafIdChanged() {
        return leafChanged;
    }
}
