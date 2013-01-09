
package org.jboss.essc.web.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.wicket.comp.editable.EditableLink;
import org.jboss.essc.wicket.comp.editable.EditableLinkAjaxBehavior;

/**
 *
 *  @author Ondrej Zizka
 */
public class EditableTestPage extends WebPage {

    // State

    private String linkHref = "http://ondra.zizka.cz/";


    public EditableTestPage() {
        add( new EditableLink("link", new PropertyModel(this, "linkHref")).add( new EditableLinkAjaxBehavior() ) );
        //add( new WebMarkupContainer("link") );
    }

}
