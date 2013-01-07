package org.motech.location.repository.service.impl;

import java.util.List;

import org.motech.location.repository.dao.LocationCouchDAO;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifier;
import org.motech.location.repository.domain.MotechLocationIdentifier;
import org.motech.location.repository.service.LocationRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationRepositoryServiceImpl implements LocationRepositoryService {

    
    @Autowired
    private LocationCouchDAO providerDao;
    
    @Override
    public Location getLocationByIdentifier(LocationIdentifier identifier) {
        return providerDao.queryLocationByIdentifier(identifier);
    }

    @Override
    public Location getLocationByIdentifiers(List<LocationIdentifier> identifiers) {
        //
        return null;
    }

    @Override
    public void saveLocation(Location location) {
        providerDao.addLocation(location);
    }

    @Override
    public Location getLocationById(String id) {
        return providerDao.queryLocationByIdString(id);
    }

    @Override
    public Location getLocationByMotechId(MotechLocationIdentifier motechId) {
        return providerDao.queryLocationByMotechId(motechId);
    }

}
