package org.motechproject.couch.mrs.repository.impl;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllCouchProvidersImpl extends MotechBaseRepository<CouchProvider> implements AllCouchProviders {

    protected AllCouchProvidersImpl(@Qualifier("couchProviderDatabaseConnector") CouchDbConnector db) {
        super(CouchProvider.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_externalId", map = "function(doc) { if (doc.type ==='Provider') { emit(doc.person.externalId, doc._id); }}")
    public List<CouchProvider> findByExternalId(String externalId) {
        if (externalId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_externalId").key(externalId).includeDocs(true);
        return db.queryView(viewQuery, CouchProvider.class);
    }

    @Override
    public void addProvider(CouchProvider provider) throws MRSCouchException {
        this.add(provider);

    }

    @Override
    public void update(CouchProvider provider) {
        this.update(provider);
    }

    @Override
    public void remove(CouchProvider provider) {
        this.remove(provider);
    }

    @Override
    public List<CouchProvider> getAllProviders() {
        return this.getAll();
    }

}
