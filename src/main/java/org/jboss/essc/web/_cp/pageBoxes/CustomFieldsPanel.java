package org.jboss.essc.web._cp.pageBoxes;

import java.util.ArrayList;
import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.devutils.debugbar.DebugBar;
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
    ProductCustomField addedField = new ProductCustomField();

    // Components
    final FeedbackPanel feedbackPanel;


    public CustomFieldsPanel( String id, final IModel<Map<String,ProductCustomField>> fieldListModel, final FeedbackPanel feedbackPanel ) {
        
        super( id, fieldListModel );
        this.feedbackPanel = feedbackPanel;

        this.setOutputMarkupId( true );

        // Field rows.
        // new PropertyModel<ProductCustomField>(this, "fields")
        ListView<ProductCustomField> listView;
        add( listView = new ListView<ProductCustomField>("fieldsRows", new ArrayList(fieldListModel.getObject().values())){
            @Override
            protected void populateItem( final ListItem<ProductCustomField> item ) {
                item.add( new CustomFieldRowPanel("fieldRow", item.getModel()){
                    // Delete icon was clicked.
                    @Override
                    protected void onDelete( AjaxRequestTarget target ) {
                        Map<String,ProductCustomField> fieldsMap = (Map<String,ProductCustomField>) CustomFieldsPanel.this.getDefaultModelObject();
                        fieldsMap.remove( item.getModelObject().getName() );
                        target.add( CustomFieldsPanel.this ); // Update UI.
                        try {
                            CustomFieldsPanel.this.onChange( target ); // Persists.
                        } catch (Exception ex){
                            feedbackPanel.error( ex.toString() );
                        }
                    }
                });
            }
        });

        // "Add field" row.
        add( new CustomFieldRowPanel("addFieldRow", new PropertyModel<ProductCustomField>(this, "addedField")){
            { setOutputMarkupId( true ); }
            
            @Override public void onAjaxChange( AjaxRequestTarget target ) {
                target.add( CustomFieldsPanel.this );
                target.add( this );
                target.add( feedbackPanel );
                try {
                    // Add addedField to product.customFields and save to DB.
                    ProductCustomField newField  = (ProductCustomField) this.getDefaultModelObject();
                    Map<String,ProductCustomField> fields = (Map<String,ProductCustomField>) CustomFieldsPanel.this.getDefaultModelObject();
                    fields.put( newField.getName(), newField );
                    //addedField = new ProductCustomField();
                    this.setDefaultModelObject( new ProductCustomField() );

                    CustomFieldsPanel.this.onChange( target ); // Perists.
                    CustomFieldsPanel.this.info("Changed.");
                    feedbackPanel.info("Changed b."); // TODO: Leave only one.
                } catch (Exception ex){
                    feedbackPanel.error( ex.toString() );
                    DebugBar b = (DebugBar) get("debugBar");
                    b.info( ex );
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    /**
     *  Called when the fields values have changed.
     */
    protected void onChange( AjaxRequestTarget target ) { }
    
}// class
