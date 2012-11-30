package org.motechproject.commcarestdemo.eventhandlers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.commcarestdemo.controllers.VxmlCalculator;
import org.motechproject.commcarestdemo.util.CommcareUtil;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.sms.api.service.SmsService;

@Component
public class DueVisitHandler {

    private static Logger logger = LoggerFactory.getLogger(DueVisitHandler.class);

    @Autowired
    private MessageSource messages;

    @Autowired
    private CommcareUtil commcareUtil;

    @Autowired
    private CMSLiteService cmsLiteService;

    @Autowired
    private IVRService voxeoService;

    @Autowired
    private SmsService smsService;
    
    @Autowired
    private VxmlCalculator vxmlCalculator;
    
    @Autowired
    private OpenMRSUtil openMrsUtil;
    
    @Autowired
    private CommcareUserService userService;
    
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    public void handle(MilestoneEvent milestoneEvent) {
        
        logger.debug("Handled due visit milestone event for: " + milestoneEvent.getExternalId() + " --- " + milestoneEvent.getScheduleName() + " --- "
                + milestoneEvent.getWindowName());
        
        String motechID = milestoneEvent.getExternalId();

        DateTime lastVisit = openMrsUtil.dateOfLastEncounter(motechID, DemoConstants.VISIT_ENCOUNTER_TYPE, null);

        if (lastVisit == null || !fulfilledVisit(lastVisit, milestoneEvent)) {
            sendDueMessage(motechID, milestoneEvent);
        }      
        
        scheduleTrackingService.fulfillCurrentMilestone(milestoneEvent.getExternalId(), milestoneEvent.getScheduleName(), LocalDate.now(), new Time(LocalTime.now()));
    }

    private void sendDueMessage(String motechID, MilestoneEvent milestoneEvent) {
        logger.warn("SENDING DUE MESSAGE");

        Map<String, String> milestoneData = milestoneEvent.getMilestoneData();

       /* String milestoneConceptName = milestoneData.get("conceptName");

        if (milestoneConceptName == null) {
            return;
        }

        String patientName = commcareUtil.getUserAssociatedWithPregnancy(motechID);
        String ivrFormat = milestoneData.get("IVRFormat");
        String smsFormat = milestoneData.get("SMSFormat");
        String language =  milestoneData.get("language");

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
            sendDueSMSToPatient(patientName, motechID, language, smsMessageNamePatient);
            sendDueSMSToProvider(patientName, motechID, language, smsMessageNameProvider);
        }*/
        
    }

    private boolean fulfilledVisit(DateTime lastVisit, MilestoneEvent milestoneEvent) {
        DateTime earliestTime = milestoneEvent.getMilestoneAlert().getEarliestDateTime();
        DateTime dueTime = milestoneEvent.getMilestoneAlert().getDueDateTime();

        logger.warn("Earliest time is: " + earliestTime + " and late time is: " + dueTime + " and the last visit was on: " + lastVisit);

        if (lastVisit.isAfter(earliestTime) && lastVisit.isBefore(dueTime)) {
            return true;
        }

        return false;
    }
        
    private void sendDueSMSToProvider(String patientName, String motechID, String language, String smsMessageNamePatient) {
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
       
        // alert to provider's phone number
        logger.warn("Sending alert to provider phone number: " + providerPhoneNum);
    }
        
    private void sendDueSMSToPatient(String patientName, String motechID, String language, String smsMessageNameProvider) {
        // only send if patient has a contact number in either the case or OpenMRS

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

    private void placeCallToPatient(String motechID, String language, String messageName) {
        if (cmsLiteService.isStringContentAvailable(language, messageName)) {
            StringContent content = null;
            try {
                content = cmsLiteService.getStringContent(language, messageName);
            } catch (ContentNotFoundException e) {
                logger.error("Failed to retrieve IVR content for language: " + language + " and name: " + messageName);
                return;
            }

            CallRequest request = new CallRequest(commcareUtil.phoneNumberOfPatient(motechID), 119, content.getValue());
            request.getPayload().put("USER_ID", motechID);
            request.getPayload().put("applicationName", "CommCareApp");
            request.setVxml(vxmlCalculator.calculateVxmlLocation(messageName));
            voxeoService.initiateCall(request);
        } else {
            logger.error("Could not find IVR content for language: " + language + " and name: " + messageName);
        }
    }

    private void placeCallToProvider(String providerPhone, String motechID, String language, String messageName) {
        if (cmsLiteService.isStringContentAvailable(language, messageName)) {
            StringContent content = null;
            try {
                content = cmsLiteService.getStringContent(language, messageName);
            } catch (ContentNotFoundException e) {
                logger.error("Failed to retrieve IVR content for language: " + language + " and name: " + messageName);
                return;
            }

            CallRequest request = new CallRequest(providerPhone, 119, content.getValue());
            request.getPayload().put("USER_ID", motechID);
            request.getPayload().put("applicationName", "CommCareApp");
            request.setVxml(vxmlCalculator.calculateVxmlLocation(messageName));
            voxeoService.initiateCall(request);
        } else {
            logger.error("Could not find IVR content for language: " + language + " and name: " + messageName);
        }
    }
}
