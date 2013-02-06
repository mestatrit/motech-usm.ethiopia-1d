package org.gates.ethiopia.reporting;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class LocationBroker {

    @PostConstruct
    public void init() {
//        List<Location> locations = locationService.getAllChildren("level1");
//        logger.warn("locations size: " + locations.size());
//
//        List<Location> locations2 = locationService.getAllChildren("level2");
//        logger.warn("locations2 size: " + locations2.size());
//        List<Location> locations3 = locationService.getAllChildren("level3");
//        logger.warn("locations3 size: " + locations3.size());
//
//        Location location = new Location();
//        location.setMotechId(UUID.randomUUID().toString());
//        List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();
//
//        Map<String, String> idProperties = new HashMap<String, String>();
//        idProperties.put("id1", "idhere");
//        idProperties.put("id2", "idhere2");
//        CustomLocationIdentifier identifier = new CustomLocationIdentifier("type1", idProperties);
//
//        
//        customIdentifiers.add(identifier);
////        location.setCustomIdentifiers(customIdentifiers);
//
////        List<String> path = new ArrayList<String>();
//
//        try {
//            locationService.addChildLocation(locations3.get(0), location);
//        } catch (LocationValidationException e) {
//            logger.warn("Caught exception" + e.getMessage());
//        }
    }
}
