package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Provider;
import org.motechproject.openmrs.ws.resource.model.ProviderListResult;

public interface ProviderResource {

    Provider createProvider(Provider provider) throws HttpException;

    ProviderListResult getProvidersById(String providerId) throws HttpException;

    String getMotechProviderIdentifierUuid() throws HttpException;

    Provider getProviderByUuid(String uuid) throws HttpException;
}
