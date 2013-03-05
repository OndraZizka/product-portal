
package org.jboss.essc.web.pages.user;

import javax.inject.Inject;
import org.apache.wicket.model.LoadableDetachableModel;
import org.jboss.essc.web.dao.UserDao;
import org.jboss.essc.web.model.User;


/**
 *
 * @author ozizka@redhat.com
 */
public class UserModel extends LoadableDetachableModel<User> {
    
    @Inject private UserDao userDao;
    
    // Data
    Long id;

    public UserModel( ) {}
    public UserModel( User user ) {
        super( user );
        this.id = user.getId();
    }
    public UserModel( long id ) {
        super();
        this.id = id;
    }
    

    @Override protected User load() {
        if( id      == null )  throw new IllegalStateException("UserModel's id was not set! Use `new User(id).setDao(dao)`");
        if( userDao == null )  throw new IllegalStateException("UserModel's dao was not set! Use `new User(id).setDao(dao)`");
        return userDao.getUser( this.id );
    }

    @Override
    public void setObject( User user ) {
        this.id = user.getId();
        super.setObject( user );
    }
    
    
    
    
    public long getId() { return id; }
    public UserModel setId( long id ) { this.id = id; return this; }

    public UserDao getUserDao() { return userDao; }
    public UserModel setUserDao( UserDao daoUser ) { this.userDao = daoUser; return this; }
    
}// class
