package org.motechproject.mapper;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mapper.CommcarePregnancyModule.CreateCaseBuilder;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterCreateListener {

    private final MRSEncounterAdapter encounterAdapter;
    private final MRSPatientAdapter patientAdapter;
    private final CommcarePregnancyModule pregnancyModule;

    @Autowired
    public EncounterCreateListener(CommcarePregnancyModule pregnancyModule, MRSEncounterAdapter encounterAdapter,
            MRSPatientAdapter patientAdapter) {
        this.pregnancyModule = pregnancyModule;
        this.encounterAdapter = encounterAdapter;
        this.patientAdapter = patientAdapter;
    }

    @MotechListener(subjects = EventSubjects.ENCOUNTER_CREATE)
    public void handleEncounterCreate(MotechEvent event) {
        String encounterId = event.getParameters().get(EventDataKeys.UUID).toString();
        MRSEncounter encounter = encounterAdapter.getEncounterById(encounterId);
        MRSPatient patient = patientAdapter.getPatient(encounter.getPatient().getId());
        String healthId = patient.getMotechId();

        CreateCaseBuilder builder = pregnancyModule.createCaseBuilder(healthId);

        for (MRSObservation<?> obs : encounter.getObservations()) {
            builder.addUpdateElement(obs.getConceptName(), obs.getValue().toString());
        }
        
        String providerName = encounter.getProvider().getFullName();
        
        builder.setProviderName(providerName);

        pregnancyModule.createCase(builder.build());
    }
}
