
package org.jboss.essc.web._cp.pageBoxes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.web.model.ReleaseCustomField;
import org.jboss.essc.wicket.comp.editable.EditableLabel;

/**
 *
 *  @author Ondrej Zizka
 */
public class ReleaseCustomFieldRowPanel extends Panel {

    public ReleaseCustomFieldRowPanel( String id, IModel<ReleaseCustomField> fieldModel ) {
        super(id, fieldModel);

        add( new Label("name",   new PropertyModel(fieldModel, "field.name")) );

        add( new Label("label",  new PropertyModel(fieldModel, "field.label")) );
        
        add( new EditableLabel<String>("value", new PropertyModel(fieldModel, "value"))
            .add(
                new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override protected void onUpdate( AjaxRequestTarget target ) {
                        ReleaseCustomFieldRowPanel.this.onAjaxChange( target );
                    }
                }
            )
        );
    }


    /**
     *  Triggered when the inner components model is changed.
     */
    protected void onAjaxChange( AjaxRequestTarget target ) {}

}
