package com.blackducksoftware.integration.eclipseplugin.internal;

import java.net.URISyntaxException;

import com.blackducksoftware.integration.validator.ValidationResultEnum;
import com.blackducksoftware.integration.validator.ValidationResults;
import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.GlobalFieldKey;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class AuthorizationValidator {
    private final HubRestConnectionService connectionService;

    private final HubServerConfigBuilder builder;

    public static final String LOGIN_SUCCESS_MESSAGE = "Successful login!";

    public AuthorizationValidator(final HubRestConnectionService connectionService,
            final HubServerConfigBuilder builder) {
        this.connectionService = connectionService;
        this.builder = builder;
    }

    public AuthorizationResponse validateCredentials(final String username, final String password, final String hubUrl,
            final String proxyUsername, final String proxyPassword, final String proxyPort, final String proxyHost,
            final String ignoredProxyHosts, final String timeout) {
        setHubServerConfigBuilderFields(builder, username, password, hubUrl, proxyUsername, proxyPassword, proxyPort,
                proxyHost, ignoredProxyHosts, timeout);
        
        
        //final ValidationResults results = builder.buildResults();
//        final ValidationResults results = builder.createValidator().assertValid();
//        if (results.isSuccess()) {
//            try {
//                RestConnection connection = connectionService.getCredentialsRestConnection(results.getConstructedObject());
//                return new AuthorizationResponse(connection, LOGIN_SUCCESS_MESSAGE);
//            } catch (final IllegalArgumentException e) {
//                return new AuthorizationResponse(e.getMessage());
//            } catch (final URISyntaxException e) {
//                return new AuthorizationResponse(e.getMessage());
//            } catch (final BDRestException e) {
//                return new AuthorizationResponse(e.getMessage());
//            } catch (final EncryptionException e) {
//                return new AuthorizationResponse(e.getMessage());
//            }
//
//        }
        
        try{
        	HubServerConfig config = builder.build();
        	RestConnection connection = connectionService.getCredentialsRestConnection(config);
        	return new AuthorizationResponse(connection, LOGIN_SUCCESS_MESSAGE);
        } catch (IllegalStateException e) {
        	return new AuthorizationResponse(e.getMessage());
        } catch (IllegalArgumentException e) {
			return new AuthorizationResponse(e.getMessage());
		} catch (EncryptionException e) {
			return new AuthorizationResponse(e.getMessage());
		} catch (HubIntegrationException e) {
			return new AuthorizationResponse(e.getMessage());
		}
        
        //return new AuthorizationResponse(results.getAllResultString(ValidationResultEnum.ERROR));
    }

    private void setHubServerConfigBuilderFields(final HubServerConfigBuilder builder, final String username,
            final String password, final String hubUrl, final String proxyUsername, final String proxyPassword,
            final String proxyPort, final String proxyHost, final String ignoredProxyHosts, final String timeout) {
        builder.setUsername(username);
        builder.setPassword(password);
        builder.setHubUrl(hubUrl);
        builder.setTimeout(timeout);
        builder.setProxyUsername(proxyUsername);
        builder.setProxyPassword(proxyPassword);
        builder.setProxyHost(proxyHost);
        builder.setProxyPort(proxyPort);
        builder.setIgnoredProxyHosts(ignoredProxyHosts);
    }
}
