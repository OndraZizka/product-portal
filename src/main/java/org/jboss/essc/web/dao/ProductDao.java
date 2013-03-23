package org.jboss.essc.web.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages Product entities.
 */
@Stateless
public class ProductDao {
    private static final Logger log = LoggerFactory.getLogger(ProductDao.class);

    @PersistenceContext
    private EntityManager em;


    public List<Product> getProducts_orderName(int limit) {
        return this.em.createQuery("SELECT p FROM Product p ORDER BY p.name").getResultList();
    }

    /**
     * Get Product by ID.
     */
    public Product getProduct(Long id) {
        return this.em.find(Product.class, id);
    }

    /**
     * Get Product by name.
     * @throws  NoResultException if no such product found.
     */
    public Product getProductByName( String name ) {
        return this.em.createQuery(
                "SELECT p FROM Product p "
                + "LEFT JOIN FETCH p.customFields cf "
                + "WHERE p.name = ?1", Product.class).setParameter(1, name).getSingleResult();
    }
    /**
     * Find Product by name.
     * @returns null if not found.
     */
    public Product findProductByName( String name ) {
        List<Product> list = this.em.createQuery("SELECT p FROM Product p WHERE p.name = ?1", Product.class).setParameter(1, name).getResultList();
        if( list.isEmpty() )  return null;
        return list.get(0);
    }


    /**
     * Add a new Product.
     */
    public Product addProduct( Product prod ) {
        return this.em.merge( prod );
    }

    /**
     * Remove a Product.
     */
    public void remove(Product prod) {
        Product managed = this.em.merge(prod);
        this.em.remove(managed);
        this.em.flush();
    }

    
    public Product update( Product product ) {
        Product managed = this.em.merge(product);
        return managed;
    }

    
    public void deleteIncludingReleases( Product prod ) {
        prod = this.em.merge(prod);
        
        // Delete releases
        // Ends up with Hibernate screwing up SQL - "cross join"
        //int up = this.em.createQuery( "DELETE FROM Release r WHERE r.product.name = ?1" ).setParameter( 1, prod.getName() ).executeUpdate();
        int up = this.em.createQuery( "DELETE FROM Release r WHERE r.product IN "
                + "(SELECT p FROM Product p WHERE p.name = ?1)" ).setParameter( 1, prod.getName() ).executeUpdate();
        log.debug("Deleted " + up);
        
        this.em.remove(prod);
        this.em.flush();
    }


    /**
     *  Is user in at least one of groups which can edit the product?
     */
    public boolean canBeUpdatedBy( Product prod, User user ) {
        //this.em.createQuery("SELECT COUNT(*) FROM Product p, User u WHERE p = :prod AND u.groups CONTAINS (SELECT p FROM Product p WHERE p.name = ?1)" ).setParameter( 1, prod.getName() ).executeUpdate();
        
        String editorsGroupPrefix = prod.getEditorsGroupPrefix();
        if( user == null )
            return ".".equals(editorsGroupPrefix) || "*".equals(editorsGroupPrefix);
        return user.isInGroups_Prefix(editorsGroupPrefix);
    }
    
}// class
