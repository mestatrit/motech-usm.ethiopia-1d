package org.motech.location.repository.service;

import java.util.List;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifier;
import org.motech.location.repository.domain.MotechLocationIdentifier;

public interface LocationRepositoryService {
    
    Location getLocationById(String id);
    
    Location getLocationByIdentifier(LocationIdentifier identifier);
    
    Location getLocationByIdentifiers(List<LocationIdentifier> identifiers);
    
    Location getLocationByMotechId(MotechLocationIdentifier motechId);
    
    void saveLocation(Location location);
}
