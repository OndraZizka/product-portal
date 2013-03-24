package org.jboss.essc.web.pages.rel.co;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jboss.essc.web.DAO.ReleaseDao;
import org.jboss.essc.web.model.ProductCustomField;
import org.jboss.essc.web.model.Release;
import org.jboss.essc.web.model.ReleaseCustomField;

/**
 *
 *  @author Ondrej Zizka
 */
public class ReleaseCustomFieldsPanel extends Panel {

    @Inject private ReleaseDao releaseDao;
    

    //public ReleaseCustomFieldsPanel( String id, IModel<List<ReleaseCustomField>> listModel ) {
    public ReleaseCustomFieldsPanel( String id, final IModel<Release> releaseModel ) {
        super( id, releaseModel );

        Map<String, ProductCustomField> customFields = releaseModel.getObject().getProduct().getCustomFields();

        add( new ListView<ProductCustomField>("fieldsRows", new ArrayList(customFields.values())){
            @Override
            protected void populateItem( final ListItem<ProductCustomField> item ) {
                CustomFieldPrototypeInstanceModel cfpiModel = new CustomFieldPrototypeInstanceModel(item.getModelObject(), releaseModel.getObject());
                cfpiModel.setDao(releaseDao);
                item.add( new ReleaseCustomFieldRowPanel("fieldRow", cfpiModel){
                    @Override protected void onAjaxChange( AjaxRequestTarget target ) {
                        ReleaseCustomFieldsPanel.this.onAjaxChange( target, item );
                    }
                });
            }
        });

    }

    /** Called when some field`s row`s input value changes. */
    protected void onAjaxChange( AjaxRequestTarget target, ListItem<ProductCustomField> item ) { }
    

    /**
     *  Model which gives instance's value if exists, otherwise prototype's value.
     */
    static class CustomFieldPrototypeInstanceModel implements IModel<String> {

        private ReleaseDao releaseDao; // Not injected by wicket-cdi; -> via set().

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
                instanceField = new ReleaseCustomField(this.release, this.protoField);
                instanceField.setValue( value );
                this.release.getCustomFields().put( protoField.getName(), instanceField );
                //this.releaseDao.storeReleaseCustomField( instanceField ); // TRYING
            }
            else
                instanceField.setValue( value );
        }

        public ProductCustomField getProtoField() {
            return protoField;
        }

        

        @Override public void detach() { }

        private void setDao( ReleaseDao releaseDao ) {
            this.releaseDao = releaseDao;
        }
    }

    

    /**  Type-safe wrap of getDefaultModel(). */
    protected IModel<List<ReleaseCustomField>> getModel(){
        return (IModel<List<ReleaseCustomField>>) this.getDefaultModel();
    }

}
