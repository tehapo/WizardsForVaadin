package org.vaadin.teemu.wizards;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class SetupStep implements WizardStep {

    public String getCaption() {
        return "Initialize the Wizard";
    }

    public Component getContent() {
        VerticalLayout content = new VerticalLayout(getText());
        content.setMargin(true);
        return content;
    }

    private Label getText() {
        return new DemoLabel(
                "<h2>Initialize the Wizard</h2><p>Create an instance of the <code>Wizard</code> by calling the default constructor.</p>"
                        + "<pre>Wizard myWizard = new Wizard()</pre><p>After instantiation you can add some steps to the wizard by "
                        + "calling the <code>addStep</code> method. The method accepts instances of the <code>WizardStep</code> interface "
                        + "so you need to implement that in order to display your steps."
                        + "<pre>myWizard.addStep(new FirstStep());\nmyWizard.addStep(new SecondStep());\nmyWizard.addStep(new ThirdStep());\nmyWizard.addStep(new FourthStep());</pre>");
    }

    public boolean onAdvance() {
        return true;
    }

    public boolean onBack() {
        return true;
    }

}
