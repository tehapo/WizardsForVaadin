package org.vaadin.teemu.wizards;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;

public class DemoLabel extends Label {

    public DemoLabel(String html) {
        super(html, ContentMode.HTML);
        setWidth("100%");
    }

}
