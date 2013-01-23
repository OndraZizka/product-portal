
package org.jboss.essc.web.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.jboss.essc.web.pages.BaseLayoutPage;

/**
 *
 *  @author Ondrej Zizka
 */
public class NotFoundPage extends BaseLayoutPage {

    public NotFoundPage( String message ) {

        add( new Label("message", message) );

    }

}
