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
package com.blackducksoftware.integration.eclipseplugin.views.utils;

import java.util.Comparator;

import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;

public class DependencyTableViewerComparator implements Comparator<ComponentModel> {

    @Override
    public int compare(ComponentModel o1, ComponentModel o2) {
        int[] gav1vulns = o1.getVulnerabilityCount();
        int[] gav2vulns = o2.getVulnerabilityCount();
        for (int i = 0; i < gav1vulns.length; i++) {
            int compareVal = gav2vulns[i] - gav1vulns[i];
            if (compareVal != 0) {
                return compareVal;
            }
        }
        if (!o1.getComponentIsKnown()) {
            return -1;
        } else if (!o2.getComponentIsKnown()) {
            return 1;
        }
        return (o1.getGav().getArtifactId() + o1.getGav().getVersion()).compareTo(
                (o2.getGav().getArtifactId() + o2.getGav().getVersion()));
    }
}
