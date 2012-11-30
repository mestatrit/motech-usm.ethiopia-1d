package org.motechproject.commcarestdemo.eventhandlers;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
import org.motechproject.commcarestdemo.vxml.VxmlCalculator;
import org.motechproject.event.MotechEvent;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
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
    private CMSLiteService cmsLiteService;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private IVRService voxeoService;  
    
    @Autowired
    private OpenMRSUtil openMrsUtil;

    @Autowired
    private SmsService smsService;

    @Autowired
    private VisitHandlerCommon visitHandlerCommon;

    @Autowired
    private VxmlCalculator vxmlCalculator;

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
            placeCallToPatient(motechID, language, messageNamePatient);
            //placeCallToProvider(motechID, language, ivrMessageNameProvider);
        }

        if (visitHandlerCommon.isSmsTrue() && language != null) {
            sendLateSMSToPatient(patientName, motechID, language, messageNamePatient);
            //sendLateSMSToProvider(patientName, motechID, language, smsMessageNameProvider);
        }
    }
    
    private void sendLateSMSToPatient(String patientName, String motechID, String language, String smsMessageNamePatient) {       
        if (!visitHandlerCommon.isFormedMessage()){
            return;
        }
        
        // only send if patient has a contact number in either the case or OpenMRS
        String patientPhoneNum = visitHandlerCommon.getPatientPhone();

        if (patientPhoneNum != null) {
            // MessageSource resources = new
            // ClassPathXmlApplicationContext("applicationContext-commcare-scheduletracking-demo.xml");
            // String message = resources.getMessage("SMSVisitToPatient", new
            // Object[] { patientName }, "Required", null);
            String message = "Hello " + patientName + ". You are due for a visit. You must complete this visit within 5 minutes.";
            smsService.sendSMS(patientPhoneNum, message);
        }
        logger.warn("Sending alert to patient phone number: " + patientPhoneNum);
    }

    private void placeCallToPatient(String motechID, String language, String messageName) {
        if (!visitHandlerCommon.isFormedMessage()){
            return;
        }
        
        String patientPhoneNum = visitHandlerCommon.getPatientPhone();

        if (patientPhoneNum != null) {
            if (cmsLiteService.isStringContentAvailable(language, messageName)) {
                StringContent content = null;
                try {
                    content = cmsLiteService.getStringContent(language, messageName);
                } catch (ContentNotFoundException e) {
                    logger.error("Failed to retrieve IVR content for language: " + language + " and name: " + messageName);
                    return;
                }

                CallRequest request = new CallRequest(patientPhoneNum, 119, content.getValue());
                request.getPayload().put("USER_ID", motechID);
                request.getPayload().put("applicationName", "CommCareApp");
                request.setMotechId(motechID);
                request.setOnBusyEvent(new MotechEvent("CALL_BUSY"));
                request.setOnFailureEvent(new MotechEvent("CALL_FAIL"));
                request.setOnNoAnswerEvent(new MotechEvent("CALL_NO_ANSWER"));
                request.setOnSuccessEvent(new MotechEvent("CALL_SUCCESS"));
                request.setVxml(vxmlCalculator.calculateVxmlLocation(messageName));
                voxeoService.initiateCall(request);
            } else {
                logger.error("Could not find IVR content for language: " + language + " and name: " + messageName);
            }
        } else {
            return;
        }
    }

    private void sendLateSMSToProvider(String patientName, String motechID, String language, String smsMessageNamePatient) {
        if (!visitHandlerCommon.isFormedMessage()){
            return;
        }
        
        if (visitHandlerCommon.getUserId() == null) { // No provider found
            return;
        }

        String providerPhoneNum = visitHandlerCommon.getProviderPhone();

        if (providerPhoneNum != null) {

            // MessageSource resources = new
            // ClassPathXmlApplicationContext("applicationContext-commcare-scheduletracking-demo.xml");
            // String message = resources.getMessage("SMSVisitToProvider", new
            // Object[] { motechID }, "Required", null);
            String message = "Your patient with ID " + motechID + " is due for a visit in the next 5 minutes.";
            smsService.sendSMS(providerPhoneNum, message);
            logger.warn("Sending alert to provider phone number: " + providerPhoneNum);
        }
    }

    private void placeCallToProvider(String motechID, String language, String messageName) {
        
        if (!visitHandlerCommon.isFormedMessage()){
            return;
        }
        
        if (visitHandlerCommon.getUserId() == null) { // No provider found
            return;
        }

        String providerPhoneNum = visitHandlerCommon.getProviderPhone();
        if (providerPhoneNum == null) {
            return;
        }

        if (cmsLiteService.isStringContentAvailable(language, messageName)) {
            StringContent content = null;
            try {
                content = cmsLiteService.getStringContent(language, messageName);
            } catch (ContentNotFoundException e) {
                logger.error("Failed to retrieve IVR content for language: " + language + " and name: " + messageName);
                return;
            }

            CallRequest request = new CallRequest(providerPhoneNum, 119, content.getValue());
            request.getPayload().put("USER_ID", motechID);
            request.getPayload().put("applicationName", "CommCareApp");
            request.setVxml(vxmlCalculator.calculateVxmlLocation(messageName));
            voxeoService.initiateCall(request);
        } else {
            logger.error("Could not find IVR content for language: " + language + " and name: " + messageName);
        }
    }
}
