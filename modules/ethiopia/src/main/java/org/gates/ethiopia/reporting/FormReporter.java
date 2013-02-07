package org.gates.ethiopia.reporting;

import java.io.IOException;
import java.util.List;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;

public final class FormReporter {

    private FormReporter() { }

    public static CommcareReport calculateFields(List<String> fields, List<CommcareForm> forms) {

        CommcareReport result = new CommcareReport();

        for (String field : fields) {
            int totalValue = 0;
            for (CommcareForm form : forms) {
                FormValueElement formData = form.getForm();

                FormValueElement formValue = formData.getElementByName(field);

                String value = ("0");

                if (formValue != null) {
                    value = formValue.getValue();
                }

                int formTotal;

                try {
                    formTotal = Integer.parseInt(value);
                } catch (NullPointerException | NumberFormatException e){
                    formTotal = Integer.valueOf(0);
                }

                totalValue += formTotal;
            }

            result.getReportingValues().put(field, totalValue + "");

        }
        result.setNumberOfForms(forms.size());
        return result;
    }

    public static String generateJsonString(CommcareReport report) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(report);
        } catch (JsonGenerationException e) {
            return null;
        } catch (JsonMappingException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }
}
