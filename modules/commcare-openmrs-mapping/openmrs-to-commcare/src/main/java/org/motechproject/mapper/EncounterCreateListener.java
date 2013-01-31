package org.motechproject.mapper;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mapper.CommcarePregnancyModule.CreateCaseBuilder;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterCreateListener {

    private static Logger logger = LoggerFactory.getLogger("openmrs-commcare-mapper");

    private final EncounterAdapter encounterAdapter;
    private final PatientAdapter patientAdapter;
    private final CommcarePregnancyModule pregnancyModule;

    @Autowired
    public EncounterCreateListener(CommcarePregnancyModule pregnancyModule, EncounterAdapter encounterAdapter,
            PatientAdapter patientAdapter) {
        this.pregnancyModule = pregnancyModule;
        this.encounterAdapter = encounterAdapter;
        this.patientAdapter = patientAdapter;
    }

    @MotechListener(subjects = EventSubjects.ENCOUNTER_CREATE)
    public void handleEncounterCreate(MotechEvent event) {

        logger.warn("Received encounter create event");


        String encounterId = event.getParameters().get(EventDataKeys.UUID).toString();
        Encounter encounter = encounterAdapter.getEncounterById(encounterId);
        Patient patient = patientAdapter.getPatient(encounter.getPatient().getPatientId());
        String healthId = patient.getMotechId();

        CreateCaseBuilder builder = pregnancyModule.createCaseBuilder(healthId);

        for (Observation<?> obs : encounter.getObservations()) {
            builder.addUpdateElement(obs.getConceptName(), obs.getValue().toString());
        }

        Provider provider = encounter.getProvider();

        OpenMRSPerson person = (OpenMRSPerson) provider.getPerson();

        String providerName = person.getFullName();

        builder.setProviderName(providerName);

        pregnancyModule.createCase(builder.build());
    }
}
