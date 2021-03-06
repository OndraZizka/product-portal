package org.jboss.essc.web.pages.home;

import org.jboss.essc.web.pages.home.co.AboutSmallBox;
import org.jboss.essc.web.pages.home.co.RecentChangesBox;
import org.jboss.essc.web.pages.BaseLayoutPage;
import org.jboss.essc.web.pages.home.co.RecentReleasesBox;


/**
 * HomePage.
 * 
 * @author Ondrej Zizka
 */
@SuppressWarnings("serial")
public class HomePage extends BaseLayoutPage {

    private static final int RECENT_RELEASES_ROWS = 6;
    private static final int RECENT_CHANGES_ROWS = 6;
    
    
    public HomePage() {

        add( new AboutSmallBox("aboutBox") );
        
        add( new RecentReleasesBox("recentReleases", RECENT_RELEASES_ROWS) );
        
        add( new RecentChangesBox("recentChanges", RECENT_CHANGES_ROWS) );
    }
    
}// class
