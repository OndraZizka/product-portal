package org.jboss.essc.integ.trackers.model;

import java.util.List;

/**
 * 
 * @author Ondrej Zizka
 */
public class BugzillaProductsResultWrapper {

    private List<ExternalProjectInfo> products;

    public List<ExternalProjectInfo> getProducts() { return products; }
    public void setProducts(List<ExternalProjectInfo> products) { this.products = products; }    
    
}
