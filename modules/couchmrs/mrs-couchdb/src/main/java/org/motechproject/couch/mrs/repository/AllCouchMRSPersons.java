package org.motechproject.couch.mrs.repository;

import java.util.List;
import org.motechproject.couch.mrs.model.CouchPersonImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;

public interface AllCouchMRSPersons {

    List<CouchPersonImpl> findByExternalId(String externalId);

    void addPerson(CouchPersonImpl person) throws MRSCouchException;

    void update(CouchPersonImpl person);

    void remove(CouchPersonImpl person);

    List<CouchPersonImpl> findAllPersons();
}
