package org.jboss.essc.web.pages.worktags;

import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.dao.WorkDao;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.NotFoundPage;
import org.jboss.essc.web.util.GenericIdLDM;

/**
 *
 * @author Ondrej Zizka
 */
public class WorkUnitPage extends BaseLayoutPage {
    
    @Inject private WorkDao daoWork;
    @PersistenceContext EntityManager em;

    private final Long wuid;
    

    private IModel<WorkUnit> model;    

    public WorkUnitPage( PageParameters params ) {
        
        setVersioned(false);
        
        // Process the ID
        try {
            this.wuid = params.get("id").toLong();
        } catch ( NumberFormatException ex ){
            throw new RestartResponseAtInterceptPageException( new NotFoundPage("Invalid work unit id."));
        }
        if( null == this.wuid )
            throw new RestartResponseAtInterceptPageException( new NotFoundPage("Work unit id not set."));
        
        this.model = new GenericIdLDM(this.wuid, WorkUnit.class, em );
        if( null == model.getObject() )
            throw new RestartResponseAtInterceptPageException( new NotFoundPage("Work unit #"+this.wuid+" not found."));
                
        this.setDefaultModel(new CompoundPropertyModel<>(this.model));

        // Components
        add(new Label("title"));
        add(new DateLabel("created", new PatternDateConverter("yyyy-MM-dd", true)));
        add(new Label("author"));
        add(new Label("tags", new TagsToStringModel(new PropertyModel<Set<WorkTag>>(this.model, "tags"), daoWork)));
        add(new MultiLineLabel("note"));
    }
    

    
    static PageParameters params(Long id) {
        return new PageParameters().add("id", id);
    }
        
}// class
