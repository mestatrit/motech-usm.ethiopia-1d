package org.motech.location.repository.service.impl;

import java.util.List;

import org.motech.location.repository.dao.LocationIdentifierCouchDAO;
import org.motech.location.repository.domain.LocationIdentifierType;
import org.motech.location.repository.service.LocationIdentifierService;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationIdentifierServiceImpl implements LocationIdentifierService {

    private MotechJsonReader jsonReader = new MotechJsonReader();

    @Autowired
    private LocationIdentifierCouchDAO locationIdentifierDAO;

    @Override
    public List<LocationIdentifierType> getAllIdentifierTypes() {
        return locationIdentifierDAO.getAllIdentifierTypes();
    }

    @Override
    public LocationIdentifierType getIdentifierTypeByName(String identifierName) {
        return locationIdentifierDAO.getIdentifierTypeByName(identifierName);
    }

    @Override
    public void addIdentifierType(LocationIdentifierType identifierType) {
        locationIdentifierDAO.add(identifierType);
    }

    @Override
    public void addIdentifierTypeJson(String identifierTypeJson) {
       LocationIdentifierType identifierType = (LocationIdentifierType) jsonReader.readFromString(identifierTypeJson,  LocationIdentifierType.class);
       addIdentifierType(identifierType);
    }

    @Override
    public void removeIdentifierType(String identifierName) {
        LocationIdentifierType type = getIdentifierTypeByName(identifierName);
        if (type != null) {
            locationIdentifierDAO.remove(type);
        }

    }

}
