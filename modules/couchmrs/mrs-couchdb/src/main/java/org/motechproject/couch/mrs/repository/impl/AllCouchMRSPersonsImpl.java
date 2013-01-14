package org.motechproject.couch.mrs.repository.impl;

import java.util.Collections;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.couch.mrs.model.CouchPersonImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchMRSPersons;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllCouchMRSPersonsImpl extends MotechBaseRepository<CouchPersonImpl> implements AllCouchMRSPersons {

    @Autowired
    protected AllCouchMRSPersonsImpl(@Qualifier("couchMRSDatabaseConnector") CouchDbConnector db) {
        super(CouchPersonImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_externalId", map = "function(doc) { if (doc.type ==='Person') { emit(doc.externalId, doc._id); }}")
    public List<CouchPersonImpl> findByExternalId(String externalId) {
        if (externalId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_externalId").key(externalId).includeDocs(true);
        return db.queryView(viewQuery, CouchPersonImpl.class);
    }

    @Override
    public void addPerson(CouchPersonImpl person) throws MRSCouchException {
        if (person.getExternalId() == null) {
            throw new NullPointerException("External ID cannot be null.");
        }
        if (!findByExternalId(person.getExternalId()).isEmpty()) {
            update(person);
            return;
        }
        try {
            super.add(person);
        } catch (IllegalArgumentException e) {
            throw new MRSCouchException(e.getMessage(), e);
        }
    }

    @Override
    public void update(CouchPersonImpl person) {
        super.update(person);
    }

    @Override
    public void remove(CouchPersonImpl person) {
        super.remove(person);
    }

    @Override
    @View(name = "findAllPersons", map = "function(doc) {if (doc.type == 'Person') {emit(null, doc._id);}}")
    public List<CouchPersonImpl> findAllPersons() {
        List<CouchPersonImpl> ret = queryView("findAllPersons");
        if (null == ret) {
            ret = Collections.<CouchPersonImpl> emptyList();
        }
        return ret;
    }
}
