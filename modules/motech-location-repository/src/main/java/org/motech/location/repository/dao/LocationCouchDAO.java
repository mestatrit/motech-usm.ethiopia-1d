package org.motech.location.repository.dao;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motech.location.repository.domain.Location;
import org.motech.location.repository.domain.LocationIdentifier;
import org.motech.location.repository.domain.MotechLocationIdentifier;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class LocationCouchDAO extends MotechBaseRepository<Location> {
    
    private static final String FUNCTION_DOC_EMIT_LOCATION_IDENTIFIER = "function(doc) { if(doc.type === \'Location\') for (var identifier in doc.identifiers) emit([doc.identifiers[identifier].identifierName, doc.identifiers[identifier].identity], doc._id);}";

    private static final String FUNCTION_DOC_EMIT_LOCATION_MOTECH_IDENTIFIER = "function(doc) { if(doc.type === \'Location\') emit(doc.motechId.identity, doc._id);}";
    
    private static final String FUNCTION_DOC_EMIT_LOCATION_STRING_ID = "function(doc) { if(doc.type === \'Location\') for (var identifier in doc.identifiers) emit(doc.identifiers[identifier].identity, doc._id);}";

    @Autowired
    protected LocationCouchDAO(@Qualifier("locationRepositoryDatabaseConnector") CouchDbConnector db) {
        super(Location.class, db);
        initStandardDesignDocument();
    }
    
    public void addLocation(Location location) {
        this.add(location);
    }
    
    public void updateLocation(Location location) {
        this.update(location);
    }
    
    public void removeLocation(Location location) {
        this.remove(location);
    }

    @View(name = "find_location_by_identifier", map = FUNCTION_DOC_EMIT_LOCATION_IDENTIFIER)

    public Location queryLocationByIdentifier(LocationIdentifier identifier) {
        List<Location> locations = queryView("find_location_by_identifier", ComplexKey.of(identifier.getIdentifierName(), identifier.getIdentity()));
        return locations.size() > 0 ? locations.get(0) : null;
    }

    @View(name = "find_location_by_id_string", map = FUNCTION_DOC_EMIT_LOCATION_STRING_ID)
    public Location queryLocationByIdString (String id) {
        List<Location> locations = queryView("find_location_by_id_string", id);
        return locations.size() > 0 ? locations.get(0) : null;
    }

    @View(name = "find_location_by_motech_id", map = FUNCTION_DOC_EMIT_LOCATION_MOTECH_IDENTIFIER)
    public Location queryLocationByMotechId(MotechLocationIdentifier motechId) {
        List<Location> locations = queryView("find_location_by_motech_id", motechId.getIdentity());
        return locations.size() > 0 ? locations.get(0) : null;
    }
}
