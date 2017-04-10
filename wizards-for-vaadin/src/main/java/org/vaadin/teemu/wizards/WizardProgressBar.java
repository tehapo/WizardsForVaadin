package org.vaadin.teemu.wizards;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import java.util.List;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

/**
 * Displays a progress bar for a {@link Wizard}.
 */
@SuppressWarnings("serial")
@StyleSheet("wizard-progress-bar.css")
public class WizardProgressBar extends CustomComponent implements
        WizardProgressListener {

    private final Wizard wizard;
    private final ProgressBar progressBar = new ProgressBar();
    private final HorizontalLayout stepCaptions = new HorizontalLayout();
    private int activeStepIndex;
    //Amount of steps displayed at a time in the header.
    private int maxStepsDisplayed = -1;//Show all

    public WizardProgressBar(Wizard wizard) {
        setStyleName("wizard-progress-bar");
        this.wizard = wizard;

        stepCaptions.setWidth("100%");
        progressBar.setWidth("100%");
        progressBar.setHeight("13px");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.addComponent(stepCaptions);
        layout.addComponent(progressBar);
        setCompositionRoot(layout);
        setWidth("100%");
    }

    private void updateProgressBar() {
        int stepCount = wizard.getSteps().size();
        float padding = (1.0f / stepCount) / 2;
        float progressValue = padding + activeStepIndex / (float) stepCount;
        progressBar.setValue(progressValue);
    }

    private void updateStepCaptions() {
        stepCaptions.removeAllComponents();
        int index = 1;
        //Just show 3 at a time
        for (WizardStep step : wizard.getSteps()) {
            if (index > activeStepIndex) {
                Label label = createCaptionLabel(index, step);
                stepCaptions.addComponent(label);
            }
            index++;
            if (maxStepsDisplayed > 0 && index > activeStepIndex + maxStepsDisplayed) {
                break;
            }
        }
    }

    private Label createCaptionLabel(int index, WizardStep step) {
        Label label = new Label(index + ". " + step.getCaption());
        label.addStyleName("step-caption");

        // Add styles for themeing.
        if (wizard.isCompleted(step)) {
            label.addStyleName("completed");
        }
        if (wizard.isActive(step)) {
            label.addStyleName("current");
        }
        if (wizard.isFirstStep(step)) {
            label.addStyleName("first");
        }
        if (wizard.isLastStep(step)) {
            label.addStyleName("last");
        }

        return label;
    }

    private void updateProgressAndCaptions() {
        updateProgressBar();
        updateStepCaptions();
    }

    @Override
    public void activeStepChanged(WizardStepActivationEvent event) {
        List<WizardStep> allSteps = wizard.getSteps();
        activeStepIndex = allSteps.indexOf(event.getActivatedStep());
        updateProgressAndCaptions();
    }

    @Override
    public void stepSetChanged(WizardStepSetChangedEvent event) {
        updateProgressAndCaptions();
    }

    @Override
    public void wizardCompleted(WizardCompletedEvent event) {
        progressBar.setValue(1.0f);
        updateStepCaptions();
    }

    @Override
    public void wizardCancelled(WizardCancelledEvent event) {
        // NOP, no need to react to cancellation
    }

    /**
     * Get amount of steps to display on the header at a time. A value of -1
     * means all will be displayed.
     *
     * @return the maxStepsDisplayed
     */
    public int getMaxStepsDisplayed() {
        return maxStepsDisplayed;
    }

    /**
     * Set the maximum amount of steps to display on the header at a time to
     * avoid them being overlapping.
     *
     * A value of -1 means all will be displayed.
     *
     * @param maxStepsDisplayed the maxStepsDisplayed to set
     */
    public void setMaxStepsDisplayed(int maxStepsDisplayed) {
        this.maxStepsDisplayed = maxStepsDisplayed;
        updateStepCaptions();
    }
}
