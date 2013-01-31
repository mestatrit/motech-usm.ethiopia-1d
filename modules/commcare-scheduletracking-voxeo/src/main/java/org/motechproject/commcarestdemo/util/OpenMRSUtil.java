package org.motechproject.commcarestdemo.util;

import java.util.List;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.services.EncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSUtil {

    @Autowired
    private EncounterAdapter mrsEncounterAdapter;

    public DateTime dateOfLastEncounter(String motechId, String encounterType, List<Observation> requiredObservations) {

        List<Encounter> encounters = mrsEncounterAdapter.getEncountersByEncounterType(motechId, encounterType);

        DateTime latestDate = null;

        if (encounters == null) {
            return null;
        }

        for (Encounter encounter : encounters) {
            if (latestDate == null) {
                latestDate = new DateTime(encounter.getDate());
            } else {
                DateTime tempDate = new DateTime(encounter.getDate());
                if (tempDate.isAfter(latestDate)) {
                    latestDate = tempDate;
                }
            }
        }

        if (latestDate != null) {
            latestDate = latestDate.plusHours(8).plusMinutes(5);
        }
        return latestDate;
    }
}
