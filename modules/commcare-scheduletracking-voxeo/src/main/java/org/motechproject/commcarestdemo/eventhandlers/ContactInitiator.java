package org.motechproject.commcarestdemo.eventhandlers;

import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.commcarestdemo.vxml.VxmlCalculator;
import org.motechproject.event.MotechEvent;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.sms.api.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContactInitiator {
    
    private static Logger logger = LoggerFactory.getLogger(DueVisitHandler.class);

    @Autowired
    private CMSLiteService cmsLiteService;

    @Autowired
    private IVRService voxeoService;

    @Autowired
    private SmsService smsService;
    
    @Autowired
    private VxmlCalculator vxmlCalculator;
    
    @Autowired
    private VisitHandlerCommon visitHandlerCommon;
    
    public void sendSMSToProvider(String patientName, String motechID, String message) {
        // retrieve the case and check the user id, map it to OpenMRS

        if (!visitHandlerCommon.isFormedMessage()){
            return;
        }
        
        if (visitHandlerCommon.getUserId() == null) { // No provider found
            return;
        }

        String providerPhoneNum = visitHandlerCommon.getProviderPhone();

        if (providerPhoneNum != null) {
            smsService.sendSMS(providerPhoneNum, message);
            logger.warn("Sending alert to provider phone number: " + providerPhoneNum);
        }
    }
        
    public void sendSMSToPatient(String patientName, String motechID, String message) {
        // only send if patient has a contact number in either the case or OpenMRS
        if (!visitHandlerCommon.isFormedMessage()){
            return;
        }
        
        // only send if patient has a contact number in either the case or OpenMRS
        String patientPhoneNum = visitHandlerCommon.getPatientPhone();

        if (patientPhoneNum != null) {
            smsService.sendSMS(patientPhoneNum, message);
        }
        logger.warn("Sending alert to patient phone number: " + patientPhoneNum);
    }

    public void placeCallToPatient(String motechID, String language, String messageName) {        
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

    public void placeCallToProvider(String motechID, String language, String messageName) {
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
