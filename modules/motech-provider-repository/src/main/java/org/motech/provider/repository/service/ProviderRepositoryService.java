package org.motech.provider.repository.service;

import java.util.List;

import org.motech.provider.repository.domain.Provider;
import org.motech.provider.repository.domain.ProviderIdentifier;

public interface ProviderRepositoryService {
    
    Provider getProviderByIdentifier(ProviderIdentifier identifier);
    
    Provider getProviderByIdentifiers(List<ProviderIdentifier> identifiers);
    
    void saveProvider(Provider provider);
}
