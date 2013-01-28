
package org.jboss.essc.web._cp.pageBoxes;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.jboss.essc.web._cp.pageBoxes.ReleaseCustomFieldsPanel.CustomFieldPrototypeInstanceModel;
import org.jboss.essc.wicket.comp.editable.EditableLabel;

/**
 *
 *  @author Ondrej Zizka
 */
public class ReleaseCustomFieldRowPanel extends Panel {

    public ReleaseCustomFieldRowPanel( String id, CustomFieldPrototypeInstanceModel cfpiModel ) {
        super(id, cfpiModel);

        add( new Label("name",   cfpiModel.getProtoField().getName()) );

        add( new Label("label",  cfpiModel.getProtoField().getName()) );
        
        add( new EditableLabel<String>("value", cfpiModel)
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
