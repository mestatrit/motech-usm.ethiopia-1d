package org.motech.provider.repository.dao;

import java.util.List;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motech.provider.repository.domain.ProviderIdBroker;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mrs.services.ProviderAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderCouchDAO extends MotechBaseRepository<ProviderIdBroker> {

    private static final String FUNCTION_DOC_EMIT_MOTECH_IDENTIFIER = "function(doc) { if(doc.type === \'ProviderIdBroker\') emit(doc.motechId, doc._id);}";

    private static final String FUNCTION_DOC_EMIT_PROVIDER_BY_PROPERTY_AND_VALUE = "function(doc) { if(doc.type === \'ProviderIdBroker\') for (var identifier in doc.identifiers) { for (var fieldValue in doc.identifiers[identifier].identifyingProperties) emit([fieldValue, doc.identifiers[identifier].identifyingProperties[fieldValue]], doc._id)};}";

    private static final String FUNCTION_DOC_EMIT_PROVIDERS_BY_LOCATION_ID = "function(doc) { if (doc.type === \'ProviderIdBroker\') for (var location in doc.locationIdentities) emit(doc.locationIdentities[location], doc)\n}";

    @Autowired
    private ProviderAdapter providerAdapter;

    @Autowired
    protected ProviderCouchDAO(@Qualifier("providerRepositoryDatabaseConnector") CouchDbConnector db) {
        super(ProviderIdBroker.class, db);
        initStandardDesignDocument();
    }

    public void addProvider(ProviderIdBroker provider) {
        providerAdapter.saveProvider(provider.getMrsProvider());
        this.add(provider);
    }

    public void updateProvider(ProviderIdBroker provider) {
        providerAdapter.saveProvider(provider.getMrsProvider());
        this.update(provider);
    }

    public void removeProvider(ProviderIdBroker provider) {
        this.remove(provider);
    }

    @View(name = "find_by_motech_id", map = FUNCTION_DOC_EMIT_MOTECH_IDENTIFIER)
    public ProviderIdBroker queryProviderByMotechId(String motechId) {
        List<ProviderIdBroker> providers = queryView("find_by_motech_id", motechId);
        for (ProviderIdBroker provider : providers) {
            provider.setMrsProvider(providerAdapter.getProviderByProviderId(provider.getMotechId()));
        }
        return providers.size() > 0 ? providers.get(0) : null;
    }

    @View(name = "find_providers_by_property_and_value", map = FUNCTION_DOC_EMIT_PROVIDER_BY_PROPERTY_AND_VALUE)
    public List<ProviderIdBroker> queryProvidersByPropertyAndValue(String property, String value) {
        List<ProviderIdBroker> providers = queryView("find_providers_by_property_and_value", ComplexKey.of(property, value));

        for (ProviderIdBroker provider : providers) {
            provider.setMrsProvider(providerAdapter.getProviderByProviderId(provider.getMotechId()));
        }               

        return providers;
    }

    @View(name = "find_providers_by_location_id", map = FUNCTION_DOC_EMIT_PROVIDERS_BY_LOCATION_ID)
    public List<ProviderIdBroker> queryProvidersByLocationId(String locationId) {
        List<ProviderIdBroker> providers = queryView("find_providers_by_location_id", locationId);

        for (ProviderIdBroker provider : providers) {
            provider.setMrsProvider(providerAdapter.getProviderByProviderId(provider.getMotechId()));
        }        

        return providers;
    }
}
