package org.motech.location.repository.dao;

import org.ektorp.CouchDbConnector;
import org.motech.location.repository.domain.LocationIdentifierType;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class LocationIdentifierCouchDAO extends MotechBaseRepository<LocationIdentifierType> {
    
    @Autowired
    protected LocationIdentifierCouchDAO(@Qualifier("locationRepositoryDatabaseConnector") CouchDbConnector db) {
        super(LocationIdentifierType.class, db);
        initStandardDesignDocument();
    }
    
    public void addLocationIdentifierType(LocationIdentifierType locationIdentifierType) {
        this.add(locationIdentifierType);
    }
    
    public void updateLocationIdentifierType(LocationIdentifierType locationIdentifierType) {
        this.update(locationIdentifierType);
    }
    
    public void removeLocationIdentifierType(LocationIdentifierType locationIdentifierType) {
        this.remove(locationIdentifierType);
    }

}
