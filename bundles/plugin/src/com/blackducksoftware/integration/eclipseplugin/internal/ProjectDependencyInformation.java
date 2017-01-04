package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.build.GavWithType;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.dataservice.license.LicenseDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class ProjectDependencyInformation {

    private final ComponentCache componentCache;

    private final Map<String, Map<Gav, DependencyInfo>> projectInfo = new HashMap<>();

    private final ProjectInformationService projService;

    private final WorkspaceInformationService workspaceService;

    private VulnerabilityView componentView;

    public ProjectDependencyInformation(final ProjectInformationService projService, WorkspaceInformationService workspaceService,
            ComponentCache componentCache) {
        this.projService = projService;
        this.workspaceService = workspaceService;
        this.componentCache = componentCache;
    }

    public void setComponentView(final VulnerabilityView componentView) {
        this.componentView = componentView;
    }

    public void removeComponentView() {
        componentView = null;
    }

    public void addNewProject(final String projectName) {
        if (!projectInfo.containsKey(projectName)) {
            addProject(projectName);
        }
    }

    public void addAllProjects() {
        String[] projects = workspaceService.getJavaProjectNames();
        for (String project : projects) {
            addProject(project);
        }
    }

    public void addProject(String projectName) {
        final GavWithType[] gavs = projService.getMavenAndGradleDependencies(projectName);
        final Map<Gav, DependencyInfo> deps = new ConcurrentHashMap<>();
        for (final GavWithType gav : gavs) {
            try {
                deps.put(gav.getGav(), componentCache.getCache().get(gav));
            } catch (final ExecutionException e) {
                /*
                 * Thrown if exception occurs when accessing key gav from cache. If an exception is
                 * thrown, info associated with that gav is inaccessible, and so don't put any
                 * information related to said gav into hashmap associated with the project
                 */
            	System.out.println(e.getStackTrace());
            }
        }
        projectInfo.put(projectName, deps);
    }

    public void addWarningToProject(final String projectName, final GavWithType gav) {
        final Map<Gav, DependencyInfo> deps = projectInfo.get(projectName);
        if (deps != null) {
            try {
                deps.put(gav.getGav(), componentCache.getCache().get(gav));
                if (componentView != null) {
                    componentView.resetInput();
                }
            } catch (ExecutionException e) {
                /*
                 * Thrown if exception occurs when accessing key gav from cache. If an exception is
                 * thrown, info associated with that gav is inaccessible, and so don't put any
                 * information related to said gav into hashmap associated with the project
                 */
            }
        }
    }

    public void removeProject(final String projectName) {
        projectInfo.remove(projectName);
    }

    public void removeWarningFromProject(final String projectName, final Gav gav) {
        final Map<Gav, DependencyInfo> dependencies = projectInfo.get(projectName);
        if (dependencies != null) {
            dependencies.remove(gav);
            if (componentView != null) {
                componentView.resetInput();
            }
        }
    }

    public boolean containsProject(final String projectName) {
        return projectInfo.containsKey(projectName);
    }

    public Gav[] getAllDependencyGavs(final String projectName) {
        final Map<Gav, DependencyInfo> dependencyInfo = projectInfo.get(projectName);
        if (dependencyInfo != null) {
            return dependencyInfo.keySet().toArray(new Gav[dependencyInfo.keySet().size()]);
        } else {
            return new Gav[0];
        }
    }

    //TODO deprecate
    public Map<Gav, List<VulnerabilityItem>> getVulnMap(String projectName) {
        Map<Gav, List<VulnerabilityItem>> vulnMap = new HashMap<Gav, List<VulnerabilityItem>>();
    	
    	Map<Gav, DependencyInfo> projDepInfo = projectInfo.get(projectName);
        for(Map.Entry<Gav, DependencyInfo> entry : projDepInfo.entrySet()) {
        	vulnMap.put(entry.getKey(), entry.getValue().getVulnList());
        }
    	
    	return vulnMap;
    }
    
    public Map<Gav, DependencyInfo> getDependencyInfoMap(String projectName) {
    	return projectInfo.get(projectName);
    }

    public void renameProject(String oldName, String newName) {
        Map<Gav, DependencyInfo> info = projectInfo.get(oldName);
        projectInfo.put(newName, info);
        projectInfo.remove(oldName);
    }

    public void updateCache(RestConnection connection) throws HubIntegrationException {
        if (connection != null) {
            HubServicesFactory servicesFactory = new HubServicesFactory(connection);
            //TODO logging
            VulnerabilityDataService vulnService = servicesFactory.createVulnerabilityDataService(new IntBufferedLogger());
            LicenseDataService licenseService = servicesFactory.createLicenseDataService();
            componentCache.setVulnService(vulnService, licenseService);
            addAllProjects();
        } else {
            componentCache.setVulnService(null, null);
        }
    }

}
