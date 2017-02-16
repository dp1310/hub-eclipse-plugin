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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class ProjectInformationService {

    private final DependencyInformationService dependencyInformationService;

    private final FilePathGavExtractor extractor;

    public ProjectInformationService(final DependencyInformationService dependencyInformationService,
            final FilePathGavExtractor extractor) {
        this.dependencyInformationService = dependencyInformationService;
        this.extractor = extractor;
    }

    public int getNumBinaryDependencies(final List<IPackageFragmentRoot> packageFragmentRoots) {
        int numBinary = 0;
        for (final IPackageFragmentRoot root : packageFragmentRoots) {
            try {
                if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
                    numBinary++;
                }
            } catch (final JavaModelException e) {
                /*
                 * Occurs if root does not exist or an exception occurs while accessing
                 * resource. If this happens, assume root is not binary and therefore do
                 * not increment count
                 */
            }
        }
        return numBinary;
    }

    public List<Gav> getGavsFromFilepaths(List<URL> mavenAndGradleFilePaths) {
        List<Gav> gavs = new ArrayList<>();
        for (URL filePath : mavenAndGradleFilePaths) {
            Gav tempGav = getGavFromFilepath(filePath);
            if (tempGav != null)
                gavs.add(tempGav);
        }
        return gavs;
    }

    public Gav getGavFromFilepath(URL dependencyFilepath) {
        if (dependencyInformationService.isMavenDependency(dependencyFilepath)) {
            URL m2Repo;
			try {
				m2Repo = JavaCore.getClasspathVariable(ClasspathVariables.MAVEN).toFile().toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
            //final String device = m2Repo.getDevice();
            //String osString = m2Repo.toOSString();
            //if (device != null) {
            //    osString = osString.replaceFirst(device, "");
            //}
            final Gav gav = extractor.getMavenPathGav(dependencyFilepath, m2Repo);
            // TODO: No hardcoded strings
            return new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion());
        } else if (dependencyInformationService.isGradleDependency(dependencyFilepath)) {
            final Gav gav = extractor.getGradlePathGav(dependencyFilepath);
            return new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion());
        } else {
            return null;
        }
    }

    public List<URL> getProjectDependencyFilePaths(final String projectName) {
        final IJavaProject javaProject = getJavaProject(projectName);
        try {
            IPackageFragmentRoot[] packageFragmentRoots = javaProject.getPackageFragmentRoots();
            final List<URL> dependencyFilepaths = getBinaryDependencyFilepaths(Arrays.asList(packageFragmentRoots));
            return dependencyFilepaths;
        } catch (final JavaModelException e) {
            // if exception thrown when getting filepaths to source and binary dependencies, assume
            // there are no dependencies
        }
        return Arrays.asList();
    }

    public List<URL> getBinaryDependencyFilepaths(final List<IPackageFragmentRoot> packageFragmentRoots) {
        final ArrayList<URL> dependencyFilepaths = new ArrayList<>();
        for (final IPackageFragmentRoot root : packageFragmentRoots) {
            URL tempURL = getBinaryDependencyFilepath(root);
            if (tempURL != null) {
                dependencyFilepaths.add(tempURL);
            }
        }
        return dependencyFilepaths;
    }

    public URL getBinaryDependencyFilepath(final IPackageFragmentRoot packageFragmentRoot) {
        try {
            if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_BINARY) {
               return packageFragmentRoot.getPath().toFile().toURI().toURL();
            }
        } catch (final JavaModelException e) {
            /*
             * If root does not exist or exception occurs while accessing
             * resource, do not add its filepath to the list of binary
             * dependency filepaths
             */
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    public IJavaProject getJavaProject(final String projectName) {
        if (!projectName.equals("")) {
            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            if (project != null) {
                if (isJavaProject(project)) {
                    return JavaCore.create(project);
                }
            }
        }
        return null;
    }

    public boolean isJavaProject(final IProject project) {
        try {
            return project.hasNature(JavaCore.NATURE_ID);
        } catch (final CoreException e) {
            return false; // CoreException means project is closed/ doesn't exist
        }
    }
}
