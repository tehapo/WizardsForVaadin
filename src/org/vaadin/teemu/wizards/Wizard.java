package org.vaadin.teemu.wizards;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Component for displaying multi-step wizard style user interface.
 * 
 * <p>
 * The steps of the wizard must be implementations of the {@link WizardStep}
 * interface. Use the {@link #addStep(WizardStep)} method to add these steps in
 * the same order they are supposed to be displayed.
 * </p>
 * 
 * <p>
 * To react on the progress or completion of this {@code Wizard} you should add
 * one or more listeners that implement the {@link WizardProgressListener}
 * interface. These listeners are added using the
 * {@link #addListener(WizardProgressListener)} method and removed with the
 * {@link #removeListener(WizardProgressListener)}.
 * </p>
 * 
 * <p>
 * To use the default progress bar {@link WizardProgressBar} you should register
 * an instance of it as a listener and set it as the header of this
 * {@link Wizard} or optionally display it in another place in your application.
 * <br />
 * <br />
 * Example on using the progress bar:
 * 
 * <pre>
 * Wizard myWizard = new Wizard();
 * WizardProgressBar progressBar = new WizardProgressBar(wizard);
 * myWizard.addListener(progressBar);
 * myWizard.setHeader(progressBar);
 * </pre>
 * 
 * </p>
 * 
 * @author Teemu Pöntelin / Vaadin Ltd
 */
@SuppressWarnings("serial")
public class Wizard extends VerticalLayout implements ClickListener {

    private final List<WizardStep> steps = new ArrayList<WizardStep>();

    private Panel contentPanel;

    private Button nextButton;
    private Button backButton;
    private Button finishButton;

    private WizardStep currentStep;
    private Component header;

    private static final Method WIZARD_ACTIVE_STEP_CHANGED_METHOD;
    private static final Method WIZARD_STEP_SET_CHANGED_METHOD;
    private static final Method WIZARD_COMPLETED_METHOD;

    static {
        try {
            WIZARD_COMPLETED_METHOD = WizardProgressListener.class
                    .getDeclaredMethod("wizardCompleted",
                            new Class[] { WizardCompletedEvent.class });
            WIZARD_STEP_SET_CHANGED_METHOD = WizardProgressListener.class
                    .getDeclaredMethod("stepSetChanged",
                            new Class[] { WizardStepSetChangedEvent.class });
            WIZARD_ACTIVE_STEP_CHANGED_METHOD = WizardProgressListener.class
                    .getDeclaredMethod("activeStepChanged",
                            new Class[] { WizardStepActivationEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in Wizard", e);
        }
    }

    public Wizard() {
        setStyleName("wizard");
        init();
    }

    private void init() {
        contentPanel = new Panel();
        contentPanel.setSizeFull();

        nextButton = new Button("Next");
        nextButton.addListener(this);

        backButton = new Button("Back");
        backButton.addListener(this);

        finishButton = new Button("Finish");
        finishButton.addListener(this);
        finishButton.setVisible(false);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(backButton);
        footer.addComponent(nextButton);
        footer.addComponent(finishButton);

        addComponent(contentPanel);
        addComponent(footer);
        setComponentAlignment(footer, Alignment.BOTTOM_RIGHT);

        setExpandRatio(contentPanel, 1.0f);
        setSizeFull();
    }

    public void setHeader(Component header) {
        if (this.header != null) {
            replaceComponent(this.header, header);
        } else {
            addComponentAsFirst(header);
        }
        this.header = header;
    }

    public void addStep(WizardStep step) {
        if (currentStep == null) {
            currentStep = step;
            displayStep(currentStep);
        }

        steps.add(step);
        updateButtons();

        fireEvent(new WizardStepSetChangedEvent(this));
    }

    public void addListener(WizardProgressListener listener) {
        addListener(WizardCompletedEvent.class, listener,
                WIZARD_COMPLETED_METHOD);
        addListener(WizardStepActivationEvent.class, listener,
                WIZARD_ACTIVE_STEP_CHANGED_METHOD);
        addListener(WizardStepSetChangedEvent.class, listener,
                WIZARD_STEP_SET_CHANGED_METHOD);
    }

    public void removeListener(WizardProgressListener listener) {
        removeListener(WizardCompletedEvent.class, listener,
                WIZARD_COMPLETED_METHOD);
        removeListener(WizardStepActivationEvent.class, listener,
                WIZARD_ACTIVE_STEP_CHANGED_METHOD);
        removeListener(WizardStepSetChangedEvent.class, listener,
                WIZARD_STEP_SET_CHANGED_METHOD);
    }

    public List<WizardStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public boolean isCompleted(WizardStep step) {
        return steps.indexOf(step) < steps.indexOf(currentStep);
    }

    public boolean isActive(WizardStep step) {
        return (step == currentStep);
    }

    private void updateButtons() {
        if (isLastStep(currentStep)) {
            finishButton.setVisible(true);
            nextButton.setVisible(false);
        } else {
            finishButton.setVisible(false);
            nextButton.setVisible(true);
        }
        backButton.setEnabled(!isFirstStep(currentStep));
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public Button getFinishButton() {
        return finishButton;
    }

    private void displayStep(WizardStep step) {
        contentPanel.removeAllComponents();
        contentPanel.addComponent(step.getContent());
        currentStep = step;

        updateButtons();
        fireEvent(new WizardStepActivationEvent(this, step));
    }

    private boolean isFirstStep(WizardStep step) {
        return steps.indexOf(step) == 0;
    }

    private boolean isLastStep(WizardStep step) {
        return steps.indexOf(step) == (steps.size() - 1);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == nextButton) {
            nextButtonClick(event);
        } else if (event.getButton() == backButton) {
            backButtonClick(event);
        } else if (event.getButton() == finishButton) {
            finishButtonClick(event);
        }
    }

    private void finishButtonClick(ClickEvent event) {
        if (currentStep.onAdvance()) {
            // next (finish) allowed -> fire complete event
            fireEvent(new WizardCompletedEvent(this));
        }
    }

    private void nextButtonClick(ClickEvent event) {
        if (currentStep.onAdvance()) {
            // next allowed -> display next step
            int currentIndex = steps.indexOf(currentStep);
            displayStep(steps.get(currentIndex + 1));
        }
    }

    private void backButtonClick(ClickEvent event) {
        if (currentStep.onBack()) {
            // back allowed
            int currentIndex = steps.indexOf(currentStep);
            displayStep(steps.get(currentIndex - 1));
        }
    }

}
