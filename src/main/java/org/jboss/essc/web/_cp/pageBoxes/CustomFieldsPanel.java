package org.jboss.essc.web._cp.pageBoxes;

import java.util.ArrayList;
import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.essc.web.model.ProductCustomField;


/**
 *  A box with release traits OR product templates.
 * 
 * @author Ondrej Zizka
 */
public class CustomFieldsPanel extends Panel {

    // Data
    //final List<ProductCustomField> fields;
    final ProductCustomField addedField = new ProductCustomField();

    // Components
    final FeedbackPanel feedbackPanel;


    public CustomFieldsPanel( String id, final IModel<Map<String,ProductCustomField>> fieldListModel, final FeedbackPanel feedbackPanel ) {
        
        super( id, fieldListModel );
        this.feedbackPanel = feedbackPanel;

        // Field rows.
        // new PropertyModel<ProductCustomField>(this, "fields")
        add( new ListView<ProductCustomField>("fieldsRows", new ArrayList(fieldListModel.getObject().values())){
            @Override
            protected void populateItem( ListItem<ProductCustomField> item ) {
                item.add( new CustomFieldRowPanel("field", item.getModel()) );
            }
        });

        // "Add field" row.
        add( new CustomFieldRowPanel("addFieldRow", new PropertyModel<ProductCustomField>(this, "addedField")){
            @Override public void onAjaxChange( AjaxRequestTarget target ) {
                try {
                    // TODO: Add addedField to product.customFields and save to DB.
                    ProductCustomField newField  = (ProductCustomField) this.getDefaultModelObject();
                    Map<String,ProductCustomField> fields = (Map<String,ProductCustomField>) CustomFieldsPanel.this.getDefaultModelObject();
                    fields.put( newField.getName(), newField );
                    //productDao.update( product );
                    CustomFieldsPanel.this.onChange();
                } catch (Exception ex){
                    feedbackPanel.error( ex.toString() );
                }
            }
        });
    }

    /**
     *  Called when the fields values have changed.
     */
    protected void onChange(){}
    
}// class
