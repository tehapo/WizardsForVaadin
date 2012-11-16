## Getting Started

Notice that this page is written for the Wizards for Vaadin version 0.4.0. If you are using some other version, the API might differ and all features might not be available.

## Installation

Install the add-on to your Vaadin project by simply copying the JAR file from [Vaadin Directory](https://vaadin.com/addon/wizards-for-vaadin) and *compiling your widgetset* (more details [here](https://vaadin.com/directory/help/using-vaadin-add-ons)). Or alternatively you can add the add-on to your project using Maven (see the Maven POM snippet required from [Vaadin Directory](https://vaadin.com/addon/wizards-for-vaadin)).

## Basic Usage

Each step of your wizard must implement the ```WizardStep``` interface and provide the actual content as the return value of the ```getContent()``` method. You should add these steps to the wizard by calling the ```addStep(WizardStep)``` method of an ```Wizard``` instance.

```java
// instantiate the Wizard
Wizard wizard = new Wizard();

// add some steps that implement the WizardStep interface
wizard.addStep(new FirstStep());
wizard.addStep(new SecondStep());
wizard.addStep(new ThirdStep());
wizard.addStep(new FourthStep());

// add the wizard to a layout
mainLayout.addComponent(wizard);        
```

## Lifecycle Events of the Wizard

To listen for the completion, cancellation or progress of the wizard, you should add a listener that implements the ```WizardProgressListener``` interface.

```java
// add WizardProgressListener (assuming MyWizardListener implements the interface)
wizard.addListener(new MyWizardListener());
```

By default every ```Wizard``` instance have one ```WizardProgressListener``` assigned. This is the default progress bar displayed as the header of the ```Wizard```. If you would like to remove it, you can remove the header by calling ```setHeader(null)```. A good practice is also to remove it from listening to the events. You can do it with the following code. 

```java
Component defaultHeader = wizard.getHeader();
if (defaultHeader instanceof WizardProgressListener) {
    wizard.removeListener((WizardProgressListener) defaultHeader);
}
wizard.setHeader(null);
```
Of course you can also leave the default header as a listener and attach it into your own layout. 

## Localization of the Button Captions

The wizard doesn't provide any specific support for localizing the button captions ("Next", "Back", "Finish", "Cancel"). Instead you need to assign the captions yourself. See the example below on how to provide Finnish translations.
```java
wizard.getNextButton().setCaption("Seuraava");
wizard.getBackButton().setCaption("Edellinen");
wizard.getFinishButton().setCaption("Valmis");
wizard.getCancelButton().setCaption("Peruuta");
```

## Navigation with URL Fragments

Each ```WizardStep``` gets an identifier that can be used as the URI fragment for the step. This enables you to navigate between the steps with the back/forward buttons in your browser (see the [demo application](http://teemu.virtuallypreinstalled.com/wizards-for-vaadin) for an example of this). To enable the URI fragment navigation, call ```setUriFragmentEnabled(true)``` on the ```Wizard```. To provide your own identifiers instead of using the automatically generated, you should add the ```WizardStep```s with the overloaded ```addStep(WizardStep, String)``` method.