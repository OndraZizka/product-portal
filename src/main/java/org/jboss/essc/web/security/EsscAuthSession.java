
package org.jboss.essc.web.security;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.jboss.essc.web.DAO.UserDao;
import org.jboss.essc.web.model.User;


/**
 * wicket-auth-roles -based session auth.
 * 
 * @author ozizka@redhat.com
 */
public class EsscAuthSession extends AuthenticatedWebSession {
    
    @Inject private UserDao userDao;
    
    
    private User user;
    
    private EsscSettings settings = new EsscSettings();
    
    

    public EsscAuthSession( Request request ) {
        super( request );
    }

    
    @Override
    public void signOut() {
        user = null;
        super.signOut();
    }

    
    @Override
    public boolean authenticate( String name, String pass ) {
        if( this.user != null )  return true;
        return authenticate( new User( name, pass ) );
    }

    public boolean authenticate( User user_) {
        if( this.user != null )  return true;

        try {
            this.user = userDao.loadUserIfPasswordMatches( user_ );
            return true;
        } catch (NoResultException ex){
            return false;
        }
    }
    
    
    

    @Override
    public Roles getRoles() {
        if( ! isSignedIn() )  return null;
        
        // If the user is signed in, they have these roles
        //return new Roles( Roles.ADMIN ); // TODO
        return new Roles( (String[]) getUser().getGroupsNames().toArray());
    }

    public EsscSettings getSettings() { return settings; }
    
    /**  @returns currently logged user (full object), or null. */
    public User getUser() { return user; }
    public void setUser( User user ) { this.user = user; }

    public boolean isUserInGroup_Prefix(String groupPrefix) {
        if( getUser() == null )  return false;
        return getUser().isInGroups_Prefix(groupPrefix);
    }

    public boolean isUserInGroup_Pattern(String groupPattern) {
        if( getUser() == null )  return false;
        return getUser().isInGroups_Pattern(groupPattern);
    }

}// class
