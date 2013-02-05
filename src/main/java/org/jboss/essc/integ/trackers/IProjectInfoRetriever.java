package org.jboss.essc.integ.trackers;

import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;

/**
 *
 * @author ondra
 */
interface IProjectInfoRetriever {
    
    public ExternalProjectInfo retrieveProject( String projectId );
    
}
