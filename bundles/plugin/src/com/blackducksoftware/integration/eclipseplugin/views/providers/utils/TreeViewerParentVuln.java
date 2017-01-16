package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import java.util.Iterator;
import java.util.List;

import com.blackducksoftware.integration.eclipseplugin.views.providers.DependencyTableViewContentProvider;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusMeta;

public class TreeViewerParentVuln extends TreeViewerParent {

    private final List<VulnerabilityItemPlusMeta> vulns;

    public TreeViewerParentVuln(String dispName, GavWithParentProject gavWithParentProj, List<VulnerabilityItemPlusMeta> vulns) {
        super(dispName, gavWithParentProj);
        this.vulns = vulns;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public Object[] getChildren() {
        if (vulns == null || vulns.size() == 0) {
            return DependencyTableViewContentProvider.NO_VULNERABILITIES_TO_SHOW;
        }

        List<VulnerabilityItemPlusMeta> vulnList = vulns;
        Iterator<VulnerabilityItemPlusMeta> vulnIt = vulnList.iterator();
        VulnerabilityWithParentGav[] vulnsWithGavs = new VulnerabilityWithParentGav[vulnList.size()];
        int i = 0;
        while (vulnIt.hasNext()) {
            VulnerabilityWithParentGav vulnWithGav = new VulnerabilityWithParentGav(gavWithParentProject.getGav(), vulnIt.next());
            vulnsWithGavs[i] = vulnWithGav;
            i++;
        }
        return vulnsWithGavs;

    }

}
