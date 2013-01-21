
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

 *  @author Ondrej Zizka
 */
public class EditableLink4<T> extends TextField<T> {
    
    private static final ResourceReference CSS = new PackageResourceReference(EditableLink4.class, "EditableLinkAndLabel.css");
    private static final ResourceReference JS  = new PackageResourceReference(EditableLink4.class, "EditableLinkAndLabel.js");

    private TextField input;
    
    public EditableLink4( String id, IModel<T> model ) {
        super( id, model );
        this.setOutputMarkupId( true );

        this.add( AttributeModifier.append("class", " editable link passive"));
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
