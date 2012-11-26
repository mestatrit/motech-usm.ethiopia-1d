package org.motechproject.commcarestdemo.util;

import java.util.List;

import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.CommcareCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommcareUtil {


    @Autowired
    private CommcareCaseService caseService;

    public String phoneNumberOfPatient(String patientId) {
        List<CaseInfo> caseList = caseService.getAllCasesByType(DemoConstants.CASE_TYPE);

        for (CaseInfo pregnancyCase : caseList) {
            if (patientId.equals(pregnancyCase.getFieldValues().get(DemoConstants.HEALTH_ID_FIELD))) {
                return pregnancyCase.getFieldValues().get(DemoConstants.MOBILE_PHONE_FIELD);
            }
        }

        return null;
    }

    public String getUserAssociatedWithPregnancy(String patientId) {
        List<CaseInfo> caseList = caseService.getAllCasesByType(DemoConstants.CASE_TYPE);

        for (CaseInfo pregnancyCase : caseList) {
            if (patientId.equals(pregnancyCase.getFieldValues().get(DemoConstants.HEALTH_ID_FIELD))) {
                return pregnancyCase.getUserId();
            }
        }

        return null;
    }

}
