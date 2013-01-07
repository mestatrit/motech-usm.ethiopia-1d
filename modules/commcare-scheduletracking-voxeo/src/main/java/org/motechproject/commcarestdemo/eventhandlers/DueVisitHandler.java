package org.motechproject.commcarestdemo.eventhandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.joda.time.DateTime;
//import org.joda.time.LocalDate;
//import org.joda.time.LocalTime;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
//import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

@Component
public class DueVisitHandler {

    private static Logger logger = LoggerFactory.getLogger(DueVisitHandler.class);

    @Autowired
    private ContactInitiator contactInitiator;
    
    @Autowired
    private OpenMRSUtil openMrsUtil;
    
    @Autowired
    private VisitHandlerCommon visitHandlerCommon;
    

    
    public void handle(MilestoneEvent milestoneEvent) {
        
        logger.warn("Handled due visit milestone event for: " + milestoneEvent.getExternalId() + " --- " + milestoneEvent.getScheduleName() + " --- "
                + milestoneEvent.getWindowName());
        
        String motechID = milestoneEvent.getExternalId();

        DateTime lastVisit = openMrsUtil.dateOfLastEncounter(motechID, DemoConstants.VISIT_ENCOUNTER_TYPE, null);

        if (lastVisit == null || !fulfilledVisit(lastVisit, milestoneEvent)) {
            sendDueMessage(motechID, milestoneEvent);
        }      
        
        //scheduleTrackingService.fulfillCurrentMilestone(milestoneEvent.getExternalId(), milestoneEvent.getScheduleName(), LocalDate.now(), new Time(LocalTime.now()));
    }

    private void sendDueMessage(String motechID, MilestoneEvent milestoneEvent) {
        logger.warn("SENDING DUE MESSAGE");

        //sets values for milestoneEvent details
        visitHandlerCommon.formMessage(motechID, milestoneEvent);

        if (visitHandlerCommon.getMilestoneConceptName() == null) {
            return;
        }

        String patientName = visitHandlerCommon.getPatientName();
        String language = visitHandlerCommon.getLanguage();

        String messageNamePatient = "patientMessageDue";
        String messageNameProvider = "providerMessageDue";

        if (visitHandlerCommon.isIvrTrue() && language != null) {
            contactInitiator.placeCallToPatient(motechID, language, messageNamePatient);
            contactInitiator.placeCallToProvider(motechID, language, messageNameProvider);
        }

        if (visitHandlerCommon.isSmsTrue() && language != null) {
            contactInitiator.sendSMSToPatient(patientName, motechID, "Hello " + patientName + ". You are due for a visit. You must complete this visit within 5 minutes.");
            contactInitiator.sendSMSToProvider(patientName, motechID, "Your patient with ID " + motechID + " is due for a visit in the next 5 minutes.");
        }       
    }

    private boolean fulfilledVisit(DateTime lastVisit, MilestoneEvent milestoneEvent) {
        DateTime earliestTime = milestoneEvent.getMilestoneAlert().getEarliestDateTime();
        DateTime dueTime = milestoneEvent.getMilestoneAlert().getDueDateTime();

        logger.warn("Earliest time is: " + earliestTime + " and due time is: " + dueTime + " and the last visit was on: " + lastVisit);

        if (lastVisit.isAfter(earliestTime) && lastVisit.isBefore(dueTime)) {
            return true;
        }

        return false;
    }
        
}
