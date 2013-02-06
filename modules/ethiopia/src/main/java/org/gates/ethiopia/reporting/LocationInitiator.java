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

    }

    private void addOrUpdateLocation(CaseInfo hewCase) {
        String region = hewCase.getFieldValues().get(REGION);
        //        String woreda = hewCase.getFieldValues().get("woreda");
        //        String healthFacility = hewCase.getFieldValues().get("facility_name");

        addOrUpdateRegionLocation(region);
        //        addOrUpdateWoredaLocation(woreda);
        //        addOrUpdateHealthFacilityLocation(healthFacility);
    }

    //    private void addOrUpdateHealthFacilityLocation(String healthFacility) {
    //        // TODO Auto-generated method stub
    //
    //    }
    //
    //    private void addOrUpdateWoredaLocation(String woreda) {
    //        // TODO Auto-generated method stub
    //
    //    }

    private void addOrUpdateRegionLocation(String region) {
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
        } else {
            logger.warn("Region already exists");
        }

    }
}
