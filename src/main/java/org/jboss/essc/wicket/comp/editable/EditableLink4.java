
package org.jboss.essc.wicket.comp.editable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
    Editable link.
    This version extends TextField.

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
public class EditableLink4<T> extends TextField<T> {
    
    private static final ResourceReference CSS = new PackageResourceReference(EditableLink4.class, "EditableLinkAndLabel.css");
    private static final ResourceReference JS  = new PackageResourceReference(EditableLink4.class, "EditableLinkAndLabel.js");

    private TextField input;
    
    public EditableLink4( String id, IModel<T> model ) {
        super( id, model );
        this.setOutputMarkupId( true );

        this.add( AttributeModifier.replace("class", "editable link passive"));

        this.add( AttributeModifier.replace("onblur", "this.passivate(true);"));
        this.add( AttributeModifier.replace("onclick",
                              "if( validateURL(this.value) && ( ! this.active ) ){\n"
                            + "    window.open(this.value, '', 'modal=true,alwaysRaised=yes'); }"
                            + "if( ! this.active && event.shiftKey )"
                            + "    this.activate();"));

        this.add( AttributeModifier.replace("onkeydown",
                              "if( ! this.active ){\n"
                            + "    if( event.shiftKey && (event.keyCode === 13 || event.keyCode === 32) )\n"
                            + "        this.activate();\n"
                            + "    if( event.charCode !== 0 )\n"
                            + "        event.preventDefault(); // Allow special keys - Home/End etc.\n"
                            + "    return false;\n"
                            + "}\n"
                            + "if(event.keyCode === 13){ this.passivate(true); } // Enter\n"));
        this.add( AttributeModifier.replace("onkeyup",
                              "if( this.active !== true ) return;\n"
                            + "if(event.keyCode === 27){\n"
                            + "    event.preventDefault();\n"
                            + "    event.stopPropagation();\n"
                            + "    this.value = this.oldValue;\n"
                            + "    this.passivate(false);\n"
                            + "}"));
    }

    
    /**
     *  Adds the necessary JavaScript and CSS to head, and the JS to initialize.
     */
    @Override
    public void renderHead( IHeaderResponse response ) {
        super.renderHead( response );
        //response.render(CssHeaderItem.forReference(CSS)); // Wicket 6
        //response.render(JavaScriptHeaderItem.forReference(JS));
        response.renderCSSReference( CSS );
        response.renderJavaScriptReference( JS );
        
        // OnLoad - initialize the element.
        setOutputMarkupId(true);
        String onLoad = String.format("EditableLink.init4( document.getElementById('%s'));", getMarkupId());
        response.renderOnDomReadyJavaScript(onLoad);
    }

}// class
