package org.motechproject.commcarestdemo.eventhandlers;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.commcarestdemo.util.CommcareUtil;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.sms.api.service.SmsService;
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
    
    @Autowired
    private SmsService smsService;

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

        Map<String, String> milestoneData = milestoneEvent.getMilestoneData();

        String milestoneConceptName = milestoneData.get("conceptName");

        if (milestoneConceptName == null) {
            return;
        }

        String patientName = "patientNameMethod"; //commcareUtil.getUserAssociatedWithPregnancy(motechID).??();
        String ivrFormat = milestoneData.get("IVRFormat");
        String smsFormat = milestoneData.get("SMSFormat");
        String language = milestoneData.get("language");

        String messageNamePatient = "VisitToPatient";
        String messageNameProvider = "VisitToProvider";

        if ("true".equals(ivrFormat) && language != null) {
            String ivrMessageNamePatient = messageNamePatient.concat("IVR");
            String ivrMessageNameProvider = messageNameProvider.concat("IVR");
            // placeCallToPatient(motechID, language, ivrMessageNamePatient);
            // //messageName = IVRVisitToPatient
            // placeCallToProvider(providerPhoneNum, motechID, language,
            // ivrMessageNameProvider); //messageName = IVRVisitToProvider
        }

        if ("true".equals(smsFormat) && language != null) {
            String smsMessageNamePatient = messageNamePatient.concat("SMS");
            String smsMessageNameProvider = messageNameProvider.concat("SMS");
            sendLateSMSToPatient(patientName, motechID, language, smsMessageNamePatient);
            sendLateSMSToProvider(patientName, motechID, language, smsMessageNameProvider);
        }
    }

    private void sendLateSMSToProvider(String patientName, String motechID, String language, String smsMessageNamePatient) {
        // retrieve the case and check the user id, map it to OpenMRS
        String userId = commcareUtil.getUserAssociatedWithPregnancy(motechID);

        if (userId == null) {
            // No provider found
            return;
        }

        CommcareUser provider = userService.getCommcareUserById(userId);

        String providerPhoneNum = provider.getDefaultPhoneNumber();

        if (providerPhoneNum == null) {
            return;
        }
        
        // MessageSource resources = new
        // ClassPathXmlApplicationContext("applicationContext-commcare-scheduletracking-demo.xml");
        // String message = resources.getMessage("SMSVisitToProvider", new
        // Object[] { motechID }, "Required", null);
        String message = "Your patient with ID " + motechID + " is due for a visit in the next 5 minutes.";
        smsService.sendSMS(providerPhoneNum, message);
       
        logger.warn("Sending alert to provider phone number: " + providerPhoneNum);
    }

    private void sendLateSMSToPatient(String patientName, String motechID, String language, String smsMessageNameProvider) {
        // only send if patient has a contact number in either the case or
        // OpenMRS

        String phoneNum = commcareUtil.phoneNumberOfPatient(motechID);

        if (phoneNum != null) {
            // MessageSource resources = new
            // ClassPathXmlApplicationContext("applicationContext-commcare-scheduletracking-demo.xml");
            // String message = resources.getMessage("SMSVisitToPatient", new
            // Object[] { patientName }, "Required", null);
            String message = "Hello " + patientName + ". You are due for a visit. You must complete this visit within 5 minutes.";
            smsService.sendSMS(phoneNum, message);
        }

        logger.warn("Sending alert to patient phone number: " + phoneNum);
    }
}
