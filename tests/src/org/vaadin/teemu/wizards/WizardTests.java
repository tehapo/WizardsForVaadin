package org.vaadin.teemu.wizards;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

public class WizardTests {

    @Test(expected = IllegalArgumentException.class)
    public void addStep_duplicateId_exceptionThrown() {
        WizardStep step1 = Mockito.mock(WizardStep.class);
        WizardStep step2 = Mockito.mock(WizardStep.class);
        Wizard wizard = new Wizard();
        wizard.addStep(step1, "duplicateId");
        wizard.addStep(step2, "duplicateId");
    }

    /**
     * Test case for a bug reported at <a
     * href="https://vaadin.com/forum/-/message_boards/view_message/1308454"
     * >Vaadin Forum</a>.
     * 
     * Thanks to Johan Smolders for the bug report.
     */
    @Test
    public void addStep_removeAndAdd_noDuplicatesSizeIsCorrect() {
        WizardStep step1 = Mockito.mock(WizardStep.class);
        WizardStep step2 = Mockito.mock(WizardStep.class);
        WizardStep step3 = Mockito.mock(WizardStep.class);
        WizardStep step4 = Mockito.mock(WizardStep.class);

        Wizard wizard = new Wizard();
        wizard.addStep(step1);
        wizard.addStep(step2);
        wizard.addStep(step3);
        wizard.removeStep(step2);
        wizard.addStep(step4); // this should not throw exception

        // check the size
        Assert.assertEquals(3, wizard.getSteps().size());
    }

}
