
package org.jboss.essc.web.security;

import java.io.Serializable;

/**
 *  App's per-user settings.
 *  
 * @author ozizka@redhat.com
 */
public class EsscSettings implements Serializable {
    
    private boolean showInternalReleases = false;

    public boolean isShowInternalReleases() { return showInternalReleases; }
    public void setShowInternalReleases( boolean showInternalReleases ) { this.showInternalReleases = showInternalReleases; }
    
}// class
