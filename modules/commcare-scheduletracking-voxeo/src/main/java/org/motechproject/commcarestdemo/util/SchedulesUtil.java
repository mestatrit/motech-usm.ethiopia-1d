package org.motechproject.commcarestdemo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.motech.location.repository.domain.CustomLocationIdentifier;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationValidationException;
import org.motech.location.repository.domain.MotechLocationIdentifier;
//import org.motech.location.repository.domain.MotechLocationIdentifier;
import org.motech.location.repository.service.LocationIdentifierService;
import org.motech.location.repository.service.LocationRepositoryService;
import org.motech.provider.repository.domain.CommcareProviderIdentifier;
import org.motech.provider.repository.domain.MotechIdentifier;
import org.motech.provider.repository.domain.OpenMRSProviderIdentifier;
import org.motech.provider.repository.domain.Provider;
import org.motech.provider.repository.domain.ProviderIdentifier;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.motechproject.mrs.exception.UserAlreadyExistsException;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPerson;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.mrs.services.MRSFacilityAdapter;
//import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulesUtil {

    @Autowired
    private LocationRepositoryService locationService;

    @Autowired
    private ProviderRepositoryService providerService;

    @Autowired
    private MRSFacilityAdapter facilityAdapter;

    @Autowired
    private MRSUserAdapter userAdapter;

    @Autowired
    private LocationIdentifierService identifierService;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @PostConstruct
    public void init() {

        //        CommcareProviderIdentifier providerId2 = new CommcareProviderIdentifier();
        //        OpenMRSProviderIdentifier providerId1 = new OpenMRSProviderIdentifier();
        //scheduleTrackingService.getScheduleByName(DemoConstants.SCHEDULE_NAME);
        try {

            InputStream is = getClass().getClassLoader().getResourceAsStream(DemoConstants.SCHEDULE_FILE_NAME);

            StringWriter writer = new StringWriter();

            try {
                IOUtils.copy(is, writer, "UTF-8");
            } catch (IOException e) {
                logger.warn("Unable to read schedule: " + e.getMessage());
            }

            logger.warn("Adding schedule... ");


            //scheduleTrackingService.updateSchedule(writer.toString());

            bootstrapProvidersAndLocations("123", "96b15408c06fe7b6efa654f0a9ad84a6", "Bruce");       

            //            Location location = new Location();
            //
            //            MotechLocationIdentifier locationId = new MotechLocationIdentifier();
            //            locationId.setExternalId("motechlocation123");
            //
            //            location.setMotechId(locationId);
            //
            //            List<LocationIdentifier> locationIdentifiers = new ArrayList<LocationIdentifier>();
            //
            //            OpenMRSLocationIdentifier locationIdentifier = new OpenMRSLocationIdentifier();
            //            locationIdentifier.setFacilityName("USM");
            //            locationIdentifier.setUuid("12345");
            //
            //            locationIdentifiers.add(locationIdentifier);
            //
            //            CommcareLocationIdentifier commcareIdentifier = new CommcareLocationIdentifier();
            //            commcareIdentifier.setDomain("mvp-sauri-testing");
            //            commcareIdentifier.setLocationFieldValue("location");
            //            commcareIdentifier.setLocationid("12345");
            //
            //            locationIdentifiers.add(commcareIdentifier);
            //
            //            location.setIdentifiers(locationIdentifiers);
            //
            //
            //            Provider provider = new Provider();
            //
            //            MotechIdentifier motechId = new MotechIdentifier();
            //            motechId.setExternalId("testExternalId");
            //
            //            provider.setMotechId(motechId);
            //
            //            List<ProviderIdentifier> providerIdentifiers = new ArrayList<ProviderIdentifier>();
            //
            //            providerId1.setUserName("openMrsUsername");
            //            providerId1.setUuid("openMrsUUID");
            //
            //            providerId2.setDomain("mvp-sauri-testing");
            //            providerId2.setUserId("1234567");
            //            providerId2.setUsername("demouser");
            //
            //            providerIdentifiers.add(providerId1);
            //            providerIdentifiers.add(providerId2);
            //
            //            provider.setIdentifiers(providerIdentifiers);
            //
            //            List<String> locationIds = new ArrayList<String>();
            //            locationIds.add("location1");
            //            locationIds.add("location2");
            //            locationIds.add("locationtest");
            //
            //            provider.setLocationIdentities(locationIds);
            //
            //            logger.warn("Locations: " + locationIds.size());
            //
            //            //test services
            //            locationService.saveLocation(location);
            //
            //            providerService.saveProvider(provider);
            //        } catch (Exception e) {
            //            logger.error("Caught first exception: " + e.getMessage());
            //        }
            //
            //        logger.warn("got here");
            //        try {
            //            Provider providerReturned = providerService.getProviderByIdentifier(providerId2);
            //
            //            if (providerReturned == null) {
            //                logger.warn("No Provider");
            //            } else {
            //                logger.warn("Got provider: " + providerReturned.getMotechId().getExternalId());
            //                logger.warn("Identifier size: " + providerReturned.getIdentifiers().size());
            //
            //                List<ProviderIdentifier> identifiers = providerReturned.getEquivalentIdentifierByType("openmrs_provider_id", providerId2);
            //                OpenMRSProviderIdentifier identifier = (OpenMRSProviderIdentifier) identifiers.get(0);
            //
            //                logger.warn("Identifier: " + identifier.getIdentity() + " " + identifier.getIdentifierName());
            //            } 
            //        } catch (Exception e) {
            //            logger.error("Caught exception: " + e.getMessage());
            //        }
            //        try {
            //            Provider providerReturned = providerService.getProviderByIdentifier(providerId1);
            //
            //            if (providerReturned == null) {
            //                logger.warn("No Provider");
            //            } else {
            //                logger.warn("Got provider 2: " + providerReturned.getMotechId().getExternalId());
            //                logger.warn("Identifier size: " + providerReturned.getIdentifiers().size());
            //            } 
            //        } catch (Exception e) {
            //            logger.error("Caught exception: " + e.getMessage());
            //        }
            //        try {
            //            CommcareProviderIdentifier commcare = new CommcareProviderIdentifier();
            //            commcare.setUserId("blahblah");
            //            Provider providerReturned = providerService.getProviderByIdentifier(commcare);
            //
            //            if (providerReturned == null) {
            //                logger.warn("No Provider");
            //            } else {
            //                logger.warn("Got provider 2: " + providerReturned.getMotechId().getExternalId());
            //                logger.warn("Identifier size: " + providerReturned.getIdentifiers().size());
            //            } 
        } catch (Exception e) {
            logger.error("Caught exception: " + e.getMessage());
        }       

        //        List<Provider> providers = providerService.getProvidersByLocationId("location1");
        //        logger.warn("Number of providers for location 1: " + providers.size());
        //        List<Provider> providers2 = providerService.getProvidersByLocationId("location3");
        //        logger.warn("Number of providers for location 3: " + providers2.size());
        //        
        //        Location location = locationService.getLocationById("12345");
        //        logger.warn("Location: " + location.getMotechId().getExternalId());

    }

    public void bootstrapProvidersAndLocations(String motechString, String commcareUserId, String userName) throws UserAlreadyExistsException {
        MotechIdentifier motechId = new MotechIdentifier();
        motechId.setExternalId(motechString);

        Provider demoProvider1 = providerService.getProviderByMotechId(motechId);

        saveLocation("id1", "facilityname!");

        if (demoProvider1 != null) {
            Provider provider = new Provider();
            provider.setMotechId(motechId);

            List<String> locationIdentities = new ArrayList<String>();
            List<ProviderIdentifier> providerIds = new ArrayList<ProviderIdentifier>();

            MRSFacility facility = null;

            //Make sure USM facility is in both OpenMRS and Motech repository
            List<MRSFacility> facilities = facilityAdapter.getFacilities("USM");
            if (facilities.size() == 0) {
                facility = new MRSFacility("USM", null, null, null, null);
                facility = facilityAdapter.saveFacility(facility);
            } else {
                facility = facilities.get(0);
            }

            //String facilityName = facility.getName();
            String locationId = facility.getId();

            logger.warn("location id: " + locationId);

            locationIdentities.add(locationId);

            if (locationService.getLocationById(locationId) == null) {
                //saveLocation(locationId, facilityName);
            }

            if (userAdapter.getUserByUserName(userName) == null) {

                MRSUser user = new MRSUser();
                user.userName(userName);
                user.securityRole("Provider");

                MRSPerson person = new MRSPerson().address("USM Fantasy Land").dateOfBirth(new Date()).gender("M").firstName("Bruce").lastName("MacLeod");

                user.person(person);

                Map<String, Object> dataMap = userAdapter.saveUser(user);
                MRSUser userObj = (MRSUser) dataMap.get("User");
                MRSPerson personObj = userObj.getPerson();

                OpenMRSProviderIdentifier openMrsProvider = new OpenMRSProviderIdentifier();
                openMrsProvider.setUserName(userName);
                openMrsProvider.setUuid(personObj.getId());
                providerIds.add(openMrsProvider);
            } else {
                OpenMRSProviderIdentifier openMrsProvider = new OpenMRSProviderIdentifier();
                openMrsProvider.setUserName(userName);
                openMrsProvider.setUuid(userAdapter.getUserByUserName(userName).getId());
                providerIds.add(openMrsProvider);
            }

            CommcareProviderIdentifier commcareId = new CommcareProviderIdentifier();
            commcareId.setDomain("mvp-sauri-testing");
            commcareId.setUsername(userName);
            commcareId.setUserId(commcareUserId);
            providerIds.add(commcareId);

            provider.setLocationIdentities(locationIdentities);
            provider.setIdentifiers(providerIds);
            providerService.saveProvider(provider);
        }
    }

    private void saveLocation(String locationId, String facilityName) {
        Location location = new Location();
        Map<String, String> identifyingProperties = new HashMap<String, String>();
        identifyingProperties.put("one", facilityName);
        identifyingProperties.put("uuid", locationId);
        identifyingProperties.put("uniqueId", "280af894-59d6-4d7c-9adb-769f54ab55d3");
        CustomLocationIdentifier custom = new CustomLocationIdentifier("blah3", identifyingProperties);
        List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
        customIdentifiers.add(custom);
        location.setCustomIdentifiers(customIdentifiers);
        //locationService.saveLocation(location);


        String identifier = "{\"identifierName\":\"blah3\",\"identifyingProperties\":{\"one\":\"true\",\"two\":\"false\"},\"identifyingComponents\":[\"one\",\"two\",\"three\"]}";

        identifierService.addIdentifierTypeJson(identifier);

        logger.warn("Before");
        logger.warn("Size: " + identifierService.getAllIdentifierTypes().size());

        try {
            locationService.saveLocationValidated(location);
        } catch (LocationValidationException e) {
            // TODO Auto-generated catch block
            logger.warn("Exception: " + e.getMessage());
        }
        Location location2 = new Location();

        CustomLocationIdentifier custom2 = new CustomLocationIdentifier("blah3", null);
        List<CustomLocationIdentifier> customIdentifiers2 = new ArrayList<CustomLocationIdentifier>();
        customIdentifiers2.add(custom2);
        location2.setCustomIdentifiers(customIdentifiers2);

        try {
            locationService.saveLocationValidated(location2);
        } catch (LocationValidationException e) {
            // TODO Auto-generated catch block
            logger.warn("Exception: " + e.getMessage());
        }

        logger.warn("about to save graph");
        childTest();
    }

    private void childTest() {
        Location parent = new Location();
        parent.setMotechId(new MotechLocationIdentifier("parent"));

        Location firstChild = new Location();
        firstChild.setMotechId(new MotechLocationIdentifier("2ndtier"));

        List<Location> thirdTierChildren = new ArrayList<Location>();

        Location childOne = new Location();
        childOne.setMotechId(new MotechLocationIdentifier("childOne"));

        Location childTwo = new Location();
        childTwo.setMotechId(new MotechLocationIdentifier("childTwo"));

        Location childThree = new Location();
        childThree.setMotechId(new MotechLocationIdentifier("childThree"));
        
        thirdTierChildren.add(childOne);
        thirdTierChildren.add(childTwo);
        thirdTierChildren.add(childThree);
        
        
        locationService.saveLocation(parent);
        try {
            locationService.addChildLocation(parent,  firstChild);
            locationService.addChildLocations(firstChild, thirdTierChildren);
        } catch (LocationValidationException e) {
            logger.warn("Unable to save child locations");
        }
    }

}