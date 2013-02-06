package org.motech.location.repository.service;

import java.util.List;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationValidationException;

public interface LocationRepositoryService {

    Location getLocationById(String id);

    List<Location> getLocationsByPropertyValue(String property, String value);

    List<Location> getAllLocationsByType(String type);

    Location getLocationByMotechId(String motechId);

    void saveLocation(Location location);

    void saveLocationValidated(Location location) throws LocationValidationException;

    void addChildLocation(Location parent, Location child) throws LocationValidationException;

    void addChildLocations(Location parent, List<Location> children) throws LocationValidationException;

    List<Location> getImmediateChildren(Location parent);

    List<Location> getAllChildren(String node);

    List<Location> getAllChildrenByType(String node, String locationType);

    Location getParent(Location child);
}
