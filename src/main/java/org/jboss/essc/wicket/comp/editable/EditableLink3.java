
package org.jboss.essc.wicket.comp.editable;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
    EditableLabel and Editable link.
 *  @author Ondrej Zizka
 */
public class EditableLink3 extends Panel {
    
    private static final ResourceReference CSS = new PackageResourceReference(EditableLink3.class, "EditableLinkAndLabel.css");
    private static final ResourceReference JS  = new PackageResourceReference(EditableLink3.class, "EditableLinkAndLabel.js");

    private TextField input;
    
    public EditableLink3( String id, IModel<?> model, AjaxEventBehavior ajaxEventBehavior ) {
        super( id, new Model() );
        
        this.input = new TextField("input", model){
            // Pass the onModelChanged() call.
            @Override protected void onModelChanged() {
                EditableLink3.this.setDefaultModelObject( this.getDefaultModelObject() );
                EditableLink3.this.onModelChanged();
            }
        };
        
        // AjaxEventBehavior from the param.
        this.input.setOutputMarkupId( true );
        if( ajaxEventBehavior != null )
            this.input.add( ajaxEventBehavior );

        add( this.input );
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
        String onLoad = String.format("EditableLink.init( document.getElementById('%s'));", getMarkupId());
        response.renderOnDomReadyJavaScript(onLoad);
    }

    /**
     *  Called by AJAX behavior.
     *  Sets the default model object.
     *  Overridable.
     */
    public void onChange() {
        System.err.println("onChange(); modelObject: " + this.getDefaultModelObjectAsString() );
    }

}
