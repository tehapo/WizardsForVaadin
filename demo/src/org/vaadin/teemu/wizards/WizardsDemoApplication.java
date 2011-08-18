package org.vaadin.teemu.wizards;

import java.util.Date;

import org.vaadin.teemu.wizards.Wizard.WizardCompletedEvent;
import org.vaadin.teemu.wizards.Wizard.WizardCompletedListener;

import com.vaadin.Application;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class WizardsDemoApplication extends Application implements
        WizardCompletedListener {

    // DummyBean is used as some dummy data in a form.
    private DummyBean bean = new DummyBean();

    private Wizard wizard;

    private VerticalLayout mainLayout;

    @Override
    public void init() {
        // setup the main window
        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        Window mainWindow = new Window("WizardsDemoApplication");
        mainWindow.setContent(mainLayout);
        setMainWindow(mainWindow);

        // create the Wizard component and add the steps
        bean = new DummyBean();
        wizard = new Wizard();
        wizard.addListener(this);
        wizard.addStep(new FirstStep());
        wizard.addStep(new SecondStep());
        wizard.addStep(new ThirdStep());
        wizard.addStep(new FourthStep());
        wizard.setHeight("500px");
        wizard.setWidth("700px");
        mainLayout.addComponent(wizard);
        mainLayout.setComponentAlignment(wizard, Alignment.TOP_CENTER);

    }

    public static class DummyBean {
        private String name = "Teemu";
        private String company = "Vaadin Ltd";
        private Date birthDay = new Date(0);

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public Date getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(Date birthDay) {
            this.birthDay = birthDay;
        }

        @Override
        public String toString() {
            return "name=" + name + "\ncompany=" + company + "\nbirthDay="
                    + birthDay;
        }
    }

    private class FirstStep implements WizardStep {
        public String getCaption() {
            return "Intro";
        }

        public Component getContent() {
            return new Label(
                    "<h2>Wizards for Vaadin add-on</h2><p>This is a demo application of the "
                            + "Wizards for Vaadin add-on. The goal of this add-on is to provide a simple framework for easily creating wizard style "
                            + "user interfaces. Please use the controls below this content area to navigate "
                            + "through this wizard that demonstrates the features of this add-on.</p><h3>Additional information</h3>"
                            + "<ul><li><a href=\"\">Wizards for Vaadin at Vaadin Directory</a></li>"
                            + "<li><a href=\"\">Source code</a></li></ul>",
                    Label.CONTENT_XHTML);
        }

        public boolean onAdvance() {
            return true;
        }

        public boolean onBack() {
            return true;
        }
    }

    private class SecondStep implements WizardStep {
        private Form form = new Form();

        public String getCaption() {
            return "Simple form";
        }

        public Component getContent() {
            form.setWriteThrough(false);
            form.setItemDataSource(new BeanItem<DummyBean>(bean));
            return form;
        }

        public boolean onAdvance() {
            form.commit();

            // TODO here you could commit the form and store data into db
            WizardsDemoApplication.this.getMainWindow().showNotification(
                    "Saved");
            return true;
        }

        public boolean onBack() {
            return true;
        }
    }

    private class ThirdStep implements WizardStep {

        public String getCaption() {
            return "Third step";
        }

        public Component getContent() {
            return new Label(
                    "In the previous step you added following details:\n"
                            + bean.toString(), Label.CONTENT_PREFORMATTED);
        }

        public boolean onAdvance() {
            return true;
        }

        public boolean onBack() {
            return true;
        }
    }

    private class FourthStep implements WizardStep {

        private CheckBox allowBack;
        private VerticalLayout layout;

        public String getCaption() {
            return "No turning back";
        }

        public Component getContent() {
            if (layout == null) {
                allowBack = new CheckBox("Allow back?", false);

                layout = new VerticalLayout();
                layout.addComponent(allowBack);
                layout.addComponent(new Label(
                        "You can finish the wizard by clicking the Finish button or create more steps with the button below."));
                layout.addComponent(new Button("Add new steps",
                        new Button.ClickListener() {

                            private static final long serialVersionUID = 1L;

                            public void buttonClick(ClickEvent event) {
                                wizard.addStep(new WizardStep() {

                                    public boolean onBack() {
                                        return true;
                                    }

                                    public boolean onAdvance() {
                                        return true;
                                    }

                                    public Component getContent() {
                                        VerticalLayout layout = new VerticalLayout();
                                        layout.addComponent(new Label(
                                                "This step was created on "
                                                        + new Date()));
                                        return layout;
                                    }

                                    public String getCaption() {
                                        return "Generated step";
                                    }
                                });
                            }
                        }));
            }
            return layout;
        }

        public boolean onAdvance() {
            return true;
        }

        public boolean onBack() {
            boolean allowed = allowBack.booleanValue();
            if (!allowed) {
                WizardsDemoApplication.this.getMainWindow().showNotification(
                        "Not allowed, sorry");
            }
            return allowed;
        }

    }

    public void wizardCompleted(WizardCompletedEvent event) {
        wizard.setVisible(false);
        getMainWindow().showNotification("Wizard Completed!");
        Button startOverButton = new Button("Run the demo again",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        WizardsDemoApplication.this.close();
                    }
                });
        mainLayout.addComponent(startOverButton);
        mainLayout.setComponentAlignment(startOverButton,
                Alignment.MIDDLE_CENTER);
    }
}
