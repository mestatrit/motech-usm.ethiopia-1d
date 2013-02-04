package org.motech.provider.repository.dao;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motech.provider.repository.domain.MotechIdentifier;
import org.motech.provider.repository.domain.Provider;
import org.motech.provider.repository.domain.ProviderIdentifier;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderCouchDAO extends MotechBaseRepository<Provider> {

    
    private static final String FUNCTION_DOC_EMIT_IDENTIFIER = "function(doc) { if(doc.type === \'Provider\') for (var identifier in doc.identifiers) emit([doc.identifiers[identifier].identifierName, doc.identifiers[identifier].identity], doc._id);}";

    private static final String FUNCTION_DOC_EMIT_MOTECH_IDENTIFIER = "function(doc) { if(doc.type === \'Provider\') emit(doc.motechId, doc._id);}";
    
    private static final String FUNCTION_DOC_EMIT_PROVIDERS_LOCATION_ID = "function(doc) { if(doc.type === \'Provider\') for (var location in doc.locationIdentities) { emit(doc.locationIdentities[location], doc.id); }}";

    @Autowired
    protected ProviderCouchDAO(@Qualifier("providerRepositoryDatabaseConnector") CouchDbConnector db) {
        super(Provider.class, db);
        initStandardDesignDocument();
    }
    
    public void addProvider(Provider provider) {
        this.add(provider);
    }
    
    public void updateProvider(Provider provider) {
        this.update(provider);
    }
    
    public void removeProvider(Provider provider) {
        this.remove(provider);
    }

    @View(name = "find_by_identifier", map = FUNCTION_DOC_EMIT_IDENTIFIER)
    public Provider queryProviderByIdentifier(ProviderIdentifier identifier) {
        List<Provider> providers = queryView("find_by_identifier", ComplexKey.of(identifier.getIdentifierName(), identifier.getIdentity()));
        return providers.size() > 0 ? providers.get(0) : null;
    }
    
    @View(name = "find_by_motech_id", map = FUNCTION_DOC_EMIT_MOTECH_IDENTIFIER)
    public Provider queryProviderByMotechId(MotechIdentifier identifier) {
        List<Provider> providers = queryView("find_by_motech_id", identifier.getIdentity());
        return providers.size() > 0 ? providers.get(0) : null;
    }

    @View(name = "find_by_location_id", map = FUNCTION_DOC_EMIT_PROVIDERS_LOCATION_ID)
    public List<Provider> queryProvidersByLocationId(String locationId) {
        return  queryView("find_by_location_id", locationId);
    }
    
    
}
