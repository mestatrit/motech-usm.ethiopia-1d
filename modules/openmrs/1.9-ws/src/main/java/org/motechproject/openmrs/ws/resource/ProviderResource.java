package org.motechproject.openmrs.ws.resource;

import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.model.Provider;

public interface ProviderResource {

    Provider createProvider(Provider provider) throws HttpException;

    Provider getProviderById(String providerId) throws HttpException;

    String getMotechProviderIdentifierUuid() throws HttpException;
}
