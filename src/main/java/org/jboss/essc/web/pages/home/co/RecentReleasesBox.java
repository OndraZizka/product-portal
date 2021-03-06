package org.jboss.essc.web.pages.home.co;

import org.jboss.essc.web.pages.prod.co.ReleasesBox;
import java.util.List;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.Release;


/**
 * A list of recent releases.
 * 
 * @author Ondrej Zizka
 */
public class RecentReleasesBox extends ReleasesBox {

    public RecentReleasesBox( String id, Product forProduct, int numReleases ) {
        super( id, forProduct, numReleases );
    }

    public RecentReleasesBox( String id, int numReleases ) {
        super( id, numReleases );
    }

    
    protected List<Release> loadReleases(boolean showInternal){
        return this.dao.getReleases_orderDateDesc(this.numReleases, showInternal );
    }
    
}// class
