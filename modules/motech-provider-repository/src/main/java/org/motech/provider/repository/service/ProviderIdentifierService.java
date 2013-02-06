package org.motech.provider.repository.service;

import java.util.List;
import org.motech.provider.repository.domain.ProviderIdentifierType;

public interface ProviderIdentifierService {

    List<ProviderIdentifierType> getAllIdentifierTypes();

    ProviderIdentifierType getIdentifierTypeByName(String identifierName);

    void addIdentifierType(ProviderIdentifierType identifierType);
    
    void addIdentifierTypeJson(String identifierTypeJson);

    void removeIdentifierType(String identifierName);

}
