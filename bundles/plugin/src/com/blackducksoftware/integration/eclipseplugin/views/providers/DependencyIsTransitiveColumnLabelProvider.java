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
package com.blackducksoftware.integration.eclipseplugin.views.providers;

import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;

public class DependencyIsTransitiveColumnLabelProvider extends DependencyTreeViewLabelProvider {

    private final DependencyTableViewContentProvider cp;

    public DependencyIsTransitiveColumnLabelProvider(DependencyTableViewContentProvider cp) {
        this.cp = cp;
    }

    @Override
    public String getText(Object input) {
        if (input instanceof GavWithParentProject) {
            if (((GavWithParentProject) input).getParentProject().equals(cp.getInputProject())) {
                return "Direct Dependency";
            }
            return "Transitive Dependency";
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
    }

    @Override
    public String getTitle() {
        return "Dependency Type";
    }

}
