package org.motechproject.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CaseTask;
import org.motechproject.commcare.domain.CreateTask;
import org.motechproject.commcare.domain.UpdateTask;
import org.motechproject.commcare.service.CommcareCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommcarePregnancyModule {

    private final CommcareCaseService caseService;
    private final List<CaseElementMapping> mappings;

    @Autowired
    public CommcarePregnancyModule(CommcareCaseService caseService) {
        this.caseService = caseService;

        mappings = new ArrayList<>();

        CaseElementMapping mapping = new CaseElementMapping("Emergency Referral", "emrg_referral");
        mapping.addFieldValue("true", "yes");
        mapping.addFieldValue("false", "no");
        mappings.add(mapping);

        mapping = new CaseElementMapping("Estimated Date of Conception", "edd");
        mappings.add(mapping);
    }

    public class CreateCaseBuilder {
        CaseTask caseTask;

        public CreateCaseBuilder(String healthId) {
            caseTask = new CaseTask();
            caseTask.setUserId("e0ea969f871d0fb29209cac1411a85f7");
            UpdateTask updateTask = new UpdateTask();
            updateTask.setFieldValues(new HashMap<String, String>());
            updateTask.getFieldValues().put("health_id", healthId);
            caseTask.setUpdateTask(updateTask);
        }

        public void addUpdateElement(String conceptName, String value) {
            for (CaseElementMapping mapping : mappings) {
                if (mapping.handles(conceptName)) {
                    UpdateTask update = caseTask.getUpdateTask();
                    addCaseElements(update, mapping.getCaseElement(), mapping.translateValue(value));
                    break;
                }
            }
        }

        public CaseTask build() {
            CreateTask task = new CreateTask();
            task.setCaseName("test_case" + UUID.randomUUID());
            task.setCaseType("test_form");
            task.setOwnerId("e0ea969f871d0fb29209cac1411a85f7");
            caseTask.setCreateTask(task);

            return caseTask;
        }
    }

    public CaseElementMapping getCaseMapping(String conceptName) {
        CaseElementMapping match = null;
        for (CaseElementMapping mapping : mappings) {
            if (mapping.handles(conceptName)) {
                match = mapping;
                break;
            }
        }

        return match;
    }

    public void updateCase(CaseElementMapping caseElementMapping, String value, String motechId) {
        List<CaseInfo> cases = caseService.getAllCasesByType("test_form");
        CaseInfo targetCase = null;
        for (CaseInfo ccCase : cases) {
            String healthId = ccCase.getFieldValues().get("health_id");
            if (motechId.equals(healthId)) {
                targetCase = ccCase;
                break;
            }
        }

        UpdateTask update = getUpdateTask(targetCase.getCaseName(), targetCase.getCaseType());
        addCaseElements(update, caseElementMapping.getCaseElement(), caseElementMapping.translateValue(value));

        CaseTask task = createCaseTask(targetCase, update);
        caseService.uploadCase(task);
    }

    private void addCaseElements(UpdateTask update, String caseElement, String translateValue) {
        update.getFieldValues().put(caseElement, translateValue);
    }

    private UpdateTask getUpdateTask(String caseName, String caseType) {
        UpdateTask update = new UpdateTask();
        update.setCaseName(caseName);
        update.setCaseType(caseType);
        Map<String, String> fieldValues = new HashMap<>();
        update.setFieldValues(fieldValues);
        return update;
    }

    private CaseTask createCaseTask(CaseInfo targetCase, UpdateTask update) {
        CaseTask task = new CaseTask();
        task.setCaseId(targetCase.getCaseId());
        task.setUpdateTask(update);
        task.setUserId(targetCase.getUserId());

        return task;
    }

    public CreateCaseBuilder createCaseBuilder(String healthId) {
        return new CreateCaseBuilder(healthId);
    }

    public void createCase(CaseTask caseTask) {
        caseService.uploadCase(caseTask);
    }
}
