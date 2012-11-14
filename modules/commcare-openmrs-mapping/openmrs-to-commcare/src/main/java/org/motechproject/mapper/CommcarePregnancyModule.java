package org.motechproject.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CaseTask;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.CreateTask;
import org.motechproject.commcare.domain.UpdateTask;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.dao.MotechJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

@Component
public class CommcarePregnancyModule {

    private static final String MAPPING_FILE_NAME = "commcare-mappings.json";

    private static Logger logger = LoggerFactory.getLogger("openmrs-commcare-mapper");

    private static final MotechJsonReader READER = new MotechJsonReader();

    private final CommcareCaseService caseService;
    private final CommcareUserService userService;
    private final List<CommcareMapping> mappings;

    @Autowired
    public CommcarePregnancyModule(CommcareCaseService caseService, CommcareUserService userService) {
        this.caseService = caseService;
        this.userService = userService;

        mappings = getAllMappings();

        logger.warn("# of mappings: " + mappings.size());

        // CaseElementMapping mapping = new
        // CaseElementMapping("Emergency Referral", "emrg_referral");
        // mapping.addFieldValue("true", "yes");
        // mapping.addFieldValue("false", "no");
        // mappings.add(mapping);
        //
        // mapping = new CaseElementMapping("Estimated Date of Conception",
        // "edd");
        // mappings.add(mapping);
    }

    public static List<CommcareMapping> getAllMappings() {
        InputStream is = CommcarePregnancyModule.class.getClassLoader().getResourceAsStream(MAPPING_FILE_NAME);

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {
            logger.error("Error retreiving all mappings: " + e.getMessage());
        }

        return readJson(writer.toString());
    }

    public static List<CommcareMapping> readJson(String json) {
        Type type = new TypeToken<List<CommcareMapping>>() {
        } .getType();
        return (List<CommcareMapping>) READER.readFromString(json, type);
    }

    public class CreateCaseBuilder {
        private CaseTask caseTask;
        private String providerName;

        public CreateCaseBuilder(String healthId) {
            caseTask = new CaseTask();
            caseTask.setUserId("e0ea969f871d0fb29209cac1411a85f7");
            UpdateTask updateTask = new UpdateTask();
            updateTask.setFieldValues(new HashMap<String, String>());
            updateTask.getFieldValues().put("health_id", healthId);
            caseTask.setUpdateTask(updateTask);
        }

        public void addUpdateElement(String conceptName, String value) {
            for (CommcareMapping commcareMapping : mappings) {
                List<CaseElementMapping> caseElements = commcareMapping.getMappings();
                for (CaseElementMapping mapping : caseElements) {
                    if (mapping.handles(conceptName)) {
                        logger.warn("Handles this mapping");
                        UpdateTask update = caseTask.getUpdateTask();
                        addCaseElements(update, mapping.getCaseElement(), mapping.translateValue(value));
                        break;
                    }
                }
            }
        }

        public CaseTask build() {
            CreateTask task = new CreateTask();
            task.setCaseName("test_case" + UUID.randomUUID());
            task.setCaseType("test_form");
            String ownerId = retrieveProviderId(providerName);
            if (ownerId == null) {
                ownerId = "";
            }
            task.setOwnerId(ownerId);
            caseTask.setCreateTask(task);

            return caseTask;
        }

        private String retrieveProviderId(String providerName2) {
            CommcareUser user = userService.getCommcareUserById(providerName2);
            return user.getUsername();
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }
    }

    public CommcareMapping getCaseMapping(String conceptName) {
        CommcareMapping match = null;
        for (CommcareMapping commcareMapping : mappings) {
            List<CaseElementMapping> caseMappings = commcareMapping.getMappings();
            for (CaseElementMapping mapping : caseMappings) {
                if (mapping.handles(conceptName)) {
                    match = commcareMapping;
                    break;
                }
            }
        }

        return match;
    }

    public void updateCase(CommcareMapping match, String value, String motechId) {
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
        // addCaseElements(update, match.getCaseElement(),
        // match.translateValue(value));

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
