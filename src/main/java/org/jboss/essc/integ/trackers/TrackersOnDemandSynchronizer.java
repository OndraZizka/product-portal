package org.jboss.essc.integ.trackers;

import org.jboss.essc.integ.trackers.model.ExternalVersionInfo;
import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *  Periodically checks the issue tracker for new versions,
 *  and if a new one is found, it creates a Release in Product Portal.
 * 
 *  @author Ondrej Zizka
 *  @deprecated  We rather simply create Releases upfront.
 */
@Stateless
public class TrackersOnDemandSynchronizer {
    private static final Logger log = LoggerFactory.getLogger(TrackersOnDemandSynchronizer.class);
    
    // Jira cache expiration.
    private static final long EXPIRATION_MS = 24 * 3600 * 1000; // 1 day
    
    
    @PersistenceContext private EntityManager em;
    
    
    
    
    /**
     *  Retrieve project info from local cache.
     *  If not found or cache record expired, load from remote Jira.
     *  @deprecated  We rely on @Schedule.
     */
    public ExternalProjectInfo loadOrRetrieveProject(String projectId) {
        
        ExternalProjectInfo project = null;
        try {
            project = em.createQuery("SELECT p FROM JiraProject p WHERE p.key = :key", ExternalProjectInfo.class).setParameter("key", projectId).getSingleResult();
        } catch (NoResultException e) { /* ok */ }
        
        // If expired, delete info about project from cache.
        if( project != null && project.getLastUpdated() + EXPIRATION_MS < System.currentTimeMillis()){
            em.createQuery("DELETE FROM JiraVersion v WHERE v.project = :project") .setParameter("project", project) .executeUpdate();
            // Cascaded
            //em.createQuery("DELETE FROM JiraProject p WHERE p.key = :key") .setParameter("key", projectId) .executeUpdate();
        }

        // Project not in cache, retrieve.
        if( project == null ){ 
            try {
                project = new BugzillaRetriever().retrieveProject(projectId);
                project.setLastUpdated(System.currentTimeMillis());
            } catch (Exception e) {
                return null; // failure
            }

            // Persist all versions first.
            for (ExternalVersionInfo v : project.getVersions()) {
                v.setProject(project);
                em.persist(v);
            }

            // and the project itself
            em.persist(project);
        }
        else  {
        }

        return project;
    }

    /**
     * Get list of ids of versions of the specified project that are marked as
     * released
     *
     * @param key key of the project
     * @return list of ids or null when there are no versions or an error
     * occured
     */
    public List<Long> getReleasedIds(String key) {
        ExternalProjectInfo p = loadOrRetrieveProject(key);
        if (p == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Long> ids = em.createQuery("SELECT v.jiraId FROM JiraVersion v WHERE v.project = :project AND v.released = true")
                .setParameter("project", p)
                .getResultList();

        return ids;
    }

    /**
     * Get list of version names for the given project.
     *
     * @param key
     * @return list of version strings or null on failure
     */
    public List<String> getVersionNames(String key) {
        ExternalProjectInfo p = loadOrRetrieveProject(key);
        if (p == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<String> versionNames = em.createQuery("SELECT v.name FROM JiraVersion v WHERE v.project = :project")
                .setParameter("project", p)
                .getResultList();

        return versionNames;
    }

}// class
