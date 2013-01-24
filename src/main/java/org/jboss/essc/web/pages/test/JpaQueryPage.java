
package org.jboss.essc.web.pages.test;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.jboss.essc.web.model.ProductCustomField;
import org.jboss.essc.web.model.ReleaseCustomField;

/**
 *
 *  @author Ondrej Zizka
 */
public class JpaQueryPage extends WebPage {
    
    @PersistenceContext EntityManager em;

    private String query = "";

    private List<Object> result = Collections.EMPTY_LIST;

    public JpaQueryPage() {

        Form form = new Form("form", new CompoundPropertyModel(this) ){
            @Override protected void onSubmit() {
                try {
                    result = em.createQuery( query ).getResultList();
                }
                catch (Exception ex){
                    result = new ArrayList(2);
                    result.add( ex );
                    result.add( query );
                }
            }
        };
        add( form );

        form.add( new TextArea("query") );
        this.query = 
                "SELECT rel, rel.product FROM Release rel\n"
                + " LEFT JOIN FETCH rel.product.customFields\n"
                + " LEFT OUTER JOIN FETCH rel.customFields\n"
                + " WHERE rel.product.name = 'EAP' AND rel.version = '6.0.1.GA'";

        
        form.add( new ListView("result") {
            @Override
            protected void populateItem( ListItem item ) {
                Object obj = item.getModelObject();
                String content;
                // Exceptions.
                if( obj instanceof Exception ){
                    content = ((Exception)obj).toString();
                    item.add(  AttributeModifier.replace( "style", "white-space: normal") );
                }
                // Other objects - JSON.
                else {
                    try {
                        content = toJSON( obj );
                    } catch (Throwable ex){
                        content = "toJSON() threw " + ex.toString() + "\n";
                        try {
                            content += obj.toString();
                        } catch (Exception ex2) {
                            content += obj.getClass() + " but toString() threw: " + ex2.toString();
                        }
                    }
                }
                item.add( new Label("item", content) );
            }
        } );
        
    }// const


    /**
     *  Convert any object to JSON.
     */
    private static String toJSON( Object object ) {
        //Gson gson = new Gson();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .addDeserializationExclusionStrategy( new ExclusionStrategy() {
                    @Override public boolean shouldSkipField( FieldAttributes f ) {
                        return false;
                    }
                    @Override public boolean shouldSkipClass( Class<?> clazz ) {
                        if( ReleaseCustomField.class.equals( clazz ) )
                                return true;
                        if( ProductCustomField.class.equals( clazz ) )
                                return true;
                        return false;
                    }
                } )
                .create();
        return gson.toJson(object);
    }

    /**
     *  Convert any object to JSON, using Jackson.
     */
    private static String toJSON2( Object object ) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .addDeserializationExclusionStrategy( new ExclusionStrategy() {
                    @Override public boolean shouldSkipField( FieldAttributes f ) {
                        return false;
                    }
                    @Override public boolean shouldSkipClass( Class<?> clazz ) {
                        if( ReleaseCustomField.class.equals( clazz ) )
                                return true;
                        if( ProductCustomField.class.equals( clazz ) )
                                return true;
                        return false;
                    }
                } )
                .create();
        return gson.toJson(object);
    }

}// class
