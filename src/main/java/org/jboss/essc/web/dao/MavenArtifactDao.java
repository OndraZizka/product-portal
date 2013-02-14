package org.jboss.essc.web.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.jboss.essc.web.model.MavenArtifact;
import org.jboss.essc.web.model.Release;

/**
 * A bean which manages Contact entities.
 */
@Stateless
public class MavenArtifactDao {

    public static final boolean WITH_DEPS = true;
    public static final boolean WITHOUT_DEPS = false;
    
    
    @PersistenceContext private EntityManager em;


    public List<Release> getDepsOfRelease( Release rel) {
        return this.em.createQuery("SELECT ma FROM MavenArtifact ma, Release rel WHERE ma MEMBER OF rel.deps AND rel = ?1")
                .setParameter(1, rel).getResultList();
    }

    
    /**
     * Get Release by ID.
     */
    public MavenArtifact getMavenArtifact(Long id) {
        return this.em.find(MavenArtifact.class, id);
    }
    
    
    public MavenArtifact findMavenArtifact(MavenArtifact ma) {
        return this.findMavenArtifact(ma.getGroupId(), ma.getGroupId(), ma.getVersion(), ma.getPackaging(), ma.getClassifier());
    }
    
    
    /**
     *  Get Release by G:A:V:P:C.
     *  @throws  NoResultException if not found.
     */
    public MavenArtifact getMavenArtifact( String groupId, String artifactId, String version, String packaging, String classifier ) throws  NoResultException {
        return this.em.createQuery("SELECT ma FROM MavenArtifact ma "
                + " WHERE ma.groupId  = ?1"
                + " AND ma.artifactId = ?2"
                + " AND ma.version    = ?3"
                + " AND ma.packaging  = ?4"
                + " AND ma.classifier = ?5"
                , MavenArtifact.class)
                .setParameter(1, groupId)
                .setParameter(2, artifactId)
                .setParameter(3, version)
                .setParameter(4, packaging)
                .setParameter(5, classifier)
                .getSingleResult();
    }
    
    /**
     *  Get Release by G:A:V:P:C.
     * @returns null if not found.
     */
    public MavenArtifact findMavenArtifact( String groupId, String artifactId, String version, String packaging, String classifier ) {
        try {
            return getMavenArtifact(groupId, artifactId, version, packaging, classifier);
        } catch( NoResultException ex ){
            return null;
        }
    }

    

    /**
     * Update a Release.
     */
    public MavenArtifact update(MavenArtifact rel) {
        MavenArtifact managed = this.em.merge(rel);
        return managed;
    }

    /**
     * Remove a MavenArtifact.
     */
    public void remove(MavenArtifact rel) {
        MavenArtifact managed = this.em.merge(rel);
        this.em.remove(managed);
        this.em.flush();
    }


}// class
