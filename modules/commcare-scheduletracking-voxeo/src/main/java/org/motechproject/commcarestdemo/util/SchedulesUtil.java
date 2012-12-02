package org.motechproject.commcarestdemo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.motech.location.repository.domain.CommcareLocationIdentifier;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifier;
import org.motech.location.repository.domain.MotechLocationIdentifier;
import org.motech.location.repository.domain.OpenMRSLocationIdentifier;
import org.motech.location.repository.service.LocationRepositoryService;
import org.motech.provider.repository.domain.CommcareProviderIdentifier;
import org.motech.provider.repository.domain.MotechIdentifier;
import org.motech.provider.repository.domain.OpenMRSProviderIdentifier;
import org.motech.provider.repository.domain.Provider;
import org.motech.provider.repository.domain.ProviderIdentifier;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulesUtil {

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private LocationRepositoryService locationService;

    @Autowired
    private ProviderRepositoryService providerService;

    private Logger logger = LoggerFactory.getLogger("gates-ethiopia");

    @PostConstruct
    public void init() {

        CommcareProviderIdentifier providerId2 = new CommcareProviderIdentifier();
        OpenMRSProviderIdentifier providerId1 = new OpenMRSProviderIdentifier();
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

            scheduleTrackingService.updateSchedule(writer.toString());

            Location location = new Location();

            MotechLocationIdentifier locationId = new MotechLocationIdentifier();
            locationId.setExternalId("motechlocation123");

            location.setMotechId(locationId);

            List<LocationIdentifier> locationIdentifiers = new ArrayList<LocationIdentifier>();

            OpenMRSLocationIdentifier locationIdentifier = new OpenMRSLocationIdentifier();
            locationIdentifier.setFacilityName("USM");
            locationIdentifier.setUuid("12345");

            locationIdentifiers.add(locationIdentifier);

            CommcareLocationIdentifier commcareIdentifier = new CommcareLocationIdentifier();
            commcareIdentifier.setDomain("mvp-sauri-testing");
            commcareIdentifier.setLocationFieldValue("location");
            commcareIdentifier.setLocationid("12345");

            locationIdentifiers.add(commcareIdentifier);

            location.setIdentifiers(locationIdentifiers);


            Provider provider = new Provider();

            MotechIdentifier motechId = new MotechIdentifier();
            motechId.setExternalId("testExternalId");

            provider.setMotechId(motechId);

            List<ProviderIdentifier> providerIdentifiers = new ArrayList<ProviderIdentifier>();

            providerId1.setUserName("openMrsUsername");
            providerId1.setUuid("openMrsUUID");

            providerId2.setDomain("mvp-sauri-testing");
            providerId2.setUserId("1234567");
            providerId2.setUsername("demouser");

            providerIdentifiers.add(providerId1);
            providerIdentifiers.add(providerId2);

            provider.setIdentifiers(providerIdentifiers);

            List<String> locationIds = new ArrayList<String>();
            locationIds.add("location1");
            locationIds.add("location2");
            locationIds.add("locationtest");

            provider.setLocationIdentities(locationIds);

            logger.warn("Locations: " + locationIds.size());

            //test services
            locationService.saveLocation(location);

            providerService.saveProvider(provider);
        } catch (Exception e) {
            logger.error("Caught first exception: " + e.getMessage());
        }

        logger.warn("got here");
        try {
            Provider providerReturned = providerService.getProviderByIdentifier(providerId2);

            if (providerReturned == null) {
                logger.warn("No Provider");
            } else {
                logger.warn("Got provider: " + providerReturned.getMotechId().getExternalId());
                logger.warn("Identifier size: " + providerReturned.getIdentifiers().size());

                List<ProviderIdentifier> identifiers = providerReturned.getEquivalentIdentifierByType("openmrs_provider_id", providerId2);
                OpenMRSProviderIdentifier identifier = (OpenMRSProviderIdentifier) identifiers.get(0);

                logger.warn("Identifier: " + identifier.getIdentity() + " " + identifier.getIdentifierName());
            } 
        } catch (Exception e) {
            logger.error("Caught exception: " + e.getMessage());
        }
        try {
            Provider providerReturned = providerService.getProviderByIdentifier(providerId1);

            if (providerReturned == null) {
                logger.warn("No Provider");
            } else {
                logger.warn("Got provider 2: " + providerReturned.getMotechId().getExternalId());
                logger.warn("Identifier size: " + providerReturned.getIdentifiers().size());
            } 
        } catch (Exception e) {
            logger.error("Caught exception: " + e.getMessage());
        }
        try {
            CommcareProviderIdentifier commcare = new CommcareProviderIdentifier();
            commcare.setUserId("blahblah");
            Provider providerReturned = providerService.getProviderByIdentifier(commcare);

            if (providerReturned == null) {
                logger.warn("No Provider");
            } else {
                logger.warn("Got provider 2: " + providerReturned.getMotechId().getExternalId());
                logger.warn("Identifier size: " + providerReturned.getIdentifiers().size());
            } 
        } catch (Exception e) {
            logger.error("Caught exception: " + e.getMessage());
        }       

        List<Provider> providers = providerService.getProvidersByLocationId("location1");
        logger.warn("Number of providers for location 1: " + providers.size());
        List<Provider> providers2 = providerService.getProvidersByLocationId("location3");
        logger.warn("Number of providers for location 3: " + providers2.size());
        
        Location location = locationService.getLocationById("12345");
        logger.warn("Location: " + location.getMotechId().getExternalId());

    }

}
