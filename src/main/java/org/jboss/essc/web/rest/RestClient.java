package org.jboss.essc.web.rest;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.*;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.Release;

/**
 * @author ozizka@redhat.com
 */

@Path("/")
public interface RestClient {
    
    @GET
    @Path("/products")
    @Produces("application/json")
    public List<Product> getProducts();
    
    @GET
    @Path("/releases/{product}")
    @Produces("application/json")
    public List<Release> getReleases( @PathParam("product") String product );
    
    @GET
    @Path("/release/{product}/{version}")
    @Produces("application/json")
    public Release getReleaseInfo( 
            @PathParam("product") String product, 
            @PathParam("version") String version ) throws IOException;
    
    
    @PUT
    @Path("/release/{product}/{version}")
    @Consumes("application/json")
    @Produces("application/json")
    public Release addRelease( 
            @PathParam("product") String prodName, 
            @PathParam("version") String version ) throws IOException;
    

}// class
