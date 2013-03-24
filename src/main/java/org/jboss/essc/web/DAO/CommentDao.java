package org.jboss.essc.web.DAO;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.essc.web.model.Comment;
import org.jboss.essc.web.model.User;


/**
 * A bean which manages Comment entities.
 */
@Stateless
public class CommentDao {

    @PersistenceContext
    private EntityManager em;


    public List<Comment> getCommentsByUser(User user) {
        return this.em.createQuery("SELECT c FROM Comment c WHERE c.author = ?1 ORDER BY c.posted")
                .setParameter(1, user).getResultList();
    }

    /**
     * Get Comment by ID.
     */
    public Comment getComment(Long id) {
        return this.em.find(Comment.class, id);
    }


    /**
     * Remove a Comment.
     */
    public void remove(Comment cmt) {
        Comment managed = this.em.merge(cmt);
        this.em.remove(managed);
        this.em.flush();
    }

    
    public Comment update( Comment cmt ) {
        Comment managed = this.em.merge(cmt);
        return managed;
    }
   
}// class
