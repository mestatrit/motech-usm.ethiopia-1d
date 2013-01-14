package org.motechproject.couch.mrs.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.motechproject.couch.mrs.util.CouchAttributeDeserializer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.domain.Person;
import static org.motechproject.commons.date.util.DateUtil.setTimeZone;

@TypeDiscriminator("doc.type === 'Person'")
public class CouchPersonImpl extends MotechBaseDataObject implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String type = "Person";

    @JsonProperty
    private String externalId;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String middleName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String preferredName;
    @JsonProperty
    private String address;
    @JsonProperty
    private DateTime dateOfBirth;
    @JsonProperty
    private Boolean birthDateEstimated;
    @JsonProperty
    private Integer age;
    @JsonProperty
    private String gender;
    @JsonProperty
    private Boolean dead;
    @JsonProperty
    @JsonDeserialize(using = CouchAttributeDeserializer.class)
    private List<Attribute> attributes = new ArrayList<Attribute>();
    @JsonProperty
    private DateTime deathDate;

    public CouchPersonImpl(Person person) {
        this.externalId = person.getId();
        this.firstName = person.getFirstName();
        this.middleName = person.getMiddleName();
        this.lastName = person.getLastName();
        this.preferredName = person.getPreferredName();
        this.address = person.getAddress();
        this.dateOfBirth = person.getDateOfBirth();
        this.birthDateEstimated = person.getBirthDateEstimated();
        this.age = person.getAge();
        this.gender = person.getGender();
        this.dead = person.isDead();
        this.attributes = person.getAttributes();
        this.deathDate = person.getDeathDate();
    }

    public CouchPersonImpl() {
        super();
        this.setType(type);
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DateTime getDateOfBirth() {
        return setTimeZone(dateOfBirth);
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getBirthDateEstimated() {
        return birthDateEstimated;
    }

    public void setBirthDateEstimated(Boolean birthDateEstimated) {
        this.birthDateEstimated = birthDateEstimated;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean isDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public DateTime getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(DateTime deathDate) {
        this.deathDate = deathDate;
    }

    public String attrValue(String key) {
        List<Attribute> atts = new ArrayList<Attribute>();
        for (Attribute att : attributes) {
            if ((att.getName().matches(key))) {
                atts.add(att);
            }
        }
        return CollectionUtils.isNotEmpty(atts) ? atts.get(0).getValue() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CouchPersonImpl)) {
            return false;
        }

        CouchPersonImpl other = (CouchPersonImpl) o;

        return Objects.equals(externalId, other.getExternalId()) && Objects.equals(firstName, other.getFirstName())
                && Objects.equals(lastName, other.getLastName()) && Objects.equals(address, other.getAddress())
                && Objects.equals(getDateOfBirth(), other.getDateOfBirth()) && Objects.equals(gender, other.getGender())
                && Objects.equals(attributes, other.getAttributes());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + ObjectUtils.hashCode(externalId);
        hash = hash * 31 + ObjectUtils.hashCode(firstName);
        hash = hash * 31 + ObjectUtils.hashCode(lastName);
        hash = hash * 31 + ObjectUtils.hashCode(address);
        hash = hash * 31 + ObjectUtils.hashCode(dateOfBirth);
        hash = hash * 31 + ObjectUtils.hashCode(gender);
        hash = hash * 31 + ObjectUtils.hashCode(attributes);
        return hash;
    }

    public void addAttribute(CouchAttribute attribute) {
        attributes.add(attribute);
    }
}
