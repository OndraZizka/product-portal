package org.jboss.essc.web.pages.user;

import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.dao.UserDaoBean;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.model.UserGroup;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.NotFoundPage;

/**
 *
 * @author ondra
 */
public class UserProfilePage extends BaseLayoutPage {
    
    //@PersistenceContext private EntityManager em;
    @Inject private UserDaoBean daoUser;
    

    public static PageParameters params(String name) {
        return new PageParameters().set("name", name);
    }

    public UserProfilePage( PageParameters params ) {
        
        // Show given user, or current, if logged in.
        User user = getSession().getUser();
        
        String userName = params.get("name").toString();
        if( null != userName ){
            user = daoUser.getUserByName(userName, true);
            if( null == user )
                throw new RestartResponseException( new NotFoundPage("User not found: " + userName) );
        }
        
        if( null == user )
            throw new RestartResponseException( new NotFoundPage("User name was not specified and you're not logged in.") );
        
        init( user );
    }

    
    /** Inits components. */
    private void init(User user) {
        
        this.add(new FeedbackPanel("feedback"));
        
        this.setDefaultModel( new CompoundPropertyModel(user) );
        
        this.add(new Label("name") );

        // Comma-separated groups.
        StringBuilder sb = new StringBuilder();
        for( UserGroup g : user.getGroups() )
            sb.append( g.getName() ).append(", ");
        String groups = StringUtils.chomp( sb.toString(), ", ");
        this.add(new Label("groups", groups));
    }
    
}// class
