
package org.jboss.essc.web.pages.prod.co;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.image.Image;
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

        add( new EditableLabel<String>("name",  new PropertyModel(fieldDataModel, "name"))
                .add(createOnChange(fieldDataModel))
        );
        
        add( new EditableLabel<String>("label", new PropertyModel(fieldDataModel, "label"))
                .add(createOnChange(fieldDataModel))
        );
        add( new Image("delete", "icoTrash.png").add( new AjaxEventBehavior("onclick") {
            @Override protected void onEvent( AjaxRequestTarget target ) {
                ProductCustomField field = (ProductCustomField) CustomFieldRowPanel.this.getDefaultModelObject();
                onDelete( field.getName(), target );
            }
        }) );

    }


    /**  Called when delete icon is clicked. */
    protected void onDelete( String name, AjaxRequestTarget target ) {}


    /* Creates onchange handlers for name and label textfields. */
    private AjaxFormComponentUpdatingBehavior createOnChange( final IModel<ProductCustomField> parentModel ){
        return
        new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override protected void onUpdate( AjaxRequestTarget target ) {
                //EditableLabel<String> el = (EditableLabel)getComponent();
                //el.setModelObject("AAAAA!" + el.getModelObject());///
                //target.add(getComponent());///
                Object obj1 = getComponent().getDefaultModelObject();
                Object obj2 = parentModel.getObject();

                if(        StringUtils.isBlank( parentModel.getObject().getName() )
                        || StringUtils.isBlank( parentModel.getObject().getLabel() ) ) // TODO: Hibernate Validator?
                    return;

                CustomFieldRowPanel.this.onAjaxChange( target );
            }
        };
    }

    /**
     *  Triggered when the inner components model is changed.
     */
    protected void onAjaxChange( AjaxRequestTarget target ) {}

}
