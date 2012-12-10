package org.motechproject.commcarestdemo.eventhandlers;

import java.util.Map;

import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.commcarestdemo.util.CommcareUtil;
import org.motechproject.commcarestdemo.util.DemoConstants;
import org.motechproject.commcarestdemo.util.OpenMRSUtil;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitHandlerCommon {
    
    @Autowired
    CommcareUtil commcareUtil;
    
    @Autowired
    OpenMRSUtil openMRSUtil;
    
    @Autowired
    private CommcareUserService userService;
    
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    private Map<String, String> milestoneData;
    private String milestoneConceptName;    
    private String patientName;
    private String patientPhone;
    private String providerPhone;
    private String userId;
    private String ivrFormat;
    private String smsFormat;
    private String language; 
    private boolean ivrTrue;
    private boolean smsTrue;
    private boolean formedMessage;

    public void formMessage(String motechID, MilestoneEvent milestoneEvent) {
        setMilestoneData(null);
        setMilestoneConceptName("");
        setPatientName("");
        setPatientPhone("");
        setProviderPhone("");
        setIvrFormat("");
        setLanguage("");
        setIvrTrue(false);
        setSmsTrue(false);        
        setMilestoneData(milestoneEvent.getMilestoneData());
        setMilestoneConceptName(milestoneData.get("conceptName"));
        setFormedMessage(false);
        
        if (milestoneConceptName == null) {
            return;
        }

        setPatientName(commcareUtil.getNameOfPatient((motechID)));
        setIvrFormat(milestoneData.get("IVRFormat"));
        setSmsFormat(milestoneData.get("SMSFormat"));
        setLanguage(milestoneData.get("language"));
        
        // retrieve the case and check the user id, map it to OpenMRS
        //setUserId(commcareUtil.getUserAssociatedWithPregnancy(motechID));
        setUserId(getUserFromEnrollment(motechID));
        
        CommcareUser provider = userService.getCommcareUserById(userId);
        setProviderPhone(provider.getDefaultPhoneNumber());
        
        setPatientPhone(commcareUtil.phoneNumberOfPatient(motechID));
        
        if ("true".equals(ivrFormat) && language != null) {
            setIvrTrue(true);
        }

        if ("true".equals(smsFormat) && language != null) {
            setSmsTrue(true);
        }
        setFormedMessage(true);
    }
    
    private String getUserFromEnrollment(String motechID) {
        EnrollmentRecord record = scheduleTrackingService.getEnrollment(motechID, DemoConstants.SCHEDULE_NAME);
        return record.getMetadata().get(DemoConstants.PROVIDER_RESPONSIBLE);
    }

    public Map<String, String> getMilestoneData() {
        return milestoneData;
    }

    public void setMilestoneData(Map<String, String> milestoneData) {
        this.milestoneData = milestoneData;
    }

    public String getMilestoneConceptName() {
        return milestoneConceptName;
    }

    public void setMilestoneConceptName(String milestoneConceptName) {
        this.milestoneConceptName = milestoneConceptName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public void setProviderPhone(String providerPhone) {
        this.providerPhone = providerPhone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getIvrFormat() {
        return ivrFormat;
    }

    public void setIvrFormat(String ivrFormat) {
        this.ivrFormat = ivrFormat;
    }

    public String getSmsFormat() {
        return smsFormat;
    }

    public void setSmsFormat(String smsFormat) {
        this.smsFormat = smsFormat;
    }

    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isIvrTrue() {
        return ivrTrue;
    }

    public void setIvrTrue(boolean ivrTrue) {
        this.ivrTrue = ivrTrue;
    }

    public boolean isSmsTrue() {
        return smsTrue;
    }

    public void setSmsTrue(boolean smsTrue) {
        this.smsTrue = smsTrue;
    }

    public boolean isFormedMessage() {
        return formedMessage;
    }

    public void setFormedMessage(boolean formedMessage) {
        this.formedMessage = formedMessage;
    }

}
