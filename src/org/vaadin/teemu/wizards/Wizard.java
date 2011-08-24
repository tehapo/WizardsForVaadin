package org.vaadin.teemu.wizards;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class Wizard extends VerticalLayout implements ClickListener {

    private final List<WizardStep> steps = new ArrayList<WizardStep>();

    private Panel contentPanel;

    private WizardProgressBar progressBar;

    private Button nextButton;
    private Button backButton;
    private Button finishButton;

    private WizardStep currentStep;

    private static final Method WIZARD_COMPLETED_METHOD;

    static {
        try {
            WIZARD_COMPLETED_METHOD = WizardCompletedListener.class
                    .getDeclaredMethod("wizardCompleted",
                            new Class[] { WizardCompletedEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException(
                    "Internal error finding methods in Wizard");
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

        progressBar = new WizardProgressBar(this);
        progressBar.setWidth("100%");

        addComponent(progressBar);
        addComponent(contentPanel);
        addComponent(footer);
        setComponentAlignment(footer, Alignment.BOTTOM_RIGHT);

        setExpandRatio(contentPanel, 1.0f);
        setSizeFull();
    }

    public void addStep(WizardStep step) {
        if (currentStep == null) {
            currentStep = step;
            displayStep(currentStep);
        }

        steps.add(step);
        updateButtons();

        progressBar.requestRepaint();
    }

    public void addListener(WizardCompletedListener listener) {
        addListener(WizardCompletedEvent.class, listener,
                WIZARD_COMPLETED_METHOD);
    }

    public void removeListener(WizardCompletedListener listener) {
        removeListener(WizardCompletedEvent.class, listener,
                WIZARD_COMPLETED_METHOD);
    }

    public List<WizardStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public boolean isCompleted(WizardStep step) {
        return steps.indexOf(step) < steps.indexOf(currentStep);
    }

    public boolean isCurrentStep(WizardStep step) {
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
        progressBar.requestRepaint();
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
            fireWizardCompleteEvent();
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

    private void fireWizardCompleteEvent() {
        fireEvent(new WizardCompletedEvent(this));
    }

    public class WizardCompletedEvent extends Component.Event {

        public WizardCompletedEvent(Wizard source) {
            super(source);
        }

        /**
         * Returns the {@link Wizard} component that was just completed.
         * 
         * @return the completed {@link Wizard}
         */
        public Wizard getWizard() {
            return (Wizard) getSource();
        }

    }

    public interface WizardCompletedListener extends Serializable {

        /**
         * Called when a {@link Wizard} is complete.
         * 
         * @param event
         *            {@link Component.Event} object containing details about
         *            the completion
         */
        public void wizardCompleted(WizardCompletedEvent event);

    }
}
