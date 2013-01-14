package org.motechproject.mrs.services;

import org.motechproject.mrs.domain.Provider;

public interface ProviderAdapter {

    Provider saveProvider(Provider provider);

    Provider getProviderByMotechId(String motechId);

}
