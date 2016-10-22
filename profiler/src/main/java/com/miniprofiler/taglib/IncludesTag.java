package com.miniprofiler.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.miniprofiler.MiniProfiler;
import com.miniprofiler.RenderOptions;
import com.miniprofiler.RenderPosition;
import com.miniprofiler.RequestHandlerServlet;

public class IncludesTag extends TagSupport {
    private final RenderOptions options = new RenderOptions();

    @Override
    public int doEndTag() throws JspException {
        try {
            this.pageContext.getOut().write(RequestHandlerServlet.renderIncludes(MiniProfiler.getCurrent(), options));
        } catch (IOException e) {
            throw new JspException(e);
        }
        return EVAL_PAGE;
    }

    public RenderPosition getPosition() {
        return options.getPosition();
    }

    public void setPosition(RenderPosition position) {
        options.setPosition(position);
    }

    public Boolean getShowTrivial() {
        return options.getShowTrivial();
    }

    public void setShowTrivial(Boolean showTrivial) {
        options.setShowTrivial(showTrivial);
    }

    public Boolean getShowTimeWithChildren() {
        return options.getShowTimeWithChildren();
    }

    public void setShowTimeWithChildren(Boolean showTimeWithChildren) {
        options.setShowTimeWithChildren(showTimeWithChildren);
    }

    public Integer getMaxTracesToShow() {
        return options.getMaxTracesToShow();
    }

    public void setMaxTracesToShow(Integer maxTracesToShow) {
        options.setMaxTracesToShow(maxTracesToShow);
    }

    public Boolean getStartHidden() {
        return options.getStartHidden();
    }

    public void setStartHidden(Boolean startHidden) {
        options.setStartHidden(startHidden);
    }

    public Boolean getShowControls() {
        return options.getShowControls();
    }

    public void setShowControls(Boolean showControls) {
        options.setShowControls(showControls);
    }
}
