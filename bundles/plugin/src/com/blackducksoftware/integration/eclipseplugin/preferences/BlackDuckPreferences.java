/**
 * hub-eclipse-plugin
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
package com.blackducksoftware.integration.eclipseplugin.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.blackducksoftware.integration.eclipseplugin.startup.Activator;

/*
 * Class that implements the general Black Duck preferences page
 */
public class BlackDuckPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	public BlackDuckPreferences() {
	}

	public BlackDuckPreferences(final String title) {
		super(title);
	}

	public BlackDuckPreferences(final String title, final ImageDescriptor image) {
		super(title, image);
	}

	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(Activator.getPlugin().getPreferenceStore());
	}

	@Override
	protected Control createContents(final Composite parent) {
		return null;
	}

}
