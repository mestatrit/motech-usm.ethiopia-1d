package org.motechproject.couch.mrs.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Provider;

@TypeDiscriminator("doc.type === 'Provider'")
public class CouchProvider extends MotechBaseDataObject implements Provider {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private CouchPersonImpl person;

    public CouchProvider(Person person) {
        populateFields(person);
    }

    public CouchProvider(Provider provider) {
        this.person = new CouchPersonImpl(provider.getPerson());
    }

    public Person getPerson() {
        return convertToCouchPerson(person);
    }

    private Person convertToCouchPerson(CouchPersonImpl person) {

        CouchPerson personToReturn = new CouchPerson();

        personToReturn.setAddress(person.getAddress());
        personToReturn.setAge(person.getAge());
        personToReturn.setAttributes(person.getAttributes());
        personToReturn.setBirthDateEstimated(person.getBirthDateEstimated());
        personToReturn.setDateOfBirth(person.getDateOfBirth());
        personToReturn.setDead(person.isDead());
        personToReturn.setDeathDate(person.getDeathDate());
        personToReturn.setFirstName(person.getFirstName());
        personToReturn.setGender(person.getGender());
        personToReturn.setId(person.getExternalId());
        personToReturn.setLastName(person.getLastName());
        personToReturn.setMiddleName(person.getMiddleName());
        personToReturn.setPreferredName(person.getPreferredName());

        return personToReturn;
    }

    @Override
    public void setPerson(Person person) {
        populateFields(person);
    }

    private void populateFields(Person person) {
        this.person.setAddress(person.getAddress());
        this.person.setAttributes(person.getAttributes());
        this.person.setDateOfBirth(person.getDateOfBirth());
        this.person.setExternalId(person.getId());
        this.person.setFirstName(person.getFirstName());
        this.person.setGender(person.getGender());
        this.person.setLastName(person.getLastName());
    }
}
