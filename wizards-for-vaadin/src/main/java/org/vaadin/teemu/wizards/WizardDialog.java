//    // -----------------------------------------------
//    // USAGE SAMPLE
//    // -----------------------------------------------
//    new WizardDialog()
//    {
//        public void Start(WizardProgressListener d)
//        {
//            addStep(new IntroStep(), "intro");
//            addStep(new SetupStep(), "setup");
//            addStep(new ListenStep(), "listen");
//            addStep(new LastStep(this), "last");
//            Open();
//        }
//
//        @Override
//        public void wizardCompleted(WizardCompletedEvent event) { Notification.show("wizardCompleted"); setVisible(false); Close();  }
//        @Override
//        public void activeStepChanged(WizardStepActivationEvent event) { Notification.show("activeStepChanged"); }
//        @Override
//        public void stepSetChanged(WizardStepSetChangedEvent event) { Notification.show("stepSetChanged"); }
//        @Override
//        public void wizardCancelled(WizardCancelledEvent event) { Notification.show("wizardCancelled"); setVisible(false); Close(); }
//
//    }.Start();
//

package org.vaadin.teemu.wizards;

import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

@SuppressWarnings("serial")
public abstract class WizardDialog extends Wizard implements WizardProgressListener, FocusListener
{

    private Window dlgWizard = new Window();

    public void Open()
    {
        // Wizard will run inside a popup window
        setUriFragmentEnabled(false);
        dlgWizard.setModal(true);
        dlgWizard.setResizable(true);
        dlgWizard.setHeight(650, Unit.PIXELS);
        dlgWizard.setWidth(900, Unit.PIXELS);
        dlgWizard.center();
        dlgWizard.addCloseListener(wizardPopupCloseListener);
        dlgWizard.addFocusListener(this);
        addListener(this);

        VerticalLayout mainFrame = new VerticalLayout();
        mainFrame.setMargin(true);
        mainFrame.addComponent(this);
        mainFrame.setExpandRatio(this, 1.0f);
        mainFrame.setSizeFull();
        dlgWizard.setContent(mainFrame);
        // Add popup to UI
        UI.getCurrent().addWindow(dlgWizard);
    }

    public void Close()
    {
        if( dlgWizard!=null )
        {
            dlgWizard.removeCloseListener(wizardPopupCloseListener);
            UI.getCurrent().removeWindow(dlgWizard);
        }
    }

    public CloseListener wizardPopupCloseListener = new CloseListener()
    {
        @Override
        public void windowClose(CloseEvent e)
        {
            cancel();
        }

    };

    @Override
    public void focus(FocusEvent event)
    {
        // Refresh ...
        if (currentStep != null)
            fireEvent(new WizardStepActivationEvent(this, currentStep));
    }

}
