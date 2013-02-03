package org.jboss.essc.web.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.essc.web.model.DepChangeProposal;
import org.jboss.essc.web.model.User;


/**
 * A bean which manages DepChangeProposal entities.
 */
@Stateless
public class DepChangeDao {

    @PersistenceContext
    private EntityManager em;


    public void persist( DepChangeProposal d ){
        em.persist( this );
    }

    public List<DepChangeProposal> getDepChangeProposalsByUser(User user) {
        return this.em.createQuery("SELECT d FROM DepChangeProposal d WHERE d.author = ?1 ORDER BY d.posted")
                .setParameter(1, user).getResultList();
    }

    /**
     * Get DepChangeProposal by ID.
     */
    public DepChangeProposal getDepChangeProposal(Long id) {
        return this.em.find(DepChangeProposal.class, id);
    }


    /**
     * Remove a DepChangeProposal.
     */
    public void remove(DepChangeProposal cmt) {
        DepChangeProposal managed = this.em.merge(cmt);
        this.em.remove(managed);
        this.em.flush();
    }

    
    public DepChangeProposal update( DepChangeProposal cmt ) {
        DepChangeProposal managed = this.em.merge(cmt);
        return managed;
    }
   
}// class
