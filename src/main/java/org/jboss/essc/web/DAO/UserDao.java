package org.jboss.essc.web.DAO;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringUtils;
import org.jboss.essc.ex.UserMailAlreadyExistsException;
import org.jboss.essc.ex.UserNameAlreadyExistsException;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.model.UserGroup;


/**
 * Manages User entities.
 */
@Stateless
public class UserDao {

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
        return getUserByName(name, false);
    }
    
    public User getUserByName( String name, boolean withGroups ) throws NoResultException {
        String ljf = withGroups ? "LEFT JOIN FETCH u.groups" : "";
        return this.em.createQuery("SELECT u FROM User u "+ljf+" WHERE u.name = ?1", User.class).setParameter(1, name).getSingleResult();
    }
    
    public User getUserByMail(String mail) {
        return this.em.createQuery("SELECT u FROM User u WHERE u.mail = ?1", User.class).setParameter(1, mail).getSingleResult();
    }
    
    
    /**
     * Get User, verify the password or temp password.
     * @returns User if auth succeeded.
     */
    public User loadUserIfPasswordMatches( User user ) {
        return this.em.createQuery(
            "SELECT u FROM User u LEFT JOIN FETCH u.groups "
            + " WHERE u.name = :name AND (u.passHash = :passHash OR u.passTemp = :passHash)", User.class)
                .setParameter("name",     user.getName())
                .setParameter("passHash", user.rehashPass())
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

    /**
     *  Sets all temp passwords to NULL. To be executed regularly.
     */
    public void eraseTempPasswords() {
        em.createQuery("UPDATE User u SET u.passTemp = NULL").executeUpdate();
    }
    
    /**
     *  @returns true if given user is a member of given user group.
     */
    public boolean isUserInGroup( User user, UserGroup group ){
        List res = em.createQuery("SELECT 1 FROM User u, UserGroup g WHERE u = :user AND g = :group AND g MEMBER OF u.groups")
                .setParameter("user", user)
                .setParameter("group", group)
                .getResultList();
        return res.size() > 0;
    }

    /**
     *  @returns a group of given name, or null if not found.
     */
    public UserGroup findGroupByName( String name ){
        List<UserGroup> grps = em.createQuery("SELECT g FROM UserGroup g WHERE g.name = :name", UserGroup.class)
                .setParameter("name", name)
                .getResultList();
        return grps.isEmpty() ? null : grps.get(0);
    }

    /**
     *  @param  prefix  Group name prefix. If it doesn't end with . or .*, '.' is appended.
     *  @returns the groups whose name is given prefix or starts with given prefix with a '.' immediately following it.
     * 
     *  Ex.  Prefix "foo" will match "foo" and "foo.bar", but not "foobar.baz".
     */
    public List<UserGroup> getGroupsByPrefix( String prefix ){
        if( null == prefix )  throw new IllegalArgumentException("prefix was null.");

        // Remove trailing . or .*
        StringUtils.chomp(prefix, ".");
        StringUtils.chomp(prefix, ".*");
        
        List<UserGroup> grps = em.createQuery("SELECT g FROM UserGroup g WHERE g.name = :prefix OR g.name LIKE CONCAT(:prefix, '.%')", UserGroup.class)
                .setParameter("prefix", prefix)
                .getResultList();
        return grps;
    }

}// class
