package org.motechproject.commcarestdemo.eventhandlers;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LateVisitHandler {

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @Autowired
    private ScheduleTrackingService scheduleTrackingService; 
    
    @Autowired
    private OpenMRSUtil openMrsUtil;

    @Autowired
    private VisitHandlerCommon visitHandlerCommon;
    
    @Autowired
    private ContactInitiator contactInitiator;

    public void handle(MilestoneEvent milestoneEvent) {
        String motechID = milestoneEvent.getExternalId();
        DateTime lastVisit = openMrsUtil.dateOfLastEncounter(motechID, DemoConstants.VISIT_ENCOUNTER_TYPE, null);
        if (lastVisit == null || !fulfilledVisit(lastVisit, milestoneEvent)) {
            sendLateMessage(motechID, milestoneEvent);
        }
        // The schedule's milestone is fulfilled since either the patient had a visit or late messages were sent
        scheduleTrackingService.fulfillCurrentMilestone(milestoneEvent.getExternalId(), milestoneEvent.getScheduleName(), LocalDate.now(), new Time(LocalTime.now()));
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

    private void sendLateMessage(String motechID, MilestoneEvent milestoneEvent) {
        logger.warn("SENDING LATE MESSAGE");

        //sets values for milestoneEvent details
        visitHandlerCommon.formMessage(motechID, milestoneEvent);

        if (visitHandlerCommon.getMilestoneConceptName() == null) {
            return;
        }

        String patientName = visitHandlerCommon.getPatientName();
        String language = visitHandlerCommon.getLanguage();

        String messageNamePatient = "patientMessageMissed";
        String messageNameProvider = "providerMessageMissed";

        if (visitHandlerCommon.isIvrTrue() && language != null) {
            contactInitiator.placeCallToPatient(motechID, language, messageNamePatient);
            contactInitiator.placeCallToProvider(motechID, language, messageNameProvider);
        }

        if (visitHandlerCommon.isSmsTrue() && language != null) {
            contactInitiator.sendSMSToPatient(patientName, motechID, "Hello " + "patientNameMethod" + ". You have missed your visit. Please visit your clinic as soon as possible.");
            contactInitiator.sendSMSToProvider(patientName, motechID, "Your patient with ID " + motechID + " has missed a visit. Please contact your patient to be sure they are aware of this.");
        }
    }

}
