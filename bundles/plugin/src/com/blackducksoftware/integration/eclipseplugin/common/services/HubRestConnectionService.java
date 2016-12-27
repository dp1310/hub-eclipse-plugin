package com.blackducksoftware.integration.eclipseplugin.common.services;

import java.net.URISyntaxException;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.CredentialsRestConnection;

/*
 * Wrapper class for testing purposes
 */
public class HubRestConnectionService {
    public CredentialsRestConnection getCredentialsRestConnection(final HubServerConfig config)
            throws IllegalArgumentException, EncryptionException, HubIntegrationException {
        return new CredentialsRestConnection(config);
    }
}
