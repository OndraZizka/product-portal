package org.jboss.essc.integ.trackers;

import org.jboss.essc.integ.trackers.model.BugzillaResultWrapper;
import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 *  See http://www.bugzilla.org/docs/tip/en/html/api/
 * 
 *   Example output from BZ for  EAP id 226:
 * 
 *   https://bugzilla.redhat.com/jsonrpc.cgi?method=Product.get&params=[{%22ids%22:[1,2]}]     params=[{"ids":[226]}]
 *  
 *   {"error":null,"id":"https://bugzilla.redhat.com/","result":{
 *      "products":[{
 *          "milestones":[...],
 *          "name":"JBoss Enterprise Application Platform 6",
 *          "components":[...],
 *          "default_release":"---",
 *          "description":"JBoss Enterprise Application Platform 6",
 *          "versions":[
 *              {"is_active":true,"name":"6.0","id":1258,"sort_key":0},
 *              {"is_active":true,"name":"6.0.1","id":2202,"sort_key":0},
 *              {"is_active":true,"name":"6.1.0","id":2203,"sort_key":0}, ...],
 *          "default_milestone":"---",
 *          "releases":[
 *              {"is_active":true,
 *               "name":"EAP 6.0.0",
 *               "id":1020,
 *               "sort_key":0
 *              }, ...],
 *          "is_active":true,
 *          "has_unconfirmed":false,
 *          "classification":"JBoss",
 *          "id":226
 *      }]
 *   }}
 *
 *  @author Ondrej Zizka
 */
public class BugzillaRetriever implements IProjectInfoRetriever {

    private ObjectMapper mapper;

    public BugzillaRetriever(){
        mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
    }

    
    /**
     *  Retrieve project info from Bugzilla.
     */
    public ExternalProjectInfo retrieveProject(String projectId) {
        try {
            BugzillaResultWrapper result = mapper.readValue( getUrlForProject( projectId ), BugzillaResultWrapper.class);
            if( ! StringUtils.isBlank( result.getError() ))
                throw new Exception("Can't retrieve project " + projectId + ", Bugzilla returned: " + result.getError());

            List<ExternalProjectInfo> projects = result.getResult().getProducts();
            ExternalProjectInfo project = projects.get(0);
            project.setLastUpdated(System.currentTimeMillis());
            return project;
        } catch (Exception e) {
            return null;
        }
    }
    
    private URL getUrlForProject( String projectId ){
        try {
            return new URL("https://bugzilla.redhat.com/jsonrpc.cgi?method=Product.get&params=[{%22ids%22:["+projectId+"]}]");
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void main( String[] args ){
        new BugzillaRetriever().retrieveProject("226");
    }
    
}// class
