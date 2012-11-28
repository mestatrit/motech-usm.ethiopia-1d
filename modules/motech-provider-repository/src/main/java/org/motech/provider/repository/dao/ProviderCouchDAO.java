package org.motech.provider.repository.dao;

import org.ektorp.CouchDbConnector;
import org.motech.provider.repository.domain.Provider;
import org.motech.provider.repository.domain.ProviderIdentifier;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderCouchDAO extends MotechBaseRepository<Provider> {

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

    public Provider queryProviderByIdentifier(ProviderIdentifier identifier) {
        // TODO Auto-generated method stub
        return null;
    }
}
