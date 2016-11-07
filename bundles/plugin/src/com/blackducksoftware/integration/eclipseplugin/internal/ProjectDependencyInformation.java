package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.HashMap;
import java.util.Iterator;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;

public class ProjectDependencyInformation {

	private final HashMap<String, HashMap<Gav, Warning>> projectInfo;
	private final ProjectInformationService projService;

	public ProjectDependencyInformation(final ProjectInformationService projService) {
		projectInfo = new HashMap<String, HashMap<Gav, Warning>>();
		this.projService = projService;
	}

	public void addProject(final String projectName) {
		final Gav[] gavs = projService.getMavenAndGradleDependencies(projectName);
		final HashMap<Gav, Warning> deps = new HashMap<Gav, Warning>();
		for (final Gav gav : gavs) {
			// API call to make warning
			deps.put(gav, null);
		}
		projectInfo.put(projectName, deps);
	}

	public void addWarningToProject(final String projectName, final Gav gav) {
		final HashMap<Gav, Warning> deps = projectInfo.get(projectName);
		// API call to make warning
		deps.put(gav, null);
	}

	public void removeProject(final String projectName) {
		projectInfo.remove(projectName);
	}

	public void removeWarningFromProject(final String projectName, final Gav gav) {
		final HashMap<Gav, Warning> dependencies = projectInfo.get(projectName);
		dependencies.remove(gav);
	}

	public boolean containsProject(final String projectName) {
		return projectInfo.containsKey(projectName);
	}

	public void printAllInfo() {
		final Iterator<String> nameIt = projectInfo.keySet().iterator();
		System.out.println("WORKSPACE INFO:");
		System.out.println("----------------");
		while (nameIt.hasNext()) {
			final String name = nameIt.next();
			System.out.println("PROJECT: " + name);
			final Iterator<Gav> gavIt = projectInfo.get(name).keySet().iterator();
			System.out.println("DEPENDENCIES:");
			while (gavIt.hasNext()) {
				final Gav gav = gavIt.next();
				System.out.println(gav.getGroupId() + ":" + gav.getArtifactId() + ":" + gav.getVersion());
			}
			System.out.println("----------------");
		}
	}

}
