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
package com.blackducksoftware.integration.eclipseplugin.internal.listeners;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
// import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;
import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class ProjectDependenciesChangedListener implements IElementChangedListener {
    private final ProjectDependencyInformation information;

    private final FilePathGavExtractor extractor;

    private final DependencyInformationService depService;

    public ProjectDependenciesChangedListener(final ProjectDependencyInformation information,
            final FilePathGavExtractor extractor, final DependencyInformationService depService) {
        this.information = information;
        this.extractor = extractor;
        this.depService = depService;
    }

    @Override
    public void elementChanged(final ElementChangedEvent event) {
        visit(event.getDelta());
    }

    public String getProjectNameFromElement(final IJavaElement el) throws CoreException {
        final IJavaProject javaProj = el.getJavaProject();
        if (javaProj != null) {
            final IProject proj = javaProj.getProject();
            if (proj != null) {
                final IProjectDescription description = proj.getDescription();
                if (description != null) {
                    return description.getName();
                }
            }
        }
        return null;
    }

    public void removeDependency(final IJavaElement el) throws CoreException, MalformedURLException {
        final String projName = getProjectNameFromElement(el);
        if (projName != null) {
            final URL projectUrl = el.getPath().toFile().toURI().toURL();
            if (depService.isGradleDependency(projectUrl)) {
                final Gav gav = extractor.getGradlePathGav(projectUrl);
                // TODO: No hardcoded strings.
                information.removeWarningFromProject(projName, new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion()));
            } else if (depService.isMavenDependency(projectUrl)) {
                final URL mavenURL = JavaCore.getClasspathVariable(ClasspathVariables.MAVEN).toFile().toURI().toURL();
                final Gav gav = extractor.getMavenPathGav(projectUrl, mavenURL);
                information.removeWarningFromProject(projName, new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion()));
            }
        }

    }

    public void addDependency(final IJavaElement el) throws CoreException, MalformedURLException {
        final String projName = getProjectNameFromElement(el);
        if (projName != null) {
            final URL projectUrl = el.getPath().toFile().toURI().toURL();
            if (depService.isGradleDependency(projectUrl)) {
                final Gav gav = extractor.getGradlePathGav(projectUrl);
                // TODO: No hardcoded strings.
                information.addWarningToProject(projName, new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion()));
            } else if (depService.isMavenDependency(projectUrl)) {
                final URL mavenURL = JavaCore.getClasspathVariable(ClasspathVariables.MAVEN).toFile().toURI().toURL();
                final Gav gav = extractor.getMavenPathGav(projectUrl, mavenURL);
                information.addWarningToProject(projName, new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion()));
            }
        }
    }

    private void visit(final IJavaElementDelta delta) {
        final IJavaElement el = delta.getElement();
        switch (el.getElementType()) {
        case IJavaElement.JAVA_MODEL: {
            visitChildren(delta);
            break;
        }
        case IJavaElement.JAVA_PROJECT: {
            if (isClasspathChanged(delta.getFlags())) {
                visitChildren(delta);
            }
            break;
        }
        case IJavaElement.PACKAGE_FRAGMENT_ROOT: {
            if ((delta.getFlags() & IJavaElementDelta.F_REMOVED_FROM_CLASSPATH) != 0 || (delta.getKind() & IJavaElementDelta.REMOVED) != 0) {
                try {
                    removeDependency(el);
                } catch (final CoreException | MalformedURLException e) {
                	e.printStackTrace();
                }
            }
            if ((delta.getFlags() & IJavaElementDelta.F_ADDED_TO_CLASSPATH) != 0
                    || (delta.getKind() & IJavaElementDelta.ADDED) != 0) {
                try {
                    addDependency(el);
                } catch (final CoreException | MalformedURLException e) {
                	e.printStackTrace();
                }
            }
            break;
        }
        default: {
            break;
        }
        }

    }

    private boolean isClasspathChanged(final int flags) {
        return 0 != (flags & (IJavaElementDelta.F_CLASSPATH_CHANGED | IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED));
    }

    private void visitChildren(final IJavaElementDelta delta) {
        for (final IJavaElementDelta c : delta.getAffectedChildren()) {
            visit(c);
        }
    }

}
