package org.motechproject.mrs.services;

import java.util.List;
import org.motechproject.mrs.domain.Provider;

/**
 * An interface to persist providers
 */
public interface ProviderAdapter {

    /**
     * Persists a provider
     * @param provider The provider to save
     * @return The saved provider object
     */
    Provider saveProvider(Provider provider);

    /**
     * Retrieves a provider by the provider's motech id, may include a filtered list
     * @param motechId The motech id of the provider
     * @return The provider given by the motech id
     */
    List<? extends Provider> getProviderByProviderId(String motechId);

}
