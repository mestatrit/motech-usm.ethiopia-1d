package org.motechproject.openmrs.ws.resource.impl;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.ProviderResource;
import org.motechproject.openmrs.ws.resource.model.Provider;
import org.motechproject.openmrs.ws.resource.model.ProviderListResult;
import org.motechproject.openmrs.ws.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderResourceImpl implements ProviderResource {
    
//    private static final Logger LOGGER = Logger.getLogger(ProviderResourceImpl.class);

    private final RestClient restClient;
    private final OpenMrsInstance openmrsInstance;

    @Autowired
    public ProviderResourceImpl(RestClient restClient, OpenMrsInstance openmrsInstance) {
        this.restClient = restClient;
        this.openmrsInstance = openmrsInstance;
    }
    
    @Override
    public Provider createProvider(Provider provider) throws HttpException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProviderListResult getProvidersById(String providerId) throws HttpException {
        String json = restClient.getJson(openmrsInstance.toInstancePathWithParams("/provider?q={id}&v=full",
                providerId));
        return (ProviderListResult) JsonUtils.readJson(json, ProviderListResult.class);
    }
    
    @Override
    public Provider getProviderByUuid(String uuid) throws HttpException {
        String responseJson = null;
        responseJson = restClient.getJson(openmrsInstance.toInstancePathWithParams("/provider/{uuid}?v=full", uuid));
        return (Provider) JsonUtils.readJson(responseJson, Provider.class);
    }

    @Override
    public String getMotechProviderIdentifierUuid() throws HttpException {
        // TODO Auto-generated method stub
        return null;
    }

}
