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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;
import com.blackducksoftware.integration.hub.buildtool.Gav;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JavaCore.class, ResourcesPlugin.class })
public class ProjectInformationServiceTest {

    @Mock
    IWorkspace workspace;

    @Mock
    IWorkspaceRoot workspaceRoot;

    @Mock
    IProject testProject, nonJavaProject, javaProject;

    @Mock
    IJavaProject testJavaProject;

    @Mock
    DependencyInformationService depService;

    @Mock
    IPackageFragmentRoot nonBinaryRoot, binaryRoot1, binaryRoot2, mavenRoot1, mavenRoot2, gradleRoot1, gradleRoot2;

    @Mock
    IPath nonBinaryPath, binaryPath1, binaryPath2, mavenPath, mavenPath1, mavenPath2, gradlePath1, gradlePath2;

    @Mock
    File nonBinaryFile, binaryFile1, binaryFile2, mavenFile, mavenFile1, mavenFile2, gradleFile1, gradleFile2;

    @Mock
    URI nonBinaryURI, binaryURI1, binaryURI2, mavenURI, mavenURI1, mavenURI2, gradleURI1, gradleURI2;

    @Mock
    URL nonBinaryURL, binaryURL1, binaryURL2, mavenURL, mavenURL1, mavenURL2, gradleURL1, gradleURL2;

    @Mock
    FilePathGavExtractor extractor;

    @Mock
    Gav mavenGav1, mavenGav2, gradleGav1, gradleGav2;

    private URL MAVEN_1;

    private URL MAVEN_2;

    private URL GRADLE_1;

    private URL GRADLE_2;

    private URL NOT_GRAVEN_1;

    private URL NOT_GRAVEN_2;

    private URL MAVEN_REPO_PATH;

    private final String TEST_PROJECT_NAME = "test project";

    private final String MAVEN_1_GROUP = "maven.1.group";

    private final String MAVEN_1_ARTIFACT = "maven.1.artifact";

    private final String MAVEN_1_VERSION = "maven.1.version";

    private final Gav MAVEN_1_GAV = new Gav(MAVEN_1_GROUP, MAVEN_1_ARTIFACT, MAVEN_1_VERSION);

    private final String MAVEN_2_GROUP = "maven.2.group";

    private final String MAVEN_2_ARTIFACT = "maven.2.artifact";

    private final String MAVEN_2_VERSION = "maven.2.version";

    private final Gav MAVEN_2_GAV = new Gav(MAVEN_2_GROUP, MAVEN_2_ARTIFACT, MAVEN_2_VERSION);

    private final String GRADLE_1_GROUP = "gradle.1.group";

    private final String GRADLE_1_ARTIFACT = "gradle.1.artifact";

    private final String GRADLE_1_VERSION = "gradle.1.version";

    private final Gav GRADLE_1_GAV = new Gav(GRADLE_1_GROUP, GRADLE_1_ARTIFACT, GRADLE_1_VERSION);

    private final String GRADLE_2_GROUP = "gradle.2.group";

    private final String GRADLE_2_ARTIFACT = "gradle.2.artifact";

    private final String GRADLE_2_VERSION = "gradle.2.version";

    private final Gav GRADLE_2_GAV = new Gav(GRADLE_2_GROUP, GRADLE_2_ARTIFACT, GRADLE_2_VERSION);

    @Test
    public void testGettingNumBinaryDependencies() {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        try {
            Mockito.when(nonBinaryRoot.getKind()).thenReturn(0);
            Mockito.when(binaryRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
            final List<IPackageFragmentRoot> roots = Arrays.asList(nonBinaryRoot, binaryRoot1);
            assertEquals("Not counting binary dependencies properly", 1, service.getNumBinaryDependencies(roots));
        } catch (final CoreException e) {
        }
    }

    @Test
    public void testIsJavaProject() throws CoreException {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        Mockito.when(nonJavaProject.hasNature(JavaCore.NATURE_ID)).thenReturn(false);
        Mockito.when(javaProject.hasNature(JavaCore.NATURE_ID)).thenReturn(true);
        assertTrue("Service says Java project is not a Java project", service.isJavaProject(javaProject));
        assertFalse("Service says non-Java project is a Java project", service.isJavaProject(nonJavaProject));

    }

    @Test
    public void testGettingBinaryFilepaths() throws CoreException, MalformedURLException {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        Mockito.when(nonBinaryRoot.getKind()).thenReturn(0);
        Mockito.when(nonBinaryRoot.getPath()).thenReturn(new Path(""));
        Mockito.when(binaryRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(binaryRoot1.getPath()).thenReturn(new Path(""));
        final List<IPackageFragmentRoot> roots = Arrays.asList(nonBinaryRoot, binaryRoot1);
        final List<URL> binaryDependencies = service.getBinaryDependencyFilepaths(roots);
        assertEquals("Not gettting binary dependencies correctly", 1, binaryDependencies.size());
        assertEquals("Not getting correct binary dependencies", Arrays.asList(binaryURL1), binaryDependencies);
    }

    private void prepareRootsAndPaths() throws CoreException, MalformedURLException {
        Mockito.when(mavenRoot1.getPath()).thenReturn(mavenPath1);
        Mockito.when(mavenRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(mavenPath1.toFile()).thenReturn(mavenFile1);
        Mockito.when(mavenFile1.toURI()).thenReturn(mavenURI1);
        Mockito.when(mavenURI1.toURL()).thenReturn(MAVEN_1);
        Mockito.when(mavenRoot2.getPath()).thenReturn(mavenPath2);
        Mockito.when(mavenRoot2.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(mavenPath2.toFile()).thenReturn(mavenFile2);
        Mockito.when(mavenFile2.toURI()).thenReturn(mavenURI2);
        Mockito.when(mavenURI2.toURL()).thenReturn(MAVEN_2);
        Mockito.when(gradleRoot1.getPath()).thenReturn(gradlePath1);
        Mockito.when(gradleRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(gradlePath1.toFile()).thenReturn(gradleFile1);
        Mockito.when(gradleFile1.toURI()).thenReturn(gradleURI1);
        Mockito.when(gradleURI1.toURL()).thenReturn(GRADLE_1);
        Mockito.when(gradleRoot2.getPath()).thenReturn(gradlePath2);
        Mockito.when(gradleRoot2.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(gradlePath2.toFile()).thenReturn(gradleFile2);
        Mockito.when(gradleFile2.toURI()).thenReturn(gradleURI2);
        Mockito.when(gradleURI2.toURL()).thenReturn(GRADLE_2);
    }

    private void prepareDependencyTypes() {
        Mockito.when(depService.isMavenDependency(MAVEN_1)).thenReturn(true);
        Mockito.when(depService.isGradleDependency(MAVEN_1)).thenReturn(false);
        Mockito.when(depService.isMavenDependency(MAVEN_2)).thenReturn(true);
        Mockito.when(depService.isGradleDependency(MAVEN_1)).thenReturn(false);
        Mockito.when(depService.isMavenDependency(GRADLE_1)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(GRADLE_1)).thenReturn(true);
        Mockito.when(depService.isMavenDependency(GRADLE_2)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(GRADLE_2)).thenReturn(true);
        Mockito.when(depService.isMavenDependency(NOT_GRAVEN_1)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(NOT_GRAVEN_1)).thenReturn(false);
        Mockito.when(depService.isMavenDependency(NOT_GRAVEN_2)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(NOT_GRAVEN_2)).thenReturn(false);
    }

    private void prepareGavElements() {
        Mockito.when(mavenGav1.getGroupId()).thenReturn(MAVEN_1_GROUP);
        Mockito.when(mavenGav1.getArtifactId()).thenReturn(MAVEN_1_ARTIFACT);
        Mockito.when(mavenGav1.getVersion()).thenReturn(MAVEN_1_VERSION);
        Mockito.when(mavenGav2.getGroupId()).thenReturn(MAVEN_2_GROUP);
        Mockito.when(mavenGav2.getArtifactId()).thenReturn(MAVEN_2_ARTIFACT);
        Mockito.when(mavenGav2.getVersion()).thenReturn(MAVEN_2_VERSION);
        Mockito.when(gradleGav1.getGroupId()).thenReturn(GRADLE_1_GROUP);
        Mockito.when(gradleGav1.getArtifactId()).thenReturn(GRADLE_1_ARTIFACT);
        Mockito.when(gradleGav1.getVersion()).thenReturn(GRADLE_1_VERSION);
        Mockito.when(gradleGav2.getGroupId()).thenReturn(GRADLE_2_GROUP);
        Mockito.when(gradleGav2.getArtifactId()).thenReturn(GRADLE_2_ARTIFACT);
        Mockito.when(gradleGav2.getVersion()).thenReturn(GRADLE_2_VERSION);
    }

    private void prepareExtractor() {
        Mockito.when(extractor.getMavenPathGav(MAVEN_1, MAVEN_REPO_PATH)).thenReturn(MAVEN_1_GAV);
        Mockito.when(extractor.getMavenPathGav(MAVEN_2, MAVEN_REPO_PATH)).thenReturn(MAVEN_2_GAV);
        Mockito.when(extractor.getGradlePathGav(GRADLE_1)).thenReturn(GRADLE_1_GAV);
        Mockito.when(extractor.getGradlePathGav(GRADLE_2)).thenReturn(GRADLE_2_GAV);
    }

    private void prepareGavsWithType() {
        Mockito.when(mavenGav1.getNamespace()).thenReturn("maven");
        Mockito.when(mavenGav2.getNamespace()).thenReturn("maven");
        Mockito.when(gradleGav1.getNamespace()).thenReturn("maven");
        Mockito.when(gradleGav2.getNamespace()).thenReturn("maven");
    }

    @Test
    public void testGettingGavsFromFilepaths() throws MalformedURLException {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        prepareExtractor();
        prepareDependencyTypes();
        prepareGavElements();
        prepareGavsWithType();
        PowerMockito.mockStatic(JavaCore.class);
        Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
        Mockito.when(mavenPath.toFile().toURI().toURL()).thenReturn(MAVEN_REPO_PATH);
        prepareExtractor();
        final List<URL> dependencies = Arrays.asList(MAVEN_1, MAVEN_2, GRADLE_1, GRADLE_2, NOT_GRAVEN_1, NOT_GRAVEN_2);
        final List<Gav> gavs = service.getGavsFromFilepaths(dependencies);
        final List<Gav> expectedGavMessages = Arrays.asList(
                new Gav("maven", MAVEN_1_GAV.getGroupId(), MAVEN_1_GAV.getArtifactId(), MAVEN_1_GAV.getVersion()),
                new Gav("maven", MAVEN_2_GAV.getGroupId(), MAVEN_2_GAV.getArtifactId(), MAVEN_2_GAV.getVersion()),
                new Gav("maven", GRADLE_1_GAV.getGroupId(), GRADLE_1_GAV.getArtifactId(), GRADLE_1_GAV.getVersion()),
                new Gav("maven", GRADLE_2_GAV.getGroupId(), GRADLE_2_GAV.getArtifactId(), GRADLE_2_GAV.getVersion()));
        assertEquals("Not getting gavs from filepaths correctly", expectedGavMessages, gavs);
    }

    @Test
    public void testGettingAllMavenAndGradleDependencyMessages() throws MalformedURLException {

        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        try {
            PowerMockito.mockStatic(ResourcesPlugin.class);
            PowerMockito.mockStatic(JavaCore.class);
            prepareDependencyTypes();
            prepareGavElements();
            prepareRootsAndPaths();
            prepareExtractor();
            prepareGavsWithType();
            final IPackageFragmentRoot[] roots = new IPackageFragmentRoot[] { mavenRoot1, mavenRoot2, gradleRoot1,
                    gradleRoot2 };
            final List<Gav> expectedGavMessages = Arrays.asList(
                    new Gav("maven", MAVEN_1_GAV.getGroupId(), MAVEN_1_GAV.getArtifactId(), MAVEN_1_GAV.getVersion()),
                    new Gav("maven", MAVEN_2_GAV.getGroupId(), MAVEN_2_GAV.getArtifactId(), MAVEN_2_GAV.getVersion()),
                    new Gav("maven", GRADLE_1_GAV.getGroupId(), GRADLE_1_GAV.getArtifactId(), GRADLE_1_GAV.getVersion()),
                    new Gav("maven", GRADLE_2_GAV.getGroupId(), GRADLE_2_GAV.getArtifactId(), GRADLE_2_GAV.getVersion()));
            Mockito.when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);
            Mockito.when(workspace.getRoot()).thenReturn(workspaceRoot);
            Mockito.when(workspaceRoot.getProject(TEST_PROJECT_NAME)).thenReturn(testProject);
            Mockito.when(testProject.hasNature(JavaCore.NATURE_ID)).thenReturn(true);
            Mockito.when(JavaCore.create(testProject)).thenReturn(testJavaProject);
            Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
            Mockito.when(mavenPath.toFile().toURI().toURL()).thenReturn(MAVEN_REPO_PATH);
            Mockito.when(testJavaProject.getPackageFragmentRoots()).thenReturn(roots);
            final List<Gav> noDeps = service.getGavsFromFilepaths(service.getProjectDependencyFilePaths(""));
            assertEquals(Arrays.asList(), noDeps);
            final List<Gav> deps = service.getGavsFromFilepaths(service.getProjectDependencyFilePaths(TEST_PROJECT_NAME));
            assertEquals(expectedGavMessages, deps);
        } catch (final CoreException e) {
        }
    }
}
