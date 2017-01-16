package com.blackducksoftware.integration.eclipseplugin.common.services;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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

    public boolean isJavaProject(final IProject project) {
        try {
            return project.hasNature(JavaCore.NATURE_ID);
        } catch (final CoreException e) {
            return false; // CoreException means project is closed/ doesn't exist
        }
    }

    public int getNumBinaryDependencies(final IPackageFragmentRoot[] packageFragmentRoots) {
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
        // TODO remove
        System.out.println(numBinary);
        return numBinary;
    }

    public String[] getBinaryDependencyFilepaths(final IPackageFragmentRoot[] packageFragmentRoots) {
        final int numBinary = getNumBinaryDependencies(packageFragmentRoots);
        final String[] dependencyFilepaths = new String[numBinary];
        int i = 0;
        for (final IPackageFragmentRoot root : packageFragmentRoots) {
            try {
                if (root.getKind() == IPackageFragmentRoot.K_BINARY) {
                    final IPath path = root.getPath();
                    final String device = path.getDevice();
                    String osString = path.toOSString();
                    if (device != null) {
                        osString = osString.replaceFirst(device, "");
                    }
                    dependencyFilepaths[i] = osString;
                    i++;
                }
            } catch (final JavaModelException e) {
                /*
                 * If root does not exist or exception occurs while accessing
                 * resource, do not add its filepath to the list of binary
                 * dependency filepaths
                 */
            }
        }
        return dependencyFilepaths;
    }

    public Gav[] getMavenAndGradleDependencies(final String projectName) {
        if (projectName.equals("")) {
            return new Gav[0];
        }
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project == null) {
            return new Gav[0];
        }
        if (isJavaProject(project)) {
            final IJavaProject javaProject = JavaCore.create(project);
            IPackageFragmentRoot[] packageFragmentRoots;
            try {
                packageFragmentRoots = javaProject.getPackageFragmentRoots();
                final String[] dependencyFilepaths = getBinaryDependencyFilepaths(packageFragmentRoots);
                return getGavsFromFilepaths(dependencyFilepaths);
            } catch (final JavaModelException e) {
                // if exception thrown when getting filepaths to source and binary dependencies, assume
                // there are no dependencies
                return new Gav[0];
            }
        } else {
            return new Gav[0];
        }
    }

    public int getNumMavenAndGradleDependencies(final String[] dependencyFilepaths) {
        int numDeps = 0;
        for (final String filepath : dependencyFilepaths) {
            if (dependencyInformationService.isMavenDependency(filepath)
                    || dependencyInformationService.isGradleDependency(filepath)) {
                numDeps++;
            }
        }
        return numDeps;
    }

    public Gav[] getGavsFromFilepaths(final String[] dependencyFilepaths) {
        final int numDeps = getNumMavenAndGradleDependencies(dependencyFilepaths);
        final Gav[] gavsWithType = new Gav[numDeps];
        int gavsIndex = 0;
        for (String dependencyFilepath : dependencyFilepaths) {
            if (dependencyInformationService.isMavenDependency(dependencyFilepath)) {
                final Gav gav = extractor.getMavenPathGav(dependencyFilepath,
                        JavaCore.getClasspathVariable(ClasspathVariables.MAVEN).toString());
                // TODO: No hardcoded strings
                gavsWithType[gavsIndex] = new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion());
                gavsIndex++;
            } else if (dependencyInformationService.isGradleDependency(dependencyFilepath)) {
                final Gav gav = extractor.getGradlePathGav(dependencyFilepath);
                gavsWithType[gavsIndex] = new Gav("maven", gav.getGroupId(), gav.getArtifactId(), gav.getVersion());
                gavsIndex++;
            }
        }
        return gavsWithType;
    }
}
