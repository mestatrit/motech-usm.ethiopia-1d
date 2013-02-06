package org.motech.location.repository.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.motech.location.repository.dao.LocationCouchDAO;
import org.motech.location.repository.domain.CustomLocationIdentifier;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifierType;
import org.motech.location.repository.domain.LocationValidationException;
import org.motech.location.repository.service.LocationIdentifierService;
import org.motech.location.repository.service.LocationRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationRepositoryServiceImpl implements LocationRepositoryService {

    @Autowired
    private LocationIdentifierService identifierService;

    @Autowired
    private LocationCouchDAO locationDao;

    @Override
    public void saveLocation(Location location) {
        locationDao.addLocation(location);
    }

    @Override
    public void saveLocationValidated(Location location) throws LocationValidationException {
        List<CustomLocationIdentifier> identifiers = location.getCustomIdentifiers();
        if (identifiers != null && identifiers.size() > 0) {
            for (CustomLocationIdentifier identifier : identifiers) {
                LocationIdentifierType validateType = identifierService.getIdentifierTypeByName(identifier.getIdentifierType());
                if (validateType == null || !validateLocationIdentifier(validateType, identifier)) {
                    throw new LocationValidationException("Location did not pass validation");
                }
            }
        }
        saveLocation(location);
    }

    private boolean validateLocationIdentifier(LocationIdentifierType validateType, CustomLocationIdentifier identifier) throws LocationValidationException {
        Map<String, Boolean> fieldRequirements = validateType.getIdentifyingProperties();
        if (identifier.getIdentifyingProperties() == null || (fieldRequirements.size() > 0 && identifier.getIdentifyingProperties().size() == 0)) {
            throw new LocationValidationException("No identifying properties in the location where expected");
        }
        for (Entry<String, Boolean> entry : fieldRequirements.entrySet()) {
            if (entry.getValue()) {
                if (!identifier.getIdentifyingProperties().containsKey(entry.getKey())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Location getLocationById(String id) {
        return locationDao.queryLocationByIdString(id);
    }

    @Override
    public Location getLocationByMotechId(String motechId) {
        return locationDao.queryLocationByMotechId(motechId);
    }

    @Override
    public void addChildLocation(Location parent, Location child) throws LocationValidationException {
        List<String> parentPath = null;
        if (parent != null) {
            parentPath = parent.getPath();
        }
        List<String> childPath = new ArrayList<String>();
        if (parentPath == null || parentPath.size() == 0) {
            childPath.add(child.getMotechId());
            child.setPath(childPath);
            saveLocationValidated(child);
        } else {
            childPath.addAll(parentPath);
            childPath.add(child.getMotechId());
            child.setPath(childPath);
            saveLocationValidated(child);
        }
    }

    @Override
    public void addChildLocations(Location parent, List<Location> children) throws LocationValidationException {
        if (children != null && parent != null) {
            for (Location child : children) {
                addChildLocation(parent, child);
            }
        }

    }

    @Override
    public List<Location> getImmediateChildren(Location parent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getAllChildren(String node) {
        return locationDao.queryChildLocationNodes(node);
    }

    @Override
    public Location getParent(Location child) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Location> getAllChildrenByType(String node, String locationType) {
        return locationDao.queryChildLocationNodesByType(node, locationType);
    }

    @Override
    public List<Location> getLocationsByPropertyValue(String property, String value) {
        return locationDao.queryLocationByPropertyValue(property, value);
    }

    @Override
    public List<Location> getAllLocationsByType(String type) {
        return locationDao.queryLocationsByType(type);
    }

}
