package org.vaadin.teemu.wizards;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ListenStep implements WizardStep {

    public String getCaption() {
        return "Listen for Progress";
    }

    public Component getContent() {
        VerticalLayout content = new VerticalLayout();

        Label text = getText();
        content.addComponent(text);

        Embedded arrow = getArrow();
        content.addComponent(arrow);

        return content;
    }

    private Label getText() {
        return new Label(
                "<h2>Listen for Progress</h2><p class=\"narrow\">The <code>WizardProgressListener</code> provides lifecycle methods to react "
                        + "on the progress made by user.</p><p class=\"narrow\">The add-on package contains a default progress bar for displaying the progress but you "
                        + "can also use any other implementations of the interface for displaying the progress.</p>"
                        + "<p>Use the following code to display the default <code>WizardProgressBar</code> as the header. Note that it has client-side GWT code so using "
                        + "it requires widgetset compilation.</p>"
                        + "<pre>WizardProgressBar progressBar = new WizardProgressBar(wizard);\nmyWizard.addListener(progressBar);\nmyWizard.setHeader(progressBar);</pre>",
                Label.CONTENT_XHTML);
    }

    private Embedded getArrow() {
        Embedded arrow = new Embedded("", new ThemeResource("img/arrow-up.png"));
        arrow.setStyleName("listen-arrow");
        return arrow;
    }

    public boolean onAdvance() {
        return true;
    }

    public boolean onBack() {
        return true;
    }

}
