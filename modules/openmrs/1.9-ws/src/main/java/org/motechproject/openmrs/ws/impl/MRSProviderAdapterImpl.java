package org.motechproject.openmrs.ws.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.services.ProviderAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.ProviderResource;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Provider;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MRSProviderAdapterImpl implements ProviderAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MRSProviderAdapterImpl.class);

    private final Map<String, String> attributeTypeUuidCache = new HashMap<String, String>();

    private final ProviderResource providerResource;

    @Autowired
    public MRSProviderAdapterImpl(ProviderResource providerResource) {
        this.providerResource = providerResource;
    }

    @Override
    public OpenMRSProvider saveProvider(org.motechproject.mrs.domain.Provider provider) {
        Validate.notNull(provider, "Provider canont be null");
        Provider converted = ConverterUtils.convertToProvider(provider);
        Provider saved = null;

        try {
            saved = providerResource.createProvider(converted);
        } catch (HttpException e) {
            LOGGER.error("Failed to create provider for: " + provider.getProviderId());
            throw new MRSException(e);
        }

        provider.setProviderId(saved.getUuid());

        saveAttributesForProvider(provider);

        return new OpenMRSProvider(converted.getIdentifier(), (OpenMRSPerson) provider.getPerson());
    }

    private void saveAttributesForProvider(org.motechproject.mrs.domain.Provider provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public Provider getProviderByProviderId(String motechId) {
        // TODO Auto-generated method stub
        return null;
    }

}
