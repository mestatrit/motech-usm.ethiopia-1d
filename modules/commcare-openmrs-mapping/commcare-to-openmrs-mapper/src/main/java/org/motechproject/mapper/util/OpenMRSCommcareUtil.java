package org.motechproject.mapper.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mrs.domain.Encounter;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.User;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.EncounterDto;
import org.motechproject.mrs.model.ObservationDto;
import org.motechproject.mrs.model.PersonDto;
import org.motechproject.mrs.model.ProviderDto;
import org.motechproject.mrs.services.EncounterAdapter;
import org.motechproject.mrs.services.FacilityAdapter;
import org.motechproject.mrs.services.PatientAdapter;
import org.motechproject.mrs.services.UserAdapter;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMRSCommcareUtil {

    private static final String MAPPING_CONFIGURATION_FILE_NAME = "mappingConfiguration.properties";

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

    @Autowired
    private SettingsFacade settings;

    public Person findProvider(String providerName) {
        //CouchDB module does not have a user service yet
        String destination = settings.getProperties(MAPPING_CONFIGURATION_FILE_NAME).getProperty("destination");

        if (FormMappingConstants.DESTINATION_COUCHDB.equals(destination)) {
            return null;
        }

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

    public void addEncounter(Patient patient, Set<ObservationDto> observations, String providerName,
            Date encounterDate, String facilityName, String encounterType) {

        Facility facility = findFacility(facilityName);

        ProviderDto provider = new ProviderDto();

        Person providerPerson = findProvider(providerName);

        if (providerPerson == null) {
            providerPerson = new PersonDto();
            providerPerson.setPersonId("UnknownProvider");
        }

        provider.setPerson(providerPerson);
        provider.setProviderId(providerPerson.getPersonId());

        logger.info("Using provider: " + provider);

        Encounter mrsEncounter = new EncounterDto();
        mrsEncounter.setFacility(facility);
        mrsEncounter.setDate(new DateTime(encounterDate));
        mrsEncounter.setPatient(patient);
        mrsEncounter.setProvider(provider);
        mrsEncounter.setEncounterType(encounterType);
        mrsEncounter.setObservations(observations);
        mrsEncounter.setEncounterId(UUID.randomUUID().toString());

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
            motechId = getCaseId(element.getElementByName(FormMappingConstants.CASE_ELEMENT), idFieldName);
        }

        return motechId;
    }
}
