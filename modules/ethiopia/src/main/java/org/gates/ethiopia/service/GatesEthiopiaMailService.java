package org.gates.ethiopia.service;

import java.util.List;

public interface GatesEthiopiaMailService {

    void sendReminderMail(String externalId, String emailAddress);

    void scheduleMail(String recipient, String emailAddress, String region);

    void sendAggregateEmailReminder(String emailAddress, String body, String subject);

    void sendAggregateEmailReminder(List<String> recipients, String body, String subject);

}
