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
import org.jboss.essc.web.DAO.WorkDao;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;
import org.jboss.essc.web.pages.user.UserAccountPage;
import org.jboss.essc.web.pages.user.UserProfilePage;

/**
 * A listing of WorkUnits.
 * 
 * @author Ondrej Zizka
 */
public class WorkUnitListingPanel extends Panel {

    @Inject protected WorkDao dao;
    

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
                item.add( new BookmarkablePageLink("author", UserProfilePage.class, UserProfilePage.params(wu.getAuthor().getName()))
                        .add( new Label("label", wu.getAuthor().getName()) ) 
                );
                item.add( new BookmarkablePageLink("title",  WorkUnitPage.class, WorkUnitPage.params(wu.getId()))
                        .add( new Label("label", wu.getTitle()) ) 
                );
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
