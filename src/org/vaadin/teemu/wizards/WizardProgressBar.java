package org.vaadin.teemu.wizards;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;

/**
 * WizardProgressBar displays the progress bar displayed in top of
 * {@link Wizard}.
 */
@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(org.vaadin.teemu.wizards.client.ui.VWizardProgressBar.class)
class WizardProgressBar extends AbstractComponent {

    private final Wizard wizard;

    public WizardProgressBar(Wizard wizard) {
        this.wizard = wizard;
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
            target.addAttribute("current", wizard.isCurrentStep(step));
            target.endTag("step");
        }
        target.endTag("steps");
    }

}
