package org.vaadin.teemu.wizards;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
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

    @Override
    public void wizardCompleted(WizardCompletedEvent event) {
        endWizard("Wizard Completed!");
    }

    @Override
    public void activeStepChanged(WizardStepActivationEvent event) {
        // display the step caption as the window title
        Page.getCurrent().setTitle(event.getActivatedStep().getCaption());
    }

    @Override
    public void stepSetChanged(WizardStepSetChangedEvent event) {
        // NOP, not interested on this event
    }

    @Override
    public void wizardCancelled(WizardCancelledEvent event) {
        endWizard("Wizard Cancelled!");
    }

    private void endWizard(String message) {
        wizard.setVisible(false);
        Notification.show(message);
        Page.getCurrent().setTitle(message);
        Button startOverButton = new Button("Run the demo again",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        // Close the session and reload the page.
                        VaadinSession.getCurrent().close();
                        Page.getCurrent().setLocation("");
                    }
                });
        Button startDialogVersionOfWizardButton = new Button
        (
            "Demo the Wizard Dialog Version",
            new Button.ClickListener()
            {
                @Override
                public void buttonClick(ClickEvent event)
                {
                    new WizardDialog()
                    {
                        private WizardProgressListener delegate;
                        public void Start(WizardProgressListener d)
                        {
                            delegate=d;
                            addStep
                            (
                                new IntroStep()
                                {
                                    @Override
                                    public Label getText() {
                                        return new DemoLabel(
                                                "<h2>Wizards for Vaadin add-on : WIZARD DIALOG MODE</h2><p>DEMO OF the Dialog POPUP version. Close the dialog popup to see the full web page Wizard version.</p><h3>Additional information</h3>"
                                                        + "<ul><li><a href=\"https://vaadin.com/addon/wizards-for-vaadin\">Wizards for Vaadin </a> at Vaadin Directory</li>"
                                                        + "<li><a href=\"https://github.com/tehapo/WizardsForVaadin/\">Project page</a> on GitHub (including source code of this demo)</li>"
                                                        + "<li><a href=\"https://vaadin.com/teemu/\">Author homepage</a> at Vaadin</li></ul>");
                                    }
                                }
                                ,
                                "Dialog BOX Wizard test"
                            );
                            addStep(new SetupStep(), "setup");
                            addStep(new ListenStep(), "listen");
                            addStep(new LastStep(this), "last");
                            Open();
                        }

                        @Override
                        public void wizardCompleted(WizardCompletedEvent event) { setVisible(false); Close();  }
                        @Override
                        public void activeStepChanged(WizardStepActivationEvent event) { delegate.activeStepChanged(event); }
                        @Override
                        public void stepSetChanged(WizardStepSetChangedEvent event) { delegate.stepSetChanged(event); }
                        @Override
                        public void wizardCancelled(WizardCancelledEvent event) { setVisible(false); Close(); }
                    }
                    .Start(WizardsDemoApplication.this);
                }
            }
        );
        mainLayout.addComponent(startOverButton);
        mainLayout.setComponentAlignment(startOverButton, Alignment.TOP_CENTER);
        mainLayout.addComponent(startDialogVersionOfWizardButton);
        mainLayout.setComponentAlignment(startDialogVersionOfWizardButton, Alignment.TOP_CENTER);
    }

}
