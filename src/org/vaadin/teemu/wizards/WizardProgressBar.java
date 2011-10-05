package org.vaadin.teemu.wizards;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;

/**
 * WizardProgressBar displays the progress bar for a {@link Wizard}.
 */
@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(org.vaadin.teemu.wizards.client.ui.VWizardProgressBar.class)
public class WizardProgressBar extends AbstractComponent implements
        WizardProgressListener {

    private final Wizard wizard;
    private boolean completed;

    public WizardProgressBar(Wizard wizard) {
        this.wizard = wizard;
        setWidth("100%");
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        /*-
         steps
             step
                 caption
                 completed
                 current
             step
         steps
         */
        target.startTag("steps");
        for (WizardStep step : wizard.getSteps()) {
            target.startTag("step");
            target.addAttribute("caption", step.getCaption());
            target.addAttribute("completed", wizard.isCompleted(step));
            target.addAttribute("current", wizard.isActive(step));
            target.endTag("step");
        }
        target.endTag("steps");
        target.addAttribute("complete", completed);
    }

    public void activeStepChanged(WizardStepActivationEvent event) {
        requestRepaint();
    }

    public void stepSetChanged(WizardStepSetChangedEvent event) {
        requestRepaint();
    }

    public void wizardCompleted(WizardCompletedEvent event) {
        completed = true;
        requestRepaint();
    }

    public void wizardCancelled(WizardCancelledEvent event) {
        // NOP, no need to react to cancellation
    }

}
