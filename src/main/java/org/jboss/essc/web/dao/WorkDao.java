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
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;


/**
 * A bean which manages WorkTag entities.
 */
@Stateless
public class WorkDao {

    @PersistenceContext
    private EntityManager em;

    
    
    public Iterable getTagStartingWith(String string) {
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
           "SELECT wuSimilar, COUNT(*) AS score FROM WorkUnit wuBase "
           + "  LEFT JOIN wuBase.tags AS wubTags "
           + "  , WorkUnit wuSimilar  " // ON wubTags IN (wuSimilar.tags) - JPQL JOINS don't support ON...
           + "  WHERE wuBase = :base "
           + "    AND wubTags IN (wuSimilar.tags) "
           + "  GROUP BY wuSimilar "
           + "  ORDER BY score DESC ";
        return em.createQuery( jpql )
                .setParameter("base", wu)
                //.setMaxResults(maxResults)
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
   
}// class
