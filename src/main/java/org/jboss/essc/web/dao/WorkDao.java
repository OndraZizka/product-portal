package org.jboss.essc.web.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
    
    public List<WorkTag> getTagsByNames(String inputValue) {
        List<String> tagNames = Arrays.asList( StringUtils.split(inputValue) );
        return em.createQuery("SELECT wt FROM WorkTag wt WHERE wt.name IN ?1")
                .setParameter(1, tagNames)
                .getResultList();
    }

    
    public void createWorkUnit( WorkUnit wu ) {
        this.em.persist( wu );
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
            sb.append("IF('").append( StringEscapeUtils.escapeSql(wt.getName()) ).append("' IN wu.tags, 1,0)");
        }
        return em.createQuery(
            "SELECT wu.tags, (" + sb.toString() + ") AS score "
            + "  FROM WorkUnit wu WHERE wu.tags IN ?1")
                .setParameter(1, wu.getTags())
                .setMaxResults(maxResults)
                .getResultList();
        
        /*   2nd option:
           SELECT wuSimilar, COUNT(*) AS score FROM WorkUnit wuBase 
               LEFT JOIN wuBase.tags AS wubTags
               LEFT JOIN WorkUnit wuSimilar ON wubTags IN wuSimilar.tags
               WHERE wuBase = :base
               GROUP BY wuSimilar
               ORDER BY score DESC
         */
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
