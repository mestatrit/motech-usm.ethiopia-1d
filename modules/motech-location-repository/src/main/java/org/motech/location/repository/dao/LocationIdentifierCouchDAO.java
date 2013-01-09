package org.motech.location.repository.dao;

import java.util.List;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
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
        LocationIdentifierType byName = getIdentifierTypeByName(locationIdentifierType.getIdentifierName());
        if (byName == null) {
            this.add(locationIdentifierType);
        } else {
            this.remove(byName);
            this.add(locationIdentifierType);
        }
    }

    public void updateLocationIdentifierType(LocationIdentifierType locationIdentifierType) {
        this.update(locationIdentifierType);
    }

    public void removeLocationIdentifierType(LocationIdentifierType locationIdentifierType) {
        this.remove(locationIdentifierType);
    }

    public List<LocationIdentifierType> getAllIdentifierTypes() {
        return super.getAll();
    }

    @View(name = "identifier_type_by_name", map = "function(doc) { if(doc.type === 'LocationIdentifierType') emit(doc.identifierName); }")
    public LocationIdentifierType getIdentifierTypeByName(String identifierName) {
        List<LocationIdentifierType> records = queryView("identifier_type_by_name", identifierName);
        if (records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }

}
