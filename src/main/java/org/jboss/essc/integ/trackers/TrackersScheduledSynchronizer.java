package org.jboss.essc.integ.trackers;

import org.jboss.essc.integ.trackers.model.ExternalVersionInfo;
import org.jboss.essc.integ.trackers.model.ExternalProjectInfo;
import java.util.List;
import javax.ejb.Schedule;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringUtils;
import org.jboss.essc.web.dao.ProductDaoBean;
import org.jboss.essc.web.dao.ReleaseDaoBean;
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
    
    
    
    @PersistenceContext private EntityManager em;
    
    @Inject private ProductDaoBean daoProduct;
    @Inject private ReleaseDaoBean daoRelease;
    
    
    
    @Schedule(hour = "11", minute = "29", info = "Issue tracker version -> Release synchronization")
    public void createReleasesForNewVersionsOfAllProducts(){
        try {
            final BugzillaRetriever bz = new BugzillaRetriever();

            // For each product...
            List<Product> products = daoProduct.getProducts_orderName(1000);
            for( Product product : products ) {
                if( StringUtils.isBlank( product.getExtIdBugzilla() ))
                    continue;

                //List<Long> = daoProduct.getVersionsOfProduct();
                List<Release> releases = daoRelease.getReleasesOfProduct( product, true );

                // For each version in Bugzilla...
                ExternalProjectInfo projInfo = bz.retrieveProject( product.getExtIdBugzilla() );
                for (ExternalVersionInfo verInfo : projInfo.getVersions() ) {
                    Release newRel = new Release(null, product, verInfo.getName());
                    newRel.setExtIdBugzilla( "" + verInfo.getExternalId() );
                    if( releases.contains( newRel ) ) // Relies on equals()!
                       continue;
                    newRel.setNote("Imported from Bugzilla");
                    try {
                        daoRelease.addRelease( newRel );
                    }
                    catch( Exception ex ){
                        log.error("Couldn't store a release imported from Bugzilla: " + ex, ex);
                    }
                }
            }// for each product
        }
        catch( Exception ex ){
            log.error("Couldn't imported releases from Bugzilla: " + ex, ex);
        }
        
    }// createReleasesForNewVersionsOfAllProducts()


}// class
