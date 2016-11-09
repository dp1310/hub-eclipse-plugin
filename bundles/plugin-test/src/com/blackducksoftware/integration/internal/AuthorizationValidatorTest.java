package com.blackducksoftware.integration.internal;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.builder.ValidationResultEnum;
import com.blackducksoftware.integration.builder.ValidationResults;
import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationValidator;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.global.GlobalFieldKey;
import com.blackducksoftware.integration.hub.global.HubServerConfig;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationValidatorTest {

	@Mock
	HubRestConnectionService connectionService;
	@Mock
	HubServerConfigBuilder builder;
	@Mock
	HubServerConfig config;
	@Mock
	ValidationResults<GlobalFieldKey, HubServerConfig> results;
	@Mock
	IllegalArgumentException illegalArgumentException;
	@Mock
	URISyntaxException uriSyntaxException;
	@Mock
	BDRestException bdRestException;
	@Mock
	EncryptionException encryptionException;

	private final String ERROR_MSG = "ValidationResults error message";
	private final String ILLEGAL_ARGUMENT_EXCEPTION_MSG = "illegal argument exception message";
	private final String URI_SYNTAX_EXCEPTION_MSG = "URI syntax exception message";
	private final String BD_REST_EXCEPTION_MSG = "BD rest exception message";
	private final String ENCRYPTION_EXCEPTION_MSG = "encryption exception message";

	@Test
	public void testValidationResultsFailure() {
		final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
		Mockito.when(builder.buildResults()).thenReturn(results);
		Mockito.when(results.isSuccess()).thenReturn(false);
		Mockito.when(results.getAllResultString(ValidationResultEnum.ERROR)).thenReturn(ERROR_MSG);
		final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "");
		assertEquals(ERROR_MSG, message);

	}

	@Test
	public void testValidationResultsSuccessAndNoExceptionsThrown() {
		final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
		Mockito.when(builder.buildResults()).thenReturn(results);
		Mockito.when(results.isSuccess()).thenReturn(true);
		final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "");
		assertEquals(AuthorizationValidator.LOGIN_SUCCESS_MESSAGE, message);
	}

	@Test
	public void testIllegalArgumentExceptionThrown()
			throws IllegalArgumentException, URISyntaxException, BDRestException, EncryptionException {
		final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
		Mockito.when(builder.buildResults()).thenReturn(results);
		Mockito.when(results.isSuccess()).thenReturn(true);
		Mockito.when(results.getConstructedObject()).thenReturn(config);
		Mockito.when(illegalArgumentException.getMessage()).thenReturn(ILLEGAL_ARGUMENT_EXCEPTION_MSG);
		Mockito.doThrow(illegalArgumentException).when(connectionService).getCredentialsRestConnection(config);
		final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "");
		assertEquals(ILLEGAL_ARGUMENT_EXCEPTION_MSG, message);
	}

	@Test
	public void testURISyntaxExceptionThrown()
			throws IllegalArgumentException, URISyntaxException, BDRestException, EncryptionException {
		final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
		Mockito.when(builder.buildResults()).thenReturn(results);
		Mockito.when(results.isSuccess()).thenReturn(true);
		Mockito.when(results.getConstructedObject()).thenReturn(config);
		Mockito.when(uriSyntaxException.getMessage()).thenReturn(URI_SYNTAX_EXCEPTION_MSG);
		Mockito.doThrow(uriSyntaxException).when(connectionService).getCredentialsRestConnection(config);
		final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "");
		assertEquals(URI_SYNTAX_EXCEPTION_MSG, message);
	}

	@Test
	public void testBDRestExceptionThrown()
			throws IllegalArgumentException, URISyntaxException, BDRestException, EncryptionException {
		final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
		Mockito.when(builder.buildResults()).thenReturn(results);
		Mockito.when(results.isSuccess()).thenReturn(true);
		Mockito.when(results.getConstructedObject()).thenReturn(config);
		Mockito.when(bdRestException.getMessage()).thenReturn(BD_REST_EXCEPTION_MSG);
		Mockito.doThrow(bdRestException).when(connectionService).getCredentialsRestConnection(config);
		final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "");
		assertEquals(BD_REST_EXCEPTION_MSG, message);
	}

	@Test
	public void testEncryptionExceptionThrown()
			throws IllegalArgumentException, URISyntaxException, BDRestException, EncryptionException {
		final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
		Mockito.when(builder.buildResults()).thenReturn(results);
		Mockito.when(results.isSuccess()).thenReturn(true);
		Mockito.when(results.getConstructedObject()).thenReturn(config);
		Mockito.when(encryptionException.getMessage()).thenReturn(ENCRYPTION_EXCEPTION_MSG);
		Mockito.doThrow(encryptionException).when(connectionService).getCredentialsRestConnection(config);
		final String message = validator.validateCredentials("", "", "", "", "", "", "", "", "");
		assertEquals(ENCRYPTION_EXCEPTION_MSG, message);
	}
}
