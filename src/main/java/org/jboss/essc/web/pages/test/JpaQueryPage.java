
package org.jboss.essc.web.pages.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;

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
                    result = new ArrayList(1);
                    result.add( ex );
                }
            }
        };
        add( form );

        form.add( new TextArea("query") );

        
        form.add( new ListView("result") {
            @Override
            protected void populateItem( ListItem item ) {
                item.add( new Label("item", item.getModelObject().toString() ) );
            }
        } );
        
    }// const

}// class
