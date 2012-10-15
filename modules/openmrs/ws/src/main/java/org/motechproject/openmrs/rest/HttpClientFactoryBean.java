package org.motechproject.openmrs.rest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.FactoryBean;

/**
 * Factory bean to create an HTTP client with BASIC authentication
 */
public class HttpClientFactoryBean implements FactoryBean<HttpClient> {

    private String user;
    private String password;
    private HttpClient httpClient;
    private SettingsFacade facade;

    public HttpClientFactoryBean(SettingsFacade facade) {
        this.facade = facade;
    }

    public void init() {
        this.user = facade.getProperty("openmrs.user");
        this.password = facade.getProperty("openmrs.password");     
    }

    @Override
    public HttpClient getObject() throws Exception {
        if (httpClient == null) {
            initializeHttpClient();
        }

        return httpClient;
    }

    private void initializeHttpClient() {
        httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        httpClient.getState().setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials(user, password));
    }

    @Override
    public Class<?> getObjectType() {
        return HttpClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
