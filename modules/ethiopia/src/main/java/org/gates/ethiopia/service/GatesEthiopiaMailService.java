package org.gates.ethiopia.service;

public interface GatesEthiopiaMailService {

    void sendReminderMail(String externalId, String emailAddress);

    void scheduleMail(String recipient, String emailAddress, String region);

    void sendAggregateEmailReminder(String emailAddress, String body, String subject);

}
