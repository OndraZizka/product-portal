/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jboss.essc.web.pages.user;

import javax.inject.Inject;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jboss.essc.web.dao.UserDaoBean;
import org.jboss.essc.web.model.User;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public UserProfilePage( PageParameters params ) {
        
        String userName = params.get("name").toString();
        if( null == userName )
            throw new RestartResponseException( new NotFoundPage("User name was not specified.") );
        
        User user = daoUser.getUserByName(userName);
        if( null == user )
            throw new RestartResponseException( new NotFoundPage("User not found: " + userName) );
        
        init( user );
    }

    
    private void init(User user) {
        
        this.add(new FeedbackPanel("feedback"));
        
        this.setDefaultModel( new CompoundPropertyModel(user) );
        this.add(new Label("name") );
    }
    
}// class
