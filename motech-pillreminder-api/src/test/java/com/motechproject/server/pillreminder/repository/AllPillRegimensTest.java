package com.motechproject.server.pillreminder.repository;

import com.motechproject.server.pillreminder.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static com.motechproject.server.pillreminder.util.TestUtil.newDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testPillReminder.xml"})
public class AllPillRegimensTest {

    @Autowired
    private AllPillRegimens allPillRegimens;

    private Date startDate;
    private Date endDate;

    @Before
    public void setUp() {
        startDate = newDate(2011, 1, 1);
        endDate = newDate(2011, 3, 1);
    }

    @Test
    public void shouldSaveThePillRegimenWithoutDosage() {
        PillRegimen pillRegimen = new PillRegimen("1234", startDate, endDate, null);

        allPillRegimens.add(pillRegimen);

        assertNotNull(pillRegimen.getId());
        allPillRegimens.remove(pillRegimen);
    }

    @Test
    public void shouldSaveThePillRegimenWithDosages() {
        Medicine medicine = new Medicine("m1");
        Reminder reminder = new Reminder(newDate(2011, 1, 21));
        Set<Medicine> medicines = new HashSet<Medicine>();
        medicines.add(medicine);
        Set<Reminder> reminders = new HashSet<Reminder>();
        reminders.add(reminder);

        Dosage dosage = new Dosage(medicines,reminders);
        Set<Dosage> dosages = new HashSet<Dosage>();
        dosages.add(dosage);

        PillRegimen pillRegimen = new PillRegimen("1234", startDate, endDate, dosages);
        allPillRegimens.add(pillRegimen);

        assertNotNull(pillRegimen.getId());

        PillRegimen pillRegimenFromDB = allPillRegimens.get(pillRegimen.getId());
        assertNotNull(pillRegimenFromDB.getDosages());
        assertFalse(pillRegimenFromDB.getDosages().isEmpty());

        allPillRegimens.remove(pillRegimen);
    }

}