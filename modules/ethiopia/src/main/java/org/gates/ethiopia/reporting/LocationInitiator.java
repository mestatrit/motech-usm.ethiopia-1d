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
import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.service.CommcareCaseService;
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

    @Autowired
    private LocationRepositoryService locationService;

    @Autowired
    private LocationIdentifierService locationIdService;

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
    }

    private void outputLocations() {
        List<Location> regions = locationService.getAllLocationsByType("region");

        for (Location region : regions) {
            //Get facilities for each region
            List<Location> facilities = locationService.getAllChildrenByType(region.getMotechId(), FACILITY);
            logger.warn("Region: " + region.getCustomIdentifiers().get(0).getIdentifyingProperties().get(REGION) + " has " + facilities.size() + " facilities");
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
        addOrUpdateHealthFacilityLocation(parentWoreda, healthFacility);
    }

    private void addOrUpdateHealthFacilityLocation(Location parentWoreda, String facilityName) {
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
        } else {
            logger.warn("Health facility already exists");
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
