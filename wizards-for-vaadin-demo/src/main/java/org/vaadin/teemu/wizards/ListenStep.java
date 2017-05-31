package org.vaadin.teemu.wizards;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ListenStep implements WizardStep {

    @Override
    public String getCaption() {
        return "Listen for Progress";
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
                "<h2>Listen for Progress</h2><p class=\"narrow\">The <code>WizardProgressListener</code> provides lifecycle methods to react "
                + "on the progress made by user.</p><p class=\"narrow\">By default the add-on displays a default <code>WizardProgressBar</code> (as seen above) for displaying the progress. You "
                + "can also use any other implementation of the interface for displaying the progress.</p>"
                + "<p>To register a new listener, use the <code>addListener</code> method of the <code>Wizard</code> class. For removal there is also <code>removeListener</code> method.</p>"
                + "<pre>WizardProgressListener myListener = new MyProgressListener();\nmyWizard.addListener(myListener);</pre>"
                + "<p>If you don't want to display the default progress bar, you can hide it by calling <code>setHeader(null)</code>. "
                + "The default progress bar component is also registered as a listener, so a good practice would be also to remove it (unless you want to display it in any other place on your application).</p>"
                + "<pre>Component defaultHeader = myWizard.getHeader();\nif (defaultHeader instanceof WizardProgressListener) {\n    myWizard.removeListener((WizardProgressListener) defaultHeader);\n}\nmyWizard.setHeader(null);</pre>",
                ContentMode.HTML);
    }

    private Embedded getArrow() {
        Embedded arrow = new Embedded("", new ThemeResource("img/arrow-up.png"));
        arrow.setStyleName("listen-arrow");
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
