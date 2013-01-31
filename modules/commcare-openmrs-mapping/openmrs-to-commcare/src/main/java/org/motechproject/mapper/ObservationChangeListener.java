package org.motechproject.mapper;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.services.ObservationAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listens on Observation update events from the Atom Feed and updates a case if the observation updated is in the case
 */
@Component
public class ObservationChangeListener {

    private final ObservationAdapter obsAdapter;
    private final PatientAdapter patientAdapter;
    private final CommcarePregnancyModule pregnancyApplication;
//    private static Logger logger = LoggerFactory.getLogger("openmrs-commcare-mapper");

    @Autowired
    public ObservationChangeListener(CommcarePregnancyModule pregnancyApplication, ObservationAdapter obsAdapter,
            PatientAdapter patientAdapter) {
        this.pregnancyApplication = pregnancyApplication;
        this.obsAdapter = obsAdapter;
        this.patientAdapter = patientAdapter;

    }

    @MotechListener(subjects = EventSubjects.OBSERVATION_CREATE)
    public void handleObservationUpdate(MotechEvent event) {

        String observationId = event.getParameters().get(EventDataKeys.UUID).toString();
        Observation<?> obs = obsAdapter.getObservationById(observationId);

        // do we care about this observation type
        CommcareMapping match = pregnancyApplication.getCaseMapping(obs.getConceptName());
        if (match == null) {
            return;
        }

        // get patient to retrieve motech id
        String patientId = obs.getPatientId();

        if (patientId != null) {
            Patient patient = patientAdapter.getPatient(patientId);

            pregnancyApplication.updateCase(match, obs.getValue().toString(), patient.getMotechId(), obs.getConceptName());
        }
    }
}
