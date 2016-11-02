package com.blackducksoftware.integration.eclipseplugin.preferences;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.test.utils.swtbot.SWTBotPreferenceUtils;
import com.blackducksoftware.integration.eclipseplugin.test.utils.swtbot.SWTBotProjectUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ActiveJavaProjectsTest {

	private static SWTWorkbenchBot bot;
	private static SWTBotPreferenceUtils botPrefUtils;
	private static SWTBotProjectUtils botProjectUtils;

	private static final String TEST_JAVA_PROJECT_NAME = "pref-test-java-project";
	private static final String TEST_NON_JAVA_PROJECT_NAME = "pref-test-non-java-project";
	private static final String TEST_MAVEN_PROJECT_ARTIFACT_ID = "test.artifact.id";
	private static final String TEST_MAVEN_PROJECT_GROUP_ID = "test.group.id";

	@BeforeClass
	public static void setUpWorkspace() {
		bot = new SWTWorkbenchBot();
		botPrefUtils = new SWTBotPreferenceUtils(bot);
		botProjectUtils = new SWTBotProjectUtils(bot);
		try {
			bot.viewByTitle("Welcome").close();
		} catch (final RuntimeException e) {
		}
		botProjectUtils.createJavaProject(TEST_JAVA_PROJECT_NAME);
		botProjectUtils.createNonJavaProject(TEST_NON_JAVA_PROJECT_NAME);
		botProjectUtils.createMavenProject(TEST_MAVEN_PROJECT_GROUP_ID, TEST_MAVEN_PROJECT_ARTIFACT_ID);
	}

	@Test
	public void testThatAllJavaProjectsShow() {
		botPrefUtils.getActiveJavaProjectsPage();
		final SWTBot pageBot = bot.activeShell().bot();
		assertNotNull(pageBot.checkBox(TEST_JAVA_PROJECT_NAME));
		assertNotNull(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID));
		try {
			pageBot.checkBox(TEST_NON_JAVA_PROJECT_NAME);
			fail();
		} catch (final WidgetNotFoundException e) {
		}
		bot.activeShell().close();
	}

	@Test
	public void testThatActivateByDefaultWorks() {
		botPrefUtils.setPrefsToActivateScanByDefault();
		botPrefUtils.getActiveJavaProjectsPage();
		final SWTBot pageBot = bot.activeShell().bot();
		pageBot.button("Restore Defaults").click();
		assertTrue(pageBot.checkBox(TEST_JAVA_PROJECT_NAME).isChecked());
		assertTrue(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID).isChecked());
		bot.activeShell().close();
	}

	@Test
	public void testThatDoNotActivateByDefaultWorks() {
		botPrefUtils.setPrefsToNotActivateScanByDefault();
		botPrefUtils.getActiveJavaProjectsPage();
		final SWTBot pageBot = bot.activeShell().bot();
		pageBot.button("Restore Defaults").click();
		assertFalse(pageBot.checkBox(TEST_JAVA_PROJECT_NAME).isChecked());
		assertFalse(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID).isChecked());
		bot.activeShell().close();
	}

	@Ignore
	public void testThatBlackDuckDefaultWorks() {
		botPrefUtils.restoreAllBlackDuckDefaults();
		botPrefUtils.getActiveJavaProjectsPage();
		final SWTBot pageBot = bot.activeShell().bot();
		pageBot.button("Restore Defaults").click();
		assertTrue(pageBot.checkBox(TEST_JAVA_PROJECT_NAME).isChecked());
		assertTrue(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID).isChecked());
		bot.activeShell().close();
	}

	@AfterClass
	public static void tearDownWorkspace() {
		botProjectUtils.deleteProjectFromDisk(TEST_JAVA_PROJECT_NAME);
		botProjectUtils.deleteProjectFromDisk(TEST_MAVEN_PROJECT_ARTIFACT_ID);
		botProjectUtils.deleteProjectFromDisk(TEST_NON_JAVA_PROJECT_NAME);
		bot.resetWorkbench();
	}

}
