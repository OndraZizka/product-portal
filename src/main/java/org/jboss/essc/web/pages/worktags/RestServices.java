package org.jboss.essc.web.pages.worktags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.StringUtils;
import org.jboss.essc.web.dao.UserDao;
import org.jboss.essc.web.dao.WorkDao;
import org.jboss.essc.web.model.User;
import org.jboss.essc.web.model.WorkTag;
import org.jboss.essc.web.model.WorkUnit;
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
    
    @Inject private WorkDao daoWork; 
    @Inject private UserDao daoUser; 


    /**
     * WorkTag of given name.
     */
    @GET @Path("/workTag/{name}")
    @Produces("application/json")
    @Formatted
    public WorkTag getWorkTag( @PathParam("name") String name, @Context HttpServletResponse res ) throws IOException {
        final WorkTag tag = daoWork.findTagByName(name);
        if( tag == null ){
            res.sendError(404, "No such release.");
        }
        return tag;
    }
    
    /**
     * WorkTags by prefix.
     */
    @GET @Path("/workTags/byPrefix/{prefix}")
    @Produces("application/json")
    @Formatted
    public List<WorkTag> getWorkTagsByPrefix( @PathParam("prefix") String prefix, @Context HttpServletResponse res ) throws IOException {
        final List<WorkTag> tags = daoWork.getTagsStartingWith(prefix);
        return tags;
    }
    
    /**
     * Work units with given tags.
     */
    @GET @Path("/workUnits/byTags/{tags}")
    @Produces("application/json")
    @Formatted
    public List<WorkUnit> getWorkUnitsByTags(  @PathParam("tags") String tags,  @Context HttpServletResponse res ) {
        log.debug("WUs with tags: " + tags);
        List<WorkUnit> wus = daoWork.getWorkUnitsWithTags(tags);
        for( WorkUnit wu : wus ) {
            //wu.setTags(null); // EAGER
            wu.setWorkers(null);
        }
        return RestUtils.rewrap(wus, "workUnit");
    }
        
    
    /**
     *  Create a work unit.
     * 
     *  Ex.:  http://localhost:8080/essc-portal/rest/workUnit/foo,bar?author=ozizka&title=Hi+there!&note=Note
     */
    @GET @Path("workUnit/{tags}")
    //@Consumes("application/json")
    @Produces("application/json")
    public WorkUnit addWorkUnit( @PathParam("tags") String tagNames, 
                                 @QueryParam("author") String author, 
                                 @QueryParam("title") String title, 
                                 @QueryParam("note") String note, 
                                 @QueryParam("url") String url, 
                                 @QueryParam("workers") String workerNames, 
                                 @Context HttpServletResponse res ) throws IOException
    {
        if( StringUtils.isBlank(tagNames) || StringUtils.isBlank(author) || StringUtils.isBlank(title) ){
            res.sendError(400, "tags, author and title must be set.");
        }
        
        log.info("Creating work unit: " + tagNames );
        
        WorkUnit wu = new WorkUnit();
        wu.setCreated( new Date() );
        wu.setTitle(title);
        wu.setUrl(url);
        wu.setNote(note);
        
        // Tags
        List<WorkTag> tags = daoWork.loadOrCreateTagsByNames( StringUtils.split(tagNames,',') );
        wu.setTags( new HashSet(tags) );
        
        // Author
        try {
            User user = daoUser.getUserByName(author);
            wu.setAuthor( user );
        }
        catch( NoResultException ex ){
            res.sendError(404, "User not found: " + author); return null;
        }
        // Workers
        if( null != workerNames ){
            String[] userNames = StringUtils.split(workerNames,',');
            List<User> workers = new ArrayList(userNames.length);
            for( String name : userNames ) {
                try {
                    workers.add( daoUser.getUserByName(name));
                }catch( Throwable ex ){
                    log.warn("User not found, not adding work unit worker: " + name);
                }
            }
            wu.setWorkers( new HashSet(workers) );
        }

        
        // Add a work unit.
        try {
            return daoWork.createWorkUnit( wu );
        } catch (Exception ex) {
            res.sendError(500, "Failed creating the work unit: " + ex);
            return null;
        }
    }

    
    /**
     *  Wrapper for WorkUnits - needed for JSON client...
     */
    public static final class WorkUnitWrapper {
        public final WorkUnit workUnit;
        public WorkUnitWrapper(WorkUnit wu) { this.workUnit = wu; }        
    }
    
}// class
