package org.motechproject.openmrs.helper;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mrs.domain.Attribute;
import org.motechproject.mrs.model.OpenMRSAttribute;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.model.OpenMRSPerson;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static org.hamcrest.Matchers.equalTo;

@Component
public class PatientHelper {
    public Patient buildOpenMrsPatient(OpenMRSPatient patient,
                                       PatientIdentifierType patientIdentifierType, Location location,
                                       List<PersonAttributeType> allPersonAttributeTypes) {

        final Patient openMRSPatient = new Patient(createPersonWithNames(patient));
        openMRSPatient.addIdentifier(new PatientIdentifier(patient.getMotechId(), patientIdentifierType, location));
        openMRSPatient.setGender(patient.getPerson().getGender());
        openMRSPatient.setBirthdate(patient.getPerson().getDateOfBirth().toDate());
        openMRSPatient.setBirthdateEstimated(patient.getPerson().getBirthDateEstimated());
        openMRSPatient.setDead(patient.getPerson().isDead());
        openMRSPatient.setDeathDate(patient.getPerson().deathDate().toDate());
        setPatientAddress(openMRSPatient, patient.getPerson().getAddress());
        setPersonAttributes(patient, openMRSPatient, allPersonAttributeTypes);
        return openMRSPatient;
    }

    private Person createPersonWithNames(OpenMRSPatient patient) {
        final Person person = new Person();
        for (PersonName name : getAllNames(patient)) {
            person.addName(name);
        }
        return person;
    }

    private List<PersonName> getAllNames(OpenMRSPatient patient) {
        final List<PersonName> personNames = new ArrayList<PersonName>();
        OpenMRSPerson mrsPerson = patient.getPerson();
        personNames.add(new PersonName(mrsPerson.getFirstName(), mrsPerson.getMiddleName(), mrsPerson.getLastName()));
        return personNames;
    }

    private void setPatientAddress(Patient patient, String address) {
        if (address != null) {
            PersonAddress personAddress = new PersonAddress();
            personAddress.setAddress1(address);
            patient.addAddress(personAddress);
        }
    }

    private void setPersonAttributes(OpenMRSPatient patient, Patient openMRSPatient,
                                     List<PersonAttributeType> allPersonAttributeTypes) {
        OpenMRSPerson mrsPerson = patient.getPerson();
        if (CollectionUtils.isNotEmpty(mrsPerson.getAttributes())) {
            for (Attribute attribute : mrsPerson.getAttributes()) {
                PersonAttributeType attributeType = (PersonAttributeType) selectUnique(allPersonAttributeTypes,
                        having(on(PersonAttributeType.class).getName(), equalTo(attribute.getName())));
                openMRSPatient.addAttribute(new PersonAttribute(attributeType, attribute.getValue()));
            }
        }
    }
}
