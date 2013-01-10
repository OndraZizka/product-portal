
package org.jboss.essc.web.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
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
    private String ajaxUpdatedVal = "";


    public EditableTestPage() {
        add( new EditableLink("link", new PropertyModel(this, "linkHref")){
            @Override public void onChange() {
                ajaxUpdatedVal = this.getDefaultModelObjectAsString();
            }
        }.add( new EditableLinkAjaxBehavior() ) );
        add( new Label("ajaxUpdatedVal", new PropertyModel(this, "ajaxUpdatedVal") ) );
    }

}
