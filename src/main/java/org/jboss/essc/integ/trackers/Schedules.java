package org.jboss.essc.integ.trackers;

import javax.annotation.Resource;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 * Central place for Tracker integartion schedules.
 * @author Ondrej Zizka
 */
@Stateless
public class Schedules {
    
    //@Inject TrackersOnDemandSynchronizer sync;
    @Inject TrackersScheduledSynchronizer sync;
    @Resource TimerService timer;
    
    //@Schedule(dayOfWeek = "*", dayOfMonth = "*", hour = "*", minute = "23", info = "Issue tracker version -> Release synchronization")
    public void syncReleases(){
        sync.createSyncNewVersionsOfAllProducts();
    }
    
}
