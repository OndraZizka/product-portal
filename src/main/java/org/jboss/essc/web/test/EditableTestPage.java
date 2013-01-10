
package org.jboss.essc.web.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
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
    private String ajaxUpdatedVal = "orig val";


    public EditableTestPage() {

        EditableLink link = new EditableLink("link", new PropertyModel(this, "linkHref")){
            @Override public void onChange() {
                ajaxUpdatedVal = (String) this.getDefaultModelObject();
            }
        };
        link.add( new EditableLinkAjaxBehavior() );

        add( link );

        add( new Label("ajaxUpdatedVal", new PropertyModel(this, "ajaxUpdatedVal") ) );

        add( new Label("pageInfo", new AbstractReadOnlyModel<String>() {
            @Override public String getObject() {
                return "Page ID: " + EditableTestPage.this.getPageId() + " #: " + EditableTestPage.this.hashCode();
            }
        } ) );
    }

}
