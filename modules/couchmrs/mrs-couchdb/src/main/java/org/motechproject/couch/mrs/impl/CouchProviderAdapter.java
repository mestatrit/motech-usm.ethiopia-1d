package org.motechproject.couch.mrs.impl;

import java.util.List;

import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.mrs.domain.Provider;
import org.motechproject.mrs.services.ProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;

public class CouchProviderAdapter implements ProviderAdapter {

    @Autowired
    private AllCouchProviders allCouchProviders;

    @Override
    public Provider saveProvider(Provider provider) {

        CouchProvider couchProvider = new CouchProvider(provider);

        try {
            allCouchProviders.addProvider(couchProvider);
        } catch (MRSCouchException e) {
            return null;
        }

        return couchProvider;
    }

    @Override
    public Provider getProviderByMotechId(String motechId) {
        List<CouchProvider> providers = allCouchProviders.findByExternalId(motechId);

        if (providers != null && providers.size() > 0) {
            return providers.get(0);
        }

        return null;
    }

}
