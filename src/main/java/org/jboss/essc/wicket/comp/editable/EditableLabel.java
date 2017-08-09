
package org.jboss.essc.wicket.comp.editable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

/**
    Editable label.
    This version extends TextField.

 *  @author Ondrej Zizka
 */
public class EditableLabel<T> extends TextField<T> {
    
    private static final ResourceReference CSS = new PackageResourceReference(EditableLabel.class, "EditableLinkAndLabel.css");
    private static final ResourceReference JS  = new PackageResourceReference(EditableLabel.class, "EditableLinkAndLabel.js");

    
    // For CompoundPropertyModel
    public EditableLabel(String id) {
        super(id);
    }
    
    public EditableLabel( String id, IModel<T> model ) {
        super( id, model );
        this.setOutputMarkupId( true );

        this.add( AttributeModifier.replace("class", "editable label passive"));
    }

    
    /**
     *  Adds the necessary JavaScript and CSS to head, and the JS to initialize.
     */
    @Override
    public void renderHead( IHeaderResponse response ) {
        super.renderHead( response );
        response.render(CssHeaderItem.forReference(CSS));
        response.render(JavaScriptHeaderItem.forReference(JS));
        
        // OnLoad - initialize the element.
        setOutputMarkupId(true);
        String onLoad = String.format("EditableLink.init4( document.getElementById('%s'));", getMarkupId());
        response.render(OnDomReadyHeaderItem.forScript(onLoad));
    }

}// class
