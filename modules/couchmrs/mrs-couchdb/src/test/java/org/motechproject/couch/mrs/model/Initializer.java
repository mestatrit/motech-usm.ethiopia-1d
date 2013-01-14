package org.motechproject.couch.mrs.model;

import org.joda.time.DateTime;

public class Initializer {

    public CouchPersonImpl initializePerson1() {
        CouchPersonImpl person1 = new CouchPersonImpl();
        person1.setDateOfBirth(new DateTime(2011, 12, 12, 0, 0));
        person1.setGender("female");
        person1.setExternalId("12345");
        person1.setFirstName("FirstName");
        person1.setLastName("LastName");
        person1.addAttribute(new CouchAttribute("phone number", "1234567890"));
        person1.setAddress("SomeAddress");
        return person1;
    }

    public CouchPersonImpl initializeSecondPerson() {
        CouchPersonImpl person2 = new CouchPersonImpl();
        person2.setAddress("AnAddress");
        person2.setDateOfBirth(new DateTime(2011, 12, 12, 0, 0));
        person2.setGender("female");
        person2.setExternalId("externalId");
        person2.setFirstName("AName");
        person2.setLastName("ALastName");
        return person2;
    }

    public CouchPersonImpl initializeThirdPerson() {
        CouchPersonImpl person2 = new CouchPersonImpl();
        person2.setAddress("New address");
        person2.setDateOfBirth(new DateTime(2011, 12, 12, 0, 0));
        person2.setGender("female");
        person2.setExternalId("54322");
        person2.setFirstName("FirstName");
        person2.setLastName("LastName");
        person2.addAttribute(new CouchAttribute("parity", "G4P3"));
        return person2;
    }

    public CouchPersonImpl initializePerson3WithOnlyExternalIdAndPhone() {
        CouchPersonImpl person3 = new CouchPersonImpl();
        person3.addAttribute(new CouchAttribute("phone number", "1234567890"));
        person3.setExternalId("00000");
        return person3;
    }
}
