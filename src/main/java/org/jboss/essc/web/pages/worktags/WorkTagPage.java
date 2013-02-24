package org.jboss.essc.web.pages.worktags;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.dao.WorkDao;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.NotFoundPage;
import org.jboss.essc.web.pages.user.UserProfilePage;

/**
 *
 * @author Ondrej Zizka
 */
public class WorkTagPage extends BaseLayoutPage {

    @Inject private WorkDao daoWork;
    @PersistenceContext EntityManager em;

    
    // Data
    private String name;
    
    private IModel<WorkTag> model;
    
    
    public WorkTagPage( PageParameters params ) {
        
        setVersioned(false);
        
        // Process the ID
        this.name = params.get("name").toString();
        if( null == this.name )
            throw new RestartResponseAtInterceptPageException( new NotFoundPage("Work tag name not set."));
        
        // Model
        this.model = new LoadableDetachableModel<WorkTag>() {
            @Override protected WorkTag load() {
                return daoWork.findTagByName(name);
            }
        };
        
        if( null == model.getObject() )
            throw new RestartResponseAtInterceptPageException( new NotFoundPage("Work tag #"+this.name+" not found."));
                
        this.setDefaultModel(new CompoundPropertyModel<>(this.model));
        
        // Components
        
        add( new Label("name") );
        
        // Authors of most WorkUnits with this tag.
        add( new ListView<User>("authors", new LoadableDetachableModel<List<User>>() {
                @Override protected List<User> load() {
                    return daoWork.getTopAuthorsOfWorkUnitsWithTag( name, 10 );
                }
            }){
                @Override protected void populateItem(ListItem<User> item) {
                    User user = item.getModelObject();
                    item.add( new BookmarkablePageLink("link", UserProfilePage.class, UserProfilePage.params(user.getName()))
                            .add( new Label("label", user.getName()))
                            .add( new Label("tagUsagesCount", "6"))
                    );
                }
            }
        );
        
        // WorkUnits with this tag.
        add( new ListView<WorkUnit>("workUnits", new LoadableDetachableModel<List<WorkUnit>>() {
                @Override protected List<WorkUnit> load() {
                    return daoWork.getWorkUnitsWithTag( name );
                }
            }){
                @Override protected void populateItem(ListItem<WorkUnit> item) {
                    WorkUnit wu = item.getModelObject();
                    item.add( new BookmarkablePageLink("link", WorkUnitPage.class, WorkUnitPage.params(wu.getId()))
                            .add( new Label("label", wu.getTitle()))
                    );
                }
            }
        );
        
    }

    static PageParameters params(WorkTag tag) {
        return new PageParameters().add("name", tag.getName());
    }
    
}// class
