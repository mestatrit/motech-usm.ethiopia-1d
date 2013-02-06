package org.gates.ethiopia.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.motech.location.repository.domain.CustomLocationIdentifier;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifierType;
import org.motech.location.repository.domain.LocationValidationException;
import org.motech.location.repository.service.LocationIdentifierService;
import org.motech.location.repository.service.LocationRepositoryService;
import org.motech.provider.repository.domain.CustomProviderIdentifier;
import org.motech.provider.repository.domain.ProviderIdBroker;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.couch.mrs.model.CouchAttribute;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationInitiator {

    public static final String REGION = "region";

    public static final String WOREDA = "woreda";

    public static final String FACILITY_NAME = "facility_name";

    public static final String FACILITY = "facility";

    public static final String HEW_NAME = "hew_name";

    public static final String HEW_MOBILE_NUMBER = "hew_mobile_number";

    public static final String COMMCARE_HEW = "HEW";

    private static final String COMMCARE_CASE = "COMMCARE_CASE";

    private static final String CASE_ID = "case_id";

    @Autowired
    private LocationRepositoryService locationService;

    @Autowired
    private LocationIdentifierService locationIdService;

    @Autowired
    private ProviderRepositoryService providerService;

    @Autowired
    private CommcareFormService formService;

    @Autowired
    private CommcareCaseService caseService;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @PostConstruct
    public void initialize() {
        logger.warn("initializing...");

        initiateIdentifierTypes();

        List<CaseInfo> hewCases = caseService.getAllCasesByType("hew");

        logger.warn("# of hew cases: " + hewCases.size());

        if (hewCases != null) {
            for (CaseInfo hewCase : hewCases) {
                addOrUpdateLocation(hewCase);
            }
        }

        outputLocations();

        checkNumberOfForms();
    }

    private void checkNumberOfForms() {
        List<Location> regions = locationService.getAllLocationsByType("region");

        for (Location region : regions) {
            //Get facilities for each region
            List<Location> facilities = locationService.getAllChildrenByType(region.getMotechId(), FACILITY);
            for (Location facility : facilities) {
                List<ProviderIdBroker> providers = providerService.getProvidersByLocationId(facility.getMotechId());
                for (ProviderIdBroker provider : providers) {
                    logger.warn("HEW for facility " + facility.getCustomIdentifiers().get(0).getIdentifyingProperties().get(FACILITY_NAME) + " : " + provider.getMrsProvider().getPerson().getPreferredName());
                    String caseId = provider.getIdentifiers().get(1).getIdentifyingProperties().get(CASE_ID);
                    CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);
                    checkForms(caseInfo.getXformIds());
                }
            }
        }
    }

    private void checkForms(List<String> xformIds) {
        int formsSubmitted = 0;
        for (String formId : xformIds) {
            CommcareForm form = formService.retrieveForm(formId);
            if (form != null) {
                String xmlns = form.getForm().getAttributes().get("xmlns");
                logger.warn("xmlns: " + xmlns);
                if ("http://openrosa.org/formdesigner/EA962226-BAB0-4F00-8BA3-FCCD821E7E97".equals(xmlns)) {
                    formsSubmitted++;
                    logger.warn("Submitted a form");
                } else {
                    logger.warn("other kind of form");
                }

            } else {
                logger.warn("Null form");
            }
        }
        logger.warn("# of forms submitted: " + formsSubmitted);
    }

    private void outputLocations() {
        List<Location> regions = locationService.getAllLocationsByType("region");

        for (Location region : regions) {
            //Get facilities for each region
            List<Location> facilities = locationService.getAllChildrenByType(region.getMotechId(), FACILITY);
            logger.warn("Region: " + region.getCustomIdentifiers().get(0).getIdentifyingProperties().get(REGION) + " has " + facilities.size() + " facilities");
            for (Location facility : facilities) {
                logger.warn("Facility: " + facility.getCustomIdentifiers().get(0).getIdentifyingProperties().get(FACILITY_NAME));
            }
        }
    }

    private void initiateIdentifierTypes() {
        LocationIdentifierType regionId = locationIdService.getIdentifierTypeByName(REGION);
        if (regionId == null) {
            logger.warn("Registering new id type");
            regionId = new LocationIdentifierType();
            regionId.setIdentifierName(REGION);
            locationIdService.addIdentifierType(regionId);
        } else {
            logger.warn("Id type already exists.");
        }
        LocationIdentifierType woredaId = locationIdService.getIdentifierTypeByName(WOREDA);
        if (woredaId == null) {
            logger.warn("Registering new id type");
            woredaId = new LocationIdentifierType();
            woredaId.setIdentifierName(WOREDA);
            locationIdService.addIdentifierType(woredaId);
        } else {
            logger.warn("Id type already exists.");
        }
        LocationIdentifierType facilityId = locationIdService.getIdentifierTypeByName(FACILITY_NAME);
        if (facilityId == null) {
            logger.warn("Registering new id type");
            facilityId = new LocationIdentifierType();
            facilityId.setIdentifierName(FACILITY_NAME);
            locationIdService.addIdentifierType(facilityId);
        } else {
            logger.warn("Id type already exists.");
        }
    }

    private void addOrUpdateLocation(CaseInfo hewCase) {
        String region = hewCase.getFieldValues().get(REGION);
        String woreda = hewCase.getFieldValues().get(WOREDA);
        String healthFacility = hewCase.getFieldValues().get(FACILITY_NAME);

        Location parentRegion = addOrUpdateRegionLocation(region);
        Location parentWoreda = addOrUpdateWoredaLocation(parentRegion, woreda);
        Location facility = addOrUpdateHealthFacilityLocation(parentWoreda, healthFacility);

        addOrUpdateHew(facility, hewCase);
    }

    private void addOrUpdateHew(Location facility, CaseInfo hewCase) {
        String preferredName = hewCase.getFieldValues().get(HEW_NAME);
        String mobileNumber = hewCase.getFieldValues().get(HEW_MOBILE_NUMBER);
        String caseId = hewCase.getCaseId();

        List<ProviderIdBroker> providers = providerService.getProvidersByPropertyAndValue(HEW_NAME, preferredName);

        if (providers == null || providers.size() > 0) {
            logger.warn("Provider already exists");
            //no updates for the time being
            return;
        }

        logger.warn("Registering new provider...");

        ProviderIdBroker providerBroker = new ProviderIdBroker();
        String motechId = UUID.randomUUID().toString();
        providerBroker.setMotechId(motechId);

        Person person = new CouchPerson();
        person.setPersonId(motechId);


        person.setPreferredName(preferredName);

        Attribute couchAttribute = new CouchAttribute();
        couchAttribute.setName(HEW_MOBILE_NUMBER);
        couchAttribute.setValue(mobileNumber);
        person.getAttributes().add(couchAttribute);

        Provider provider = new CouchProvider(motechId, person);

        providerBroker.setMrsProvider(provider);

        List<String> locationIdentities = new ArrayList<String>();
        locationIdentities.add(facility.getMotechId());

        providerBroker.setLocationIdentities(locationIdentities);

        List<CustomProviderIdentifier> identifiers = new ArrayList<CustomProviderIdentifier>();

        CustomProviderIdentifier identifier = new CustomProviderIdentifier();
        identifier.setIdentifierType(COMMCARE_HEW);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(HEW_NAME, preferredName);

        identifier.setIdentifyingProperties(properties);

        identifiers.add(identifier);

        CustomProviderIdentifier caseIdentifier = new CustomProviderIdentifier();
        caseIdentifier.setIdentifierType(COMMCARE_CASE);

        Map<String, String> caseProperties = new HashMap<String, String>();
        caseProperties.put(CASE_ID, caseId);

        caseIdentifier.setIdentifyingProperties(caseProperties);
        identifiers.add(caseIdentifier);

        providerBroker.setIdentifiers(identifiers);

        providerService.saveProvider(providerBroker);

    }

    private Location addOrUpdateHealthFacilityLocation(Location parentWoreda, String facilityName) {
        List<Location> locations = locationService.getLocationsByPropertyValue(FACILITY_NAME, facilityName);

        if (locations == null || locations.size() == 0) {
            logger.warn("Registering new health facility");
            Location facilityLocation = new Location();
            facilityLocation.setMotechId(UUID.randomUUID().toString());
            facilityLocation.setLocationType(FACILITY);
            List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
            CustomLocationIdentifier facilityId = new CustomLocationIdentifier();
            facilityId.setIdentifierType(FACILITY_NAME);
            Map<String, String> idProperties = new HashMap<String, String>();
            idProperties.put(FACILITY_NAME, facilityName);
            facilityId.setIdentifyingProperties(idProperties);
            customIdentifiers.add(facilityId);
            facilityLocation.setCustomIdentifiers(customIdentifiers);
            try {
                locationService.addChildLocation(parentWoreda, facilityLocation);
            } catch (LocationValidationException e) {
                logger.warn("Unable to save health facility location due to: " + e.getMessage());
            }
            return facilityLocation;
        } else {
            logger.warn("Health facility already exists");
            return locations.get(0);
        }
    }

    private Location addOrUpdateWoredaLocation(Location parentRegion, String woreda) {
        List<Location> locations = locationService.getLocationsByPropertyValue(WOREDA, woreda);

        if (locations == null || locations.size() == 0) {
            logger.warn("Registering new Woreda");
            Location woredaLocation = new Location();
            woredaLocation.setMotechId(UUID.randomUUID().toString());
            woredaLocation.setLocationType(WOREDA);
            List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
            CustomLocationIdentifier woredaId = new CustomLocationIdentifier();
            woredaId.setIdentifierType(WOREDA);
            Map<String, String> idProperties = new HashMap<String, String>();
            idProperties.put(WOREDA, woreda);
            woredaId.setIdentifyingProperties(idProperties);
            customIdentifiers.add(woredaId);
            woredaLocation.setCustomIdentifiers(customIdentifiers);
            try {
                locationService.addChildLocation(parentRegion, woredaLocation);
            } catch (LocationValidationException e) {
                logger.warn("Unable to save Woreda location due to: " + e.getMessage());
            }
            return woredaLocation;
        } else {
            logger.warn("Woreda already exists");
            return locations.get(0);
        }
    }

    private Location addOrUpdateRegionLocation(String region) {
        List<Location> locations = locationService.getLocationsByPropertyValue(REGION, region);

        if (locations == null || locations.size() == 0) {
            logger.warn("Registering new region");
            Location regionLocation = new Location();
            regionLocation.setMotechId(UUID.randomUUID().toString());
            regionLocation.setLocationType(REGION);
            List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
            CustomLocationIdentifier regionId = new CustomLocationIdentifier();
            regionId.setIdentifierType(REGION);
            Map<String, String> idProperties = new HashMap<String, String>();
            idProperties.put(REGION, region);
            regionId.setIdentifyingProperties(idProperties);
            customIdentifiers.add(regionId);
            regionLocation.setCustomIdentifiers(customIdentifiers);
            try {
                locationService.addChildLocation(null, regionLocation);
            } catch (LocationValidationException e) {
                logger.warn("Unable to save region location due to: " + e.getMessage());
            }
            return regionLocation;
        } else {
            logger.warn("Region already exists");
            return locations.get(0);
        }

    }
}
