package org.jboss.essc.web.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.jboss.essc.web.model.MavenArtifact;
import org.jboss.essc.web.model.Release;

/**
 * Manages Maven artifact entities.
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
        return this.findMavenArtifact(ma.getGroupId(), ma.getArtifactId(), ma.getVersion(), ma.getPackaging(), ma.getClassifier());
    }
    
    
    /**
     *  Get Release by G:A:V:P:C.
     *  @throws  NoResultException if not found.
     */
    public MavenArtifact getMavenArtifact( String groupId, String artifactId, String version, String packaging, String classifier ) throws  NoResultException {
        if( null == groupId )    throw new IllegalArgumentException("groupId must be defined.");
        if( null == artifactId ) throw new IllegalArgumentException("artifactId must be defined.");
        if( null == version )    throw new IllegalArgumentException("version must be defined.");
        if( null == packaging )  packaging = "jar";
        
        // Packaging could be null in db (shouldn't, though)
        boolean nonJar = "jar".equals(packaging);
        String packCond = (nonJar ? "ma.packaging  = :P" : "ma.packaging = 'jar' OR ma.packaging IS NULL");
        
        // Classifier
        boolean hasClas = null != classifier;
        String clasCond = (hasClas ? "= :C" : "IS NULL");
        
        TypedQuery<MavenArtifact> q = 
        this.em.createQuery("SELECT ma FROM MavenArtifact ma "
                + " WHERE ma.groupId  = :G"
                + " AND ma.artifactId = :A"
                + " AND ma.version    = :V"
                + " AND (" + packCond + ") "
                + " AND ma.classifier " + clasCond, MavenArtifact.class);
        
        q.setParameter("G", groupId);
        q.setParameter("A", artifactId);
        q.setParameter("V", version);
        if( nonJar )
            q.setParameter("P", packaging);
        if( hasClas )
            q.setParameter("C", classifier);
        return q.getSingleResult();
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
