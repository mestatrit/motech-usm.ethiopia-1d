package org.motech.provider.repository.service;

import java.util.List;
import org.motech.provider.repository.domain.ProviderIdBroker;

public interface ProviderRepositoryService {

    List<ProviderIdBroker> getProvidersByPropertyAndValue(String property, String value);

    List<ProviderIdBroker> getProvidersByLocationId(String motechId);

    ProviderIdBroker getProviderByMotechId(String motechId);

    void saveProvider(ProviderIdBroker provider);
}
