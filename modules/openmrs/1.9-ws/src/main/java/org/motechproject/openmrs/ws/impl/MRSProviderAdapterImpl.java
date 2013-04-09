package org.motechproject.openmrs.ws.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.motechproject.mrs.model.OpenMRSProvider;
import org.motechproject.mrs.services.ProviderAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.ProviderResource;
import org.motechproject.openmrs.ws.resource.model.Provider;
import org.motechproject.openmrs.ws.resource.model.ProviderListResult;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("providerAdapter")
public class MRSProviderAdapterImpl implements ProviderAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MRSProviderAdapterImpl.class);

//    private final Map<String, String> attributeTypeUuidCache = new HashMap<String, String>();

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

//        saveAttributesForProvider(provider);

        return new OpenMRSProvider(converted.getIdentifier(), (OpenMRSPerson) provider.getPerson());
    }

//    private void saveAttributesForProvider(org.motechproject.mrs.domain.Provider provider) {
//        // TODO Auto-generated method stub
//
//    }

    @Override
    public List<OpenMRSProvider> getProviderByProviderId(String providerId) {
        List<OpenMRSProvider> providers = new ArrayList<OpenMRSProvider>();
        ProviderListResult result = null;
        try {
            result = providerResource.getProvidersById(providerId);
        } catch (HttpException e) {
            LOGGER.error("Failed to retrieve providers with ID: " + providerId);
            throw new MRSException(e);
        }

        if (result != null) {
            List<Provider> resultingProviders = result.getResults();
            for (Provider provider : resultingProviders) {
                OpenMRSProvider returnProvider = new OpenMRSProvider(provider.getIdentifier(), ConverterUtils.convertToMrsPerson(provider.getPerson()));
                providers.add(returnProvider);
            }
        }

        return providers;
    }

}