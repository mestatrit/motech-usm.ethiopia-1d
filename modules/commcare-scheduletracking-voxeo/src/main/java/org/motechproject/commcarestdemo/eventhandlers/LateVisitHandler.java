package org.motechproject.commcarestdemo.eventhandlers;

import org.joda.time.DateTime;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LateVisitHandler {
    
    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Autowired
    private OpenMRSUtil openMrsUtil;

    public void handle(MilestoneEvent milestoneEvent) {

        String motechId = milestoneEvent.getExternalId();

        DateTime lastVisit = openMrsUtil.dateOfLastEncounter(motechId, DemoConstants.VISIT_ENCOUNTER_TYPE, null);

        if (lastVisit == null || !fulfilledVisit(lastVisit, milestoneEvent)) {
            sendLateMessage();
        }
    }



    private boolean fulfilledVisit(DateTime lastVisit, MilestoneEvent milestoneEvent) {
        DateTime dueTime = milestoneEvent.getMilestoneAlert().getDueDateTime();
        DateTime lateTime = milestoneEvent.getMilestoneAlert().getLateDateTime();
        
        logger.warn("Due time is: " + dueTime + " and late time is: " + lateTime + " and the last visit was on: " + lastVisit);

        if (lastVisit.isAfter(dueTime) && lastVisit.isBefore(lateTime)) {
            return true;
        }

        return false;
    }



    private void sendLateMessage() {
        logger.warn("SENDING LATE MESSAGE");
    }

}
