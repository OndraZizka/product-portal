
package org.jboss.essc.web.test;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.wicket.comp.editable.EditableLink;
import org.jboss.essc.wicket.comp.editable.EditableLink2;
import org.jboss.essc.wicket.comp.editable.EditableLinkAjaxBehavior;

/**
 *
 *  @author Ondrej Zizka
 */
public class EditableTestPage extends WebPage {

    // State

    private String linkHref = "http://ondra.zizka.cz/";
    private String ajaxUpdatedVal = "orig val";
    private String link2Href = "link2Href orig val";


    public EditableTestPage() {

        // Link1
        EditableLink link = new EditableLink("link", new PropertyModel(this, "linkHref")){
            @Override public void onChange() {
                ajaxUpdatedVal = (String) this.getDefaultModelObject();
            }
        };
        link.add( new EditableLinkAjaxBehavior() );

        add( link );

        add( new Label("ajaxUpdatedVal", new PropertyModel(this, "ajaxUpdatedVal") ).setOutputMarkupId(true) );

        add( new Label("pageInfo", new AbstractReadOnlyModel<String>() {
            @Override public String getObject() {
                return "Page ID: " + EditableTestPage.this.getPageId() + " #: " + EditableTestPage.this.hashCode();
            }
        } ) );

        // Link2
        add( new EditableLink2("link2", new PropertyModel(this, "link2Href"), new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                Component show2 = getPage().get("ajaxUpdatedVal2");
                show2.setDefaultModelObject( getComponent().getDefaultModelObject() );
                target.add( show2 );
            }
        } ) );

        add( new Label("ajaxUpdatedVal2", new Model("orig val 2") ).setOutputMarkupId(true) );

    }

}
