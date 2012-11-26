package org.motechproject.commcarestdemo.eventhandlers;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.commcarestdemo.util.CommcareUtil;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
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
    private CommcareUserService userService;

    @Autowired
    private OpenMRSUtil openMrsUtil;

    @Autowired
    private CommcareUtil commcareUtil;

    public void handle(MilestoneEvent milestoneEvent) {

        String motechId = milestoneEvent.getExternalId();

        DateTime lastVisit = openMrsUtil.dateOfLastEncounter(motechId, DemoConstants.VISIT_ENCOUNTER_TYPE, null);

        if (lastVisit == null || !fulfilledVisit(lastVisit, milestoneEvent)) {
            sendLateMessage(motechId);
        }

        //The schedule's milestone is fulfilled since either the patient had a visit or late messages were sent
        scheduleTrackingService.fulfillCurrentMilestone(milestoneEvent.getExternalId(), milestoneEvent.getScheduleName(), LocalDate.now());
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



    private void sendLateMessage(String patientId) {
        logger.warn("SENDING LATE MESSAGE");
        sendLateMessageToPatient(patientId);
        sendLateMessageToProvider(patientId);
    }

    private void sendLateMessageToProvider(String patientId) {
        //retrieve the case and check the user id, map it to OpenMRS
        String userId = commcareUtil.getUserAssociatedWithPregnancy(patientId);

        if (userId == null) {
            //No provider found
            return;
        }

        CommcareUser provider = userService.getCommcareUserById(userId);
        
        String providerPhoneNum = provider.getDefaultPhoneNumber();
        
        if (providerPhoneNum == null) {
            return;
        }
        
        //alert to provider's phone number
    }

    private void sendLateMessageToPatient(String patientId) {
        //only send if patient has a contact number in either the case or OpenMRS

        String phoneNum = commcareUtil.phoneNumberOfPatient(patientId);

        if (phoneNum != null) {
            //alert to patient's phone number
        }

    }
}
