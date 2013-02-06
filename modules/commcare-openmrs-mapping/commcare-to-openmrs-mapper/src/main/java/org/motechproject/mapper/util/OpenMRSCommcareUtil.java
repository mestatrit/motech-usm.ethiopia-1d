package org.motechproject.mapper.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.User;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.OpenMRSEncounter.MRSEncounterBuilder;
import org.motechproject.mrs.model.OpenMRSObservation;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.UserAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSCommcareUtil {

    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    @Autowired
    private EncounterAdapter mrsEncounterAdapter;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private FacilityAdapter mrsFacilityAdapter;

    @Autowired
    private UserAdapter mrsUserAdapter;

    @Autowired
    private PatientAdapter mrsPatientAdapter;

//    @PostConstruct
//    public void test() {
//        User user = mrsUserAdapter.getUserByUserName("Unknown");
//    }

    public Person findProvider(String providerName) {

        User provider = mrsUserAdapter.getUserByUserName(providerName);

        if (provider == null) {
            return null;
        }

        return provider.getPerson();
    }

    public Facility findFacility(String location) {
        List<? extends Facility> facilities = null;
        try {
            facilities = mrsFacilityAdapter.getFacilities(location);
        } catch (MRSException e) {
            return null;
        }
        if (facilities.size() == 0) {
            return null;
        } else if (facilities.size() > 1) {
            logger.info("Multiple facilities, returning facility with ID: " + facilities.get(0).getFacilityId());
        }

        return facilities.get(0);
    }

    public String getCaseId(FormValueElement formValueElement, String openMrsPatientIdentifier) {

        String caseId = formValueElement.getAttributes().get(FormMappingConstants.CASE_ID_ATTRIBUTE);

        CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);

        return caseInfo.getFieldValues().get(openMrsPatientIdentifier);
    }

    public void addEncounter(Patient patient, Set<OpenMRSObservation> observations, String providerName,
            Date encounterDate, String facilityName, String encounterType) {

        Facility facility = findFacility(facilityName);

        Person providerPerson = findProvider(providerName);
        OpenMRSProvider provider = new OpenMRSProvider();
        provider.setPerson(providerPerson);
        provider.setProviderId(providerPerson.getPersonId());


        logger.info("Using provider: " + provider);

        MRSEncounterBuilder builder = new MRSEncounterBuilder();

        Encounter mrsEncounter = builder.withProvider(provider).withFacility(facility).withDate(encounterDate).withPatient((OpenMRSPatient) patient).withEncounterType(encounterType).withObservations(observations).build();

        try {
            mrsEncounterAdapter.createEncounter(mrsEncounter);
            logger.info("Encounter saved");
        } catch (MRSException e) {
            logger.warn("Could not save encounter");
        }
    }

    public Patient getPatientByMotechId(String motechId) {
        return mrsPatientAdapter.getPatientByMotechId(motechId);
    }

    public String retrieveId(Map<String, String> idScheme, FormValueElement element) {
        String idSchemeType = idScheme.get(FormMappingConstants.ID_SCHEME_TYPE);
        String idFieldName = idScheme.get(FormMappingConstants.ID_SCHEME_FIELD);

        String motechId = null;

        if (idSchemeType.equals(FormMappingConstants.DEFAULT_ID_SCHEME)) {
            motechId = element.getElementByName(idFieldName).getValue();
        } else if (idSchemeType.equals(FormMappingConstants.COMMCARE_ID_SCHEME)) {
            motechId = getCaseId(element.getElementByNameIncludeCase(FormMappingConstants.CASE_ELEMENT), idFieldName);
        }

        return motechId;
    }

    public String getFacility(CommcareForm form) {
        return "Unknown Location";
    }

    //    public String getFacility(CommcareForm form) {
    //        String facilityName = null;
    //        String userId = form.getMetadata().get("userID");
    //        logger.info("User id is: " + userId);
    //        CommcareProviderIdentifier commcareId = new CommcareProviderIdentifier();
    //        commcareId.setUserId(userId);
    //        Provider provider = providerService.getProviderByIdentifier(commcareId);
    //        if (provider != null && provider.getLocationIdentities() != null) {
    //            List<String> locationIds = provider.getLocationIdentities();
    //            if (locationIds.size() > 0) {
    //                String locationId = locationIds.get(0);
    //                Location location = locationService.getLocationById(locationId);
    //                List<LocationIdentifier> identifiers = location.getIdentifiers();
    //                if (identifiers == null) {
    //                    return null;
    //                }
    //                for (LocationIdentifier locId : identifiers) {
    //                    if("openmrs_location_id".equals(locId.getIdentifierName())) {
    //                        OpenMRSLocationIdentifier openMrsLoc = (OpenMRSLocationIdentifier) locId;
    //                        facilityName = openMrsLoc.getFacilityName();
    //                        logger.info("Using : " + facilityName);
    //                        return facilityName;
    //                    }
    //                }
    //            } else {
    //                logger.warn("NO LOCATIONS FOUND FOR PROVIDER");
    //            }
    //        } else {
    //            logger.warn("NO PROVIDER FOUND");
    //        }
    //        return null;
    //    }
}