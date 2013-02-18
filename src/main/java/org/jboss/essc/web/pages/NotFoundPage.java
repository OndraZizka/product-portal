
package org.jboss.essc.web.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 *
 *  @author Ondrej Zizka
 */
public class NotFoundPage extends BaseLayoutPage {

    public NotFoundPage( String message ) {

        add( new Label("message", message) );

    }
    
    public static PageParameters params(){ 
        return new PageParameters();
    }

}// class
