package org.vaadin.teemu.wizards;

import javax.servlet.annotation.WebServlet;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Demo application for the <a
 * href="http://vaadin.com/addon/wizards-for-vaadin">Wizards for Vaadin</a>
 * add-on.
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
@SuppressWarnings("serial")
@Theme("demo")
public class WizardsDemoApplication extends UI implements
        WizardProgressListener {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = WizardsDemoApplication.class)
	public static class Servlet extends VaadinServlet {
	}
	
    private Wizard wizard;
    private VerticalLayout mainLayout;

    @Override
    protected void init(VaadinRequest request) {
        // setup the main window
        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        setContent(mainLayout);

        // create the Wizard component and add the steps
        wizard = new Wizard();
        wizard.setUriFragmentEnabled(true);
        wizard.addListener(this);
        wizard.addStep(new IntroStep(), "intro");
        wizard.addStep(new SetupStep(), "setup");
        wizard.addStep(new ListenStep(), "listen");
        wizard.addStep(new LastStep(wizard), "last");
        wizard.setHeight("600px");
        wizard.setWidth("800px");

        mainLayout.addComponent(wizard);
        mainLayout.setComponentAlignment(wizard, Alignment.TOP_CENTER);
    }

    public void wizardCompleted(WizardCompletedEvent event) {
        endWizard("Wizard Completed!");
    }

    public void activeStepChanged(WizardStepActivationEvent event) {
        // display the step caption as the window title
        Page.getCurrent().setTitle(event.getActivatedStep().getCaption());
    }

    public void stepSetChanged(WizardStepSetChangedEvent event) {
        // NOP, not interested on this event
    }

    public void wizardCancelled(WizardCancelledEvent event) {
        endWizard("Wizard Cancelled!");
    }

    private void endWizard(String message) {
        wizard.setVisible(false);
        Notification.show(message);
        Page.getCurrent().setTitle(message);
        Button startOverButton = new Button("Run the demo again",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        // Close the session and reload the page.
                        VaadinSession.getCurrent().close();
                        Page.getCurrent().setLocation("");
                    }
                });
        mainLayout.addComponent(startOverButton);
        mainLayout.setComponentAlignment(startOverButton,
                Alignment.MIDDLE_CENTER);
    }

}
