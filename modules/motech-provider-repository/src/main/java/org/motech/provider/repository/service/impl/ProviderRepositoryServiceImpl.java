package org.motech.provider.repository.service.impl;

import java.util.List;
import org.motech.provider.repository.dao.ProviderCouchDAO;
import org.motech.provider.repository.domain.ProviderIdBroker;
import org.motech.provider.repository.service.ProviderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviderRepositoryServiceImpl implements ProviderRepositoryService {

    
    @Autowired
    private ProviderCouchDAO providerDao;

    @Override
    public List<ProviderIdBroker> getProvidersByPropertyAndValue(String property, String value) {
        return providerDao.queryProvidersByPropertyAndValue(property, value);
    }

    @Override
    public ProviderIdBroker getProviderByMotechId(String motechId) {
        return providerDao.queryProviderByMotechId(motechId);
    }

    @Override
    public void saveProvider(ProviderIdBroker provider) {
        providerDao.addProvider(provider);
    }

    @Override
    public List<ProviderIdBroker> getProvidersByLocationId(String motechId) {
        return providerDao.queryProvidersByLocationId(motechId);
    }

}
