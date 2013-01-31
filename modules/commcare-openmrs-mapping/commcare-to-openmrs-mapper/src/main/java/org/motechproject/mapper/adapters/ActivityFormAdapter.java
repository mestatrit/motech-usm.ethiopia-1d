package org.motechproject.mapper.adapters;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.adapters.mappings.MRSActivity;

public interface ActivityFormAdapter {

    void adaptForm(CommcareForm form, MRSActivity activity);

}
