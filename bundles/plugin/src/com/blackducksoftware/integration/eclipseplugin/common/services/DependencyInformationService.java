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
package com.blackducksoftware.integration.eclipseplugin.common.services;

import java.io.File;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;

public class DependencyInformationService {

    public boolean isMavenDependency(final String filePath) {
        final IPath m2Repo = JavaCore.getClasspathVariable(ClasspathVariables.MAVEN);
        final String device = m2Repo.getDevice();
        String osString = m2Repo.toOSString();
        if (device != null) {
            osString = osString.replaceFirst(device, "");
        }
        final String[] m2RepoSegments = osString.split(StringEscapeUtils.escapeJava(File.separator));
        final String[] filePathSegments = filePath.split(StringEscapeUtils.escapeJava(File.separator));
        if (filePathSegments.length < m2RepoSegments.length) {
            return false;
        }
        for (int i = 0; i < m2RepoSegments.length; i++) {
            if (!filePathSegments[i].equals(m2RepoSegments[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean isGradleDependency(final String filePath) {
        final String[] filePathSegments = filePath.split(StringEscapeUtils.escapeJava(File.separator));
        if (filePathSegments.length < 3) {
            return false;
        }
        if (filePathSegments[filePathSegments.length - 3].equals("lib")
                || filePathSegments[filePathSegments.length - 2].equals("plugins")
                || filePathSegments[filePathSegments.length - 2].equals("lib")) {
            return false;
        }
        for (final String segment : filePathSegments) {
            if (segment.equals(".gradle")) {
                return true;
            }
        }
        return false;
    }
}
