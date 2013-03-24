package org.jboss.essc.integ.trackers;

import org.jboss.essc.integ.trackers.model.ExternalVersionInfo;
import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringUtils;
import org.jboss.essc.web.dao.ProductDao;
import org.jboss.essc.web.dao.ReleaseDao;
import org.jboss.essc.web.dao.TrackersDao;
import org.jboss.essc.web.model.Product;
import org.jboss.essc.web.model.Release;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 *  Periodically checks the issue tracker for new versions,
 *  and if a new one is found, it creates a Release in Product Portal.
 * 
 *  @author Ondrej Zizka
 */
@Stateless
public class TrackersScheduledSynchronizer {
    private static final Logger log = LoggerFactory.getLogger(TrackersScheduledSynchronizer.class);
    //private static final org.jboss.logging.Logger log2 = org.jboss.logging.Logger.getLogger(TrackersScheduledSynchronizer.class);
    
    
    
    @PersistenceContext private EntityManager em;
    
    @Inject private ProductDao daoProduct;
    @Inject private ReleaseDao daoRelease;
    @Inject private TrackersDao daoTrackers;
    
    private static final int MAX_PRODUCTS_QUERIED = 500; // ...
    
    
    /**
     *  Loads all products, downloads their status and creates releases
     *  for any version of which we don't have a release yet.
     * 
     *  TODO: Filter out some like "No Release", Future, etc.
     */
    public void createSyncNewVersionsOfAllProducts(){
        try {
            final BugzillaRetriever bz = new BugzillaRetriever();
            log.info("Starting synchronization with issue trackers.");

            // For each (our) product...
            List<Product> products = daoProduct.getProducts_orderName(MAX_PRODUCTS_QUERIED);
            for( Product product : products ) {
                // No BZ project ID.
                if( StringUtils.isBlank( product.getExtIdBugzilla() ))
                    continue;

                log.info("  Checking new versions of product " + product.getName());
                

                // Download BZ project info.
                ExternalProjectInfo projInfo = bz.retrieveProject( product.getExtIdBugzilla() );
                if( null == projInfo ){
                    log.error("  Failed downloading project info from Bugzilla.");
                    continue;
                }
                
                // Get or create ext project entity.
                projInfo = daoTrackers.getOrCreateProjectInfo( projInfo );
                
                log.debug("  " + projInfo.getVersions().size() + " versions to check.");
                
                // Local list of releases.
                //List<Long> = daoProduct.getVersionsOfProduct();
                List<Release> knownReleases = daoRelease.getReleasesOfProduct( product, true );

                // For each version in Bugzilla...
                for (ExternalVersionInfo verInfo : projInfo.getVersions() ) {
                    
                    if( IVersionNamesFilter.DEFAULT.isOK( verInfo.getName() ) )
                        continue;
                    
                    Release newRel = new Release(null, product, verInfo.getName());
                    newRel.setExtIdBugzilla( "" + verInfo.getExternalId() );
                    
                    // Is it new?
                    if( knownReleases.contains( newRel ))  continue;
                    log.info("    Seems to be new: " + verInfo);
                    //createReleaseIfNew( newRel );
                    verInfo.setProject(projInfo);
                    createExternalVersionInfo( verInfo, knownReleases );
                }
            }// for each product
            log.info("Finished synchronization with issue trackers.");
        }
        catch( Exception ex ){
            log.error("Couldn't import releases from Bugzilla: " + ex, ex);
        }
        
    }// createReleasesForNewVersionsOfAllProducts()

    
    
    /**
     *  Creates version info entity.
     */
    private boolean createExternalVersionInfo( ExternalVersionInfo verInfo, List<Release> knownReleases ) {
        try {
            daoTrackers.addVersionInfoIfNew( verInfo );
            return true;
        }
        catch( Exception ex ){
            log.error("Couldn't store a release imported from Bugzilla: " + ex, ex);
            return false;
        }
    }

    
    /**
     *  @deprecated  Data from Bugzilla are total mess. We will rather not create releases from that.
     */
    private boolean createReleaseIfNew(Release newRel) {
        
        newRel.setNote("Imported from Bugzilla");
        try {
            daoRelease.addRelease( newRel );
        }
        catch( Exception ex ){
            log.error("Couldn't store a release imported from Bugzilla: " + ex, ex);
        }
        return false;
    }


}// class
