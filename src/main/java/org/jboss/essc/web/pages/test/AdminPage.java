
package org.jboss.essc.web.pages.test;

import javax.inject.Inject;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.jboss.essc.integ.trackers.TrackersScheduledSynchronizer;

/**
 *  Admin page to invoke certain actions during development.
 *  @author Ondrej Zizka
 */
public class AdminPage extends WebPage {
    
    @Inject private TrackersScheduledSynchronizer syncer;


    public AdminPage() {
        Form form = new Form("form");
        add(form);
        form.add( new AjaxButton("scanBugzilla"){
            @Override public void onEvent(IEvent<?> event) {
                //if( isDevel() )
                    syncer.createReleasesForNewVersionsOfAllProducts();
            }
        });
        
    }
    
    private boolean isDevel(){
        return RuntimeConfigurationType.DEVELOPMENT == getApplication().getConfigurationType();
    }


}// class
