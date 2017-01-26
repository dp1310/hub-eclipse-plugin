/**
 * hub-eclipse-plugin-test
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.eclipseplugin.common.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecurePreferencesServiceTest {

	@Mock
	ISecurePreferences rootNode;
	@Mock
	ISecurePreferences storageNode;

	private final String NODE_NAME = "node";
	private final String KEY = "key";
	private final String VALUE = "value";

	@Test
	public void testGetPreferenceFailure() throws StorageException {
		final SecurePreferencesService service = new SecurePreferencesService(NODE_NAME, rootNode);
		final StorageException e = new StorageException(StorageException.ENCRYPTION_ERROR, "error");
		Mockito.when(rootNode.node(NODE_NAME)).thenReturn(storageNode);
		Mockito.when(storageNode.get(KEY, "")).thenThrow(e);
		final String onFailedSave = service.getSecurePreference(KEY);
		assertEquals("", onFailedSave);
	}

	@Test
	public void testGetPreferenceSuccess() throws StorageException {
		final SecurePreferencesService service = new SecurePreferencesService(NODE_NAME, rootNode);
		Mockito.when(rootNode.node(NODE_NAME)).thenReturn(storageNode);
		Mockito.when(storageNode.get(KEY, "")).thenReturn(VALUE);
		final String onFailedSave = service.getSecurePreference(KEY);
		assertEquals(VALUE, onFailedSave);
	}

	@Test
	public void testSavePreferenceFailure() throws StorageException {
		final SecurePreferencesService service = new SecurePreferencesService(NODE_NAME, rootNode);
		Mockito.when(rootNode.node(NODE_NAME)).thenReturn(storageNode);
		Mockito.doNothing().when(storageNode).put(VALUE, KEY, false);
		assertTrue(service.saveSecurePreference(VALUE, KEY, false));
	}

	@Test
	public void testSavePreferenceSuccess() throws StorageException {
		final SecurePreferencesService service = new SecurePreferencesService(NODE_NAME, rootNode);
		Mockito.when(rootNode.node(NODE_NAME)).thenReturn(storageNode);
		final StorageException e = new StorageException(StorageException.ENCRYPTION_ERROR, "error");
		Mockito.doThrow(e).when(storageNode).put(VALUE, KEY, false);
		assertFalse(service.saveSecurePreference(VALUE, KEY, false));
	}
}
