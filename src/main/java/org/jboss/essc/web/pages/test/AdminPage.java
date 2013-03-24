
package org.jboss.essc.web.pages.test;

import javax.inject.Inject;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.jboss.essc.integ.trackers.Schedules;
import org.jboss.essc.integ.trackers.TrackersScheduledSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Admin page to invoke certain actions during development.
 *  @author Ondrej Zizka
 */
public class AdminPage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(TrackersScheduledSynchronizer.class);
    private static final org.jboss.logging.Logger log2 = org.jboss.logging.Logger.getLogger(TrackersScheduledSynchronizer.class);
    
    @Inject private Schedules sched;


    public AdminPage() {
        
        Form form = new Form("form");
        add(form);
        
        // Scan  bugzilla
        form.add( new AjaxButton("scanBugzilla"){
            @Override public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                //if( isDevel() )
                    sched.syncReleases();
            }
        });
        
            
        // Logging tests
        form.add( new AjaxButton("log4jINFO"){
            @Override public void onSubmit(AjaxRequestTarget target, Form<?> form) { log.info("Info log4j"); }
        });
        form.add( new AjaxButton("log4jDEBUG"){
            @Override public void onSubmit(AjaxRequestTarget target, Form<?> form) { log.info("Debug log4j"); }
        });
        form.add( new AjaxButton("jbossloggingINFO"){
            @Override public void onSubmit(AjaxRequestTarget target, Form<?> form) { log2.info("Info JBoss Logging"); }
        });
        form.add( new AjaxButton("jbossloggingDEBUG"){
            @Override public void onSubmit(AjaxRequestTarget target, Form<?> form) { log2.info("Debug JBoss Logging"); }
        });
        
    }// const()
    
    
    
    private boolean isDevel(){
        return RuntimeConfigurationType.DEVELOPMENT == getApplication().getConfigurationType();
    }


}// class
