package org.jboss.essc.web.test.integ.trackers;

import org.jboss.essc.integ.trackers.BugzillaRetriever;
import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import org.jboss.essc.integ.trackers.model.ExternalVersionInfo;
import org.jboss.essc.integ.trackers.JiraRetriever;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ondra
 */
public class IssueTrackersRetrieversTest {
    
    @Test
    public void testRetrieveProjectFromBugzilla() {
        ExternalProjectInfo proj = new BugzillaRetriever().retrieveProject("226");
        Assert.assertTrue("project 226 contains version #1258 (EAP 6.0)", proj.getVersions().contains(new ExternalVersionInfo(1258)));
    }
    
    @Test
    public void testRetrieveProjectFromJira() {
        ExternalProjectInfo proj = new JiraRetriever().retrieveProject("AS7");
        Assert.assertEquals(new Long(12311211), proj.getExternalId());
        Assert.assertTrue("project AS7 contains version #12319535 (7.1.3.Final (EAP))", proj.getVersions().contains(new ExternalVersionInfo(12319535)));
    }
    
}// class