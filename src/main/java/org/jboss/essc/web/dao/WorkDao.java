package org.jboss.essc.web.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;


/**
 * A bean which manages WorkTag entities.
 */
@Stateless
public class WorkDao {

    @PersistenceContext
    private EntityManager em;

    
    public WorkTag findTagByName(String name) {
        List<WorkTag> res = em.createQuery("SELECT wt FROM WorkTag wt WHERE wt.name = ?1", WorkTag.class)
                                  .setParameter(1, name)
                                  .getResultList();
        return res.isEmpty() ? null : res.get(0);
    }
   
    
    public List<WorkTag> getTagsStartingWith(String string) {
        return em.createQuery("SELECT wt FROM WorkTag wt WHERE wt.name LIKE CONCAT(?1, '%') OR wt.name LIKE CONCAT('%-', ?1, '%')")
                .setParameter(1, string)
                .getResultList();
    }
    
    public List<WorkTag> getTagsByNames( String[] tagNamesA ) {
        List<String> tagNames = Arrays.asList( tagNamesA );
        return em.createQuery("SELECT wt FROM WorkTag wt WHERE wt.name IN ?1")
                .setParameter(1, tagNames)
                .getResultList();
    }
    
    
    /**
     *  Loads tags from DB by name; if given tag doens't exist, creates non-persisted object.
     */
    public List<WorkTag> loadOrCreateTagsByNames( String[] tagNamesA ) {
        
        List<WorkTag> tagsByNames = getTagsByNames(tagNamesA);
        //CollectionUtils.toMap() // Needs commons-collections
        Map<String, WorkTag> map = new HashMap(tagNamesA.length);
        for( WorkTag workTag : tagsByNames ) {
            map.put( workTag.getName(), workTag);
        }
        
        List<WorkTag> tags = new ArrayList<>( tagNamesA.length );
        for( String tagName : tagNamesA ) {
            WorkTag tag = map.get( tagName );
            if( tag == null )
                tag = new WorkTag(tagName);
            tags.add(tag);
        }
        return tags;
    }

    
    public WorkUnit createWorkUnit( WorkUnit wu ) {
        WorkUnit wu2 = this.em.merge( wu );
        this.em.persist( wu2 );
        return wu2;
    }

    /**
     * Returns work units which are similar to the given one;
     * currently it means that it has some same tags.
     */
    public List<WorkUnit> getWorkUnitsSimilarTo(WorkUnit wu, int maxResults) {
        
        if( wu.getTags() == null || wu.getTags().isEmpty() )
            return Collections.EMPTY_LIST;
        
        // Sum - 1pt for each tag: ( IF('foo' IN wu.tags, 1,0) + IF('bar' IN wu.tags, 1,0) + ...)
        StringBuilder sb = new StringBuilder();
        for( WorkTag wt : wu.getTags() ){
            sb.append("IF('").append( StringEscapeUtils.escapeSql(wt.getName()) ).append("' IN wu.tags, 1,0) + ");
        }

        String jpql =
        /* Doesn't work - IN can't be used in SELECT clause :-/
            "SELECT wu.tags, (" + sb.toString() + " 0) AS score "
            + "  FROM WorkUnit wu WHERE wu.tags IN (?1)";
                .setParameter(1, wu.getTags())
        */
        
        // 2nd way.
           "SELECT NEW org.jboss.essc.web.dao.WorkDao.WorkUnitWithCount(wuSimilar, COUNT(*)) FROM WorkUnit wuBase "
           + "  LEFT JOIN wuBase.tags AS wubTags "
           + "  , WorkUnit wuSimilar  " // ON wubTags IN (wuSimilar.tags) - JPQL JOINS don't support ON...
           + "  WHERE wuBase = :base "
           + "    AND wubTags MEMBER OF wuSimilar.tags "
           + "  GROUP BY wuSimilar "
           + "  ORDER BY score DESC ";
        return em.createQuery( jpql )
                .setParameter("base", wu)
                //.setMaxResults(maxResults)
                .getResultList();        
    }



    /**
     *  Returns work units with a tag of given name.
     */
    public List<WorkUnit> getWorkUnitsWithTag( String tagName ) {
        //String jpql = "SELECT wu FROM WorkUnit wu WHERE :tag IN (wu.tags)";
        //String jpql = "SELECT wu FROM WorkUnit wu WHERE (SELECT wt FROM WorkTag wt WHERE wt.name = :tagName) IN wu.tags";
        //String jpql = "SELECT wu FROM WorkUnit wu, (SELECT wt FROM WorkTag wt WHERE wt.name = 'tag1') wt" +
        //              " WHERE wt IN wu.tags";
        String jpql = "SELECT wu FROM WorkUnit wu, WorkTag wt WHERE wt.name = LOWER(:tagName) AND wt MEMBER OF wu.tags";
        
        return em.createQuery( jpql )
                .setParameter("tagName", tagName)
                .getResultList();
    }
    
    /**
     *  Returns work units with tags of given comma-separated list of names.
     */
    public List<WorkUnit> getWorkUnitsWithTags(String tagNames) {
        String[] tags = StringUtils.split(tagNames,',');
        if( StringUtils.contains(tagNames, ' ')){
            for (int i = 0; i < tags.length; i++) {
                tags[i] = tags[i].trim();
            }
        }
        String jpql = "SELECT wu FROM WorkUnit wu, WorkTag wt WHERE wt.name IN (:tagNames) AND wt MEMBER OF wu.tags GROUP BY wu.id";
        
        return em.createQuery( jpql )
                .setParameter("tagNames", Arrays.asList(tags))
                .getResultList();
    }
    
    
    
    
    /**
     *  Returns authors with most work units having given tag.
     */
    public List<User> getTopAuthorsOfWorkUnitsWithTag( String tagName, int maxResults ) {
        return em.createQuery("SELECT u FROM User u, WorkUnit wu, WorkTag wt WHERE wt.name = :tagName AND wt MEMBER OF wu.tags AND u = wu.author")
                .setParameter("tagName", tagName)
                .setMaxResults(maxResults)
                .getResultList();        
    }


    /**
     * Get WorkTag by ID.
     */
    public WorkTag getWorkTag( Long id ) {
        return this.em.find(WorkTag.class, id);
    }


    /**
     * Remove a WorkTag.
     */
    public void remove( WorkTag tag ) {
        WorkTag managed = this.em.merge(tag);
        this.em.remove(managed);
        this.em.flush();
    }

    
    public WorkTag update( WorkTag tag ) {
        WorkTag managed = this.em.merge(tag);
        return managed;
    }
    
    
    public static class WorkUnitWithCount {
        private WorkUnit wu;
        private int count;

        public WorkUnitWithCount(WorkUnit wu, int count) {
            this.wu = wu;
            this.count = count;
        }

        public WorkUnit getWu() { return wu; }
        public void setWu(WorkUnit wu) { this.wu = wu; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }        
        
    }

}// class
