package org.motech.provider.repository.service.impl;

import java.util.List;

import org.motech.provider.repository.dao.ProviderCouchDAO;
import org.motech.provider.repository.domain.Provider;
import org.motech.provider.repository.domain.ProviderIdentifier;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderRepositoryServiceImpl implements ProviderRepositoryService {

    
    @Autowired
    private ProviderCouchDAO providerDao;
    
    @Override
    public Provider getProviderByIdentifier(ProviderIdentifier identifier) {
        return providerDao.queryProviderByIdentifier(identifier);
    }

    @Override
    public Provider getProviderByIdentifiers(List<ProviderIdentifier> identifiers) {
        //
        return null;
    }

    @Override
    public void saveProvider(Provider provider) {
        providerDao.addProvider(provider);
    }

}
