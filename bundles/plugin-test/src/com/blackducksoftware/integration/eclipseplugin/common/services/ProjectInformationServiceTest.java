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
	IProject testProject;
	@Mock
	IJavaProject testJavaProject;
	@Mock
	IProject nonJavaProject;
	@Mock
	IProject javaProject;
	@Mock
	DependencyInformationService depService;
	@Mock
	IPackageFragmentRoot nonBinaryRoot;
	@Mock
	IPath nonBinaryPath;
	@Mock
	IPackageFragmentRoot binaryRoot1;
	@Mock
	IPath binaryPath1;
	@Mock
	IPackageFragmentRoot binaryRoot2;
	@Mock
	IPath binaryPath2;
	@Mock
	IPath mavenPath;
	@Mock
	FilePathGavExtractor extractor;
	@Mock
	Gav mavenGav1;
	@Mock
	IPackageFragmentRoot mavenRoot1;
	@Mock
	IPath mavenPath1;
	@Mock
	Gav mavenGav2;
	@Mock
	IPackageFragmentRoot mavenRoot2;
	@Mock
	IPath mavenPath2;
	@Mock
	Gav gradleGav1;
	@Mock
	IPackageFragmentRoot gradleRoot1;
	@Mock
	IPath gradlePath1;
	@Mock
	Gav gradleGav2;
	@Mock
	IPackageFragmentRoot gradleRoot2;
	@Mock
	IPath gradlePath2;

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
	private final String MAVEN_1_GAV_MESSAGE = "group: maven.1.group, artifact: maven.1.artifact, version: maven.1.version";
	private final String MAVEN_2_GROUP = "maven.2.group";
	private final String MAVEN_2_ARTIFACT = "maven.2.artifact";
	private final String MAVEN_2_VERSION = "maven.2.version";
	private final String MAVEN_2_GAV_MESSAGE = "group: maven.2.group, artifact: maven.2.artifact, version: maven.2.version";
	private final String GRADLE_1_GROUP = "gradle.1.group";
	private final String GRADLE_1_ARTIFACT = "gradle.1.artifact";
	private final String GRADLE_1_VERSION = "gradle.1.version";
	private final String GRADLE_1_GAV_MESSAGE = "group: gradle.1.group, artifact: gradle.1.artifact, version: gradle.1.version";
	private final String GRADLE_2_GROUP = "gradle.2.group";
	private final String GRADLE_2_ARTIFACT = "gradle.2.artifact";
	private final String GRADLE_2_VERSION = "gradle.2.version";
	private final String GRADLE_2_GAV_MESSAGE = "group: gradle.2.group, artifact: gradle.2.artifact, version: gradle.2.version";

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
		Mockito.when(extractor.getMavenPathGav(MAVEN_1, MAVEN_REPO_PATH)).thenReturn(mavenGav1);
		Mockito.when(extractor.getMavenPathGav(MAVEN_2, MAVEN_REPO_PATH)).thenReturn(mavenGav2);
		Mockito.when(extractor.getGradlePathGav(GRADLE_1)).thenReturn(gradleGav1);
		Mockito.when(extractor.getGradlePathGav(GRADLE_2)).thenReturn(gradleGav2);
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
		PowerMockito.mockStatic(JavaCore.class);
		Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
		Mockito.when(mavenPath.toString()).thenReturn(MAVEN_REPO_PATH);
		prepareExtractor();
		final String[] dependencies = new String[] { MAVEN_1, MAVEN_2, GRADLE_1, GRADLE_2, NOT_GRAVEN_1, NOT_GRAVEN_2 };
		final String[] gavMessages = service.getGavsFromFilepaths(dependencies);
		final String[] expectedGavMessages = new String[] { MAVEN_1_GAV_MESSAGE, MAVEN_2_GAV_MESSAGE,
				GRADLE_1_GAV_MESSAGE, GRADLE_2_GAV_MESSAGE };
		assertArrayEquals(expectedGavMessages, gavMessages);
	}

	@Test
	public void testGettingGavMessage() {
		final ProjectInformationService service = new ProjectInformationService(depService, extractor);
		prepareGavElements();
		final String mavenGavMessage1 = service.getGavMessage(mavenGav1);
		assertEquals(MAVEN_1_GAV_MESSAGE, mavenGavMessage1);
		final String mavenGavMessage2 = service.getGavMessage(mavenGav2);
		assertEquals(MAVEN_2_GAV_MESSAGE, mavenGavMessage2);
		final String gradleGavMessage1 = service.getGavMessage(gradleGav1);
		assertEquals(GRADLE_1_GAV_MESSAGE, gradleGavMessage1);
		final String gradleGavMessage2 = service.getGavMessage(gradleGav2);
		assertEquals(GRADLE_2_GAV_MESSAGE, gradleGavMessage2);
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
			final IPackageFragmentRoot[] roots = new IPackageFragmentRoot[] { mavenRoot1, mavenRoot2, gradleRoot1,
					gradleRoot2 };
			final String[] expectedGavMessages = new String[] { MAVEN_1_GAV_MESSAGE, MAVEN_2_GAV_MESSAGE,
					GRADLE_1_GAV_MESSAGE, GRADLE_2_GAV_MESSAGE };
			Mockito.when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);
			Mockito.when(workspace.getRoot()).thenReturn(workspaceRoot);
			Mockito.when(workspaceRoot.getProject(TEST_PROJECT_NAME)).thenReturn(testProject);
			Mockito.when(testProject.hasNature(JavaCore.NATURE_ID)).thenReturn(true);
			Mockito.when(JavaCore.create(testProject)).thenReturn(testJavaProject);
			Mockito.when(JavaCore.getClasspathVariable(ClasspathVariables.MAVEN)).thenReturn(mavenPath);
			Mockito.when(mavenPath.toString()).thenReturn(MAVEN_REPO_PATH);
			Mockito.when(testJavaProject.getPackageFragmentRoots()).thenReturn(roots);
			final String[] noDeps = service.getMavenAndGradleDependencies("");
			assertArrayEquals(new String[0], noDeps);
			final String[] deps = service.getMavenAndGradleDependencies(TEST_PROJECT_NAME);
			assertArrayEquals(expectedGavMessages, deps);
		} catch (final CoreException e) {
		}
	}
}