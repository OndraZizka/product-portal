package org.jboss.essc.web._cp.pageBoxes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jboss.essc.web.model.ProductCustomField;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.model.ReleaseCustomField;

/**
 *
 *  @author Ondrej Zizka
 */
public class ReleaseCustomFieldsPanel extends Panel {

    //public ReleaseCustomFieldsPanel( String id, IModel<List<ReleaseCustomField>> listModel ) {
    public ReleaseCustomFieldsPanel( String id, final IModel<Release> releaseModel ) {
        super( id, releaseModel );

        Map<String, ProductCustomField> customFields = releaseModel.getObject().getProduct().getCustomFields();

        add( new ListView<ProductCustomField>("fieldsRows", new ArrayList(customFields.values())){
            @Override
            protected void populateItem( final ListItem<ProductCustomField> item ) {
                CustomFieldPrototypeInstanceModel cfpiModel = new CustomFieldPrototypeInstanceModel(item.getModelObject(), releaseModel.getObject());
                item.add( new ReleaseCustomFieldRowPanel("fieldRow", cfpiModel));
            }
        });

    }

    /**
     *  Model which gives instance's value if exists, otherwise prototype's value.
     */
    static class CustomFieldPrototypeInstanceModel implements IModel<String> {

        ProductCustomField protoField;
        Release release;

        public CustomFieldPrototypeInstanceModel( ProductCustomField protoField, Release release ) {
            this.protoField = protoField;
            this.release = release;
        }

        @Override public String getObject() {
            //ProductCustomField protoField = this.release.getProduct().getCustomFields.get( this.name );
            //if( null == protoField )
            //    return null;
            ReleaseCustomField instanceField = this.release.getCustomFields().get( protoField.getName() );
            if( null == instanceField ){
                return protoField.getDefaultValue();
                //return "";
            }
            return instanceField.getEffectiveValue();
        }

        @Override public void setObject( String value ) {
            ReleaseCustomField instanceField = this.release.getCustomFields().get( protoField.getName() );
            if( null == instanceField ){
                this.release.getCustomFields().put(
                        protoField.getName(),
                        instanceField = new ReleaseCustomField(this.release, this.protoField) );
            }
            instanceField.setValue( value );
        }

        public ProductCustomField getProtoField() {
            return protoField;
        }

        

        @Override public void detach() { }
    }

    
    /**
     *  Model which gives instance's value if exists, otherwise prototype's value.
     */
    class xCustomFieldPrototypeInstanceModel implements IModel<String> {

        Map<String, ProductCustomField> prototype;
        ReleaseCustomField instanceField;

        public xCustomFieldPrototypeInstanceModel( ReleaseCustomField instanceField ) {
            this.instanceField = instanceField;
        }

        @Override public String getObject() {
            //ProductCustomField protoField = this.prototype.get( this.instanceField.getName() );
            //if( null == protoField )
            //    return null;
            return this.instanceField.getEffectiveValue();
        }

        @Override public void setObject( String value ) {
            this.instanceField.setValue( value );
        }

        @Override public void detach() { }

    }


    protected IModel<List<ReleaseCustomField>> getModel(){
        return (IModel<List<ReleaseCustomField>>) this.getDefaultModel();
    }

}
