package com.blackducksoftware.integration.eclipseplugin.internal;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.validator.HubServerConfigValidator;
import com.blackducksoftware.integration.validator.ValidationResults;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationValidatorTest {

    @Mock
    HubRestConnectionService connectionService;

    @Mock
    HubServerConfigBuilder builder;

    @Mock
    HubServerConfig config;

    @Mock
    ValidationResults results;

    @Mock
    IllegalArgumentException illegalArgumentException;

    @Mock
    EncryptionException encryptionException;

    @Mock
    HubServerConfigValidator configValidator;

    private final String ERROR_MSG = "ValidationResults error message";

    private final String ILLEGAL_ARGUMENT_EXCEPTION_MSG = "illegal argument exception message";

    private final String ENCRYPTION_EXCEPTION_MSG = "encryption exception message";

    @Test
    public void testValidationResultsFailure() {
        Mockito.when(builder.createValidator()).thenReturn(configValidator);
        Mockito.when(configValidator.assertValid()).thenReturn(results);
        Mockito.when(builder.build()).thenReturn(config);
        Mockito.when(results.isSuccess()).thenReturn(false);
        Mockito.when(results.getAllResultString()).thenReturn(ERROR_MSG);
        final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
        final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "").getResponseMessage();
        assertEquals(ERROR_MSG, message);

    }

    @Test
    public void testValidationResultsSuccess() {
        Mockito.when(builder.createValidator()).thenReturn(configValidator);
        Mockito.when(configValidator.assertValid()).thenReturn(results);
        Mockito.when(builder.build()).thenReturn(config);
        Mockito.when(results.isSuccess()).thenReturn(true);
        final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
        final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "").getResponseMessage();
        assertEquals(AuthorizationValidator.LOGIN_SUCCESS_MESSAGE, message);
    }

    @Test
    public void testIllegalArgumentExceptionThrown()
            throws IllegalArgumentException, URISyntaxException, EncryptionException, HubIntegrationException {
        Mockito.when(builder.createValidator()).thenReturn(configValidator);
        Mockito.when(configValidator.assertValid()).thenReturn(results);
        Mockito.when(results.isSuccess()).thenReturn(true);
        Mockito.when(builder.build()).thenReturn(config);
        Mockito.when(illegalArgumentException.getMessage()).thenReturn(ILLEGAL_ARGUMENT_EXCEPTION_MSG);
        Mockito.doThrow(illegalArgumentException).when(connectionService).getCredentialsRestConnection(config);
        final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
        final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "").getResponseMessage();
        assertEquals(ILLEGAL_ARGUMENT_EXCEPTION_MSG, message);
    }

    @Test
    public void testEncryptionExceptionThrown()
            throws IllegalArgumentException, URISyntaxException, EncryptionException, HubIntegrationException {
        Mockito.when(builder.createValidator()).thenReturn(configValidator);
        Mockito.when(configValidator.assertValid()).thenReturn(results);
        Mockito.when(results.isSuccess()).thenReturn(true);
        Mockito.when(builder.build()).thenReturn(config);
        Mockito.when(encryptionException.getMessage()).thenReturn(ENCRYPTION_EXCEPTION_MSG);
        Mockito.doThrow(encryptionException).when(connectionService).getCredentialsRestConnection(config);
        final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
        final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "").getResponseMessage();
        assertEquals(ENCRYPTION_EXCEPTION_MSG, message);
    }
}
