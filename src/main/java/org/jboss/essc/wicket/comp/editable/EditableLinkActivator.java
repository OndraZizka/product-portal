
package org.jboss.essc.wicket.comp.editable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 *  Image which activates given EditableLink4 when clicked.
 *  @author Ondrej Zizka
 */
public class EditableLinkActivator extends Behavior {

    private EditableLink4 link;

    //private Component component;

    public EditableLinkActivator( EditableLink4 link ) {
        this.link = link;
    }

    // Moved here because getMarkupId() should be called after component was added to page.
    @Override
    public void onConfigure( Component hostComponent ) {
        hostComponent.add( AttributeModifier.replace("onclick", String.format("Wicket.$('%s').activate()", this.link.getMarkupId())) );
    }

    /*
    @Override
    public void bind( Component hostComponent ) {
        this.component = hostComponent;
    }/**/

}// class
