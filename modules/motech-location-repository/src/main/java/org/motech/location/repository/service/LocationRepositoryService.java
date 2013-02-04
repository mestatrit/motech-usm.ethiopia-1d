package org.motech.location.repository.service;

import java.util.List;

import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationValidationException;
import org.motech.location.repository.domain.MotechLocationIdentifier;

public interface LocationRepositoryService {
    
    Location getLocationById(String id);
    
    Location getLocationByMotechId(MotechLocationIdentifier motechId);
    
    void saveLocation(Location location);

    void saveLocationValidated(Location location) throws LocationValidationException;
    
    void addChildLocation(Location parent, Location child) throws LocationValidationException;
    
    void addChildLocations(Location parent, List<Location> children) throws LocationValidationException;
    
    List<Location> getImmediateChildren(Location parent);
    
    List<Location> getAllChildren(Location parent);
    
    Location getParent(Location child);
}
