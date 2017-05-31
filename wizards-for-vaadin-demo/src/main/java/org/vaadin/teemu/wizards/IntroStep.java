package org.vaadin.teemu.wizards;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class IntroStep implements WizardStep {

    @Override
    public String getCaption() {
        return "Intro";
    }

    @Override
    public Component getContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);

        Label text = getText();
        content.addComponent(text);

        Embedded arrow = getArrow();
        content.addComponent(arrow);

        return content;
    }

    private Label getText() {
        return new Label(
                "<h2>Wizards for Vaadin add-on</h2><p>This is a demo application of the "
                + "Wizards for Vaadin add-on.</p><p>The goal of this add-on is to provide a simple framework for easily creating wizard style "
                + "user interfaces. Please use the controls below this content area to navigate "
                + "through this wizard that demonstrates the features and usage of this add-on.</p><h3>Additional information</h3>"
                + "<ul><li><a href=\"https://vaadin.com/addon/wizards-for-vaadin\">Wizards for Vaadin </a> at Vaadin Directory</li>"
                + "<li><a href=\"http://code.google.com/p/wizards-for-vaadin/\">Project page</a> at Google Project Hosting (including source code of this demo)</li>"
                + "<li><a href=\"https://vaadin.com/teemu/\">Author homepage</a> at Vaadin</li></ul>",
                ContentMode.HTML);
    }

    private Embedded getArrow() {
        Embedded arrow = new Embedded("", new ThemeResource(
                "img/arrow-down.png"));
        arrow.setStyleName("intro-arrow");
        return arrow;
    }

    @Override
    public boolean onAdvance() {
        return true;
    }

    @Override
    public boolean onBack() {
        return true;
    }
}
