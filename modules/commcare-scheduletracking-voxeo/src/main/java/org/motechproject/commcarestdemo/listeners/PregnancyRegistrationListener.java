package org.motechproject.commcarestdemo.listeners;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PregnancyRegistrationListener {

    @Autowired
    private CommcareFormService formService;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private EventRelay eventRelay;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @MotechListener(subjects = EventSubjects.FORM_STUB_EVENT)
    public void handleFormEvent(MotechEvent event) {
        eventRelay.sendEventMessage(new MotechEvent("verificationEvent"));
        Map<String, Object> parameters = event.getParameters();

        String receivedOn = (String) parameters.get(EventDataKeys.RECEIVED_ON);
        String formId = (String) parameters.get(EventDataKeys.FORM_ID);
        List<String> caseIds = (List<String>) parameters.get(EventDataKeys.CASE_IDS);

        logger.warn("Received form (schedule tracking listener): " + formId);

        CommcareForm form = null;

        if (formId != null && formId.trim().length() > 0) {
            form = formService.retrieveForm(formId);
        } else {
            logger.info("Form Id was null");
        }

        if ("http://openrosa.org/formdesigner/882FC273-E436-4BA1-B8CC-9CA526FFF8C2".equals(form.getForm().getAttributes().get("xmlns"))) {
            logger.info("Received pregnancy registration form...");

            String caseId = getCaseId(form);

            String healthId = getHealthId(caseId);

            if (healthId != null) {
                enrollPregnancy(healthId);
            }

        }

        FormValueElement rootElement = null;

        if (form != null) {
            rootElement = form.getForm();
        }

        String caseType = rootElement.getElementByNameIncludeCase("case_type").getValue();

        if ("pregnancy".equals(caseType)) {

        }
    }

    private String getCaseId(CommcareForm form) {
        return form.getForm().getElementByNameIncludeCase("case").getAttributes().get("case_id");
    }

    private void enrollPregnancy(String healthId) {
        // EnrollmentRecord enrollment =
        // scheduleTrackingService.getEnrollment(healthId,
        // "pregnancy_schedule");

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest();

        enrollmentRequest.setEnrollmentDate(LocalDate.now());
        enrollmentRequest.setEnrollmentTime(new Time(LocalTime.now()));
        enrollmentRequest.setExternalId(healthId);
        enrollmentRequest.setScheduleName("pregnancy_schedule");

        scheduleTrackingService.enroll(enrollmentRequest);

        logger.info("Health id: " + healthId + " now enrolled.");

    }

    private String getHealthId(String caseId) {
        CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);

        return caseInfo.getFieldValues().get("health_id");
    }

}
