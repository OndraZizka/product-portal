
package org.jboss.essc.wicket.comp.editable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;

/**
 *  Image which activates given EditableLink4 when clicked.
 *  @author Ondrej Zizka
 */
public class EditableLinkActivatorImage extends Image {

    private EditableLink4 link;

    public EditableLinkActivatorImage( String id, EditableLink4 link ) {
        super( id );
        //this.setOutputMarkupPlaceholderTag(false);
        this.link = link;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add( AttributeModifier.replace("onclick", String.format("Wicket.$('%s').activate()", this.link.getMarkupId())) );
    }

}// class
