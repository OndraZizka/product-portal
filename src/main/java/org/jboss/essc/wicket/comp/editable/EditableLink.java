
package org.jboss.essc.wicket.comp.editable;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
    EditableLabel and Editable link.

    They have two states:

      * Active  - editation enabled, behaves like an input box.
      * Passive - editation disabled, behaves like a label or link.

    EditableLink can get activated by:
      * External call to activate().
      * Shift + click. DONE: it was caused by user-select.

    EditableLabel can get enabled simply by clicking on it.

    Both can be passivated by:
     * Pressing Enter - leaves the new value.
     * Pressing Escape - reverts to original value.
     * Removing focus - by clicking elsewhere or pressing Tab.

    The "disabled" property is not used as it blocks all events in Firefox.
    The "readOnly" property is used when passivated.
    The "user-select" CSS property is not used, it would prevent Home/End.
    Escape needs to be caught in "onkeyup", otherwise value change would be overriden.

    Known issues:
     * Home/End don't work in EditableLink.

    Resources:
     * Key events demo: http://www.javascripter.net/faq/keyboardeventproperties.htm
     * MartinG gave me this as example of AJAXifying:
       Behavior: https://github.com/wicketstuff/core/blob/master/jdk-1.6-parent/autocomplete-tagit-parent/autocomplete-tagit/src/main/java/org/wicketstuff/tagit/TagItAjaxBehavior.java
       JS:       https://github.com/wicketstuff/core/blob/master/jdk-1.6-parent/autocomplete-tagit-parent/autocomplete-tagit/src/main/resources/org/wicketstuff/tagit/res/tag-it.tmpl.js


 *  @author Ondrej Zizka
 */
public class EditableLink extends Panel {
    
    private static final ResourceReference CSS = new PackageResourceReference(EditableLink.class, "EditableLinkAndLabel.css");
    private static final ResourceReference JS = new PackageResourceReference(EditableLink.class, "EditableLinkAndLabel.js");

    
    public EditableLink( String id, IModel<?> model ) {
        super( id, model );
    }


    @Override
    public void renderHead( IHeaderResponse response ) {
        super.renderHead( response );
        //response.render(CssHeaderItem.forReference(CSS)); // Wicket 6
        //response.render(JavaScriptHeaderItem.forReference(JS));
        response.renderCSSReference( CSS );
        response.renderJavaScriptReference( JS );
        
        // OnLoad - initialize the element.
        setOutputMarkupId(true);
        String onLoad = String.format("EditableLink.init( document.getElementById('%s'));", getMarkupId());
        response.renderOnDomReadyJavaScript(onLoad);
    }

    /**
     *  Called by AJAX behavior.
     *  Sets the default model object.
     *  Overridable.
     */
    public void onChange() {
        System.out.println("onChange(); modelObject: " + this.getDefaultModelObjectAsString() );
    }

}
