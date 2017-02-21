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
package com.blackducksoftware.integration.eclipseplugin.common.constants;

public class InspectionStatus {
    public static final String INITIALIZING = "Initializing component inspector...";

    public static final String NO_SELECTED_PROJECT = "No open project selected";

    public static final String PROJECT_INSPECTION_ACTIVE = "Inspecting project...";

    public static final String PROJECT_INSPECTION_SCHEDULED = "Project scheduled for inspection";

    public static final String PROJECT_INSPECTION_INACTIVE = "Inspection not activated for current project";

    public static final String PROJECT_NEEDS_INSPECTION = "Project has not yet been inspected";

    public static final String CONNECTION_DISCONNECTED = "Cannot connect to Hub instance";

    public static final String CONNECTION_OK = "Connected to Hub instance - double-click any component to open it in the Hub";
}
