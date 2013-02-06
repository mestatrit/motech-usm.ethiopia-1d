package org.motech.provider.repository.dao;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motech.provider.repository.domain.ProviderIdentifierType;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderIdentifierCouchDAO extends MotechBaseRepository<ProviderIdentifierType> {

    @Autowired
    protected ProviderIdentifierCouchDAO(@Qualifier("providerRepositoryDatabaseConnector") CouchDbConnector db) {
        super(ProviderIdentifierType.class, db);
        initStandardDesignDocument();
    }

    public void addLocationIdentifierType(ProviderIdentifierType providerIdentifierType) {
        ProviderIdentifierType byName = getIdentifierTypeByName(providerIdentifierType.getIdentifierName());
        if (byName == null) {
            this.add(providerIdentifierType);
        } else {
            this.remove(byName);
            this.add(providerIdentifierType);
        }
    }

    public void updateLocationIdentifierType(ProviderIdentifierType providerIdentifierType) {
        this.update(providerIdentifierType);
    }

    public void removeLocationIdentifierType(ProviderIdentifierType providerIdentifierType) {
        this.remove(providerIdentifierType);
    }

    public List<ProviderIdentifierType> getAllIdentifierTypes() {
        return super.getAll();
    }

    @View(name = "identifier_type_by_name", map = "function(doc) { if(doc.type === 'ProviderIdentifierType') emit(doc.identifierName); }")
    public ProviderIdentifierType getIdentifierTypeByName(String identifierName) {
        List<ProviderIdentifierType> records = queryView("identifier_type_by_name", identifierName);
        if (records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }
}
