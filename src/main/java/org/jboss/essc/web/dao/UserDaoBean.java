package org.jboss.essc.web.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.jboss.essc.ex.UserMailAlreadyExistsException;
import org.jboss.essc.ex.UserNameAlreadyExistsException;
import org.jboss.essc.web.model.User;


/**
 * A bean which manages Contact entities.
 */
@Stateless
public class UserDaoBean {

    @PersistenceContext
    private EntityManager em;


    public List<User> getUsers_orderName(int limit) {
        return this.em.createQuery("SELECT u FROM User u ORDER BY u.name").getResultList();
    }

    /**
     * Get User by ID.
     */
    public User getUser(Long id) {
        return this.em.find(User.class, id);
    }

    /**
     * Get User by name.
     */
    public User getUserByName( String name ) throws NoResultException {
        return this.em.createQuery("SELECT u FROM User u WHERE u.name = ?1", User.class).setParameter(1, name).getSingleResult();
    }
    
    public User getUserByMail(String mail) {
        return this.em.createQuery("SELECT u FROM User u WHERE u.mail = ?1", User.class).setParameter(1, mail).getSingleResult();
    }
    
    
    /**
     * Get User by name.
     */
    public User loadUserIfPasswordMatches( User user ) {
        return this.em.createQuery("SELECT u FROM User u WHERE u.name = :name AND u.pass = MD5(:pass)", User.class)
                .setParameter("name", user.getName())
                .setParameter("pass", user.getPass())
                .getSingleResult();
    }
    

    /**
     * Add a new User.
     */
    public User addUser( User user ) throws UserNameAlreadyExistsException, UserMailAlreadyExistsException {
        if( this.userNameExists(user.getName())){
            throw new UserNameAlreadyExistsException("User name already exists: " + user.getName());
        }
        if( this.userNameExists(user.getName())){
            throw new UserMailAlreadyExistsException("User with email already exists: " + user.getMail());
        }
        return this.em.merge( user );
    }

    /**
     * Remove a User.
     */
    public void remove(User user) {
        User managed = this.em.merge(user);
        this.em.remove(managed);
        this.em.flush();
    }

    
    public User update( User user ) {
        User managed = this.em.merge(user);
        return managed;
    }

    public boolean userNameExists(String name) {
        List list = this.em.createQuery("SELECT 1 FROM User u WHERE u.name = :name", Object.class)
                .setParameter("name", name) .getResultList();
        return ! list.isEmpty();
    }
    
    public boolean userMailExists(String mail) {
        List list = this.em.createQuery("SELECT 1 FROM User u WHERE u.mail = :mail", Object.class)
                .setParameter("mail", mail) .getResultList();
        return ! list.isEmpty();
    }

}// class
