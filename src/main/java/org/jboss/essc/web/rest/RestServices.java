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
import org.jboss.essc.web.util.RestUtils;
import org.jboss.resteasy.annotations.providers.jaxb.Formatted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ozizka@redhat.com
 */

@Path("/")
public class RestServices {
    private static final Logger log = LoggerFactory.getLogger(RestServices.class);
    
    @Inject private ReleaseDaoBean daoRel; 
    @Inject private ProductDaoBean daoProd; 


    /**
     * [{"product":{"id":1,"name":"EAP","note":null,"extIdJira":null,"extIdBugzilla":"226"}]
     */
    @GET
    @Path("/products")
    @Produces("application/json")
    @Formatted
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
        return RestUtils.rewrap(prods, "product");
    }
    
    @GET
    @Path("/releases/{product}")
    @Produces("application/json")
    @Formatted
    public List<Release> getReleases( 
            @PathParam("product") String product, 
            @Context HttpServletResponse res,
            @Context SecurityContext sc ) 
    {
        log.debug("Releases of: " + product);
        List<Release> releases = daoRel.getReleasesOfProduct(product, true);
        for( Release rel : releases ) {
            rel.setCustomFields(null);
            rel.setDeps(null);
        }
        //return rel;
        return RestUtils.rewrap(releases, "release");
    }
    
    @GET
    @Path("/release/{product}/{version}")
    @Produces("application/json")
    @Formatted
    public Release getReleaseInfo( 
            @PathParam("product") String product, 
            @PathParam("version") String version, 
            @Context HttpServletResponse res,
            @Context SecurityContext sc ) throws IOException
    {
        log.debug("Release: " + product + " " + version);
        
        try {
            return daoRel.getRelease(product, version, daoRel.WITHOUT_DEPS).setDeps(null);
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
        log.debug("Release: " + prodName + " " + version);
        
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
            res.sendError(409, "Release already exists: " + prodName + " " + version);
            return null;
        } catch (NoResultException ex){ /* OK */ }
        
        // Add a release to the product.
        try {
            return daoRel.addRelease( prod, version );
        } catch (Exception ex) {
            res.sendError(500, "Failed adding a release " + prodName + " " + version + ": " + ex);
            return null;
        }
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
