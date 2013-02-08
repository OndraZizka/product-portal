package org.jboss.essc.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.jboss.essc.web.dao.ProductDaoBean;
import org.jboss.essc.web.dao.ReleaseDaoBean;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.Release;

/**
 * @author ozizka@redhat.com
 */

@Path("/")
public class RestServices {
    
    @Inject private ReleaseDaoBean daoRel; 
    @Inject private ProductDaoBean daoProd; 


    /**
     * [{"product":{"id":1,"name":"EAP","note":null,"extIdJira":null,"extIdBugzilla":"226"}]
     */
    @GET
    @Path("/products")
    @Produces("application/json")
    //@XmlElement(type = Product.class, name = "product") // No effect
    //@XmlElementWrapper(name="productx") // No effect
    //@org.jboss.resteasy.annotations.providers.jaxb.json.Mapped(attributesAsElements = "product") // No effect?
    //@org.jboss.resteasy.annotations.providers.jaxb.Wrapped(element = "product")  // No effect
    public List<ProductWrapper> getProducts( @Context SecurityContext sc ) {
        final List<Product> prods = daoProd.getProducts_orderName(0);
        for (Product prod : prods) {
            prod.setTraits(null);
        }
        //return rewrap(prods);
        return rewrap(prods, "product");
    }
    
    @GET
    @Path("/releases/{product}")
    @Produces("application/json")
    public List<Release> getReleases( 
            @PathParam("product") String product, 
            @Context HttpServletResponse res,
            @Context SecurityContext sc ) 
    {
        System.out.println("Releases of: " + product);
        List<Release> rel = daoRel.getReleasesOfProduct(product, true);
        //return rel;
        return rewrap(rel, "release");
    }
    
    @GET
    @Path("/release/{product}/{version}")
    @Produces("application/json")
    public Release getReleaseInfo( 
            @PathParam("product") String product, 
            @PathParam("version") String version, 
            @Context HttpServletResponse res,
            @Context SecurityContext sc ) throws IOException
    {
        System.out.println("Release: " + product + " " + version);
        
        try {
            return daoRel.getRelease(product, version);
        } catch (NoResultException ex){
            res.sendError(404, "No such release.");
            return null;
        }/* catch (EJBException ex){
            if( ex.getCausedByException() instanceof NoResultException)
            res.sendError(404, "No such release.");
            return null;
        }*/
    }
    
    
    @PUT
    @Path("/release/{product}/{version}")
    @Consumes("application/json")
    @Produces("application/json")
    public Release addRelease( 
            @PathParam("product") String prodName, 
            @PathParam("version") String version, 
            @Context HttpServletResponse res,
            @Context SecurityContext sc ) throws IOException
    {
        System.out.println("Release: " + prodName + " " + version);
        
        // Get product
        Product prod;
        try {
            prod = daoProd.getProductByName(prodName);
        } catch (NoResultException ex){
            res.sendError(404, "No such product: " + prodName);
            return null;
        }

        // Verify that the relese doesn't exist yet.
        try {
            daoRel.getRelease( prodName, version );
        } catch (NoResultException ex){
            res.sendError(404, "Release already exists: " + prodName + " " + version);
            return null;
        }
        
        // Add a release to it
        return daoRel.addRelease( prod, version );
    }

    
    
    
    /**
     *  Wrapper for Products - needed for JSON client...
     */
    private List<ProductWrapper> rewrap(List<Product> prods) {
        List<ProductWrapper> p2 = new ArrayList(prods.size());
        for( Product product : prods){
            p2.add( new ProductWrapper(product));
        }
        return p2;
    }
    
    /**
     *  Map-based generic wrapper - needed for JSON client.
     */
    private List rewrap(List items, String wrapName) {
        List p2 = new ArrayList(items.size());
        for( Object item : items){
            Map map = new HashMap();
            map.put(wrapName, item);
            p2.add( map );
        }
        return p2;
    }

    
    
    /**
     *  Wrapper for Products - needed for JSON client...
     */
    public static class ProductWrapper {
        public Product product;
        public ProductWrapper(Product product) { this.product = product; }        
    }
    
    /**
     *  Wrapper for Products - needed for JSON client...
     *  @deprecated  What is really needed is 
     *      [{"product":{"id":1,"name":"EAP","note":null,"extIdJira":null,"extIdBugzilla":"226"}]
     *         not
     *      {"products":[{"id":1,"name":"EAP","note":null,"extIdJira":null,"extIdBugzilla":"226"}]}
     */
    public static class ProductsWrapper {
        
        private List<Product> products;

        public ProductsWrapper(List<Product> prods) {
            this.products = prods;
        }
        public List<Product> getProducts() { return products; }
        public void setProducts(List<Product> prods) { this.products = prods; }
        
    }
    
    

}// class
