package org.jboss.essc.web.pages.worktags;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jboss.essc.web.dao.WorkTagDao;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;

/**
 * A listing of WorkUnits.
 * 
 * @author Ondrej Zizka
 */
public class WorkUnitListingPanel extends Panel {

    @Inject protected WorkTagDao dao;
    

    protected int numWorkUnits = 15;
    
    
    public WorkUnitListingPanel( String id, IModel<List<WorkUnit>> wusModel){
        super(id, wusModel);
        
        // WorkUnits table
        add( new ListView<WorkUnit>("rows", new CompoundPropertyModel(wusModel))
        {
            // Populate the table of releases
            @Override
            protected void populateItem( final ListItem<WorkUnit> item) {
                final WorkUnit wu = item.getModelObject();
                
                item.add( new DateLabel("created", Model.of(wu.getCreated()), new PatternDateConverter("yyyy-MM-dd", true)) );
                item.add( new Label("author", wu.getAuthor().getName()) );
                item.add( new Label("name",   wu.getName()) );
                //item.add( new Label("tags",   wu.getTagsAsString()) );
                item.add( new ListView<WorkTag>("tags", new ArrayList(wu.getTags())) {
                    @Override
                    protected void populateItem(ListItem<WorkTag> item) {
                        item.add( new BookmarkablePageLink("link", WorkTagPage.class, WorkTagPage.params(item.getModelObject()) ));
                    }
                } );
            }// populateItem()

        });
        
    }// const

}// class
