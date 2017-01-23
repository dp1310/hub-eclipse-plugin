package com.blackducksoftware.integration.eclipseplugin.internal.listeners;

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

    private final String PROJECT_NAME = "project name";

    private final String GRADLE_PATH_OS_STRING = "gradle";

    private final String MAVEN_PATH_OS_STRING = "maven";

    private final String NON_BINARY_PATH_OS_STRING = "non-binary";

    private final String MAVEN_REPO_PATH_OS_STRING = "maven repo";

    private final String MAVEN_ARTIFACT_STRING = "maven artifact";

    private final String MAVEN_GROUP_STRING = "maven group";

    private final String MAVEN_VERSION_STRING = "maven version";

    private final String GRADLE_ARTIFACT_STRING = "gradle artifact";

    private final String GRADLE_GROUP_STRING = "gradle group";

    private final String GRADLE_VERSION_STRING = "gradle version";

    private void setUpAllStubs() throws CoreException {
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
        Mockito.when(gradlePath.toOSString()).thenReturn(GRADLE_PATH_OS_STRING);
        Mockito.when(mavenPath.toOSString()).thenReturn(MAVEN_PATH_OS_STRING);
        Mockito.when(nonBinaryPath.toOSString()).thenReturn(NON_BINARY_PATH_OS_STRING);
        Mockito.when(depService.isMavenDependency(GRADLE_PATH_OS_STRING)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(GRADLE_PATH_OS_STRING)).thenReturn(true);
        Mockito.when(depService.isMavenDependency(MAVEN_PATH_OS_STRING)).thenReturn(true);
        Mockito.when(depService.isGradleDependency(MAVEN_PATH_OS_STRING)).thenReturn(false);
        Mockito.when(depService.isMavenDependency(NON_BINARY_PATH_OS_STRING)).thenReturn(false);
        Mockito.when(depService.isGradleDependency(NON_BINARY_PATH_OS_STRING)).thenReturn(false);
        PowerMockito.mockStatic(JavaCore.class);
        Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenRepoPath);
        Mockito.when(mavenRepoPath.toOSString()).thenReturn(MAVEN_REPO_PATH_OS_STRING);
        Mockito.when(extractor.getMavenPathGav(MAVEN_PATH_OS_STRING, MAVEN_REPO_PATH_OS_STRING)).thenReturn(mavenGav);
        Mockito.when(extractor.getGradlePathGav(GRADLE_PATH_OS_STRING)).thenReturn(gradleGav);
        Mockito.when(mavenGav.getGroupId()).thenReturn(MAVEN_GROUP_STRING);
        Mockito.when(mavenGav.getArtifactId()).thenReturn(MAVEN_ARTIFACT_STRING);
        Mockito.when(mavenGav.getVersion()).thenReturn(MAVEN_VERSION_STRING);
        Mockito.when(gradleGav.getGroupId()).thenReturn(GRADLE_GROUP_STRING);
        Mockito.when(gradleGav.getArtifactId()).thenReturn(GRADLE_ARTIFACT_STRING);
        Mockito.when(gradleGav.getVersion()).thenReturn(GRADLE_VERSION_STRING);
    }

    @Test
    public void testClasspathNotChanged() throws CoreException {
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
    public void testDependencyRemovedFromClasspath() throws CoreException {
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
        Mockito.verify(extractor, Mockito.times(0)).getGradlePathGav(NON_BINARY_PATH_OS_STRING);
        Mockito.verify(extractor, Mockito.times(0)).getMavenPathGav(NON_BINARY_PATH_OS_STRING,
                MAVEN_REPO_PATH_OS_STRING);
        Mockito.verify(depService, Mockito.times(1)).isGradleDependency(NON_BINARY_PATH_OS_STRING);
        Mockito.verify(depService, Mockito.times(1)).isMavenDependency(NON_BINARY_PATH_OS_STRING);
    }

}
