package org.jboss.essc.integ.trackers.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Wrapper for Bugzilla's result:
 * 
 *  {"error":null,"id":"https://bugzilla.redhat.com/","result":{
 *      "products":[{
 *          "id":226, ...
 *      }]
 *   }}

 * @author Ondrej Zizka
 */
public class BugzillaResultWrapper {
    
    @JsonProperty("error")
    private String error;
    
    private String id;
    
    
    private BugzillaProductsResultWrapper result;
    //@XmlPath("result/products") // Not supported in Jackson?
    //List<ExternalProjectInfo> products;
    
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }    
    public BugzillaProductsResultWrapper getResult() { return result; }
    public void setResult(BugzillaProductsResultWrapper result) { this.result = result; }    
    
}// class
