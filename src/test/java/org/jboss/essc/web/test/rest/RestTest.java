package org.jboss.essc.web.test.rest;

import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.rest.RestClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ondra
 */
//@RunWith(Arquillian.class)
public class RestTest {
    
    //private RestClient client;
            
    
    public RestTest() {
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        //this.client = ProxyFactory.create(RestClient.class, "http://localhost:8080/essc-portal/rest");
    }

    @Test
    public void testGetProducts() {
        // Migrated RestEasy 2.x to 3.x: https://docs.jboss.org/resteasy/docs/resteasy-upgrade-guide-en-US.pdf
        Client client = ClientBuilder.newClient();
        String url = "http://localhost:8080/essc-portal/rest";
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(url);
        RestClient restClient = target.proxy(RestClient.class);
        List<Product> products = restClient.getProducts();        
        
        Assert.assertNotNull(products);
        Assert.assertTrue( !products.isEmpty() );
        Assert.assertTrue( products.contains(new Product(null, "EAP")) );
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
}