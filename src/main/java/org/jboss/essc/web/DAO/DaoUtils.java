
package org.jboss.essc.web.DAO;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

/**
 *  Universal DAO stuff.
 * 
 *  @author Ondrej Zizka
 */
public class DaoUtils {

    @PersistenceContext private EntityManager em;


    /**
     *  Finds any entity by ID with it's collection fetched as defined.
     *  Usage:
     * 
        User user = someDao.findWithDepth(
            User.class, 15, "addresses friends.addresses"
        );
     */
    public <T> T find( Class<T> type, Object id, String fetchRelations ){

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(type);
        Root<T> root = criteriaQuery.from(type);
        addFetches( root, fetchRelations.split(" ") ); // Ex: "addresses friends.addresses"
        criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));

        return getSingleOrNoneResult( em.createQuery(criteriaQuery) );
    }

    public static <T> void addFetches(Root<T> root, String... fetchRelations) {

        for( String relation : fetchRelations ) {
            FetchParent<T, T> fetch = root;
            for( String pathSegment : relation.split(".") ) {
                fetch = fetch.fetch( pathSegment, JoinType.LEFT );
            }
        }
    }

    /**
     * @returns  the single result, or null if none was found.
     */
    public static <T> T getSingleOrNoneResult(TypedQuery<T> query) {
        query.setMaxResults(1);
        List<T> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }
    
}// class
