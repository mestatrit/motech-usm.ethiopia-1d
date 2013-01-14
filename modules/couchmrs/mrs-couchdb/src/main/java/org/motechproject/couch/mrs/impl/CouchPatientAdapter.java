package org.motechproject.couch.mrs.impl;

import java.util.Date;
import java.util.List;

import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.model.OpenMRSPatient;
import org.motechproject.mrs.services.PatientAdapter;

public class CouchPatientAdapter implements PatientAdapter {

    @Override
    public OpenMRSPatient savePatient(OpenMRSPatient patient) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OpenMRSPatient updatePatient(OpenMRSPatient patient) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OpenMRSPatient getPatient(String patientId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OpenMRSPatient getPatientByMotechId(String motechId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<OpenMRSPatient> search(String name, String motechId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer getAgeOfPatientByMotechId(String motechId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deceasePatient(String motechId, String conceptName, Date dateOfDeath, String comment)
            throws PatientNotFoundException {
        // TODO Auto-generated method stub
        
    }

}
