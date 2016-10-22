package com.miniprofiler;

public enum RenderPosition {
    LEFT("left"),
    RIGHT("right"),
    BOTTOM_LEFT("bottomleft"),
    BOTTOM_RIGHT("bottomright");

    private final String jsRepresentation;

    RenderPosition(String jsRepresentation) {
        this.jsRepresentation = jsRepresentation;
    }

    public String getJsRepresentation() {
        return jsRepresentation;
    }
}
