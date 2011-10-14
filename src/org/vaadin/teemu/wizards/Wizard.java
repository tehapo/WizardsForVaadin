package org.vaadin.teemu.wizards;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
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
 * The wizard also supports navigation through URI fragments. This feature is
 * disabled by default, but you can enable it using
 * {@link #setUriFragmentEnabled(boolean)} method. Each step will get a
 * generated identifier that is used as the URI fragment. If you wish to
 * override these with your own identifiers, you can add the steps using the
 * overloaded {@link #addStep(WizardStep, String)} method.
 * </p>
 * 
 * <p>
 * To react on the progress, cancellation or completion of this {@code Wizard}
 * you should add one or more listeners that implement the
 * {@link WizardProgressListener} interface. These listeners are added using the
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
public class Wizard extends CustomComponent implements ClickListener,
        FragmentChangedListener {

    private final List<WizardStep> steps = new ArrayList<WizardStep>();
    private final Map<String, WizardStep> idMap = new HashMap<String, WizardStep>();

    private VerticalLayout mainLayout;

    private Panel contentPanel;

    private Button nextButton;
    private Button backButton;
    private Button finishButton;
    private Button cancelButton;

    private WizardStep currentStep;
    private WizardStep lastCompletedStep;
    private Component header;
    private UriFragmentUtility uriFragment;

    private static final Method WIZARD_ACTIVE_STEP_CHANGED_METHOD;
    private static final Method WIZARD_STEP_SET_CHANGED_METHOD;
    private static final Method WIZARD_COMPLETED_METHOD;
    private static final Method WIZARD_CANCELLED_METHOD;

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
            WIZARD_CANCELLED_METHOD = WizardProgressListener.class
                    .getDeclaredMethod("wizardCancelled",
                            new Class[] { WizardCancelledEvent.class });
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
        mainLayout = new VerticalLayout();
        setCompositionRoot(mainLayout);
        setSizeFull();

        uriFragment = new UriFragmentUtility();
        uriFragment.addListener(this);
        uriFragment.setEnabled(false); // disabled by default

        contentPanel = new Panel();
        contentPanel.setSizeFull();

        nextButton = new Button("Next");
        nextButton.addListener(this);

        backButton = new Button("Back");
        backButton.addListener(this);

        finishButton = new Button("Finish");
        finishButton.addListener(this);
        finishButton.setEnabled(false);

        cancelButton = new Button("Cancel");
        cancelButton.addListener(this);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addComponent(cancelButton);
        footer.addComponent(backButton);
        footer.addComponent(nextButton);
        footer.addComponent(finishButton);

        mainLayout.addComponent(contentPanel);
        mainLayout.addComponent(footer);
        mainLayout.addComponent(uriFragment);
        mainLayout.setComponentAlignment(footer, Alignment.BOTTOM_RIGHT);

        mainLayout.setExpandRatio(contentPanel, 1.0f);
        mainLayout.setSizeFull();
    }

    public void setUriFragmentEnabled(boolean enabled) {
        uriFragment.setEnabled(enabled);
    }

    public boolean isUriFragmentEnabled() {
        return uriFragment.isEnabled();
    }

    public void setHeader(Component header) {
        if (this.header != null) {
            mainLayout.replaceComponent(this.header, header);
        } else {
            mainLayout.addComponentAsFirst(header);
        }
        this.header = header;
    }

    public void addStep(WizardStep step, String id) {
        steps.add(step);
        idMap.put(id, step);
        updateButtons();

        // notify listeners
        fireEvent(new WizardStepSetChangedEvent(this));
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        // make sure there is always a step selected
        if (currentStep == null && !steps.isEmpty()) {
            // activate the first step
            activateStep(steps.get(0));
        }

        super.paintContent(target);
    }

    public void addStep(WizardStep step) {
        addStep(step, "wizard-step-" + (steps.size() + 1));
    }

    public void addListener(WizardProgressListener listener) {
        addListener(WizardCompletedEvent.class, listener,
                WIZARD_COMPLETED_METHOD);
        addListener(WizardStepActivationEvent.class, listener,
                WIZARD_ACTIVE_STEP_CHANGED_METHOD);
        addListener(WizardStepSetChangedEvent.class, listener,
                WIZARD_STEP_SET_CHANGED_METHOD);
        addListener(WizardCancelledEvent.class, listener,
                WIZARD_CANCELLED_METHOD);
    }

    public void removeListener(WizardProgressListener listener) {
        removeListener(WizardCompletedEvent.class, listener,
                WIZARD_COMPLETED_METHOD);
        removeListener(WizardStepActivationEvent.class, listener,
                WIZARD_ACTIVE_STEP_CHANGED_METHOD);
        removeListener(WizardStepSetChangedEvent.class, listener,
                WIZARD_STEP_SET_CHANGED_METHOD);
        removeListener(WizardCancelledEvent.class, listener,
                WIZARD_CANCELLED_METHOD);
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
            finishButton.setEnabled(true);
            nextButton.setEnabled(false);
        } else {
            finishButton.setEnabled(false);
            nextButton.setEnabled(true);
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

    public Button getCancelButton() {
        return cancelButton;
    }

    private void activateStep(WizardStep step) {
        if (step == null) {
            return;
        }

        if (currentStep != null) {
            if (currentStep.equals(step)) {
                // already active
                return;
            }

            // ask if we're allowed to move
            boolean advancing = steps.indexOf(step) > steps
                    .indexOf(currentStep);
            if (advancing) {
                if (!currentStep.onAdvance()) {
                    // not allowed to advance
                    return;
                }
            } else {
                if (!currentStep.onBack()) {
                    // not allowed to go back
                    return;
                }
            }

            // keep track of the last step that was completed
            int currentIndex = steps.indexOf(currentStep);
            if (lastCompletedStep == null
                    || steps.indexOf(lastCompletedStep) < currentIndex) {
                lastCompletedStep = currentStep;
            }
        }

        contentPanel.removeAllComponents();
        contentPanel.addComponent(step.getContent());
        currentStep = step;

        updateUriFragment();
        updateButtons();
        fireEvent(new WizardStepActivationEvent(this, step));
    }

    private void activateStep(String id) {
        WizardStep step = idMap.get(id);
        if (step != null) {
            // check that we don't go past the lastCompletedStep by using the id
            int lastCompletedIndex = lastCompletedStep == null ? -1 : steps
                    .indexOf(lastCompletedStep);
            int stepIndex = steps.indexOf(step);

            if (lastCompletedIndex < stepIndex) {
                activateStep(lastCompletedStep);
            } else {
                activateStep(step);
            }
        }
    }

    private String getId(WizardStep step) {
        for (Map.Entry<String, WizardStep> entry : idMap.entrySet()) {
            if (entry.getValue().equals(step)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void updateUriFragment() {
        if (uriFragment.isEnabled()) {
            String currentStepId = getId(currentStep);
            if (currentStepId != null && currentStepId.length() > 0) {
                uriFragment.setFragment(currentStepId, false);
            } else {
                uriFragment.setFragment(null, false);
            }
        }
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
        } else if (event.getButton() == cancelButton) {
            cancelButtonClick(event);
        }
    }

    private void cancelButtonClick(ClickEvent event) {
        fireEvent(new WizardCancelledEvent(this));
    }

    private void finishButtonClick(ClickEvent event) {
        if (currentStep.onAdvance()) {
            // next (finish) allowed -> fire complete event
            fireEvent(new WizardCompletedEvent(this));
        }
    }

    private void nextButtonClick(ClickEvent event) {
        int currentIndex = steps.indexOf(currentStep);
        activateStep(steps.get(currentIndex + 1));
    }

    private void backButtonClick(ClickEvent event) {
        int currentIndex = steps.indexOf(currentStep);
        activateStep(steps.get(currentIndex - 1));
    }

    public void fragmentChanged(FragmentChangedEvent source) {
        String fragment = source.getUriFragmentUtility().getFragment();
        if (fragment.equals("") && !steps.isEmpty()) {
            // empty fragment -> set the fragment of first step
            uriFragment.setFragment(getId(steps.get(0)));
        } else {
            activateStep(fragment);
        }
    }

}
