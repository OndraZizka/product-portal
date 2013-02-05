package org.jboss.essc.integ.trackers;

import org.jboss.essc.integ.trackers.model.ExternalVersionInfo;
import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;



@Stateless
public class ExternalTrackersSynchronizer {
    
    // Jira cache expiration.
    private static final long EXPIRATION_MS = 24 * 3600 * 1000; // 1 day
    private static final String JIRA_HOST = "issues.jboss.org";
    
    
    @PersistenceContext private EntityManager em;
    

    
    /**
     *  Retrieve project info from remote Jira.
     */
    public ExternalProjectInfo loadOrRetrieveProject(String projectId) {
        
        ExternalProjectInfo project = null;
        try {
            project = em.createQuery("SELECT p FROM JiraProject p WHERE p.key = :key", ExternalProjectInfo.class).setParameter("key", projectId).getSingleResult();
        } catch (NoResultException e) { /* ok */ }
        
        // If expired, delete info about project from cache.
        if( project != null && project.getLastUpdated() + EXPIRATION_MS < System.currentTimeMillis()){
            em.createQuery("DELETE FROM JiraVersion v WHERE v.project = :project") .setParameter("project", project) .executeUpdate();
            em.createQuery("DELETE FROM JiraProject p WHERE p.key = :key") .setParameter("key", projectId) .executeUpdate();
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
        List<Long> ids = em.createQuery("select v.jiraId from JiraVersion v where v.project = :project and v.released = true")
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
        List<String> versionNames = em.createQuery("select v.name from JiraVersion v where v.project = :project")
                .setParameter("project", p)
                .getResultList();

        return versionNames;
    }

}// class
