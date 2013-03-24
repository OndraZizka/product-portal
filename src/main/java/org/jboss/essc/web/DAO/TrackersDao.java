package org.jboss.essc.web.DAO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import org.jboss.essc.integ.trackers.model.ExternalVersionInfo;

/**
 *  Manages external issue trackers data.
 */
@Stateless
public class TrackersDao {

    @PersistenceContext private EntityManager em;

    
    /**
     *  Adds version info if not exists.
     */
    public boolean addVersionInfoIfNew(ExternalVersionInfo verInfo) {
        if( versionExists(verInfo) ) return false;
        em.persist( verInfo );
        return true;
    }
    

    /**
     *  Does it exist?
     */
    private boolean versionExists( ExternalVersionInfo verInfo ) {
        
        if( verInfo.getProject() == null )
            throw new IllegalArgumentException("Querying existence of version with no project set.");
        if( verInfo.getName() == null )
            throw new IllegalArgumentException("Querying existence of version with no name set.");
        
        return this.em.createQuery("SELECT COUNT(*) FROM ExternalVersionInfo ver WHERE ver.name = ?1 AND ver.project = ?2", Long.class)
                .setParameter(1, verInfo.getName())
                .setParameter(2, verInfo.getProject())
                .getSingleResult() != 0;
    }

    public ExternalProjectInfo getOrCreateProjectInfo(ExternalProjectInfo projInfo) {
        for( ExternalProjectInfo p : 
                em.createQuery("SELECT p FROM ExternalProjectInfo p WHERE p.externalId = ?1", ExternalProjectInfo.class)
                .setParameter(1, projInfo.getExternalId())
                .getResultList() ){
            return p;
        }
        
        em.persist( projInfo );
        return projInfo;
    }


}// class
