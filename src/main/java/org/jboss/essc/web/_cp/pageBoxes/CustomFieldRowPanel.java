
package org.jboss.essc.web._cp.pageBoxes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jboss.essc.web.model.ProductCustomField;

/**
 *
 *  @author Ondrej Zizka
 */
public class CustomFieldRowPanel extends Panel {

    public CustomFieldRowPanel( String id, IModel<ProductCustomField> fieldDataModel ) {
        super(id, fieldDataModel);

        add( new TextField<String>("name",  new CompoundPropertyModel(fieldDataModel)).add(
            new AjaxFormComponentUpdatingBehavior("onchange") {
                @Override protected void onUpdate( AjaxRequestTarget target ) {
                    CustomFieldRowPanel.this.onAjaxChange( target );
                }
            }
        ) );
        
        add( new TextField<String>("label", new CompoundPropertyModel(fieldDataModel)) );

    }


    /**
     *  Triggered when the inner components model is changed.
     */
    protected void onAjaxChange( AjaxRequestTarget target ) {}

}
