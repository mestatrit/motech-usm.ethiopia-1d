package org.motechproject.couch.mrs.model;

import java.util.List;
import org.joda.time.DateTime;
import org.motechproject.mrs.domain.Person;
import org.motechproject.mrs.domain.Attribute;

public class CouchPerson implements Person {

    private CouchPersonImpl person;

    public String getId() {
        return person.getExternalId();
    }

    public void setId(String id) {
        person.setExternalId(id);
    }

    public String getFirstName() {
        return person.getFirstName();
    }

    public void setFirstName(String firstName) {
        person.setFirstName(firstName);
    }

    public String getMiddleName() {
        return person.getMiddleName();
    }

    public void setMiddleName(String middleName) {
        person.setMiddleName(middleName);
    }

    public String getLastName() {
        return person.getLastName();
    }

    public void setLastName(String lastName) {
        person.setLastName(lastName);
    }

    public String getPreferredName() {
        return person.getPreferredName();
    }

    public void setPreferredName(String preferredName) {
        person.setPreferredName(preferredName);
    }

    public String getAddress() {
        return person.getAddress();
    }

    public void setAddress(String address) {
        person.setAddress(address);
    }

    public DateTime getDateOfBirth() {
        return person.getDateOfBirth();
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        person.setDateOfBirth(dateOfBirth);
    }

    public Boolean getBirthDateEstimated() {
        return person.getBirthDateEstimated();
    }

    public void setBirthDateEstimated(Boolean birthDateEstimated) {
        person.setBirthDateEstimated(birthDateEstimated);
    }

    public Integer getAge() {
        return person.getAge();
    }

    public void setAge(Integer age) {
        person.setAge(age);
    }

    public String getGender() {
        return person.getGender();
    }

    public void setGender(String gender) {
        person.setGender(gender);
    }

    public Boolean isDead() {
        return person.isDead();
    }

    public void setDead(Boolean dead) {
        person.setDead(dead);
    }

    public DateTime getDeathDate() {
        return person.getDeathDate();
    }

    public void setDeathDate(DateTime deathDate) {
        person.setDeathDate(deathDate);
    }

    @Override
    public List<Attribute> getAttributes() {
        return person.getAttributes();
    }

    @Override
    public void setAttributes(List<Attribute> attributes) {
        person.setAttributes(attributes);
    }
}
