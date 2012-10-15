package org.motechproject.mapper.adapters.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.adapters.mappings.MRSActivity;
import org.motechproject.mapper.adapters.mappings.ObservationMapping;
import org.motechproject.mapper.adapters.mappings.OpenMRSEncounterActivity;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.OpenMRSCommcareUtil;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllEncountersAdapter implements ActivityFormAdapter {

    private static Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    @Autowired
    private OpenMRSCommcareUtil openMrsUtil;

    @Autowired
    private CommcareUserService userService;

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {

        FormValueElement rootElement = form.getForm();

        OpenMRSEncounterActivity encounterActivity = (OpenMRSEncounterActivity) activity;

        Map<String, String> idScheme = encounterActivity.getIdScheme();
        Map<String, String> encounterMappings = encounterActivity.getEncounterMappings();

        String motechId = openMrsUtil.retrieveId(idScheme, rootElement);

        MRSPatient patient = openMrsUtil.getPatientByMotechId(motechId);

        if (patient == null) {
            logger.info("Patient " + motechId + " does not exist, failed to handle form " + form.getId());
            return;
        } else {
            logger.info("Adding encounter for patient: " + motechId);
        }

        Date dateReceived = DateTime.parse(form.getMetadata().get(FormMappingConstants.FORM_TIME_END)).toDate();

        Set<MRSObservation> observations = generateObservations(form.getForm(),
                encounterActivity.getObservationMappings());

        String providerName = form.getMetadata().get(FormMappingConstants.FORM_USERNAME);

        String facilityNameField = null;

        if (encounterMappings != null) {
            facilityNameField = encounterMappings.get(FormMappingConstants.FACILITY_NAME_FIELD);
        }

        String facilityName = encounterActivity.getFacilityName();

        if (facilityNameField != null && facilityName == null) {
            FormValueElement facilityElement = rootElement.getElementByName(facilityNameField);
            if (facilityElement != null) {
                facilityName = facilityElement.getValue();
            }
        }

        if (facilityName == null) {
            if (encounterActivity.getFacilityScheme() != null && "commcareUser".equals(encounterActivity.getFacilityScheme().get("type"))) {
                String facilityUserFieldName = encounterActivity.getFacilityScheme().get("fieldName");
                if (facilityUserFieldName != null) {
                    String userId = form.getMetadata().get("userID");
                    CommcareUser user = userService.getCommcareUserById(userId);
                    facilityName = user.getUserData().get(facilityUserFieldName);
                }
            }
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + FormMappingConstants.DEFAULT_FACILITY);
            facilityName = FormMappingConstants.DEFAULT_FACILITY;
        }

        openMrsUtil.addEncounter(patient, observations, providerName, dateReceived, facilityName,
                encounterActivity.getEncounterType());
    }

    private Set<MRSObservation> generateObservations(FormValueElement form, List<ObservationMapping> observationMappings) {
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        for (ObservationMapping obs : observationMappings) {
            String conceptId = obs.getConceptId();
            if (conceptId != null && conceptId.trim().length() > 0) {
                List<FormValueElement> elements = form.getElementsByAttribute("concept_id", conceptId);
                if (elements.size() > 0) {
                    FormValueElement element = elements.get(0);
                    if (element.getValue() != null && element.getValue().trim().length() > 0) {
                        observations.addAll(addObservations(obs, element));
                    }
                }
            } else {
                String elementName = obs.getElementName();
                if (elementName != null) {
                    FormValueElement element = form.getElementByName(elementName);
                    if (element != null && element.getValue() != null && element.getValue().trim().length() > 0) {
                        observations.addAll(addObservations(obs, element));
                    }
                }
            }
        }
        return observations;
    }

    private static Collection<? extends MRSObservation> addObservations(ObservationMapping obs, FormValueElement form) {
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        if (FormMappingConstants.LIST_TYPE.equals(obs.getType())) {
            String[] values = form.getValue().split(FormMappingConstants.LIST_DELIMITER);
            Map<String, String> valueMappings = obs.getValues();
            String conceptName = obs.getConceptName();
            for (String value : values) {
                String mappedValue = null;
                if (valueMappings != null) {
                    mappedValue = valueMappings.get(value);
                }
                MRSObservation<String> observation;
                if (mappedValue != null) {
                    observation = new MRSObservation<String>(new Date(), conceptName, mappedValue);
                } else {
                    observation = new MRSObservation<String>(new Date(), conceptName, value);
                }
                observations.add(observation);
            }
        } else {
            Map<String, String> valueMappings = obs.getValues();
            String mappedValue = null;
            if (valueMappings != null) {
                mappedValue = valueMappings.get(form.getValue());
            }
            String conceptName = obs.getConceptName();
            MRSObservation<String> observation;
            if (mappedValue != null) {
                observation = new MRSObservation<String>(new Date(), conceptName, mappedValue);
            } else {
                observation = new MRSObservation<String>(new Date(), conceptName, form.getValue());
            }
            observations.add(observation);
        }
        return observations;
    }

}
