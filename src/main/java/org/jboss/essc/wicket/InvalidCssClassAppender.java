
package org.jboss.essc.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;

/**
 *
 *  @author Ondrej Zizka
 */
public class InvalidCssClassAppender extends Behavior {

    @Override
    public void bind( Component component ) {
        if( ! (component instanceof FormComponent) )
            throw new WicketRuntimeException("Only applicable to FormComponent. You tried to bound to: " + component.toString());
        super.bind( component );
    }

    @Override
    public void onComponentTag( Component component, ComponentTag tag ) {
        if( ! ((FormComponent)component).isValid() )
            tag.append("class", "invalid", " ");
    }

    public static final InvalidCssClassAppender INSTANCE = new InvalidCssClassAppender();
}
