
package org.jboss.essc.web._cp.pageBoxes;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.web.model.ProductCustomField;
import org.jboss.essc.wicket.comp.editable.EditableLabel;

/**
 *
 *  @author Ondrej Zizka
 */
public class CustomFieldRowPanel extends Panel {

    public CustomFieldRowPanel( String id, IModel<ProductCustomField> fieldDataModel ) {
        super(id, fieldDataModel);

        add( new EditableLabel<String>("name",  new PropertyModel(fieldDataModel.getObject(), "name"))
                .add(createOnChange(fieldDataModel))
        );
        
        add( new EditableLabel<String>("label", new PropertyModel(fieldDataModel.getObject(), "label"))
                .add(createOnChange(fieldDataModel))
        );

    }

    private AjaxFormComponentUpdatingBehavior createOnChange( final IModel<ProductCustomField> model ){
        return
        new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override protected void onUpdate( AjaxRequestTarget target ) {
                //EditableLabel<String> el = (EditableLabel)getComponent();
                //el.setModelObject("AAAAA!" + el.getModelObject());///
                //target.add(getComponent());///

                if( ! StringUtils.isBlank( model.getObject().getName() )
                 && ! StringUtils.isBlank( model.getObject().getLabel() )  // TODO: Hibernate Validator?
                )
                    CustomFieldRowPanel.this.onAjaxChange( target );
            }
        };
    }

    /**
     *  Triggered when the inner components model is changed.
     */
    protected void onAjaxChange( AjaxRequestTarget target ) {}

}
