package org.vaadin.teemu.wizards.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

public class VWizardProgressBar extends FlowPanel implements Paintable {

    /** Set the CSS class name to allow styling. */
    public static final String CLASSNAME = "v-wizardprogressbar";

    /** The client side widget identifier */
    protected String paintableId;

    /** Reference to the server connection object. */
    ApplicationConnection client;

    private Element barElement;
    private HorizontalPanel captions;

    /**
     * The constructor should first call super() to initialize the component and
     * then handle any initialization relevant to Vaadin.
     */
    public VWizardProgressBar() {
        // This method call of the Paintable interface sets the component
        // style name in DOM tree
        setStyleName(CLASSNAME);

        captions = new HorizontalPanel();
        captions.setWidth("100%");
        add(captions);

        Element barWrapperElement = DOM.createDiv();
        barWrapperElement.setClassName("bar-wrapper");
        getElement().appendChild(barWrapperElement);

        barElement = DOM.createDiv();
        barElement.setClassName("bar");
        barWrapperElement.appendChild(barElement);
    }

    /**
     * Called whenever an update is received from the server
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first.
        // It handles sizes, captions, tooltips, etc. automatically.
        if (client.updateComponent(this, uidl, true)) {
            // If client.updateComponent returns true there has been no changes
            // and we
            // do not need to update anything.
            return;
        }

        // Save reference to server connection object to be able to send
        // user interaction later
        this.client = client;

        // Save the client side identifier (paintable id) for the widget
        paintableId = uidl.getId();

        int offsetWidth = getOffsetWidth();

        boolean completed = uidl.getBooleanAttribute("complete");

        UIDL steps = uidl.getChildByTagName("steps");
        int numberOfSteps = steps.getChildCount();
        double stepWidth = offsetWidth / (double) numberOfSteps;
        for (int i = 0; i < numberOfSteps; i++) {
            UIDL step = steps.getChildUIDL(i);

            ProgressBarItem item;
            if (captions.getWidgetCount() > i) {
                // get the existing widget for updating
                item = (ProgressBarItem) captions.getWidget(i);
            } else {
                // create new widget and add it to the layout
                item = new ProgressBarItem(i + 1);
                captions.add(item);
            }

            // update the barElement width according to the current step
            if (!completed && step.getBooleanAttribute("current")) {
                barElement.getStyle().setWidth(
                        (i + 1) * stepWidth - stepWidth / 2, Unit.PX);
            }
            item.setWidth(stepWidth + "px");

            // update caption and class names
            item.setCaption(step.getStringAttribute("caption"));
            boolean first = (i == 0);
            boolean last = (i == steps.getChildCount() - 1);
            updateStyleNames(step, item, first, last);
        }

        if (completed) {
            barElement.getStyle().setWidth(100, Unit.PCT);
        }
    }

    private void updateStyleNames(UIDL step, ProgressBarItem item,
            boolean first, boolean last) {
        if (step.getBooleanAttribute("completed")) {
            item.addStyleName("completed");
        } else {
            item.removeStyleName("completed");
        }
        if (step.getBooleanAttribute("current")) {
            item.addStyleName("current");
        } else {
            item.removeStyleName("current");
        }
        if (first) {
            item.addStyleName("first");
        } else {
            item.removeStyleName("first");
        }
        if (last) {
            item.addStyleName("last");
        } else {
            item.removeStyleName("last");
        }
    }

    private static class ProgressBarItem extends Widget {

        private String caption;
        private final int index;
        private Element captionElement;

        public ProgressBarItem(int index) {
            Element root = Document.get().createDivElement();
            setElement(root);
            setStyleName("step");
            this.index = index;

            captionElement = Document.get().createDivElement();
            root.appendChild(captionElement);
        }

        public void setCaption(String caption) {
            if (this.caption == null || !this.caption.equals(caption)) {
                this.caption = caption;
                captionElement.setClassName("step-caption");
                captionElement.setInnerHTML("<span>" + index + ".</span> "
                        + caption);
            }
        }
    }

}
