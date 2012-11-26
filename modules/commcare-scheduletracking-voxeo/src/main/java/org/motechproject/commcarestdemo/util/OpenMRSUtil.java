package org.motechproject.commcarestdemo.util;

import java.util.List;
import org.joda.time.DateTime;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSUtil {

    @Autowired
    private MRSEncounterAdapter mrsEncounterAdapter;

    public DateTime dateOfLastEncounter(String motechId, String encounterType, List<MRSObservation> requiredObservations) {

        List<MRSEncounter> encounters = mrsEncounterAdapter.getEncountersByEncounterType(motechId, encounterType);

        DateTime latestDate = null;

        if (encounters == null) {
            return null;
        }

        for (MRSEncounter encounter : encounters) {
            if (latestDate == null) {
                latestDate = new DateTime(encounter.getDate());
            } else {
                DateTime tempDate = new DateTime(encounter.getDate());
                if (tempDate.isAfter(latestDate)) {
                    latestDate = tempDate;
                }
            }
        }

        return latestDate;
    }
}
