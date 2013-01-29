package org.jboss.essc.web._cp.pageBoxes;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * A list of releases.
 * 
 * @author Ondrej Zizka
 * @deprecated   Redirect to other page instead.
 */
public class NoItemsFoundBox extends Panel {

    public NoItemsFoundBox( String id, String heading ) {
        super( id );
        add( new Label("heading", heading ));
    }

}