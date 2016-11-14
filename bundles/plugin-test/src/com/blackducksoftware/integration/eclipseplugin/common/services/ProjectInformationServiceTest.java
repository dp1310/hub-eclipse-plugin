package com.blackducksoftware.integration.eclipseplugin.common.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.build.GavTypeEnum;
import com.blackducksoftware.integration.build.GavWithType;
import com.blackducksoftware.integration.build.utils.FilePathGavExtractor;
import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;

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
    FilePathGavExtractor extractor;

    @Mock
    Gav mavenGav1, mavenGav2, gradleGav1, gradleGav2;

    @Mock
    GavWithType mavenGavWithType1, mavenGavWithType2, gradleGavWithType1, gradleGavWithType2;

    private final String MAVEN_1 = "maven1";

    private final String MAVEN_2 = "maven2";

    private final String GRADLE_1 = "gradle1";

    private final String GRADLE_2 = "gradle2";

    private final String NOT_GRAVEN_1 = "notgraven1";

    private final String NOT_GRAVEN_2 = "notgraven2";

    private final String MAVEN_REPO_PATH = "";

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
            final IPackageFragmentRoot[] roots = new IPackageFragmentRoot[] { nonBinaryRoot, binaryRoot1 };
            assertEquals(1, service.getNumBinaryDependencies(roots));
        } catch (final CoreException e) {
        }
    }

    @Test
    public void testIsJavaProject() {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        try {
            Mockito.when(nonJavaProject.hasNature(JavaCore.NATURE_ID)).thenReturn(false);
            Mockito.when(javaProject.hasNature(JavaCore.NATURE_ID)).thenReturn(true);
            assertTrue(service.isJavaProject(javaProject));
            assertFalse(service.isJavaProject(nonJavaProject));
        } catch (final CoreException e) {
        }

    }

    @Test
    public void testGettingBinaryFilepathsWithoutDeviceIDs() {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        try {
            Mockito.when(nonBinaryRoot.getKind()).thenReturn(0);
            Mockito.when(nonBinaryRoot.getPath()).thenReturn(nonBinaryPath);
            Mockito.when(nonBinaryPath.getDevice()).thenReturn(null);
            Mockito.when(nonBinaryPath.toOSString()).thenReturn("/non/binary");
            Mockito.when(binaryRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
            Mockito.when(binaryRoot1.getPath()).thenReturn(binaryPath1);
            Mockito.when(binaryPath1.getDevice()).thenReturn(null);
            Mockito.when(binaryPath1.toOSString()).thenReturn("/binary/path/1");

            final IPackageFragmentRoot[] roots = new IPackageFragmentRoot[] { nonBinaryRoot, binaryRoot1 };
            final String[] binaryDependencies = service.getBinaryDependencyFilepaths(roots);
            assertEquals(1, binaryDependencies.length);
            assertArrayEquals(new String[] { "/binary/path/1" }, binaryDependencies);

        } catch (final CoreException e) {

        }
    }

    @Test
    public void testGettingBinaryFilepathsWithDeviceIDs() {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        try {
            Mockito.when(binaryRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
            Mockito.when(binaryRoot1.getPath()).thenReturn(binaryPath1);
            Mockito.when(binaryPath1.getDevice()).thenReturn("fake/device/id/1");
            Mockito.when(binaryPath1.toOSString()).thenReturn("fake/device/id/1/binary/path/1");
            Mockito.when(binaryRoot2.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
            Mockito.when(binaryRoot2.getPath()).thenReturn(binaryPath2);
            Mockito.when(binaryPath2.getDevice()).thenReturn("fake/device/id/2");
            Mockito.when(binaryPath2.toOSString()).thenReturn("fake/device/id/2/binary/path/2");
            final IPackageFragmentRoot[] roots = new IPackageFragmentRoot[] { binaryRoot1, binaryRoot2 };
            final String[] binaryDependencies = service.getBinaryDependencyFilepaths(roots);
            assertEquals(2, binaryDependencies.length);
            assertArrayEquals(new String[] { "/binary/path/1", "/binary/path/2" }, binaryDependencies);
        } catch (final CoreException e) {

        }
    }

    private void prepareRootsAndPaths() throws CoreException {
        Mockito.when(mavenRoot1.getPath()).thenReturn(mavenPath1);
        Mockito.when(mavenRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(mavenPath1.getDevice()).thenReturn(null);
        Mockito.when(mavenPath1.toOSString()).thenReturn(MAVEN_1);
        Mockito.when(mavenRoot2.getPath()).thenReturn(mavenPath2);
        Mockito.when(mavenRoot2.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(mavenPath2.getDevice()).thenReturn(null);
        Mockito.when(mavenPath2.toOSString()).thenReturn(MAVEN_2);
        Mockito.when(gradleRoot1.getPath()).thenReturn(gradlePath1);
        Mockito.when(gradleRoot1.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(gradlePath1.getDevice()).thenReturn(null);
        Mockito.when(gradlePath1.toOSString()).thenReturn(GRADLE_1);
        Mockito.when(gradleRoot2.getPath()).thenReturn(gradlePath2);
        Mockito.when(gradleRoot2.getKind()).thenReturn(IPackageFragmentRoot.K_BINARY);
        Mockito.when(gradlePath2.getDevice()).thenReturn(null);
        Mockito.when(gradlePath2.toOSString()).thenReturn(GRADLE_2);
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
        Mockito.when(mavenGavWithType1.getGav()).thenReturn(mavenGav1);
        Mockito.when(mavenGavWithType2.getGav()).thenReturn(mavenGav2);
        Mockito.when(gradleGavWithType1.getGav()).thenReturn(gradleGav1);
        Mockito.when(gradleGavWithType2.getGav()).thenReturn(gradleGav2);
        Mockito.when(mavenGavWithType1.getType()).thenReturn(GavTypeEnum.MAVEN);
        Mockito.when(mavenGavWithType2.getType()).thenReturn(GavTypeEnum.MAVEN);
        Mockito.when(gradleGavWithType1.getType()).thenReturn(GavTypeEnum.MAVEN);
        Mockito.when(gradleGavWithType2.getType()).thenReturn(GavTypeEnum.MAVEN);
    }

    @Test
    public void testGettingNumberMavenAndGradleDependencies() {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        prepareDependencyTypes();
        final String[] deps = new String[] { MAVEN_1, MAVEN_2, GRADLE_1, GRADLE_2, NOT_GRAVEN_1, NOT_GRAVEN_2 };
        assertEquals(4, service.getNumMavenAndGradleDependencies(deps));
    }

    @Test
    public void testGettingGavsFromFilepaths() {
        final ProjectInformationService service = new ProjectInformationService(depService, extractor);
        prepareExtractor();
        prepareDependencyTypes();
        prepareGavElements();
        prepareGavsWithType();
        PowerMockito.mockStatic(JavaCore.class);
        Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
        Mockito.when(mavenPath.toString()).thenReturn(MAVEN_REPO_PATH);
        prepareExtractor();
        final String[] dependencies = new String[] { MAVEN_1, MAVEN_2, GRADLE_1, GRADLE_2, NOT_GRAVEN_1, NOT_GRAVEN_2 };
        final GavWithType[] gavs = service.getGavsFromFilepaths(dependencies);
        final GavWithType[] expectedGavMessages = new GavWithType[] { new GavWithType(MAVEN_1_GAV, GavTypeEnum.MAVEN),
                new GavWithType(MAVEN_2_GAV, GavTypeEnum.MAVEN), new GavWithType(GRADLE_1_GAV, GavTypeEnum.MAVEN),
                new GavWithType(GRADLE_2_GAV, GavTypeEnum.MAVEN) };
        assertArrayEquals(expectedGavMessages, gavs);
    }

    @Test
    public void testGettingAllMavenAndGradleDependencyMessages() {

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
            final GavWithType[] expectedGavMessages = new GavWithType[] { new GavWithType(MAVEN_1_GAV, GavTypeEnum.MAVEN),
                    new GavWithType(MAVEN_2_GAV, GavTypeEnum.MAVEN), new GavWithType(GRADLE_1_GAV, GavTypeEnum.MAVEN),
                    new GavWithType(GRADLE_2_GAV, GavTypeEnum.MAVEN) };
            Mockito.when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);
            Mockito.when(workspace.getRoot()).thenReturn(workspaceRoot);
            Mockito.when(workspaceRoot.getProject(TEST_PROJECT_NAME)).thenReturn(testProject);
            Mockito.when(testProject.hasNature(JavaCore.NATURE_ID)).thenReturn(true);
            Mockito.when(JavaCore.create(testProject)).thenReturn(testJavaProject);
            Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
            Mockito.when(mavenPath.toString()).thenReturn(MAVEN_REPO_PATH);
            Mockito.when(testJavaProject.getPackageFragmentRoots()).thenReturn(roots);
            final GavWithType[] noDeps = service.getMavenAndGradleDependencies("");
            assertArrayEquals(new GavWithType[0], noDeps);
            final GavWithType[] deps = service.getMavenAndGradleDependencies(TEST_PROJECT_NAME);
            assertArrayEquals(expectedGavMessages, deps);
        } catch (final CoreException e) {
        }
    }
}
