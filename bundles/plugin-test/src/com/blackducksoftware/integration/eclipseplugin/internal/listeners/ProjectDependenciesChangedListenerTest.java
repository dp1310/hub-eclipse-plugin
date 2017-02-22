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
package com.blackducksoftware.integration.eclipseplugin.internal.listeners;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;
import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;
import com.blackducksoftware.integration.hub.buildtool.Gav;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JavaCore.class })
public class ProjectDependenciesChangedListenerTest {

    @Mock
    ProjectDependencyInformation information;

    @Mock
    FilePathGavExtractor extractor;

    @Mock
    DependencyInformationService depService;

    @Mock
    ElementChangedEvent e;

    @Mock
    IJavaElementDelta modelDelta, projectDelta, mavenRootDelta, gradleRootDelta, nonBinaryRootDelta;

    @Mock
    IJavaModel model;

    @Mock
    IJavaProject project;

    @Mock
    IPackageFragmentRoot mavenRoot, gradleRoot, nonBinaryRoot;

    @Mock
    IProject parentProject;

    @Mock
    IProjectDescription projectDescription;

    @Mock
    IPath gradlePath, mavenPath, nonBinaryPath, mavenRepoPath;

    @Mock
    Gav gradleGav, mavenGav;

    @Mock
    private URL GRADLE_PATH_URL;

    @Mock
    private URL MAVEN_PATH_URL;

    @Mock
    private URL NON_BINARY_PATH_URL;

    @Mock
    private URL MAVEN_REPO_PATH_URL;

    private final String PROJECT_NAME = "project name";

    private final String MAVEN_ARTIFACT_STRING = "maven artifact";

    private final String MAVEN_GROUP_STRING = "maven group";

    private final String MAVEN_VERSION_STRING = "maven version";

    private final String GRADLE_ARTIFACT_STRING = "gradle artifact";

    private final String GRADLE_GROUP_STRING = "gradle group";

    private final String GRADLE_VERSION_STRING = "gradle version";

    private void setUpAllStubs() throws CoreException, MalformedURLException {
        Mockito.when(model.getElementType()).thenReturn(IJavaElement.JAVA_MODEL);
        Mockito.when(project.getElementType()).thenReturn(IJavaElement.JAVA_PROJECT);
        Mockito.when(mavenRoot.getElementType()).thenReturn(IJavaElement.PACKAGE_FRAGMENT_ROOT);
        Mockito.when(gradleRoot.getElementType()).thenReturn(IJavaElement.PACKAGE_FRAGMENT_ROOT);
        Mockito.when(nonBinaryRoot.getElementType()).thenReturn(IJavaElement.PACKAGE_FRAGMENT_ROOT);
        Mockito.when(e.getDelta()).thenReturn(modelDelta);
        Mockito.when(modelDelta.getElement()).thenReturn(model);
        Mockito.when(projectDelta.getElement()).thenReturn(project);
        Mockito.when(mavenRootDelta.getElement()).thenReturn(mavenRoot);
        Mockito.when(gradleRootDelta.getElement()).thenReturn(gradleRoot);
        Mockito.when(nonBinaryRootDelta.getElement()).thenReturn(nonBinaryRoot);
        Mockito.when(modelDelta.getAffectedChildren()).thenReturn(new IJavaElementDelta[] { projectDelta });
        Mockito.when(projectDelta.getAffectedChildren())
                .thenReturn(new IJavaElementDelta[] { mavenRootDelta, gradleRootDelta, nonBinaryRootDelta });
        Mockito.when(mavenRoot.getJavaProject()).thenReturn(project);
        Mockito.when(gradleRoot.getJavaProject()).thenReturn(project);
        Mockito.when(nonBinaryRoot.getJavaProject()).thenReturn(project);
        Mockito.when(project.getProject()).thenReturn(parentProject);
        Mockito.when(parentProject.getDescription()).thenReturn(projectDescription);
        Mockito.when(projectDescription.getName()).thenReturn(PROJECT_NAME);
        Mockito.when(gradleRoot.getPath()).thenReturn(gradlePath);
        Mockito.when(mavenRoot.getPath()).thenReturn(mavenPath);
        Mockito.when(nonBinaryRoot.getPath()).thenReturn(nonBinaryPath);
        Mockito.when(gradlePath.toFile().toURI().toURL()).thenReturn(GRADLE_PATH_URL);
        Mockito.when(mavenPath.toFile().toURI().toURL()).thenReturn(MAVEN_PATH_URL);
        Mockito.when(nonBinaryPath.toFile().toURI().toURL()).thenReturn(NON_BINARY_PATH_URL);
        Mockito.when(depService.isMavenDependency(GRADLE_PATH_URL)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(GRADLE_PATH_URL)).thenReturn(true);
        Mockito.when(depService.isMavenDependency(MAVEN_PATH_URL)).thenReturn(true);
        Mockito.when(depService.isGradleDependency(MAVEN_PATH_URL)).thenReturn(false);
        Mockito.when(depService.isMavenDependency(NON_BINARY_PATH_URL)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(NON_BINARY_PATH_URL)).thenReturn(false);
        PowerMockito.mockStatic(JavaCore.class);
        Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenRepoPath);
        Mockito.when(mavenRepoPath.toFile().toURI().toURL()).thenReturn(MAVEN_REPO_PATH_URL);
        Mockito.when(extractor.getMavenPathGav(MAVEN_PATH_URL, MAVEN_REPO_PATH_URL)).thenReturn(mavenGav);
        Mockito.when(extractor.getGradlePathGav(GRADLE_PATH_URL)).thenReturn(gradleGav);
        Mockito.when(mavenGav.getGroupId()).thenReturn(MAVEN_GROUP_STRING);
        Mockito.when(mavenGav.getArtifactId()).thenReturn(MAVEN_ARTIFACT_STRING);
        Mockito.when(mavenGav.getVersion()).thenReturn(MAVEN_VERSION_STRING);
        Mockito.when(gradleGav.getGroupId()).thenReturn(GRADLE_GROUP_STRING);
        Mockito.when(gradleGav.getArtifactId()).thenReturn(GRADLE_ARTIFACT_STRING);
        Mockito.when(gradleGav.getVersion()).thenReturn(GRADLE_VERSION_STRING);
    }

    @Test
    public void testClasspathNotChanged() throws CoreException, MalformedURLException {
        setUpAllStubs();
        Mockito.when(projectDelta.getFlags()).thenReturn(0);
        final ProjectDependenciesChangedListener listener = new ProjectDependenciesChangedListener(information,
                extractor, depService);
        listener.elementChanged(e);
        Mockito.verify(information, Mockito.times(0)).addWarningToProject(PROJECT_NAME, gradleGav);
        Mockito.verify(information, Mockito.times(0)).addWarningToProject(PROJECT_NAME, mavenGav);
        Mockito.verify(information, Mockito.times(0)).removeWarningFromProject(PROJECT_NAME, gradleGav);
        Mockito.verify(information, Mockito.times(0)).removeWarningFromProject(PROJECT_NAME, mavenGav);
    }

    @Test
    public void testDependencyRemovedFromClasspath() throws CoreException, MalformedURLException {
        setUpAllStubs();
        Mockito.when(projectDelta.getFlags()).thenReturn(IJavaElementDelta.F_CLASSPATH_CHANGED);
        Mockito.when(mavenRootDelta.getFlags()).thenReturn(IJavaElementDelta.F_REMOVED_FROM_CLASSPATH);
        Mockito.when(mavenRootDelta.getKind()).thenReturn(0);
        Mockito.when(gradleRootDelta.getFlags()).thenReturn(0);
        Mockito.when(gradleRootDelta.getKind()).thenReturn(IJavaElementDelta.ADDED);
        Mockito.when(nonBinaryRootDelta.getKind()).thenReturn(IJavaElementDelta.CHANGED);
        Mockito.when(nonBinaryRootDelta.getFlags()).thenReturn(0);
        final ProjectDependenciesChangedListener listener = new ProjectDependenciesChangedListener(information,
                extractor, depService);
        listener.elementChanged(e);
        Mockito.verify(information, Mockito.times(0)).addWarningToProject(PROJECT_NAME,
                new Gav("maven", mavenGav.getGroupId(), mavenGav.getArtifactId(), mavenGav.getVersion()));
        Mockito.verify(information, Mockito.times(1)).removeWarningFromProject(PROJECT_NAME, mavenGav);
        Mockito.verify(information, Mockito.times(1)).addWarningToProject(PROJECT_NAME,
                new Gav("maven", gradleGav.getGroupId(), gradleGav.getArtifactId(), gradleGav.getVersion()));
        Mockito.verify(information, Mockito.times(0)).removeWarningFromProject(PROJECT_NAME, gradleGav);
        Mockito.verify(extractor, Mockito.times(0)).getGradlePathGav(NON_BINARY_PATH_URL);
        Mockito.verify(extractor, Mockito.times(0)).getMavenPathGav(NON_BINARY_PATH_URL,
                MAVEN_REPO_PATH_URL);
        Mockito.verify(depService, Mockito.times(1)).isGradleDependency(NON_BINARY_PATH_URL);
        Mockito.verify(depService, Mockito.times(1)).isMavenDependency(NON_BINARY_PATH_URL);
    }

}
