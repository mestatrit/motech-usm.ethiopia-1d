package org.motechproject.mapper.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifier;
import org.motech.location.repository.domain.OpenMRSLocationIdentifier;
import org.motech.location.repository.service.LocationRepositoryService;
import org.motech.provider.repository.domain.CommcareProviderIdentifier;
import org.motech.provider.repository.domain.Provider;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSEncounter.MRSEncounterBuilder;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSCommcareUtil {

    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");
    
    @Autowired
    private LocationRepositoryService locationService;

    @Autowired
    private ProviderRepositoryService providerService;

    @Autowired
    private MRSEncounterAdapter mrsEncounterAdapter;

    @Autowired
    private CommcareCaseService caseService;

    @Autowired
    private MRSFacilityAdapter mrsFacilityAdapter;

    @Autowired
    private MRSUserAdapter mrsUserAdapter;

    @Autowired
    private MRSPatientAdapter mrsPatientAdapter;

    public MRSPerson findProvider(String providerName) {

        MRSUser provider = mrsUserAdapter.getUserByUserName(providerName);

        if (provider == null) {
            return null;
        }

        return provider.getPerson();
    }

    public MRSFacility findFacility(String location) {
        List<MRSFacility> facilities = null;
        try {
            facilities = mrsFacilityAdapter.getFacilities(location);
        } catch (MRSException e) {
            return null;
        }
        if (facilities.size() == 0) {
            return null;
        } else if (facilities.size() > 1) {
            logger.info("Multiple facilities, returning facility with ID: " + facilities.get(0).getId());
        }

        return facilities.get(0);
    }

    public String getCaseId(FormValueElement formValueElement, String openMrsPatientIdentifier) {

        String caseId = formValueElement.getAttributes().get(FormMappingConstants.CASE_ID_ATTRIBUTE);

        CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);

        return caseInfo.getFieldValues().get(openMrsPatientIdentifier);
    }

    public void addEncounter(MRSPatient patient, Set<MRSObservation> observations, String providerName,
            Date encounterDate, String facilityName, String encounterType) {

        MRSFacility facility = findFacility(facilityName);

        MRSPerson provider = findProvider(providerName);

        logger.info("Using provider: " + provider);

        MRSEncounterBuilder builder = new MRSEncounterBuilder();

        MRSEncounter mrsEncounter = builder.withProvider(provider).withFacility(facility).withDate(encounterDate).withPatient(patient).withEncounterType(encounterType).withObservations(observations).build();

        try {
            mrsEncounterAdapter.createEncounter(mrsEncounter);
            logger.info("Encounter saved");
        } catch (MRSException e) {
            logger.warn("Could not save encounter");
        }
    }

    public MRSPatient getPatientByMotechId(String motechId) {
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
        String facilityName = null;
        String userId = form.getMetadata().get("userID");
        logger.info("User id is: " + userId);
        CommcareProviderIdentifier commcareId = new CommcareProviderIdentifier();
        commcareId.setUserId(userId);
        Provider provider = providerService.getProviderByIdentifier(commcareId);
        if (provider != null && provider.getLocationIdentities() != null) {
            List<String> locationIds = provider.getLocationIdentities();
            if (locationIds.size() > 0) {
                String locationId = locationIds.get(0);
                Location location = locationService.getLocationById(locationId);
                List<LocationIdentifier> identifiers = location.getIdentifiers();
                if (identifiers == null) {
                    return null;
                }
                for (LocationIdentifier locId : identifiers) {
                    if("openmrs_location_id".equals(locId.getIdentifierName())) {
                        OpenMRSLocationIdentifier openMrsLoc = (OpenMRSLocationIdentifier) locId;
                        facilityName = openMrsLoc.getFacilityName();
                        logger.info("Using : " + facilityName);
                        return facilityName;
                    }
                }
            } else {
                logger.warn("NO LOCATIONS FOUND FOR PROVIDER");
            }
        } else {
            logger.warn("NO PROVIDER FOUND");
        }
        return null;
    }
}
