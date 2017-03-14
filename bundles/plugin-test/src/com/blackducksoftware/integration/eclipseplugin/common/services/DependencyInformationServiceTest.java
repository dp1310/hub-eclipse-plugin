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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JavaCore.class)
public class DependencyInformationServiceTest {

    @Mock
    private IPath mavenPath;

    private final String fakeMavenClasspathVariable = "fake/maven";

    private URL[] MAVEN_DEPENDENCIES_TO_TEST;

    private URL[] GRADLE_DEPENDENCIES_TO_TEST;

    private URL[] NON_GRADLE_DEPENDENCIES_TO_TEST;

    private final DependencyInformationService service = new DependencyInformationService(Activator.getPlugin());

    @Test
    public void testIsMavenDependency() {
        PowerMockito.mockStatic(JavaCore.class);
        Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
        Mockito.when(mavenPath.toString()).thenReturn(getSystemSpecificFilepath(fakeMavenClasspathVariable, "/"));
        for (final URL dependency : MAVEN_DEPENDENCIES_TO_TEST) {
            assertTrue(dependency.toString() + " is not a maven dependency", service.isMavenDependency(dependency));
        }
    }

    @Test
    public void testIsNotMavenDependency() throws MalformedURLException {
        MAVEN_DEPENDENCIES_TO_TEST = new URL[] {
                new URL("com/blackducksoftware/integration/integration-test-common/1.0.0/integration-test-common-1.0.0.jar"),
                new URL("junit/junit/4.12/junit-4.12.jar"), new URL("org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"),
                new URL("org/mockito/mockito-all/1.10.19/mockito-all-1.10.19.jar") };
        PowerMockito.mockStatic(JavaCore.class);
        Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
        Mockito.when(mavenPath.toString()).thenReturn(getSystemSpecificFilepath(fakeMavenClasspathVariable, "/"));
        for (final URL dependency : MAVEN_DEPENDENCIES_TO_TEST) {
            assertFalse(dependency + " is a maven dependency", service.isMavenDependency(dependency));
        }
    }

    private String getSystemSpecificFilepath(final String path, final String separator) {
        return path.replaceAll(separator, StringEscapeUtils.escapeJava(File.separator));
    }

    @Test
    public void testIsGradleDependency() throws MalformedURLException {
        GRADLE_DEPENDENCIES_TO_TEST = new URL[] {
                new URL("/Users/janderson/.gradle/caches/modules-2/files-2.1/com.google.guava/guava/18.0/cce0823396aa693798f8882e64213b1772032b09/guava-18.0.jar"),
                new URL("/Users/janderson/.gradle/caches/modules-2/files-2.1/joda-time/joda-time/2.3/56498efd17752898cfcc3868c1b6211a07b12b8f/joda-time-2.3.jar"),
                new URL("/Users/janderson/.gradle/caches/modules-2/files-2.1/commons-codec/commons-codec/1.9/9ce04e34240f674bc72680f8b843b1457383161a/commons-codec-1.9.jar"),
                new URL("/Users/janderson/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.datatype/jackson-datatype-joda/2.3.3/2592678ed4fa51dfcea0e52be99578581945c861/jackson-datatype-joda-2.3.3.jar") };

        for (final URL dependency : GRADLE_DEPENDENCIES_TO_TEST) {
            assertTrue(dependency + " is not a gradle dependency",
                    service.isGradleDependency(dependency));
        }
    }

    @Test
    public void testIsNotGradleDependency() throws MalformedURLException {
        NON_GRADLE_DEPENDENCIES_TO_TEST = new URL[] {
                new URL("/Users/janderson/.gradle/wrapper/dists/gradle-2.6-bin/627v5nqkbedft1k2i5inq4nwi/gradle-2.6/lib/plugins/gradle-publish-2.6.jar"),
                new URL("/Users/janderson/.gradle/wrapper/dists/gradle-2.6-bin/627v5nqkbedft1k2i5inq4nwi/gradle-2.6/lib/plugins/gradle-ivy-2.6.jar"),
                new URL("/Users/janderson/.gradle/wrapper/dists/gradle-2.6-bin/627v5nqkbedft1k2i5inq4nwi/gradle-2.6/lib/plugins/gradle-jacoco-2.6.jar"),
                new URL("/Users/janderson/.gradle/wrapper/dists/gradle-2.6-bin/627v5nqkbedft1k2i5inq4nwi/gradle-2.6/lib/plugins/gradle-platform-play-2.6.jar"),
                new URL("/Users/janderson/.m2/repository/commons-io/commons-io/2.5/commons-io-2.5.jar") };
        for (final URL dependency : NON_GRADLE_DEPENDENCIES_TO_TEST) {
            assertFalse(dependency + " is a gradle dependency",
                    service.isGradleDependency(dependency));
        }
    }

}
