package org.motechproject.mapper;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listens on Observation update events from the Atom Feed and updates a case if the observation updated is in the case
 */
@Component
public class ObservationChangeListener {
    private final MRSObservationAdapter obsAdapter;
    private final MRSPatientAdapter patientAdapter;
    private final CommcarePregnancyModule pregnancyApplication;

    @Autowired
    public ObservationChangeListener(CommcarePregnancyModule pregnancyApplication, MRSObservationAdapter obsAdapter,
            MRSPatientAdapter patientAdapter) {
        this.pregnancyApplication = pregnancyApplication;
        this.obsAdapter = obsAdapter;
        this.patientAdapter = patientAdapter;

    }

    @MotechListener(subjects = EventSubjects.OBSERVATION_UPDATE)
    public void handleObservationUpdate(MotechEvent event) {
        String observationId = event.getParameters().get(EventDataKeys.UUID).toString();
        MRSObservation<?> obs = obsAdapter.getObservationById(observationId);

        // do we care about this observation type
        CommcareMapping match = pregnancyApplication.getCaseMapping(obs.getConceptName());
        if (match == null) {
            return;
        }

        // get patient to retrieve motech id
        String patientId = obs.getPatientId();
        MRSPatient patient = patientAdapter.getPatient(patientId);

        pregnancyApplication.updateCase(match, obs.getValue().toString(), patient.getMotechId());
    }
}
