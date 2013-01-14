package org.motechproject.couch.mrs.service;

import java.util.List;
import org.joda.time.DateTime;
import org.motechproject.couch.mrs.model.CouchPersonImpl;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchMRSPersons;
import org.motechproject.couch.mrs.repository.impl.AllCouchMRSPersonsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.motechproject.mrs.domain.Attribute;

@Service
public class CouchMRSServiceImpl implements CouchMRSService {

    @Autowired
    private AllCouchMRSPersons allCouchMRSPersons;

    @Override
    public void addPerson(String externalId, String firstName, String lastName, DateTime dateOfBirth, String gender,
            String address, List<Attribute> attributes) throws MRSCouchException {
        CouchPersonImpl person = new CouchPersonImpl();
        person.setExternalId(externalId);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setDateOfBirth(dateOfBirth);
        person.setGender(gender);
        person.setAddress(address);
        person.setAttributes(attributes);
        allCouchMRSPersons.addPerson(person);
    }

    @Override
    public void addPerson(CouchPersonImpl person) throws MRSCouchException {
        allCouchMRSPersons.addPerson(person);
    }

    @Override
    public void updatePerson(CouchPersonImpl person) {
        allCouchMRSPersons.update(person);
    }

    @Override
    public void removePerson(CouchPersonImpl person) {
        allCouchMRSPersons.remove(person);

    }

    @Override
    public List<CouchPersonImpl> findAllCouchMRSPersons() {
        return allCouchMRSPersons.findAllPersons();
    }

    @Override
    public List<CouchPersonImpl> findByExternalId(String externalId) {
        return allCouchMRSPersons.findByExternalId(externalId);
    }

    @Override
    public void removeAll() {
        ((AllCouchMRSPersonsImpl) allCouchMRSPersons).removeAll();

    }

}
