package com.miniprofiler;

public class RenderOptions {
    private RenderPosition position;
    private Boolean showTrivial;
    private Boolean showTimeWithChildren;
    private Integer maxTracesToShow;
    private Boolean startHidden;
    private Boolean showControls;

    public RenderPosition getPosition() {
        return position;
    }

    public void setPosition(RenderPosition position) {
        this.position = position;
    }

    public RenderOptions withPosition(RenderPosition position) {
        setPosition(position);
        return this;
    }

    public Boolean getShowTrivial() {
        return showTrivial;
    }

    public void setShowTrivial(Boolean showTrivial) {
        this.showTrivial = showTrivial;
    }

    public RenderOptions withShowTrivial(Boolean showTrivial) {
        this.showTrivial = showTrivial;
        return this;
    }

    public Boolean getShowTimeWithChildren() {
        return showTimeWithChildren;
    }

    public void setShowTimeWithChildren(Boolean showTimeWithChildren) {
        this.showTimeWithChildren = showTimeWithChildren;
    }

    public RenderOptions withShowTimeWithChildren(Boolean showTimeWithChildren) {
        this.showTimeWithChildren = showTimeWithChildren;
        return this;
    }

    public Integer getMaxTracesToShow() {
        return maxTracesToShow;
    }

    public void setMaxTracesToShow(Integer maxTracesToShow) {
        this.maxTracesToShow = maxTracesToShow;
    }

    public RenderOptions withMaxTracesToShow(Integer maxTracesToShow) {
        this.maxTracesToShow = maxTracesToShow;
        return this;
    }

    public Boolean getStartHidden() {
        return startHidden;
    }

    public void setStartHidden(Boolean startHidden) {
        this.startHidden = startHidden;
    }

    public RenderOptions withStartHidden(Boolean startHidden) {
        this.startHidden = startHidden;
        return this;
    }

    public Boolean getShowControls() {
        return showControls;
    }

    public void setShowControls(Boolean showControls) {
        this.showControls = showControls;
    }

    public RenderOptions withShowControls(Boolean showControls) {
        this.showControls = showControls;
        return this;
    }
}
