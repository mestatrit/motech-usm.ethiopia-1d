package org.gates.ethiopia.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.motech.location.repository.domain.CustomLocationIdentifier;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.service.LocationRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationBroker {

    @Autowired
    private LocationRepositoryService locationService;

    @PostConstruct
    public void init() {
        Location location = new Location();
        location.setMotechId("MotechId123");
        List<CustomLocationIdentifier> customIdentifiers = new ArrayList<CustomLocationIdentifier>();

        Map<String, String> idProperties = new HashMap<String, String>();
        idProperties.put("id1", "idhere");
        idProperties.put("id2", "idhere2");
        CustomLocationIdentifier identifier = new CustomLocationIdentifier("type1", idProperties);

        customIdentifiers.add(identifier);
        location.setCustomIdentifiers(customIdentifiers);
        
        List<String> path = new ArrayList<String>();
        path.add("level1");
        path.add("level2");
        path.add("level3");
        location.setPath(path);

        locationService.saveLocation(location);
    }
}
