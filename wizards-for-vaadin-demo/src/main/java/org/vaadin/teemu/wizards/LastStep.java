package org.vaadin.teemu.wizards;

import java.util.Date;

import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.v7.ui.VerticalLayout;

public class LastStep implements WizardStep {

    private CheckBox allowBack;
    private VerticalLayout layout;
    private Wizard owner;

    public LastStep(Wizard owner) {
        this.owner = owner;
    }

    public String getCaption() {
        return "Need more?";
    }

    public Component getContent() {
        if (layout == null) {
            allowBack = new CheckBox("Allow back?", false);

            layout = new VerticalLayout();
            layout.setMargin(true);
            layout.addComponent(new Label(
                    "<h2>Need more steps?</h2><p>You can also dynamically add new steps. Try it out with the button below.</p>",
                    ContentMode.HTML));
            layout.addComponent(new Button("Add new steps",
                    new Button.ClickListener() {

                        private static final long serialVersionUID = 1L;

                        public void buttonClick(ClickEvent event) {
                            owner.addStep(new WizardStep() {

                                private final Date createdAt = new Date();

                                public boolean onBack() {
                                    return true;
                                }

                                public boolean onAdvance() {
                                    return true;
                                }

                                public Component getContent() {
                                    VerticalLayout layout = new VerticalLayout();
                                    layout.setMargin(true);
                                    layout.addComponent(new Label(
                                            "This step was created on "
                                                    + createdAt));
                                    return layout;
                                }

                                public String getCaption() {
                                    return "Generated step";
                                }
                            });
                        }
                    }));

            layout.addComponent(new Label(
                    "<h2>Want to go back?</h2><p>This step is also an example of conditionally allowing you to go back.<br />"
                            + "Try to click the back button and then again after checking the checkbox below.</p>",
                    ContentMode.HTML));
            layout.addComponent(allowBack);
        }
        return layout;
    }

    public boolean onAdvance() {
        return true;
    }

    public boolean onBack() {
        boolean allowed = allowBack.getValue();
        if (!allowed) {
            Notification.show("Not allowed, sorry");
        }
        return allowed;
    }
}
