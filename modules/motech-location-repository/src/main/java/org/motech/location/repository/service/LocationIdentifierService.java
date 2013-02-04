package org.motech.location.repository.service;

import java.util.List;

import org.motech.location.repository.domain.LocationIdentifierType;

public interface LocationIdentifierService {

    List<LocationIdentifierType> getAllIdentifierTypes();

    LocationIdentifierType getIdentifierTypeByName(String identifierName);

    void addIdentifierType(LocationIdentifierType identifierType);
    
    void addIdentifierTypeJson(String identifierTypeJson);

    void removeIdentifierType(String identifierName);

}
