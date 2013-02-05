package org.jboss.essc.integ.trackers;

import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import java.net.MalformedURLException;
import java.net.URL;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * {
 *   "self":"https://issues.jboss.org/rest/api/2/project/AS7",
 *   "id":"12311211",
 *   "key":"AS7",
 *   "description":"JBoss Application Server 7",
 *   "lead":{ ... },
 *   "components":[{...}],
 *   "issueTypes":[{...}],
 *   "url":"http://jboss.org/jbossas",
 *   "assigneeType":"UNASSIGNED",
 *   "versions":[
 *      {
 *          "self":"https://issues.jboss.org/rest/api/2/version/12316378",
 *          "id":"12316378",
 *          "description":"Pertains to no release",
 *          "name":"No Release",
 *          "archived":false,
 *          "released":false
 *      },
 *      {...}],
 *   "name":"Application Server 7",
 *   "roles":{...},
 *   "avatarUrls":{...}
 * }
 * 
 * @author Ondrej Zizka
 */
public class JiraRetriever implements IProjectInfoRetriever {
    
    private ObjectMapper mapper;

    public JiraRetriever(){
        mapper = new ObjectMapper();
        mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(Feature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
    }

    
    /**
     *  Retrieve project info from Jira.
     */
    public ExternalProjectInfo retrieveProject(String projectId) {
        try {
            ExternalProjectInfo project = mapper.readValue( getUrlForProject( projectId ), ExternalProjectInfo.class );
            project.setLastUpdated(System.currentTimeMillis());
            return project;
        } catch (Exception e) {
            return null;
        }
    }
    
    private URL getUrlForProject( String projectId ){
        try {
            return new URL("https://issues.jboss.org/rest/api/2/project/" + projectId);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

}// class
